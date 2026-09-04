package com.example.engine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.model.CharacterClass
import com.example.model.CharacterModel
import com.example.model.GameItem
import com.example.model.GameMode
import com.example.model.GameRoomState
import com.example.model.GameTurnEvent
import com.example.model.Monster
import com.example.model.RollSuccessLevel
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

class RpgGameViewModel : ViewModel() {

    private val _character = MutableStateFlow(
        CharacterModel(
            name = "Valerius",
            characterClass = CharacterClass.GUERREIRO,
            level = 3,
            currentHp = 90,
            maxHp = 100,
            currentMana = 30,
            maxMana = 40,
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
                id = "item-1",
                name = "Faca Comum de Ferro",
                description = "Lâmina comum coletada no chão da cripta. Será perdida se abandonar ou morrer na masmorra.",
                isUniqueItem = false,
                isPermanent = false,
                isCommonDungeonItem = true,
                bonusStat = "+4 Ataque"
            ),
            GameItem(
                id = "item-2",
                name = "Poção Menor de Cura",
                description = "Frasco alquímico medicinal. Regenera vitalidade.",
                isUniqueItem = false,
                isPermanent = false,
                isCommonDungeonItem = true,
                bonusStat = "+25 Cura"
            ),
            GameItem(
                id = "item-3",
                name = "Amuleto de Sangue Antigo",
                description = "Item Único permanente recebido em uma campanha passada. Viaja com o personagem para sempre!",
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
            )
        )
    )
    val roomState: StateFlow<GameRoomState> = _roomState.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    private val _latestCraftedItem = MutableStateFlow<GameItem?>(null)
    val latestCraftedItem: StateFlow<GameItem?> = _latestCraftedItem.asStateFlow()

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
                    turnNumber = 1,
                    actorName = "Mestre da IA",
                    actionText = "Entrada Solo",
                    d20Roll = 15,
                    modifier = 0,
                    totalResult = 15,
                    difficultyClass = 10,
                    successLevel = RollSuccessLevel.SUCCESS,
                    gmNarrative = "Você desce sozinho pelas escadarias úmidas. A IA Game Master avaliou seu poder e ajustou a densidade de armadilhas para uma jornada solo intensa.",
                    tacticalSummary = "Sessão Solo ativa. Monstros e enigmas reescalados."
                )
            )
        )
    }

    /**
     * Host a Multiplayer Room
     */
    fun createMultiplayerRoom() {
        val code = ('A'..'Z').shuffled().take(6).joinToString("")
        val companion = CharacterModel(
            id = "char-ally-02",
            name = "Eldrin",
            characterClass = CharacterClass.MAGO,
            level = 3,
            currentHp = 45,
            maxHp = 45,
            currentMana = 70,
            maxMana = 80,
            strength = 8,
            dexterity = 12,
            intelligence = 17,
            vitality = 11,
            charisma = 13
        )

        _roomState.value = _roomState.value.copy(
            roomCode = code,
            mode = GameMode.MULTIPLAYER,
            floorNumber = 1,
            roomTitle = "Câmara da Aliança Fraturada",
            environmentDescription = "Um salão abobadado onde múltiplos jogadores conectam suas almas via WebSocket em tempo real.",
            currentMonster = Monster(
                id = "mon-multi",
                name = "Gárgula de Duas Cabeças (CR Coletivo)",
                currentHp = 80,
                maxHp = 80,
                attackPower = 15,
                description = "Monstro com escamas duras, escalonado para suportar o poder de múltiplos heróis."
            ),
            partyMembers = listOf(_character.value, companion),
            turnHistory = listOf(
                GameTurnEvent(
                    turnNumber = 1,
                    actorName = "Mestre da IA",
                    actionText = "Sala Multiplayer Criada",
                    d20Roll = 18,
                    modifier = 2,
                    totalResult = 20,
                    difficultyClass = 12,
                    successLevel = RollSuccessLevel.SUCCESS,
                    gmNarrative = "Código de Sala gerado: $code. Compartilhe com seus amigos para sincronizar turnos em tempo real!",
                    tacticalSummary = "Dificuldade escalonada para o grupo (Power Level médio ajustado)."
                )
            )
        )
    }

    /**
     * Join Room by code
     */
    fun joinRoomByCode(code: String) {
        if (code.isBlank()) return
        _roomState.value = _roomState.value.copy(
            roomCode = code.uppercase(),
            mode = GameMode.MULTIPLAYER,
            turnHistory = _roomState.value.turnHistory + GameTurnEvent(
                turnNumber = _roomState.value.turnHistory.size + 1,
                actorName = "Sistema",
                actionText = "Conectado à sala $code",
                d20Roll = 20,
                modifier = 0,
                totalResult = 20,
                difficultyClass = 10,
                successLevel = RollSuccessLevel.SUCCESS,
                gmNarrative = "Conexão WebSocket estabelecida com sucesso com a sala $code. Turnos sincronizados!",
                tacticalSummary = "Sincronização em tempo real ativa."
            )
        )
    }

    /**
     * Execute Turn Action (D20 + Modifiers + GM Evaluation)
     */
    fun executePlayerAction(actionText: String, actionType: String) {
        if (_isAiThinking.value) return

        viewModelScope.launch {
            _isAiThinking.value = true

            val d20 = Random.nextInt(1, 21)
            val modifier = when (actionType) {
                "ATTACK" -> _character.value.strength / 4
                "MAGIC" -> _character.value.intelligence / 4
                "ALCHEMY" -> 3
                else -> 2
            }
            val total = d20 + modifier
            val dc = 12 + (_roomState.value.floorNumber * 2)

            val successLevel = when {
                d20 == 20 -> RollSuccessLevel.CRITICAL_SUCCESS
                d20 == 1 -> RollSuccessLevel.CRITICAL_FAILURE
                total >= dc -> RollSuccessLevel.SUCCESS
                else -> RollSuccessLevel.FAILURE
            }

            // Resolve combat effects
            var monsterDamage = 0
            var playerDamage = 0
            var playerManaCost = 0
            var narrative = ""
            var tactical = ""

            when (actionType) {
                "ATTACK" -> {
                    if (successLevel == RollSuccessLevel.CRITICAL_SUCCESS) {
                        monsterDamage = 28 + modifier * 2
                        narrative = "${_character.value.name} encontra o ponto cego da criatura em uma investida mortal! O impacto ressoa pelas pedras milenares da tumba, abrindo uma fenda purulenta no monstro."
                        tactical = "Acerto Crítico! Causou $monsterDamage de dano esmagador!"
                    } else if (successLevel == RollSuccessLevel.SUCCESS) {
                        monsterDamage = 14 + modifier
                        narrative = "${_character.value.name} avança desferindo um golpe firme. O aço range ao atingir a carcaça da criatura, que recua rosnando de dor."
                        tactical = "Ataque bem-sucedido! Causou $monsterDamage de dano."
                    } else if (successLevel == RollSuccessLevel.CRITICAL_FAILURE) {
                        playerDamage = 12
                        narrative = "O chão escorregadio trai seu passo! ${_character.value.name} erra o ataque e o monstro aproveita a brecha para contra-atacar impiedosamente!"
                        tactical = "Falha Crítica! Desvantagem em combate: sofreu $playerDamage de dano."
                    } else {
                        playerDamage = 6
                        narrative = "O golpe é bloqueado pela couraça áspera do inimigo, que devolve um arranhão superficial em retaliação."
                        tactical = "Falha no ataque (Total $total vs DC $dc). Sofreu $playerDamage de dano."
                    }
                }
                "MAGIC" -> {
                    playerManaCost = 10
                    if (successLevel == RollSuccessLevel.SUCCESS || successLevel == RollSuccessLevel.CRITICAL_SUCCESS) {
                        monsterDamage = 22 + modifier * 2
                        narrative = "Labaredas e runas arcanas se condensam na ponta dos dedos de ${_character.value.name}, explodindo em um vórtice de energia mística sobre o monstro!"
                        tactical = "Magia bem-sucedida! Causou $monsterDamage de dano arcano."
                    } else {
                        narrative = "A concentração arcana falha diante da presença nefasta da masmorra. O feitiço se dissipa em faíscas inofensivas."
                        tactical = "Falha na conjuração arcana."
                    }
                }
                else -> {
                    narrative = "${_character.value.name} recua um passo, analisa as armadilhas no piso de pedra e antecipa os padrões de ataque da masmorra."
                    tactical = "Ação tática executada com sucesso."
                }
            }

            // Update Monster & Player stats
            val currentMonster = _roomState.value.currentMonster
            val updatedMonster = currentMonster?.let {
                val newHp = (it.currentHp - monsterDamage).coerceAtLeast(0)
                it.copy(currentHp = newHp)
            }

            val updatedPlayer = _character.value.copy(
                currentHp = (_character.value.currentHp - playerDamage).coerceIn(0, _character.value.maxHp),
                currentMana = (_character.value.currentMana - playerManaCost).coerceIn(0, _character.value.maxMana)
            )
            _character.value = updatedPlayer

            // Check party wipe rule (Purge common items, keep unique!)
            if (updatedPlayer.currentHp <= 0) {
                narrative += " [O HERÓI CAIU!] A escuridão da masmorra o consome. Itens comuns mundanos foram destruídos nas profundezas, mas seus ITENS ÚNICOS alchemicalmente forjados permanecem guardados na sua essência imortal!"
                _inventory.value = _inventory.value.filter { it.isPermanent || it.isUniqueItem }
            }

            val newEvent = GameTurnEvent(
                turnNumber = _roomState.value.turnHistory.size + 1,
                actorName = _character.value.name,
                actionText = actionText,
                d20Roll = d20,
                modifier = modifier,
                totalResult = total,
                difficultyClass = dc,
                successLevel = successLevel,
                gmNarrative = narrative,
                tacticalSummary = tactical,
                playerHpDelta = -playerDamage,
                playerManaDelta = -playerManaCost,
                monsterHpDelta = -monsterDamage
            )

            _roomState.value = _roomState.value.copy(
                currentMonster = updatedMonster,
                turnHistory = _roomState.value.turnHistory + newEvent
            )

            _isAiThinking.value = false
        }
    }

    /**
     * Synthesize items in Alchemy Lab into a Permanent Unique Item
     */
    fun combineAlchemyItems(itemA: GameItem, itemB: GameItem) {
        viewModelScope.launch {
            _isAiThinking.value = true

            // Formulate Unique Item
            val isWeaponHeal = (itemA.name.contains("Faca", ignoreCase = true) || itemA.name.contains("Espada", ignoreCase = true)) &&
                    (itemB.name.contains("Cura", ignoreCase = true) || itemB.name.contains("Poção", ignoreCase = true))

            val crafted = if (isWeaponHeal) {
                GameItem(
                    id = "unique-${System.currentTimeMillis()}",
                    name = "Lâmina do Sangue Restaurador",
                    description = "Uma lâmina lendária gravada com runas de rubi. Ao ferir inimigos, converte o impacto em névoa regenerativa que cura o portador e aliados!",
                    isUniqueItem = true,
                    isPermanent = true,
                    isCommonDungeonItem = false,
                    bonusStat = "+15 Ataque & +18 Cura por Golpe",
                    alchemyLineage = "Transmutado de '${itemA.name}' + '${itemB.name}'"
                )
            } else {
                GameItem(
                    id = "unique-${System.currentTimeMillis()}",
                    name = "Artefato Alquímico Transmutado",
                    description = "Síntese alquímica imbuída de poderes cruzados. Item permanente que viaja com o herói para futuras campanhas.",
                    isUniqueItem = true,
                    isPermanent = true,
                    isCommonDungeonItem = false,
                    bonusStat = "+12 Bônus Misto",
                    alchemyLineage = "Transmutado de '${itemA.name}' + '${itemB.name}'"
                )
            }

            // Remove consumed common items and add the permanent unique item
            val remaining = _inventory.value.filter { it.id != itemA.id && it.id != itemB.id }
            _inventory.value = remaining + crafted
            _latestCraftedItem.value = crafted

            // Add turn log
            _roomState.value = _roomState.value.copy(
                turnHistory = _roomState.value.turnHistory + GameTurnEvent(
                    turnNumber = _roomState.value.turnHistory.size + 1,
                    actorName = _character.value.name,
                    actionText = "Alquimia: Transmutação de ${itemA.name} e ${itemB.name}",
                    d20Roll = 20,
                    modifier = 5,
                    totalResult = 25,
                    difficultyClass = 14,
                    successLevel = RollSuccessLevel.CRITICAL_SUCCESS,
                    gmNarrative = "Círculos alquímicos violetas se desenham sobre a pedra. As matérias colapsam e renascem na lendária '${crafted.name}', gravada para sempre no destino do aventureiro!",
                    tacticalSummary = "Item Único Permanente Criado! Salvo no inventário persistente."
                )
            )

            _isAiThinking.value = false
        }
    }

    fun dismissCraftedBanner() {
        _latestCraftedItem.value = null
    }

    fun healHero() {
        _character.value = _character.value.copy(
            currentHp = _character.value.maxHp,
            currentMana = _character.value.maxMana
        )
    }
}
