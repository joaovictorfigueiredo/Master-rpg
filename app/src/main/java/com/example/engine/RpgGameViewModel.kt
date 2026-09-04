package com.example.engine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.R
import com.example.model.CharacterModel
import com.example.model.DungeonDefinition
import com.example.model.DEFAULT_DUNGEONS
import com.example.model.DungeonChatMessage
import com.example.model.DungeonPathChoice
import com.example.model.GameItem
import com.example.model.GameMode
import com.example.model.GameRoomState
import com.example.model.GameTurnEvent
import com.example.model.ItemType
import com.example.model.MessageSenderType
import com.example.model.Monster
import com.example.model.OnlinePartyMember
import com.example.model.RollSuccessLevel
import com.example.model.Skill
import com.example.model.Weapon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.random.Random

data class PendingAction(
    val actionText: String,
    val actionType: String, // "ATTACK", "SKILL", "EXPLORE", "CUSTOM"
    val skill: Skill? = null,
    val weapon: Weapon? = null,
    val targetDc: Int = 12
)

class RpgGameViewModel : ViewModel() {

    private val _character = MutableStateFlow(
        CharacterModel(
            name = "Valerius",
            race = "Humano",
            characterClass = "Guerreiro",
            archetype = "Cavaleiro Arcano",
            backgroundStory = "Veterano das legiões imperiais que busca redenção nas criptas profundas.",
            avatarDrawableRes = R.drawable.img_hero_portrait,
            level = 3,
            currentXp = 380,
            maxXp = 600,
            currentHp = 85,
            maxHp = 100,
            currentMana = 35,
            maxMana = 50,
            strength = 16,
            dexterity = 12,
            intelligence = 10,
            vitality = 14,
            charisma = 11
        )
    )
    val character: StateFlow<CharacterModel> = _character.asStateFlow()

    private val _inventory = MutableStateFlow(
        listOf(
            GameItem(
                id = "wep-01",
                name = "Espada Rúnica de Ferro Negro",
                description = "Lâmina forjada com canaletas arcanas que potencializam o impacto físico.",
                itemType = ItemType.WEAPON,
                isUniqueItem = false,
                isPermanent = true,
                isCommonDungeonItem = false,
                bonusStat = "+16 Dano de Corte",
                weaponData = Weapon(
                    id = "wep-01",
                    name = "Espada Rúnica de Ferro Negro",
                    description = "Lâmina forjada com canaletas arcanas.",
                    damage = 16,
                    damageType = "Corte Arcano",
                    bonusStat = "+4 Ataque Base",
                    isDungeonCrafted = false,
                    isEquipped = true
                )
            ),
            GameItem(
                id = "tool-01",
                name = "Kit de Forja Portátil do Mestre",
                description = "Conjunto de martelo rúnico, tenaz temperada e bigorna leve para forjar novas armas durante as masmorras.",
                itemType = ItemType.TOOL,
                isUniqueItem = false,
                isPermanent = true,
                isCommonDungeonItem = false,
                bonusStat = "Permite Criar Armas na Dungeon"
            ),
            GameItem(
                id = "mat-01",
                name = "Lingote de Aço das Criptas",
                description = "Metal denso imbuído de resquícios de energia sombria das paredes da masmorra.",
                itemType = ItemType.MATERIAL,
                isUniqueItem = false,
                isPermanent = false,
                isCommonDungeonItem = true,
                bonusStat = "Material de Forja"
            ),
            GameItem(
                id = "mat-02",
                name = "Cristal Ígneo Vulcânico",
                description = "Gema incandescente coletada de fendas profundas. Adiciona dano elemental a armas forjadas.",
                itemType = ItemType.MATERIAL,
                isUniqueItem = false,
                isPermanent = false,
                isCommonDungeonItem = true,
                bonusStat = "+10 Dano de Fogo em Forjas"
            ),
            GameItem(
                id = "pot-01",
                name = "Poção Alquímica de Vida Maior",
                description = "Elixir restaurador que estanca hemorragias e recupera 35 pontos de vida imediatamente.",
                itemType = ItemType.POTION,
                isUniqueItem = false,
                isPermanent = false,
                isCommonDungeonItem = true,
                bonusStat = "+35 Vida",
                healAmount = 35
            ),
            GameItem(
                id = "pot-02",
                name = "Elixir de Mana Astral",
                description = "Líquido cintilante de tom violeta que repõe 25 pontos de mana mística.",
                itemType = ItemType.POTION,
                isUniqueItem = false,
                isPermanent = false,
                isCommonDungeonItem = true,
                bonusStat = "+25 Mana",
                manaAmount = 25
            ),
            GameItem(
                id = "art-01",
                name = "Amuleto de Sangue Antigo",
                description = "Item Único permanente recebido em uma campanha passada. Viaja com o personagem para sempre!",
                itemType = ItemType.ARTIFACT,
                isUniqueItem = true,
                isPermanent = true,
                isCommonDungeonItem = false,
                bonusStat = "+10 Max HP & Proteção contra Trevas",
                alchemyLineage = "Forjado nas Minas de Morvath"
            )
        )
    )
    val inventory: StateFlow<List<GameItem>> = _inventory.asStateFlow()

    private val _roomState = MutableStateFlow(
        GameRoomState(
            roomCode = "SOLO-01",
            mode = GameMode.SOLO,
            floorNumber = 1,
            roomTitle = "O Sepulcro dos Ecos Ancestrais",
            environmentDescription = "Gotas de água gotejam do teto de pedra calcária, ecoando na escuridão. O ar cheira a mofo e cinzas antigas.",
            currentMonster = Monster(
                id = "mon-1",
                name = "Necrófago das Criptas",
                currentHp = 45,
                maxHp = 45,
                attackPower = 10,
                description = "Criatura esquelética banhada em sombras que se alimenta do medo dos aventureiros."
            ),
            partyMembers = listOf(_character.value),
            turnHistory = listOf(
                GameTurnEvent(
                    turnNumber = 0,
                    actorName = "Mestre da IA",
                    actionText = "Início da Masmorra",
                    d20Roll = 20,
                    modifier = 0,
                    totalResult = 20,
                    difficultyClass = 10,
                    successLevel = RollSuccessLevel.SUCCESS,
                    gmNarrative = "Vocês adentram os portões rangentes da cripta. As tochas tremulam revelando olhos brilhantes nas sombras. A masmorra escalonou seu desafio para o nível do seu herói.",
                    tacticalSummary = "Encontro iniciado. O Necrófago observa seus movimentos."
                )
            ),
            availablePaths = listOf(
                DungeonPathChoice(
                    id = "path-1",
                    title = "Caminho da Forja Proibida",
                    description = "Passagem ladeada por oficinas abandonadas. Rumores de ferramentas e minérios raros.",
                    riskLevel = "Médio",
                    rewardHint = "Ferramentas & Minérios",
                    emoji = "⚒️"
                ),
                DungeonPathChoice(
                    id = "path-2",
                    title = "Câmara dos Ritos Sombrios",
                    description = "Corredor tomado por névoa violeta e cânticos esquecidos. Encontros de alto perigo.",
                    riskLevel = "Perigoso",
                    rewardHint = "Artefatos Arcanos",
                    emoji = "🔮"
                ),
                DungeonPathChoice(
                    id = "path-3",
                    title = "Fissura de Águas Claras",
                    description = "Uma gruta secreta com fonte cristalina onde é possível descansar e recuperar energias.",
                    riskLevel = "Baixo",
                    rewardHint = "Fonte de Cura & Mana",
                    emoji = "💧"
                )
            )
        )
    )
    val roomState: StateFlow<GameRoomState> = _roomState.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    private val _pendingAction = MutableStateFlow<PendingAction?>(null)
    val pendingAction: StateFlow<PendingAction?> = _pendingAction.asStateFlow()

    private val _showDiceModal = MutableStateFlow(false)
    val showDiceModal: StateFlow<Boolean> = _showDiceModal.asStateFlow()

    private val _latestCraftedItem = MutableStateFlow<GameItem?>(null)
    val latestCraftedItem: StateFlow<GameItem?> = _latestCraftedItem.asStateFlow()

    // Animation Triggers
    private val _damageAnimTrigger = MutableStateFlow<Long>(0L)
    val damageAnimTrigger: StateFlow<Long> = _damageAnimTrigger.asStateFlow()

    private val _healAnimTrigger = MutableStateFlow<Long>(0L)
    val healAnimTrigger: StateFlow<Long> = _healAnimTrigger.asStateFlow()

    private val _lastDamageAmount = MutableStateFlow(0)
    val lastDamageAmount: StateFlow<Int> = _lastDamageAmount.asStateFlow()

    private val _lastHealAmount = MutableStateFlow(0)
    val lastHealAmount: StateFlow<Int> = _lastHealAmount.asStateFlow()

    // Weapon Forge Modal State
    private val _showForgeDialog = MutableStateFlow(false)
    val showForgeDialog: StateFlow<Boolean> = _showForgeDialog.asStateFlow()

    // Character Creation Modal State
    private val _showCharacterCreationDialog = MutableStateFlow(false)
    val showCharacterCreationDialog: StateFlow<Boolean> = _showCharacterCreationDialog.asStateFlow()

    // Character Creation Gate: Player creates character before starting
    private val _hasCreatedCharacter = MutableStateFlow(false)
    val hasCreatedCharacter: StateFlow<Boolean> = _hasCreatedCharacter.asStateFlow()

    // Dungeon Session Active State
    private val _isDungeonActive = MutableStateFlow(false)
    val isDungeonActive: StateFlow<Boolean> = _isDungeonActive.asStateFlow()

    // Selected Dungeon & Lobby
    private val _selectedDungeon = MutableStateFlow<DungeonDefinition?>(null)
    val selectedDungeon: StateFlow<DungeonDefinition?> = _selectedDungeon.asStateFlow()

    private val _availableDungeons = MutableStateFlow<List<DungeonDefinition>>(DEFAULT_DUNGEONS)
    val availableDungeons: StateFlow<List<DungeonDefinition>> = _availableDungeons.asStateFlow()

    // Character Info & Bag Window
    private val _showCharacterAndBagSheet = MutableStateFlow(false)
    val showCharacterAndBagSheet: StateFlow<Boolean> = _showCharacterAndBagSheet.asStateFlow()

    // Add Item Dialog
    private val _showAddItemDialog = MutableStateFlow(false)
    val showAddItemDialog: StateFlow<Boolean> = _showAddItemDialog.asStateFlow()

    // Online Multiplayer & Dungeon Chat States
    private val _isOnlineMode = MutableStateFlow(false)
    val isOnlineMode: StateFlow<Boolean> = _isOnlineMode.asStateFlow()

    private val _currentRoomCode = MutableStateFlow("SALA-4892")
    val currentRoomCode: StateFlow<String> = _currentRoomCode.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<DungeonChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<DungeonChatMessage>> = _chatMessages.asStateFlow()

    private val _onlineParty = MutableStateFlow<List<OnlinePartyMember>>(emptyList())
    val onlineParty: StateFlow<List<OnlinePartyMember>> = _onlineParty.asStateFlow()

    private val _currentSituationPrompt = MutableStateFlow("")
    val currentSituationPrompt: StateFlow<String> = _currentSituationPrompt.asStateFlow()

    fun setHasCreatedCharacter(created: Boolean) {
        _hasCreatedCharacter.value = created
    }

    fun setShowCharacterAndBagSheet(show: Boolean) {
        _showCharacterAndBagSheet.value = show
    }

    fun setShowAddItemDialog(show: Boolean) {
        _showAddItemDialog.value = show
    }

    fun setOnlineMode(online: Boolean) {
        _isOnlineMode.value = online
    }

    /**
     * Add any item directly to the player's inventory / bag!
     */
    fun addItemToInventory(item: GameItem) {
        _inventory.value = _inventory.value + item
        if (item.itemType == ItemType.WEAPON && _character.value.equippedWeapon == null) {
            item.weaponData?.let {
                _character.value = _character.value.copy(equippedWeapon = it)
            }
        }
    }

    /**
     * Join an online room by code
     */
    fun joinOnlineRoom(code: String) {
        val targetDungeon = _selectedDungeon.value ?: _availableDungeons.value.first()
        val cleanCode = if (code.isNotBlank()) code.uppercase().trim() else "SALA-${Random.nextInt(1000, 9999)}"
        selectDungeon(
            dungeon = targetDungeon,
            customRoomName = "Sala Cooperativa $cleanCode",
            isOnline = true,
            roomCode = cleanCode
        )
    }

    /**
     * Select a dungeon and initialize room session with real-time AI narrative chat
     */
    fun selectDungeon(
        dungeon: DungeonDefinition,
        customRoomName: String? = null,
        customDifficulty: String? = null,
        isOnline: Boolean = false,
        roomCode: String? = null
    ) {
        _selectedDungeon.value = dungeon
        _isOnlineMode.value = isOnline
        val assignedCode = roomCode ?: if (isOnline) "SALA-${Random.nextInt(1000, 9999)}" else "ROOM-${Random.nextInt(1000, 9999)}"
        _currentRoomCode.value = assignedCode
        val title = if (!customRoomName.isNullOrBlank()) customRoomName else dungeon.name
        val diff = customDifficulty ?: dungeon.difficulty

        val boss = Monster(
            id = "boss-${dungeon.id}",
            name = dungeon.bossName,
            currentHp = dungeon.bossHp,
            maxHp = dungeon.bossHp,
            attackPower = dungeon.bossAttack,
            description = dungeon.bossDescription
        )

        // Generate high-stakes dramatic entrance dilemma
        val initialSituation = when {
            dungeon.theme.contains("Criptas") -> "Vocês adentram as criptas e pesadas grades de ferro caem trancando a saída! Fissuras se abrem no solo borbulhando ácido cáustico, enquanto o Guardião '${dungeon.bossName}' surge das sombras com olhos flamejantes brandindo uma alabarda nefasta! Gotas corrosivas pingam do teto e a fera salta para decepar o grupo. O que você vai fazer para se livrar dessa situação e sobreviver?"
            dungeon.theme.contains("Vulcão") || dungeon.theme.contains("Magma") -> "Uma ponte de ferro incandescente racha sobre um abismo de lava borbulhante! O calor sufoca e um gêiser de magma derrete as correntes de sustentação da ponte. O Guardião '${dungeon.bossName}' bloqueia a passagem do outro lado cuspindo fogo! O chão sob seus pés está prestes a desabar na lava. O que você vai fazer para escapar desse perigo iminente?"
            dungeon.theme.contains("Bosque") -> "Uma névoa arroxeada densa cerca o grupo. Raízes com espinhos venenosos brotam violentamente da terra e envolvem seus pés, enquanto o Guardião '${dungeon.bossName}' desce dos galhos silvando com garras estendidas prontas para o bote! As raízes começam a queimar a carne. O que você vai fazer para se livrar das amarras e escapar do ataque?"
            dungeon.theme.contains("Oficina") || dungeon.theme.contains("Mecânica") -> "Engrenagens colossais rangem e jatos de vapor escaldante disparam nas paredes! As portas blindadas se selam e o Guardião '${dungeon.bossName}' gira serras de lâminas arcanas avançando a toda velocidade encurralando vocês contra o muro de engrenagens! O que você vai fazer para se livrar dessa armadilha mecânica mortal?"
            else -> "Vocês entram no salão ancestral quando o piso começa a colapsar em um fosso repleto de estacas afiadas! O Guardião '${dungeon.bossName}' conjura glifos de detonação arcana que começam a brilhar intensamente sob os pés do grupo. O que você vai fazer para se livrar dessa explosão e do abismo?"
        }

        _currentSituationPrompt.value = initialSituation

        val partyList = if (isOnline) {
            listOf(
                OnlinePartyMember(
                    id = _character.value.id,
                    name = _character.value.name,
                    characterClass = _character.value.characterClass,
                    race = _character.value.race,
                    level = _character.value.level,
                    currentHp = _character.value.currentHp,
                    maxHp = _character.value.maxHp,
                    currentMana = _character.value.currentMana,
                    maxMana = _character.value.maxMana,
                    avatarRes = _character.value.avatarDrawableRes,
                    isLocalPlayer = true,
                    isOnline = true,
                    statusDescription = "Você (Líder da Ação)"
                ),
                OnlinePartyMember(
                    id = "comp-1",
                    name = "Lyra Solstício",
                    characterClass = "Maga Arcana",
                    race = "Elfa",
                    level = (_character.value.level).coerceAtLeast(1),
                    currentHp = 70,
                    maxHp = 70,
                    currentMana = 60,
                    maxMana = 60,
                    avatarRes = R.drawable.avatar_elf_mage,
                    isLocalPlayer = false,
                    isOnline = true,
                    statusDescription = "Conectada • Focando feitiços"
                ),
                OnlinePartyMember(
                    id = "comp-2",
                    name = "Kaelen Sombra-Rápida",
                    characterClass = "Ladino",
                    race = "Humano",
                    level = (_character.value.level).coerceAtLeast(1),
                    currentHp = 80,
                    maxHp = 80,
                    currentMana = 30,
                    maxMana = 30,
                    avatarRes = R.drawable.avatar_shadow_rogue,
                    isLocalPlayer = false,
                    isOnline = true,
                    statusDescription = "Conectado • Pelas sombras"
                )
            )
        } else {
            listOf(
                OnlinePartyMember(
                    id = _character.value.id,
                    name = _character.value.name,
                    characterClass = _character.value.characterClass,
                    race = _character.value.race,
                    level = _character.value.level,
                    currentHp = _character.value.currentHp,
                    maxHp = _character.value.maxHp,
                    currentMana = _character.value.currentMana,
                    maxMana = _character.value.maxMana,
                    avatarRes = _character.value.avatarDrawableRes,
                    isLocalPlayer = true,
                    isOnline = true,
                    statusDescription = "Modo Solo"
                )
            )
        }
        _onlineParty.value = partyList

        val introMessage = DungeonChatMessage(
            id = "msg-intro-${System.currentTimeMillis()}",
            senderName = "Mestre da Masmorra (IA)",
            senderRole = "Mestre da IA",
            senderType = MessageSenderType.DUNGEON_MASTER_AI,
            avatarRes = R.drawable.img_dungeon_banner,
            messageText = "🏰 BEM-VINDO A '${title.uppercase()}'!\n\n$initialSituation",
            currentDangerSituation = "Armadilha & Confronto Iminente"
        )
        _chatMessages.value = listOf(introMessage)

        _roomState.value = GameRoomState(
            roomCode = assignedCode,
            mode = if (isOnline) GameMode.MULTIPLAYER else GameMode.SOLO,
            floorNumber = 1,
            roomTitle = title,
            environmentDescription = dungeon.description,
            currentMonster = boss,
            partyMembers = listOf(_character.value),
            turnHistory = listOf(
                GameTurnEvent(
                    turnNumber = 0,
                    actorName = "Mestre da IA",
                    actionText = "Entrada na Masmorra",
                    d20Roll = 20,
                    modifier = 0,
                    totalResult = 20,
                    difficultyClass = 10,
                    successLevel = RollSuccessLevel.SUCCESS,
                    gmNarrative = initialSituation,
                    tacticalSummary = "Sessão iniciada em '${title}'. Inimigo inicial: ${boss.name} (${boss.currentHp} HP)."
                )
            ),
            chatMessages = listOf(introMessage),
            onlineParty = partyList,
            currentSituationPrompt = initialSituation,
            availablePaths = emptyList()
        )

        _isDungeonActive.value = true
        // Per user request: open the character & bag window when dungeon is selected!
        _showCharacterAndBagSheet.value = true
    }

    /**
     * Create a custom dungeon room
     */
    fun createCustomRoom(
        roomName: String,
        theme: String,
        difficulty: String,
        modifier: String,
        isOnline: Boolean = false
    ) {
        val customDungeon = DungeonDefinition(
            id = "custom-${System.currentTimeMillis()}",
            name = if (roomName.isNotBlank()) roomName else "Masmorra dos Campeões",
            subtitle = "Sala Criada pelo Jogador ($theme)",
            description = "Uma masmorra customizada com ambiente de $theme e perigo calibrado para $difficulty. Modificador ativo: $modifier.",
            theme = theme,
            difficulty = difficulty,
            recommendedLevel = when (difficulty) {
                "Fácil" -> 1
                "Normal" -> 2
                "Difícil" -> 3
                else -> 4
            },
            bossName = "Colosso de $theme",
            bossHp = when (difficulty) {
                "Fácil" -> 60
                "Normal" -> 90
                "Difícil" -> 140
                else -> 200
            },
            bossAttack = when (difficulty) {
                "Fácil" -> 8
                "Normal" -> 14
                "Difícil" -> 20
                else -> 28
            },
            bossDescription = "Entidade temível energizada pelo ambiente de $theme.",
            lootHighlight = "Tesouros Rúnicos & $modifier",
            tagEmoji = "🏰"
        )

        _availableDungeons.value = listOf(customDungeon) + _availableDungeons.value
        selectDungeon(customDungeon, roomName, difficulty, isOnline = isOnline)
    }

    fun exitDungeonSession() {
        _isDungeonActive.value = false
        _showCharacterAndBagSheet.value = false
    }

    fun resetCharacterForNewCreation() {
        _hasCreatedCharacter.value = false
        _isDungeonActive.value = false
        _showCharacterAndBagSheet.value = false
    }

    fun createCharacterManual(
        name: String,
        race: String,
        characterClass: String,
        archetype: String,
        avatarRes: Int,
        str: Int,
        dex: Int,
        intell: Int,
        vit: Int,
        car: Int,
        starterWeapon: Weapon,
        skills: List<Skill>
    ) {
        val newChar = CharacterModel(
            id = "char-${System.currentTimeMillis()}",
            name = if (name.isNotBlank()) name else "Aventureiro",
            race = race,
            characterClass = characterClass,
            archetype = archetype,
            backgroundStory = "Herói forjado para desbravar as profundezas e forjar seu destino.",
            avatarDrawableRes = avatarRes,
            level = 1,
            currentXp = 0,
            maxXp = 300,
            currentHp = 100,
            maxHp = 100,
            currentMana = 50,
            maxMana = 50,
            strength = str,
            dexterity = dex,
            intelligence = intell,
            vitality = vit,
            charisma = car,
            equippedWeapon = starterWeapon,
            skills = skills
        )

        _character.value = newChar

        val wepItem = GameItem(
            id = starterWeapon.id,
            name = starterWeapon.name,
            description = starterWeapon.description,
            itemType = ItemType.WEAPON,
            isUniqueItem = false,
            isPermanent = true,
            isCommonDungeonItem = false,
            bonusStat = starterWeapon.bonusStat,
            weaponData = starterWeapon
        )

        _inventory.value = listOf(wepItem) + _inventory.value.filterNot { it.itemType == ItemType.WEAPON }
        _hasCreatedCharacter.value = true
        _showCharacterCreationDialog.value = false
    }

    fun equipWeapon(weapon: Weapon) {
        _character.value = _character.value.copy(equippedWeapon = weapon)
        _inventory.value = _inventory.value.map { item ->
            if (item.itemType == ItemType.WEAPON && item.weaponData != null) {
                item.copy(weaponData = item.weaponData.copy(isEquipped = item.weaponData.id == weapon.id))
            } else item
        }
    }

    fun openForgeDialog() {
        _showForgeDialog.value = true
    }

    fun closeForgeDialog() {
        _showForgeDialog.value = false
    }

    fun openCharacterCreationDialog() {
        _showCharacterCreationDialog.value = true
    }

    fun closeCharacterCreationDialog() {
        _showCharacterCreationDialog.value = false
    }

    /**
     * Start a new Solo Dungeon
     */
    fun startSoloDungeon() {
        val newCode = "SOLO-" + Random.nextInt(10, 99)
        _roomState.value = _roomState.value.copy(
            roomCode = newCode,
            mode = GameMode.SOLO,
            floorNumber = 1,
            roomTitle = "Catacumba do Rei Esquecido",
            environmentDescription = "Paredes de basalto negro decoradas com runas arcanas fulgurantes.",
            currentMonster = Monster(
                id = "mon-solo",
                name = "Sentinela de Basalto",
                currentHp = 50,
                maxHp = 50,
                attackPower = 12,
                description = "Autômato de pedra alimentado por um núcleo de chama mágica."
            ),
            partyMembers = listOf(_character.value),
            turnHistory = listOf(
                GameTurnEvent(
                    turnNumber = 0,
                    actorName = "Mestre da IA",
                    actionText = "Entrada Solo",
                    d20Roll = 18,
                    modifier = 0,
                    totalResult = 18,
                    difficultyClass = 10,
                    successLevel = RollSuccessLevel.SUCCESS,
                    gmNarrative = "Você desce sozinho as escadarias escuras. O peso da sua arma em mãos traz segurança enquanto olhos de fogo acendem no final do corredor.",
                    tacticalSummary = "Sessão Solo iniciada com regras adaptativas para seu herói."
                )
            ),
            availablePaths = generatePathsForFloor(1)
        )
    }

    /**
     * Create or Join Multiplayer Room
     */
    fun createMultiplayerRoom() {
        val newCode = "ROOM-" + Random.nextInt(100, 999)
        _roomState.value = _roomState.value.copy(
            roomCode = newCode,
            mode = GameMode.MULTIPLAYER,
            floorNumber = 2,
            roomTitle = "Salão dos Pactos Arcanos",
            environmentDescription = "Amplo templo subterrâneo com colunas de jade e névoa etérea.",
            currentMonster = Monster(
                id = "mon-multi",
                name = "Quimera das Profundezas",
                currentHp = 80,
                maxHp = 80,
                attackPower = 16,
                description = "Besta colossal com múltiplas cabeças e carapaça de ferro reforçado."
            ),
            turnHistory = listOf(
                GameTurnEvent(
                    turnNumber = 0,
                    actorName = "Mestre da IA",
                    actionText = "Sessão Cooperativa Aberta",
                    d20Roll = 15,
                    modifier = 0,
                    totalResult = 15,
                    difficultyClass = 12,
                    successLevel = RollSuccessLevel.SUCCESS,
                    gmNarrative = "A sala multiplayer $newCode foi sincronizada. Os passos dos aliados ecoam enquanto a Quimera ruge alertada pela presença da guilda.",
                    tacticalSummary = "Multiplayer ativo via WebSocket. Ações de todos os membros são transmitidas em tempo real."
                )
            ),
            availablePaths = generatePathsForFloor(2)
        )
    }

    private fun generatePathsForFloor(floor: Int): List<DungeonPathChoice> {
        return listOf(
            DungeonPathChoice(
                id = "p-forge-$floor",
                title = "Oficina do Ferreiro Arcano",
                description = "Forjas abandonadas onde ferramentas de criação e barras de aço podem ser encontradas.",
                riskLevel = "Médio",
                rewardHint = "Ferramentas & Matéria-Prima de Armas",
                emoji = "⚒️"
            ),
            DungeonPathChoice(
                id = "p-beast-$floor",
                title = "Covil da Fera Guardiã",
                description = "Caminho perigoso infestado por bestas. Alta chance de espólios lendários.",
                riskLevel = "Perigoso",
                rewardHint = "Gemas e Núcleos de Energia",
                emoji = "🐉"
            ),
            DungeonPathChoice(
                id = "p-sanctuary-$floor",
                title = "Fonte da Serenidade Mística",
                description = "Pequeno lago subterrâneo que emite luz azul e cura feridas profundas.",
                riskLevel = "Baixo",
                rewardHint = "Cura Completa e Mana",
                emoji = "✨"
            )
        )
    }

    /**
     * Advance / Branch to a new Dungeon Path
     */
    fun selectDungeonPath(path: DungeonPathChoice) {
        viewModelScope.launch {
            _isAiThinking.value = true
            val nextFloor = _roomState.value.floorNumber + 1

            // Roll D20 for path event
            val pathD20 = Random.nextInt(1, 21)

            val (newMonster, lootMsg, foundToolOrMaterial) = when {
                path.title.contains("Forja", ignoreCase = true) -> {
                    val loot = GameItem(
                        id = "loot-mat-${System.currentTimeMillis()}",
                        name = "Minério de Mitril das Profundezas",
                        description = "Metal raro encontrado na forja. Forja armas de altíssimo dano que não se desgastam.",
                        itemType = ItemType.MATERIAL,
                        isUniqueItem = false,
                        isPermanent = false,
                        isCommonDungeonItem = true,
                        bonusStat = "+18 Dano em Armas Forjadas"
                    )
                    _inventory.value = _inventory.value + loot
                    Triple(
                        Monster("mon-golem-$nextFloor", "Golem de Ferro da Forja", 60, 60, 14, "Guardião mecânico que defende os segredos das armas."),
                        "Você encontrou Minério de Mitril e uma Bigorna Arcana ativa!",
                        true
                    )
                }
                path.title.contains("Fonte", ignoreCase = true) -> {
                    // Healing
                    val healAmt = 30
                    healPlayer(healAmt)
                    Triple(
                        null,
                        "As águas místicas restauraram $healAmt HP e 20 MP!",
                        false
                    )
                }
                else -> {
                    Triple(
                        Monster("mon-beast-$nextFloor", "Senhor das Feras das Sombras", 75, 75, 18, "Monstro alfa que comanda predadores da masmorra."),
                        "O ar congela com o rugido do Senhor das Feras!",
                        false
                    )
                }
            }

            val newHistory = _roomState.value.turnHistory + GameTurnEvent(
                turnNumber = _roomState.value.turnHistory.size + 1,
                actorName = "Exploração",
                actionText = "Avançar para: ${path.title}",
                d20Roll = pathD20,
                modifier = 2,
                totalResult = pathD20 + 2,
                difficultyClass = 12,
                successLevel = if (pathD20 >= 10) RollSuccessLevel.SUCCESS else RollSuccessLevel.FAILURE,
                gmNarrative = "Você adentrou '${path.title}'. ${path.description} $lootMsg",
                tacticalSummary = "Nova área alcançada (Andar $nextFloor). Perigo: ${path.riskLevel}."
            )

            _roomState.value = _roomState.value.copy(
                floorNumber = nextFloor,
                roomTitle = path.title,
                environmentDescription = path.description,
                currentMonster = newMonster,
                turnHistory = newHistory,
                availablePaths = generatePathsForFloor(nextFloor)
            )

            // Grant XP for path exploration
            gainXp(40)

            _isAiThinking.value = false
        }
    }

    /**
     * Initiates an action, putting it into pending state and opening the manual D20 dice roller!
     */
    fun requestActionWithManualDice(
        actionText: String,
        actionType: String,
        skill: Skill? = null,
        weapon: Weapon? = null
    ) {
        val dc = when (actionType) {
            "SKILL" -> 11
            "ATTACK" -> 12
            "HEAL" -> 10
            else -> 12
        }

        _pendingAction.value = PendingAction(
            actionText = actionText,
            actionType = actionType,
            skill = skill,
            weapon = weapon ?: _character.value.equippedWeapon,
            targetDc = dc
        )
        _showDiceModal.value = true
    }

    fun dismissDiceModal() {
        _showDiceModal.value = false
        _pendingAction.value = null
    }

    /**
     * Called when the player rolls the D20 manually!
     */
    fun resolveManualDiceRoll(rolledD20: Int) {
        val pending = _pendingAction.value ?: return
        _showDiceModal.value = false
        _isAiThinking.value = true

        val char = _character.value
            val monster = _roomState.value.currentMonster

            // Calculate modifier based on action type
            val modifier = when (pending.actionType) {
                "ATTACK" -> (char.strength - 10) / 2 + ((pending.weapon?.damage ?: 0) / 5)
                "SKILL" -> {
                    val statVal = when (pending.skill?.scalingStat) {
                        "INT" -> char.intelligence
                        "DES" -> char.dexterity
                        "VIT" -> char.vitality
                        else -> char.strength
                    }
                    (statVal - 10) / 2
                }
                else -> (char.dexterity - 10) / 2
            }

            val total = rolledD20 + modifier
            val successLevel = when {
                rolledD20 == 20 -> RollSuccessLevel.CRITICAL_SUCCESS
                rolledD20 == 1 -> RollSuccessLevel.CRITICAL_FAILURE
                total >= pending.targetDc -> RollSuccessLevel.SUCCESS
                else -> RollSuccessLevel.FAILURE
            }

            var pDamage = 0
            var pHeal = 0
            var mDamage = 0
            var manaSpent = 0

            // Consume mana for skill
            if (pending.skill != null) {
                manaSpent = pending.skill.manaCost
                val newMana = (char.currentMana - manaSpent).coerceAtLeast(0)
                _character.value = _character.value.copy(currentMana = newMana)
            }

            when (successLevel) {
                RollSuccessLevel.CRITICAL_SUCCESS -> {
                    if (pending.skill?.isHealing == true) {
                        pHeal = (pending.skill.power * 1.5).toInt()
                    } else {
                        val baseDmg = (pending.skill?.power ?: pending.weapon?.damage ?: 18)
                        mDamage = (baseDmg * 1.8).toInt() + 8
                    }
                    gainXp(50)
                }
                RollSuccessLevel.SUCCESS -> {
                    if (pending.skill?.isHealing == true) {
                        pHeal = pending.skill.power
                    } else {
                        val baseDmg = (pending.skill?.power ?: pending.weapon?.damage ?: 14)
                        mDamage = baseDmg + modifier.coerceAtLeast(0)
                        // Minor counter attack from monster
                        if (monster != null && monster.currentHp > mDamage) {
                            pDamage = (monster.attackPower * 0.4).toInt()
                        }
                    }
                    gainXp(30)
                }
                RollSuccessLevel.FAILURE -> {
                    if (monster != null) {
                        pDamage = (monster.attackPower * 0.8).toInt()
                    }
                }
                RollSuccessLevel.CRITICAL_FAILURE -> {
                    if (monster != null) {
                        pDamage = (monster.attackPower * 1.4).toInt()
                    }
                }
            }

            // Apply Damage / Heal to Player
            if (pDamage > 0) {
                damagePlayer(pDamage)
            }
            if (pHeal > 0) {
                healPlayer(pHeal)
            }

            // Apply Damage to Monster
            var updatedMonster = monster
            if (monster != null && mDamage > 0) {
                val newMhp = (monster.currentHp - mDamage).coerceAtLeast(0)
                updatedMonster = monster.copy(currentHp = newMhp)
                if (newMhp == 0) {
                    gainXp(120)
                    // Drop material / tool opportunity
                    val dropItem = GameItem(
                        id = "drop-${System.currentTimeMillis()}",
                        name = "Núcleo Arcano do ${monster.name}",
                        description = "Material valioso coletado da carcaça do monstro. Pode ser usado para forjar armas permanentes!",
                        itemType = ItemType.MATERIAL,
                        isUniqueItem = false,
                        isPermanent = false,
                        isCommonDungeonItem = true,
                        bonusStat = "+15 Dano Mágico em Forja"
                    )
                    _inventory.value = _inventory.value + dropItem
                }
            }

            // Generate Narrative
            val narrative = when (successLevel) {
                RollSuccessLevel.CRITICAL_SUCCESS -> "DADO D20 = 20! CRÍTICO FULMINANTE! Seu golpe ressoa com força divina. " +
                        if (pHeal > 0) "A energia vital curou $pHeal HP!" else "A lâmina causou $mDamage de dano devastador!"
                RollSuccessLevel.SUCCESS -> "DADO D20 = $rolledD20 + $modifier = $total (Sucesso contra DC ${pending.targetDc}). " +
                        if (pHeal > 0) "Você canaliza o poder recuperando $pHeal HP." else "Você atinge o alvo com precisão, infligindo $mDamage de dano!"
                RollSuccessLevel.FAILURE -> "DADO D20 = $rolledD20 + $modifier = $total (Falha contra DC ${pending.targetDc}). A guarda vacila e o monstro contra-ataca causando $pDamage de dano!"
                RollSuccessLevel.CRITICAL_FAILURE -> "DADO D20 = 1! FALHA CRÍTICA! O impacto ricocheteia desajeitadamente e você recebe golpe direto sofrendo $pDamage de dano!"
            }

            val event = GameTurnEvent(
                turnNumber = _roomState.value.turnHistory.size + 1,
                actorName = char.name,
                actionText = pending.actionText,
                d20Roll = rolledD20,
                modifier = modifier,
                totalResult = total,
                difficultyClass = pending.targetDc,
                successLevel = successLevel,
                gmNarrative = narrative,
                tacticalSummary = if (updatedMonster != null && updatedMonster.currentHp == 0) "INIMIGO DERROTADO! Espólios de masmorra adicionados à mochila." else "Dano no monstro: -$mDamage HP | Dano no herói: -$pDamage HP",
                playerHpDelta = if (pHeal > 0) pHeal else -pDamage,
                playerManaDelta = -manaSpent,
                monsterHpDelta = -mDamage
            )

            _roomState.value = _roomState.value.copy(
                currentMonster = updatedMonster,
                turnHistory = _roomState.value.turnHistory + event,
            lastDamageDealtToPlayer = pDamage,
            lastHealingReceivedByPlayer = pHeal
        )

        _pendingAction.value = null
        _isAiThinking.value = false
    }

    /**
     * Core mechanic requested by user:
     * The player describes free-form what they do to get out of the dungeon situation.
     * The AI Master responds in real time in the chat generating the story, outcome, and new peril!
     */
    fun sendPlayerAction(actionText: String) {
        if (actionText.isBlank()) return
        val playerText = actionText.trim()

        // 1. Post player action into the chat immediately
        val playerMsg = DungeonChatMessage(
            id = "msg-p-${System.currentTimeMillis()}-${Random.nextInt(100, 999)}",
            senderName = _character.value.name,
            senderRole = "${_character.value.race} ${_character.value.characterClass}",
            senderType = MessageSenderType.LOCAL_PLAYER,
            avatarRes = _character.value.avatarDrawableRes,
            messageText = playerText
        )
        _chatMessages.value = _chatMessages.value + playerMsg

        viewModelScope.launch {
            _isAiThinking.value = true

            // 2. If in Online Mode, companion players in the room react and coordinate
            if (_isOnlineMode.value) {
                val companions = _onlineParty.value.filterNot { it.isLocalPlayer }
                if (companions.isNotEmpty()) {
                    val companion = companions.random()
                    val companionMsgText = generateCompanionReaction(companion, playerText)
                    val companionMsg = DungeonChatMessage(
                        id = "msg-comp-${System.currentTimeMillis()}-${Random.nextInt(100, 999)}",
                        senderName = companion.name,
                        senderRole = "${companion.characterClass} [ONLINE]",
                        senderType = MessageSenderType.ONLINE_PLAYER,
                        avatarRes = companion.avatarRes,
                        messageText = companionMsgText
                    )
                    _chatMessages.value = _chatMessages.value + companionMsg
                }
            }

            // 3. Roll D20 and evaluate stat modifier based on the player's description
            val char = _character.value
            val lowerAction = playerText.lowercase()
            val (statUsed, statMod) = when {
                lowerAction.contains("espada") || lowerAction.contains("machado") ||
                lowerAction.contains("atacar") || lowerAction.contains("golpe") ||
                lowerAction.contains("cortar") || lowerAction.contains("força") ||
                lowerAction.contains("arma") || lowerAction.contains("empurr") -> {
                    val weaponBonus = (char.equippedWeapon?.damage ?: 0) / 4
                    "FOR" to ((char.strength - 10) / 2 + weaponBonus)
                }
                lowerAction.contains("magia") || lowerAction.contains("feitiço") ||
                lowerAction.contains("arcano") || lowerAction.contains("fogo") ||
                lowerAction.contains("raio") || lowerAction.contains("gelo") ||
                lowerAction.contains("estudar") || lowerAction.contains("ler") -> {
                    "INT" to ((char.intelligence - 10) / 2)
                }
                lowerAction.contains("esquiv") || lowerAction.contains("desvi") ||
                lowerAction.contains("furtiv") || lowerAction.contains("adaga") ||
                lowerAction.contains("correr") || lowerAction.contains("saltar") ||
                lowerAction.contains("arco") || lowerAction.contains("flecha") -> {
                    "DES" to ((char.dexterity - 10) / 2)
                }
                lowerAction.contains("escudo") || lowerAction.contains("bloque") ||
                lowerAction.contains("resist") || lowerAction.contains("aguent") ||
                lowerAction.contains("curar") || lowerAction.contains("poção") -> {
                    "VIT" to ((char.vitality - 10) / 2)
                }
                else -> "CAR" to ((char.charisma - 10) / 2)
            }

            val d20 = Random.nextInt(1, 21)
            val totalCheck = d20 + statMod
            val targetDc = 12
            val successLevel = when {
                d20 == 20 -> RollSuccessLevel.CRITICAL_SUCCESS
                d20 == 1 -> RollSuccessLevel.CRITICAL_FAILURE
                totalCheck >= targetDc -> RollSuccessLevel.SUCCESS
                else -> RollSuccessLevel.FAILURE
            }

            // 4. Generate AI Master real-time narrative response
            val dungeon = _selectedDungeon.value ?: _availableDungeons.value.first()
            val monster = _roomState.value.currentMonster
            val previousSituation = _currentSituationPrompt.value

            val aiResult = generateAiMasterResponse(
                playerText = playerText,
                hero = char,
                dungeon = dungeon,
                monster = monster,
                previousSituation = previousSituation,
                d20 = d20,
                statUsed = statUsed,
                statMod = statMod,
                totalCheck = totalCheck,
                successLevel = successLevel
            )

            // 5. Update player stats and monster stats according to outcome
            var hpChange = 0
            var monsterHpChange = 0

            when (successLevel) {
                RollSuccessLevel.CRITICAL_SUCCESS -> {
                    monsterHpChange = -35
                    gainXp(40)
                    // Reward bonus material for weapons
                    if (Random.nextBoolean()) {
                        val dropItem = GameItem(
                            id = "drop-${System.currentTimeMillis()}",
                            name = "Cristal Rúnico de ${dungeon.theme.split(" ").first()}",
                            description = "Extraído do desarmamento perigoso na masmorra!",
                            itemType = ItemType.MATERIAL,
                            bonusStat = "Material Raro de Forja"
                        )
                        _inventory.value = _inventory.value + dropItem
                    }
                }
                RollSuccessLevel.SUCCESS -> {
                    monsterHpChange = -20
                    gainXp(25)
                }
                RollSuccessLevel.FAILURE -> {
                    hpChange = -15
                    damagePlayer(15)
                }
                RollSuccessLevel.CRITICAL_FAILURE -> {
                    hpChange = -25
                    damagePlayer(25)
                }
            }

            if (monster != null && monsterHpChange != 0) {
                val newHp = (monster.currentHp + monsterHpChange).coerceAtLeast(0)
                _roomState.value = _roomState.value.copy(
                    currentMonster = monster.copy(currentHp = newHp)
                )
            }

            _currentSituationPrompt.value = aiResult.newSituation

            val dmMessage = DungeonChatMessage(
                id = "msg-dm-${System.currentTimeMillis()}-${Random.nextInt(100, 999)}",
                senderName = "Mestre da Masmorra (IA)",
                senderRole = "Mestre da IA",
                senderType = MessageSenderType.DUNGEON_MASTER_AI,
                avatarRes = R.drawable.img_dungeon_banner,
                messageText = aiResult.narrativeText,
                d20Roll = d20,
                totalCheckResult = totalCheck,
                successLevel = successLevel,
                currentDangerSituation = aiResult.dangerSummary,
                hpDelta = hpChange,
                manaDelta = 0
            )
            _chatMessages.value = _chatMessages.value + dmMessage

            _isAiThinking.value = false
        }
    }

    private fun generateCompanionReaction(companion: OnlinePartyMember, playerAction: String): String {
        val lower = playerAction.lowercase()
        return when {
            lower.contains("ataque") || lower.contains("espada") || lower.contains("machado") -> {
                if (companion.characterClass.contains("Maga")) {
                    "\"Estou conjurando um raio de suporte para abrir a blindagem do inimigo enquanto você ataca!\""
                } else {
                    "\"Avance! Vou flanquear pelo lado cego da fera para distraí-la!\""
                }
            }
            lower.contains("esquiv") || lower.contains("desvi") || lower.contains("foge") || lower.contains("correr") -> {
                if (companion.characterClass.contains("Maga")) {
                    "\"Cuidado com o flanco! Conjurando barreira mística protetora sobre nós!\""
                } else {
                    "\"Lançando bomba de abrolhos no solo para atrasar o avanço do monstro! Corra!\""
                }
            }
            lower.contains("magia") || lower.contains("feitiço") || lower.contains("fogo") -> {
                if (companion.characterClass.contains("Maga")) {
                    "\"Sintonizando fluxo arcano! Nossas magias vão ressoar juntas em cadeia!\""
                } else {
                    "\"Mantenha a concentração! Não deixarei nenhum monstro alcançar você durante a conjuração!\""
                }
            }
            else -> {
                "\"Estou cobrindo sua retaguarda! Siga firme com o plano!\""
            }
        }
    }

    private suspend fun generateAiMasterResponse(
        playerText: String,
        hero: CharacterModel,
        dungeon: DungeonDefinition,
        monster: Monster?,
        previousSituation: String,
        d20: Int,
        statUsed: String,
        statMod: Int,
        totalCheck: Int,
        successLevel: RollSuccessLevel
    ): AiMasterTurnResult {
        // Attempt real-time Gemini generation if API key is provided
        val prompt = "Você é o Mestre de Masmorra (AI Dungeon Master) de um RPG épico de fantasia sombria.\n" +
                "Herói: ${hero.name} (${hero.race} ${hero.characterClass}, Nível ${hero.level}, HP: ${hero.currentHp}/${hero.maxHp}, Mana: ${hero.currentMana}/${hero.maxMana}, Arma: ${hero.equippedWeapon?.name ?: "Desarmado"}).\n" +
                "Masmorra: ${dungeon.name} (${dungeon.theme}). Inimigo atual: ${monster?.name ?: "Perigo da Masmorra"}.\n" +
                "Situação perigosa anterior: $previousSituation\n" +
                "Ação descrita pelo jogador: \"$playerText\"\n" +
                "Teste de D20: Rolou $d20 + mod $statMod ($statUsed) = $totalCheck. Resultado: $successLevel.\n\n" +
                "Escreva uma resposta imersiva em Português dividida exatamente em:\n" +
                "1. Desfecho narrativo imediato da ação do jogador com base no D20 (sucesso ou dano/falha).\n" +
                "2. A nova situação perigosa e urgente que surge AGORA na sala da masmorra.\n" +
                "3. Conclua perguntando: 'O que você vai fazer para se livrar dessa situação?'"

        val geminiText = requestGeminiNarrative(prompt)
        if (!geminiText.isNullOrBlank()) {
            val newDangerSummary = "Nova Ameaça Iminente"
            return AiMasterTurnResult(
                narrativeText = geminiText.trim(),
                newSituation = geminiText.substringAfterLast("\n\n", geminiText).takeLast(250),
                dangerSummary = newDangerSummary
            )
        }

        // Procedural narrative engine fallback with high fidelity
        return generateLocalRpgNarrative(
            playerText = playerText,
            hero = hero,
            dungeon = dungeon,
            monster = monster,
            d20 = d20,
            statUsed = statUsed,
            statMod = statMod,
            totalCheck = totalCheck,
            successLevel = successLevel
        )
    }

    private suspend fun requestGeminiNarrative(prompt: String): String? = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
                return@withContext null
            }
            val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
            conn.doOutput = true
            conn.connectTimeout = 8000
            conn.readTimeout = 8000

            val partsArray = org.json.JSONArray().apply {
                put(JSONObject().apply { put("text", prompt) })
            }
            val contentObj = JSONObject().apply {
                put("parts", partsArray)
            }
            val contentsArray = org.json.JSONArray().apply {
                put(contentObj)
            }
            val requestBody = JSONObject().apply {
                put("contents", contentsArray)
            }

            conn.outputStream.use { os ->
                os.write(requestBody.toString().toByteArray(Charsets.UTF_8))
            }

            if (conn.responseCode == 200) {
                val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                val jsonResponse = JSONObject(responseText)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).optString("text")
                    }
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    private fun generateLocalRpgNarrative(
        playerText: String,
        hero: CharacterModel,
        dungeon: DungeonDefinition,
        monster: Monster?,
        d20: Int,
        statUsed: String,
        statMod: Int,
        totalCheck: Int,
        successLevel: RollSuccessLevel
    ): AiMasterTurnResult {
        val monsterName = monster?.name ?: "Guardião da Masmorra"
        val weaponName = hero.equippedWeapon?.name ?: "seus punhos e coragem"

        val outcomeNarrative = when (successLevel) {
            RollSuccessLevel.CRITICAL_SUCCESS -> {
                "🎲 D20 = 20! CRÍTICO LENDÁRIO (Total: $totalCheck | Modificador $statMod de $statUsed)!\n\n" +
                "Com maestria excepcional e timing impecável, você executa sua ação: '$playerText'. " +
                "O movimento transcende qualquer defesa ordinária! $weaponName atinge os pontos vitais das defesas com precisão cirúrgica, " +
                "estilhaçando a investida de $monsterName e abrindo uma brecha monumental na câmara! " +
                "O impacto causa -35 HP no monstro e concede XP substancial ao grupo!"
            }
            RollSuccessLevel.SUCCESS -> {
                "🎲 D20 = $d20 + $statMod ($statUsed) = $totalCheck (Sucesso contra DC 12)!\n\n" +
                "Você mantém a compostura diante do terror e reage com firmeza: '$playerText'. " +
                "A tática funciona! Você consegue neutralizar a ameaça iminente, contornar as garras de $monsterName " +
                "e revidar com determinação, infligindo -20 HP de dano e assegurando posição vantajosa para a equipe."
            }
            RollSuccessLevel.FAILURE -> {
                "🎲 D20 = $d20 + $statMod ($statUsed) = $totalCheck (Falha contra DC 12)!\n\n" +
                "A pressão sufocante da masmorra cobra seu preço. Você tenta agir com bravura: '$playerText', " +
                "mas o solo irregular cede sob suas botas e seu movimento perde a precisão no último milésimo de segundo! " +
                "$monsterName aproveita a hesitação e atinge você em cheio com um golpe devastador (-15 HP de dano)!"
            }
            RollSuccessLevel.CRITICAL_FAILURE -> {
                "🎲 D20 = 1! DESASTRE CRÍTICO (Total: $totalCheck | Modificador $statMod de $statUsed)!\n\n" +
                "Um revés catastrófico! No momento em que você tenta '$playerText', a instabilidade rúnica da sala reverbera contra seu corpo! " +
                "Você tropeça gravemente, perde o equilíbrio e é arremessado contra as rochas pontiagudas, " +
                "sofrendo impacto violento direto de $monsterName (-25 HP de dano)!"
            }
        }

        // Generate next emergent dangerous situation based on dungeon theme and progress
        val nextPeril = when (Random.nextInt(5)) {
            0 -> {
                val hazard = "O teto ancestral começa a rachar violentamente e grandes estalagmites despencam do alto! Ao mesmo tempo, correntes incandescentes se erguem do fosso tentando laçar o grupo, e $monsterName prepara uma investida furiosa em investida de carga!"
                val summary = "Queda de Rocha & Carga Feroz"
                hazard to summary
            }
            1 -> {
                val hazard = "Uma onda de gás venenoso denso e esverdeado vaza das paredes bloqueando a visão e dificultando a respiração! Das sombras laterais, bestas menores saltam em emboscada enquanto $monsterName canaliza uma explosão de energia sombria!"
                val summary = "Gás Venenoso & Emboscada de Flanco"
                hazard to summary
            }
            2 -> {
                val hazard = "Pisos falsos se abrem sob seus pés revelando poços de lâminas giratórias! A temperatura sobe drasticamente e labaredas de fogo místico bloqueiam o recuo, encurralando todos na beira do abismo!"
                val summary = "Poço de Lâminas & Muro Flamejante"
                hazard to summary
            }
            3 -> {
                val hazard = "Glifos arcanos no piso começam a emitir um zumbido estridente em contagem regressiva de detonação! $monsterName ruge e bate seu machado no chão gerando uma onda de choque que derruba os aventureiros!"
                val summary = "Glifos Instáveis & Onda de Choque"
                hazard to summary
            }
            else -> {
                val hazard = "Grades pontiagudas de ferro caem nas duas extremidades isolando a equipe na câmara principal! Água ácida começa a subir pelos tornozelos corroendo as armaduras, enquanto $monsterName entra em modo de fúria berserker!"
                val summary = "Inundação Ácida & Fúria do Guardião"
                hazard to summary
            }
        }

        val fullNarrative = "$outcomeNarrative\n\n⚠️ NOVA SITUAÇÃO EMERCENTE:\n${nextPeril.first}\n\nO que você vai fazer para se livrar dessa situação?"

        return AiMasterTurnResult(
            narrativeText = fullNarrative,
            newSituation = nextPeril.first,
            dangerSummary = nextPeril.second
        )
    }

    data class AiMasterTurnResult(
        val narrativeText: String,
        val newSituation: String,
        val dangerSummary: String
    )

    fun damagePlayer(amount: Int) {
        val newHp = (_character.value.currentHp - amount).coerceAtLeast(0)
        _character.value = _character.value.copy(currentHp = newHp)
        _lastDamageAmount.value = amount
        _damageAnimTrigger.value = System.currentTimeMillis()

        // If HP == 0, Wipe Non-Permanent Items
        if (newHp == 0) {
            wipeCommonDungeonItemsOnDeath()
        }
    }

    /**
     * Trigger heal animation and increase HP
     */
    fun healPlayer(amount: Int) {
        val newHp = (_character.value.currentHp + amount).coerceAtMost(_character.value.maxHp)
        _character.value = _character.value.copy(currentHp = newHp)
        _lastHealAmount.value = amount
        _healAnimTrigger.value = System.currentTimeMillis()
    }

    /**
     * Gain XP and Level Up when reaching maxXp
     */
    fun gainXp(amount: Int) {
        var xp = _character.value.currentXp + amount
        var lvl = _character.value.level
        var maxHp = _character.value.maxHp
        var maxMp = _character.value.maxMana
        var maxXp = _character.value.maxXp

        while (xp >= maxXp) {
            xp -= maxXp
            lvl += 1
            maxXp = (maxXp * 1.35).toInt()
            maxHp += 15
            maxMp += 8
        }

        _character.value = _character.value.copy(
            level = lvl,
            currentXp = xp,
            maxXp = maxXp,
            maxHp = maxHp,
            currentHp = _character.value.currentHp.coerceAtMost(maxHp),
            maxMana = maxMp,
            currentMana = _character.value.currentMana.coerceAtMost(maxMp)
        )
    }

    /**
     * Equip a weapon from the inventory
     */
    fun equipWeapon(item: GameItem) {
        val wep = item.weaponData ?: return
        _character.value = _character.value.copy(equippedWeapon = wep)

        // Update inventory isEquipped states
        _inventory.value = _inventory.value.map {
            if (it.id == item.id) {
                it.copy(weaponData = wep.copy(isEquipped = true))
            } else if (it.itemType == ItemType.WEAPON) {
                it.copy(weaponData = it.weaponData?.copy(isEquipped = false))
            } else {
                it
            }
        }
    }

    /**
     * Use consumable potion
     */
    fun usePotion(item: GameItem) {
        if (item.healAmount > 0) {
            healPlayer(item.healAmount)
        }
        if (item.manaAmount > 0) {
            val newMp = (_character.value.currentMana + item.manaAmount).coerceAtMost(_character.value.maxMana)
            _character.value = _character.value.copy(currentMana = newMp)
            _healAnimTrigger.value = System.currentTimeMillis()
        }

        // Consume 1 from inventory
        _inventory.value = _inventory.value.filterNot { it.id == item.id }
    }

    /**
     * Craft a new weapon during dungeon with tools & materials found!
     * The weapon stays with the player across dungeons until death!
     */
    fun craftDungeonWeapon(
        toolItem: GameItem,
        materialItem: GameItem,
        weaponName: String,
        weaponType: String
    ) {
        val damageVal = 20 + Random.nextInt(4, 12)
        val finalName = if (weaponName.isNotBlank()) weaponName else "Lâmina de ${materialItem.name.replace("Lingote de ", "").replace("Cristal ", "")}"

        val newWeapon = Weapon(
            id = "wep-craft-${System.currentTimeMillis()}",
            name = finalName,
            description = "Forjada na masmorra usando '${toolItem.name}' e '${materialItem.name}'. Permanece com o herói entre dungeons até a morte!",
            damage = damageVal,
            damageType = weaponType,
            bonusStat = "+$damageVal Dano ($weaponType)",
            isDungeonCrafted = true,
            isEquipped = true
        )

        val newGameItem = GameItem(
            id = newWeapon.id,
            name = newWeapon.name,
            description = newWeapon.description,
            itemType = ItemType.WEAPON,
            isUniqueItem = false,
            isPermanent = true, // Permanece entre masmorras até o herói morrer!
            isCommonDungeonItem = false,
            isDungeonCrafted = true,
            bonusStat = newWeapon.bonusStat,
            weaponData = newWeapon
        )

        // Consume material, keep tool, add new weapon, equip it
        _inventory.value = _inventory.value.filterNot { it.id == materialItem.id } + newGameItem
        _character.value = _character.value.copy(equippedWeapon = newWeapon)
        _latestCraftedItem.value = newGameItem
        _showForgeDialog.value = false

        // Add history event
        _roomState.value = _roomState.value.copy(
            turnHistory = _roomState.value.turnHistory + GameTurnEvent(
                turnNumber = _roomState.value.turnHistory.size + 1,
                actorName = _character.value.name,
                actionText = "Forjar Arma de Masmorra: ${newWeapon.name}",
                d20Roll = 20,
                modifier = 0,
                totalResult = 20,
                difficultyClass = 10,
                successLevel = RollSuccessLevel.SUCCESS,
                gmNarrative = "O martelo rúnico ressoa contra o metal incandescente. Você forjou com sucesso '$finalName' ($weaponType - $damageVal de dano)! Esta arma viaja com você entre masmorras.",
                tacticalSummary = "Nova arma forjada equipada com sucesso (+${damageVal} dano)."
            )
        )
    }

    /**
     * Create character with AI from user description!
     * Identifies race, class, archetype/monster, distributes stats, skills, weapon & matches avatar image.
     */
    fun createCharacterFromDescription(userDescription: String) {
        _isAiThinking.value = true

        val textLower = userDescription.lowercase()

            // 1. Detect Race
            val race = when {
                textLower.contains("elfo") || textLower.contains("elfa") || textLower.contains("élfico") -> "Elfo"
                textLower.contains("drag") || textLower.contains("dracon") || textLower.contains("lagarto") || textLower.contains("escama") -> "Draconato"
                textLower.contains("orc") || textLower.contains("ogro") -> "Orc"
                textLower.contains("anão") || textLower.contains("anã") -> "Anão"
                textLower.contains("minotaur") || textLower.contains("touro") || textLower.contains("fera") -> "Minotauro Feral"
                textLower.contains("morto-vivo") || textLower.contains("esqueleto") || textLower.contains("vampir") -> "Morto-Vivo"
                textLower.contains("demônio") || textLower.contains("tiefling") -> "Tiefling"
                else -> "Humano"
            }

            // 2. Detect Class
            val characterClass = when {
                textLower.contains("mago") || textLower.contains("feiticeir") || textLower.contains("brux") || textLower.contains("arcano") -> "Mago Arcano"
                textLower.contains("ladin") || textLower.contains("assassin") || textLower.contains("sombra") || textLower.contains("adaga") || textLower.contains("furtiv") -> "Ladino das Sombras"
                textLower.contains("berserk") || textLower.contains("bárbar") || textLower.contains("fúria") -> "Berserker Feral"
                textLower.contains("clérig") || textLower.contains("paladin") || textLower.contains("sagrad") || textLower.contains("curador") || textLower.contains("luz") -> "Paladino Sagrado"
                textLower.contains("necromant") || textLower.contains("trevas") || textLower.contains("crânio") -> "Necromante Sombrio"
                textLower.contains("caçador") || textLower.contains("arqueir") || textLower.contains("arco") -> "Caçador Ranger"
                else -> "Guerreiro de Batalha"
            }

            // 3. Archetype & Avatar Match
            val (archetype, avatarRes) = when {
                characterClass.contains("Mago") || characterClass.contains("Necromante") || race == "Elfo" -> {
                    Pair("Arquimago Elemental", R.drawable.avatar_elf_mage)
                }
                characterClass.contains("Ladino") || textLower.contains("adaga") || textLower.contains("sombra") -> {
                    Pair("Lâmina Oculta", R.drawable.avatar_shadow_rogue)
                }
                race.contains("Draconato") || race.contains("Minotauro") || characterClass.contains("Berserker") -> {
                    Pair("Colosso Feral Monstruoso", R.drawable.avatar_beast_warrior)
                }
                else -> {
                    Pair("Cavaleiro Veterano", R.drawable.img_hero_portrait)
                }
            }

            // 4. Attributes distribution based on archetype
            val (str, dex, intell, vit, car) = when {
                characterClass.contains("Mago") || characterClass.contains("Necromante") -> listOf(10, 13, 18, 12, 14)
                characterClass.contains("Ladino") || characterClass.contains("Caçador") -> listOf(12, 18, 12, 13, 14)
                characterClass.contains("Berserker") || race.contains("Minotauro") -> listOf(19, 12, 8, 17, 9)
                characterClass.contains("Paladino") -> listOf(16, 10, 12, 16, 15)
                else -> listOf(16, 13, 11, 15, 12)
            }

            // Extract Name or generate from description
            val words = userDescription.trim().split(" ")
            val charName = if (words.isNotEmpty() && words[0].length in 3..14 && !words[0].contains("um", true)) {
                words[0].replaceFirstChar { it.uppercase() }
            } else {
                when (race) {
                    "Elfo" -> "Kaelen"
                    "Draconato" -> "Ignis Drakon"
                    "Minotauro Feral" -> "Bravok"
                    "Orc" -> "Gorgar"
                    else -> "Valerius"
                }
            }

            // 5. Generate Tailored Skills
            val skills = when {
                characterClass.contains("Mago") -> listOf(
                    Skill("sk-m1", "Orbe de Plasma Arcano", "Canaliza um projétil de magia pura que explode em fragmentos.", 15, 34, false, "INT", "🔮"),
                    Skill("sk-m2", "Muralha de Chamas Azuis", "Conjura um anel de fogo que consome armaduras.", 20, 42, false, "INT", "🔥"),
                    Skill("sk-m3", "Canalização Restauradora", "Restabelece vitalidade através de fluxo de mana pura.", 25, 40, true, "INT", "✨")
                )
                characterClass.contains("Ladino") -> listOf(
                    Skill("sk-l1", "Apunhalar nas Costas", "Surge por trás das sombras infligindo corte crítico letal.", 10, 36, false, "DES", "🗡️"),
                    Skill("sk-l2", "Passo Nebuloso", "Desaparece na fumaça evitando contra-ataques e preparando próximo golpe.", 12, 20, false, "DES", "💨"),
                    Skill("sk-l3", "Veneno Alquímico Rápido", "Aplica toxina paralisante na arma.", 15, 28, false, "DES", "🧪")
                )
                characterClass.contains("Berserker") || race.contains("Minotauro") -> listOf(
                    Skill("sk-b1", "Golpe Demolidor de Solo", "Esmaga o solo causando choque que desestabiliza inimigos.", 10, 38, false, "FOR", "🔨"),
                    Skill("sk-b2", "Grito de Guerra Feral", "Eleva a adrenalina causando dano adicional e resistência.", 15, 26, false, "FOR", "🦁"),
                    Skill("sk-b3", "Sede de Sangue Regenerativa", "Converte fúria em regeneração de HP imediata.", 20, 35, true, "VIT", "🩸")
                )
                else -> listOf(
                    Skill("sk-w1", "Corte Flamejante", "Lâmina incandescente que corta defesas pesadas.", 10, 26, false, "FOR", "⚔️"),
                    Skill("sk-w2", "Investida com Escudo", "Impacto com armadura que atordoa o monstro.", 12, 22, false, "FOR", "🛡️"),
                    Skill("sk-w3", "Bênção de Batalha", "Prece sagrada que estanca ferimentos no calor da luta.", 15, 30, true, "VIT", "✨")
                )
            }

            // 6. Starting Tailored Weapon
            val starterWeapon = when {
                characterClass.contains("Mago") -> Weapon("wep-mag", "Cajado das Chamas Negras", "Cajado de madeira petrificada imbuído de poder arcano.", 20, "Fogo Arcano", "+20 Dano Mágico", false, true)
                characterClass.contains("Ladino") -> Weapon("wep-rog", "Adagas Gêmeas de Obsidiana", "Lâminas venenosas afiadas para perfurar frestas de armadura.", 18, "Perfurante Sombrio", "+18 Dano Crítico", false, true)
                characterClass.contains("Berserker") || race.contains("Minotauro") -> Weapon("wep-ber", "Machado de Guerra de Basalto", "Arma brutal de duas mãos forjada para esmagar ossos.", 24, "Esmagamento Físico", "+24 Dano Brutal", false, true)
                else -> Weapon("wep-kni", "Espada Larga do Templário", "Espada rúnica de aço equilibrado com empunhadura reforçada.", 18, "Corte Físico", "+18 Dano Base", false, true)
            }

            val newChar = CharacterModel(
                id = "char-${System.currentTimeMillis()}",
                name = charName,
                race = race,
                characterClass = characterClass,
                archetype = archetype,
                backgroundStory = userDescription,
                avatarDrawableRes = avatarRes,
                level = 1,
                currentXp = 0,
                maxXp = 300,
                currentHp = 100,
                maxHp = 100,
                currentMana = 50,
                maxMana = 50,
                strength = str,
                dexterity = dex,
                intelligence = intell,
                vitality = vit,
                charisma = car,
                equippedWeapon = starterWeapon,
                skills = skills
            )

            _character.value = newChar

            // Add starting weapon to inventory
            val wepItem = GameItem(
                id = starterWeapon.id,
                name = starterWeapon.name,
                description = starterWeapon.description,
                itemType = ItemType.WEAPON,
                isUniqueItem = false,
                isPermanent = true,
                isCommonDungeonItem = false,
                bonusStat = starterWeapon.bonusStat,
                weaponData = starterWeapon
            )

        _inventory.value = listOf(wepItem) + _inventory.value.filterNot { it.itemType == ItemType.WEAPON }

        _hasCreatedCharacter.value = true
        _showCharacterCreationDialog.value = false
        _isAiThinking.value = false
    }

    /**
     * Clear common items when player dies
     */
    private fun wipeCommonDungeonItemsOnDeath() {
        // Keep permanent items, alchemy unique items, and dungeon crafted weapons
        _inventory.value = _inventory.value.filter { it.isPermanent || it.isUniqueItem || it.isDungeonCrafted }
    }

    /**
     * Alchemy fusion
     */
    fun combineAlchemyItems(itemA: GameItem, itemB: GameItem) {
        viewModelScope.launch {
            _isAiThinking.value = true

            val uniqueId = "unique-" + System.currentTimeMillis()
            val combinedName = "Artefato Quimérico de ${itemA.name.split(" ").first()} & ${itemB.name.split(" ").first()}"
            val combinedBonus = "${itemA.bonusStat} & ${itemB.bonusStat} (+20 Poder Lendário)"

            val newUnique = GameItem(
                id = uniqueId,
                name = combinedName,
                description = "Artefato Único permanente resultante da transmutação na crátera alquímica. Salvo permanentemente na ficha do herói.",
                itemType = ItemType.ARTIFACT,
                isUniqueItem = true,
                isPermanent = true,
                isCommonDungeonItem = false,
                bonusStat = combinedBonus,
                alchemyLineage = "Fusão de '${itemA.name}' + '${itemB.name}'"
            )

            _inventory.value = _inventory.value.filterNot { it.id == itemA.id || it.id == itemB.id } + newUnique
            _latestCraftedItem.value = newUnique

            _roomState.value = _roomState.value.copy(
                turnHistory = _roomState.value.turnHistory + GameTurnEvent(
                    turnNumber = _roomState.value.turnHistory.size + 1,
                    actorName = _character.value.name,
                    actionText = "Fusão Alquímica: ${newUnique.name}",
                    d20Roll = 20,
                    modifier = 0,
                    totalResult = 20,
                    difficultyClass = 10,
                    successLevel = RollSuccessLevel.SUCCESS,
                    gmNarrative = "A crátera alquímica ferveu em labaredas violetas! Os componentes se entrelaçaram dando vida a um novo Item Único Permanente que acompanhará o herói para sempre.",
                    tacticalSummary = "Transmutação concluída com sucesso. Item gravado permanentemente."
                )
            )

            _isAiThinking.value = false
        }
    }

    fun healHero() {
        _character.value = _character.value.copy(
            currentHp = _character.value.maxHp,
            currentMana = _character.value.maxMana
        )
        _healAnimTrigger.value = System.currentTimeMillis()
    }
}
