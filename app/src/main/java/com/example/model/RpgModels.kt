package com.example.model

enum class GameMode {
    SOLO,
    MULTIPLAYER
}

enum class CharacterClass {
    GUERREIRO,
    MAGO,
    LADINO,
    CLERIGO,
    ALQUIMISTA
}

data class CharacterModel(
    val id: String = "char-hero-01",
    val name: String = "Valerius",
    val characterClass: CharacterClass = CharacterClass.GUERREIRO,
    val level: Int = 3,
    val currentHp: Int = 85,
    val maxHp: Int = 100,
    val currentMana: Int = 30,
    val maxMana: Int = 40,
    val strength: Int = 16,
    val dexterity: Int = 12,
    val intelligence: Int = 10,
    val vitality: Int = 14,
    val charisma: Int = 11,
    val gold: Int = 120
) {
    val powerLevel: Int
        get() = (level * 15) + (strength + dexterity + intelligence + vitality) + (maxHp / 5)
}

data class GameItem(
    val id: String,
    val name: String,
    val description: String,
    val isUniqueItem: Boolean = false,
    val isPermanent: Boolean = false,
    val isCommonDungeonItem: Boolean = true,
    val bonusStat: String = "+0",
    val alchemyLineage: String? = null
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

data class GameRoomState(
    val roomCode: String,
    val mode: GameMode,
    val floorNumber: Int,
    val roomTitle: String,
    val environmentDescription: String,
    val currentMonster: Monster?,
    val partyMembers: List<CharacterModel>,
    val turnHistory: List<GameTurnEvent>
)
