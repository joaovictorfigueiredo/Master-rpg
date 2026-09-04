package com.example.model

import com.example.R

enum class GameMode {
    SOLO,
    MULTIPLAYER
}

enum class ItemType {
    WEAPON,
    TOOL,
    MATERIAL,
    POTION,
    ARTIFACT
}

data class Weapon(
    val id: String,
    val name: String,
    val description: String,
    val damage: Int,
    val damageType: String,
    val bonusStat: String,
    val isDungeonCrafted: Boolean = false,
    val isEquipped: Boolean = false
)

data class Skill(
    val id: String,
    val name: String,
    val description: String,
    val manaCost: Int,
    val power: Int,
    val isHealing: Boolean = false,
    val scalingStat: String = "FOR",
    val icon: String = "⚔️"
)

data class CharacterModel(
    val id: String = "char-hero-01",
    val name: String = "Valerius",
    val race: String = "Humano",
    val characterClass: String = "Guerreiro",
    val archetype: String = "Cavaleiro Arcano",
    val backgroundStory: String = "Guardião forjado nas cinzas das masmorras esquecidas.",
    val avatarDrawableRes: Int = R.drawable.img_hero_portrait,
    val level: Int = 3,
    val currentXp: Int = 380,
    val maxXp: Int = 600,
    val currentHp: Int = 85,
    val maxHp: Int = 100,
    val currentMana: Int = 35,
    val maxMana: Int = 50,
    val strength: Int = 16,
    val dexterity: Int = 12,
    val intelligence: Int = 10,
    val vitality: Int = 14,
    val charisma: Int = 11,
    val gold: Int = 120,
    val equippedWeapon: Weapon? = Weapon(
        id = "wep-starter-01",
        name = "Espada Rúnica de Ferro Negro",
        description = "Lâmina equilibrada gravada com ranhuras de canalização de impacto.",
        damage = 16,
        damageType = "Corte Arcano",
        bonusStat = "+4 Dano Base",
        isDungeonCrafted = false,
        isEquipped = true
    ),
    val skills: List<Skill> = listOf(
        Skill(
            id = "sk-01",
            name = "Golpe Sísmico",
            description = "Funde força física com choque de impacto sísmico no solo.",
            manaCost = 10,
            power = 24,
            isHealing = false,
            scalingStat = "FOR",
            icon = "⚔️"
        ),
        Skill(
            id = "sk-02",
            name = "Lampejo de Fogo Rúnico",
            description = "Dispara uma labareda arcana concentrada na fresta da armadura inimiga.",
            manaCost = 15,
            power = 32,
            isHealing = false,
            scalingStat = "INT",
            icon = "🔥"
        ),
        Skill(
            id = "sk-03",
            name = "Sopro Restaurador",
            description = "Canaliza a vontade interior para regenerar feridas críticas de combate.",
            manaCost = 20,
            power = 35,
            isHealing = true,
            scalingStat = "VIT",
            icon = "✨"
        )
    )
) {
    val powerLevel: Int
        get() = (level * 15) + (strength + dexterity + intelligence + vitality) + (maxHp / 5) + ((equippedWeapon?.damage ?: 0) * 2)
}

data class GameItem(
    val id: String,
    val name: String,
    val description: String,
    val itemType: ItemType = ItemType.MATERIAL,
    val isUniqueItem: Boolean = false,
    val isPermanent: Boolean = false,
    val isCommonDungeonItem: Boolean = true,
    val isDungeonCrafted: Boolean = false,
    val bonusStat: String = "+0",
    val alchemyLineage: String? = null,
    val weaponData: Weapon? = null,
    val healAmount: Int = 0,
    val manaAmount: Int = 0
)

data class Monster(
    val id: String,
    val name: String,
    val currentHp: Int,
    val maxHp: Int,
    val attackPower: Int,
    val description: String
)

enum class RollSuccessLevel {
    CRITICAL_SUCCESS,
    SUCCESS,
    FAILURE,
    CRITICAL_FAILURE
}

data class GameTurnEvent(
    val turnNumber: Int,
    val actorName: String,
    val actionText: String,
    val d20Roll: Int,
    val modifier: Int,
    val totalResult: Int,
    val difficultyClass: Int,
    val successLevel: RollSuccessLevel,
    val gmNarrative: String,
    val tacticalSummary: String,
    val playerHpDelta: Int = 0,
    val playerManaDelta: Int = 0,
    val monsterHpDelta: Int = 0
)

data class DungeonPathChoice(
    val id: String,
    val title: String,
    val description: String,
    val riskLevel: String, // "Baixo", "Médio", "Perigoso", "Extremo"
    val rewardHint: String, // "Ferramentas de Forja", "Baú de Minério", "Fonte Sagrada"
    val emoji: String = "🚪"
)

enum class MessageSenderType {
    DUNGEON_MASTER_AI,
    LOCAL_PLAYER,
    ONLINE_PLAYER,
    SYSTEM_EVENT
}

data class DungeonChatMessage(
    val id: String,
    val senderName: String,
    val senderRole: String,
    val senderType: MessageSenderType,
    val avatarRes: Int,
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val d20Roll: Int? = null,
    val totalCheckResult: Int? = null,
    val successLevel: RollSuccessLevel? = null,
    val currentDangerSituation: String? = null,
    val hpDelta: Int = 0,
    val manaDelta: Int = 0
)

data class OnlinePartyMember(
    val id: String,
    val name: String,
    val characterClass: String,
    val race: String,
    val level: Int,
    val currentHp: Int,
    val maxHp: Int,
    val currentMana: Int,
    val maxMana: Int,
    val avatarRes: Int,
    val isLocalPlayer: Boolean = false,
    val isOnline: Boolean = true,
    val statusDescription: String = "Pronto para o combate"
)

data class GameRoomState(
    val roomCode: String,
    val mode: GameMode,
    val floorNumber: Int,
    val roomTitle: String,
    val environmentDescription: String,
    val currentMonster: Monster?,
    val partyMembers: List<CharacterModel>,
    val turnHistory: List<GameTurnEvent>,
    val chatMessages: List<DungeonChatMessage> = emptyList(),
    val onlineParty: List<OnlinePartyMember> = emptyList(),
    val currentSituationPrompt: String = "",
    val availablePaths: List<DungeonPathChoice> = emptyList(),
    val hasCraftingAnvilNearby: Boolean = true,
    val lastDamageDealtToPlayer: Int = 0,
    val lastHealingReceivedByPlayer: Int = 0
)

data class DungeonDefinition(
    val id: String,
    val name: String,
    val subtitle: String,
    val description: String,
    val theme: String,
    val difficulty: String, // "Fácil", "Normal", "Difícil", "Pesadelo"
    val recommendedLevel: Int,
    val bossName: String,
    val bossHp: Int,
    val bossAttack: Int,
    val bossDescription: String,
    val lootHighlight: String,
    val tagEmoji: String = "⚔️"
)

val DEFAULT_DUNGEONS = listOf(
    DungeonDefinition(
        id = "dung-malakor",
        name = "Criptas Sombrias de Malakor",
        subtitle = "Catacumbas dos Antigos Reis Esquecidos",
        description = "Salas gélidas esculpidas em rocha vulcânica antiga, infestadas por esqueletos rúnicos e almas penadas. Há lendas de forjas antigas e minérios raros nas criptas inferiores.",
        theme = "Criptas das Sombras",
        difficulty = "Normal",
        recommendedLevel = 1,
        bossName = "Lorde Espectral Malakor",
        bossHp = 70,
        bossAttack = 15,
        bossDescription = "Um antigo senhor necromante empunhando cajado de osso e lâminas espectrais.",
        lootHighlight = "Lingotes de Aço das Criptas & Relíquias",
        tagEmoji = "💀"
    ),
    DungeonDefinition(
        id = "dung-ignis",
        name = "Fosso Ígneo de Ignis",
        subtitle = "Coração Vulcânico dos Dragões",
        description = "Rios de magma borbulhante e pontes de ferro incandescente. O calor extremo atrai feras cuspidoras de fogo e abriga minérios de mitril de temperatura cósmica ideais para forjar armas lendárias.",
        theme = "Vulcão de Magma",
        difficulty = "Difícil",
        recommendedLevel = 2,
        bossName = "Piroclasto, o Dragão das Brasas",
        bossHp = 95,
        bossAttack = 22,
        bossDescription = "Dragão elemental com escamas de obsidiana e sopro incandescente.",
        lootHighlight = "Minério de Mitril & Cristais de Fogo",
        tagEmoji = "🔥"
    ),
    DungeonDefinition(
        id = "dung-eldermist",
        name = "Floresta Proibida de Eldermist",
        subtitle = "Bosque dos Sussurros Venenosos",
        description = "Árvores milenares cobertas por névoa arroxeada e vinhas rúnicas vivas. Criaturas quiméricas e espíritos da floresta protegem poções alquímicas raras e madeiras élficas para cajados e arcos.",
        theme = "Bosque Arcano",
        difficulty = "Média",
        recommendedLevel = 2,
        bossName = "Quimera das Vinhas Ancestrais",
        bossHp = 80,
        bossAttack = 18,
        bossDescription = "Fera colossal composta por raízes de ébano e chifres de osso pontiagudos.",
        lootHighlight = "Elixires Restauradores & Seivas Raras",
        tagEmoji = "🌿"
    ),
    DungeonDefinition(
        id = "dung-abyss",
        name = "Cidadela Submersa do Abismo",
        subtitle = "Ruínas dos Mares Profundos",
        description = "Templos megalíticos parcialmente alagados por águas bioluminescentes escuras. Guardiões anfíbios com tridentes e tentáculos patrulham altares que guardam ferramentas de forja esquecidas.",
        theme = "Ruínas Abissais",
        difficulty = "Pesadelo",
        recommendedLevel = 3,
        bossName = "Leviatã Abissal Guardião",
        bossHp = 120,
        bossAttack = 26,
        bossDescription = "Monstro titânico com couraça de coral e olhos abissais fulgurantes.",
        lootHighlight = "Ferramentas do Mestre & Lâminas Perdidas",
        tagEmoji = "🌊"
    ),
    DungeonDefinition(
        id = "dung-golems",
        name = "Laboratório dos Golems de Éter",
        subtitle = "Oficina Arcana dos Artífices",
        description = "Engrenagens gigantescas, vapor mágico pressurizado e autômatos de cobre que operam forjas automáticas. O melhor local para encontrar ferramentas de artesanato e núcleos de energia.",
        theme = "Oficina Mecânica Arcana",
        difficulty = "Alta",
        recommendedLevel = 2,
        bossName = "Colosso a Vapor Prime",
        bossHp = 90,
        bossAttack = 20,
        bossDescription = "Autômato gigante de latão e titânio movido a fogo rúnico.",
        lootHighlight = "Kits de Forja Portáteis & Núcleos Mecânicos",
        tagEmoji = "⚙️"
    ),
    DungeonDefinition(
        id = "dung-astral",
        name = "Torre dos Ecos Celestes",
        subtitle = "Santuário das Fendas Estelares",
        description = "Plataformas flutuantes sobre o vácuo astral, onde feitiços esquecidos ganham forma física e cristais cósmicos sussurram segredos aos aventureiros dignos.",
        theme = "Santuário Astral",
        difficulty = "Heroica",
        recommendedLevel = 3,
        bossName = "Espectro Astral de Chronos",
        bossHp = 100,
        bossAttack = 24,
        bossDescription = "Entidade feita de constelações que distorce o próprio tempo nas batalhas.",
        lootHighlight = "Tomos de Skills & Joias do Vácuo",
        tagEmoji = "✨"
    )
)

