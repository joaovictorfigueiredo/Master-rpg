-- =====================================================================
-- POSTGRESQL DDL MIGRATION - AI RPG GAME MASTER ARCHITECTURE
-- =====================================================================

-- Enums
CREATE TYPE "GameMode" AS ENUM ('SOLO', 'MULTIPLAYER');
CREATE TYPE "RoomStatus" AS ENUM ('LOBBY', 'IN_DUNGEON', 'COMPLETED', 'WIPED', 'ABANDONED');
CREATE TYPE "CharacterClass" AS ENUM ('WARRIOR', 'MAGE', 'ROGUE', 'CLERIC', 'ALCHEMIST');
CREATE TYPE "ItemRarity" AS ENUM ('COMMON', 'UNCOMMON', 'RARE', 'EPIC', 'LEGENDARY', 'MYTHIC');
CREATE TYPE "ItemType" AS ENUM ('WEAPON', 'SHIELD', 'ARMOR', 'CONSUMABLE', 'CATALYST', 'RELIC', 'SCROLL');
CREATE TYPE "ActionType" AS ENUM ('ATTACK', 'CAST_SPELL', 'USE_ITEM', 'ALCHEMY_COMBINE', 'EXPLORE', 'INVESTIGATE', 'FLEE', 'DEFEND');
CREATE TYPE "DungeonDifficulty" AS ENUM ('ADAPTIVE', 'APPRENTICE', 'HEROIC', 'NIGHTMARE');

-- Users table
CREATE TABLE "User" (
    "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "email" VARCHAR(255) NOT NULL UNIQUE,
    "username" VARCHAR(60) NOT NULL UNIQUE,
    "passwordHash" VARCHAR(255) NOT NULL,
    "createdAt" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Characters table
CREATE TABLE "Character" (
    "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "userId" UUID NOT NULL REFERENCES "User"("id") ON DELETE CASCADE,
    "name" VARCHAR(100) NOT NULL,
    "characterClass" "CharacterClass" NOT NULL,
    "level" INTEGER NOT NULL DEFAULT 1,
    "experience" INTEGER NOT NULL DEFAULT 0,
    "currentHp" INTEGER NOT NULL DEFAULT 100,
    "maxHp" INTEGER NOT NULL DEFAULT 100,
    "currentMana" INTEGER NOT NULL DEFAULT 50,
    "maxMana" INTEGER NOT NULL DEFAULT 50,
    "strength" INTEGER NOT NULL DEFAULT 10,
    "dexterity" INTEGER NOT NULL DEFAULT 10,
    "intelligence" INTEGER NOT NULL DEFAULT 10,
    "vitality" INTEGER NOT NULL DEFAULT 10,
    "charisma" INTEGER NOT NULL DEFAULT 10,
    "powerLevel" INTEGER NOT NULL DEFAULT 50,
    "gold" INTEGER NOT NULL DEFAULT 0,
    "isAlive" BOOLEAN NOT NULL DEFAULT TRUE,
    "createdAt" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Spells
CREATE TABLE "Spell" (
    "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "name" VARCHAR(100) NOT NULL UNIQUE,
    "description" TEXT NOT NULL,
    "manaCost" INTEGER NOT NULL,
    "cooldownTurns" INTEGER NOT NULL DEFAULT 0,
    "damageType" VARCHAR(50) NOT NULL,
    "basePower" INTEGER NOT NULL,
    "requiredLevel" INTEGER NOT NULL DEFAULT 1
);

-- Character Learned Spells
CREATE TABLE "CharacterSpell" (
    "characterId" UUID NOT NULL REFERENCES "Character"("id") ON DELETE CASCADE,
    "spellId" UUID NOT NULL REFERENCES "Spell"("id") ON DELETE CASCADE,
    "learnedAt" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "masteryLevel" INTEGER NOT NULL DEFAULT 1,
    PRIMARY KEY ("characterId", "spellId")
);

-- Item Templates (Base Catalog)
CREATE TABLE "ItemTemplate" (
    "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "code" VARCHAR(100) NOT NULL UNIQUE,
    "name" VARCHAR(100) NOT NULL,
    "description" TEXT NOT NULL,
    "itemType" "ItemType" NOT NULL,
    "rarity" "ItemRarity" NOT NULL DEFAULT 'COMMON',
    "baseDamage" INTEGER NOT NULL DEFAULT 0,
    "baseArmor" INTEGER NOT NULL DEFAULT 0,
    "baseHeal" INTEGER NOT NULL DEFAULT 0,
    "value" INTEGER NOT NULL DEFAULT 10,
    "canBeCombined" BOOLEAN NOT NULL DEFAULT TRUE,
    "isConsumable" BOOLEAN NOT NULL DEFAULT FALSE
);

-- Inventory (Persistent Unique Items vs Disposable Dungeon Items)
CREATE TABLE "InventoryItem" (
    "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "characterId" UUID NOT NULL REFERENCES "Character"("id") ON DELETE CASCADE,
    "itemTemplateId" UUID NOT NULL REFERENCES "ItemTemplate"("id"),
    "customName" VARCHAR(150),
    "customDescription" TEXT,
    "isPermanent" BOOLEAN NOT NULL DEFAULT FALSE,
    "isCommonDungeonItem" BOOLEAN NOT NULL DEFAULT TRUE,
    "isUniqueItem" BOOLEAN NOT NULL DEFAULT FALSE,
    "alchemyLineage" JSONB,
    "bonusModifiers" JSONB,
    "quantity" INTEGER NOT NULL DEFAULT 1,
    "durability" INTEGER NOT NULL DEFAULT 100,
    "acquiredInDungeonId" VARCHAR(100),
    "createdAt" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Game Rooms (Lobbies & Active Dungeons)
CREATE TABLE "GameRoom" (
    "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "roomCode" VARCHAR(10) NOT NULL UNIQUE,
    "hostUserId" UUID NOT NULL REFERENCES "User"("id"),
    "mode" "GameMode" NOT NULL DEFAULT 'SOLO',
    "status" "RoomStatus" NOT NULL DEFAULT 'LOBBY',
    "difficulty" "DungeonDifficulty" NOT NULL DEFAULT 'ADAPTIVE',
    "currentFloor" INTEGER NOT NULL DEFAULT 1,
    "dungeonSeed" VARCHAR(64) NOT NULL,
    "calculatedPartyCR" DOUBLE PRECISION NOT NULL DEFAULT 1.0,
    "createdAt" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Participants in a Room
CREATE TABLE "RoomParticipant" (
    "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "roomId" UUID NOT NULL REFERENCES "GameRoom"("id") ON DELETE CASCADE,
    "characterId" UUID NOT NULL REFERENCES "Character"("id") ON DELETE CASCADE,
    "isHost" BOOLEAN NOT NULL DEFAULT FALSE,
    "isReady" BOOLEAN NOT NULL DEFAULT FALSE,
    "sessionHp" INTEGER NOT NULL,
    "sessionMana" INTEGER NOT NULL,
    "isActive" BOOLEAN NOT NULL DEFAULT TRUE,
    "isDead" BOOLEAN NOT NULL DEFAULT FALSE,
    "joinedAt" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "unique_room_character" UNIQUE ("roomId", "characterId")
);

-- Active Dungeon Session
CREATE TABLE "DungeonSession" (
    "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "roomId" UUID NOT NULL REFERENCES "GameRoom"("id") ON DELETE CASCADE,
    "floorNumber" INTEGER NOT NULL,
    "currentRoomTitle" VARCHAR(150) NOT NULL,
    "environmentNarration" TEXT NOT NULL,
    "activeMonsters" JSONB NOT NULL DEFAULT '[]'::jsonb,
    "roomLoot" JSONB NOT NULL DEFAULT '[]'::jsonb,
    "isBossEncounter" BOOLEAN NOT NULL DEFAULT FALSE,
    "isCleared" BOOLEAN NOT NULL DEFAULT FALSE,
    "createdAt" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "clearedAt" TIMESTAMP WITH TIME ZONE
);

-- Turn History & Roll Processing
CREATE TABLE "GameTurn" (
    "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "roomId" UUID NOT NULL REFERENCES "GameRoom"("id") ON DELETE CASCADE,
    "dungeonSessionId" UUID NOT NULL REFERENCES "DungeonSession"("id") ON DELETE CASCADE,
    "turnNumber" INTEGER NOT NULL,
    "activeCharacterId" UUID NOT NULL REFERENCES "Character"("id"),
    "playerActionText" TEXT NOT NULL,
    "actionType" "ActionType" NOT NULL,
    "targetEntity" VARCHAR(100),
    "d20Roll" INTEGER NOT NULL,
    "modifier" INTEGER NOT NULL,
    "totalScore" INTEGER NOT NULL,
    "difficultyClass" INTEGER NOT NULL,
    "isCriticalSuccess" BOOLEAN NOT NULL DEFAULT FALSE,
    "isCriticalFailure" BOOLEAN NOT NULL DEFAULT FALSE,
    "gmNarrative" TEXT NOT NULL,
    "outcomeSummary" VARCHAR(255) NOT NULL,
    "partyHpDeltas" JSONB NOT NULL DEFAULT '{}'::jsonb,
    "partyManaDeltas" JSONB NOT NULL DEFAULT '{}'::jsonb,
    "inventoryChanges" JSONB NOT NULL DEFAULT '{}'::jsonb,
    "suggestedNextActions" JSONB NOT NULL DEFAULT '[]'::jsonb,
    "timestamp" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Alchemy Recipe Journal
CREATE TABLE "AlchemyRecipeLog" (
    "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "characterId" UUID NOT NULL REFERENCES "Character"("id") ON DELETE CASCADE,
    "sourceItem1Name" VARCHAR(100) NOT NULL,
    "sourceItem2Name" VARCHAR(100) NOT NULL,
    "catalystUsed" VARCHAR(100),
    "resultingItemName" VARCHAR(150) NOT NULL,
    "resultingItemEffects" JSONB NOT NULL,
    "aiGeneratedFlavor" TEXT NOT NULL,
    "wasSuccess" BOOLEAN NOT NULL DEFAULT TRUE,
    "createdAt" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Performance Indexes
CREATE INDEX "idx_character_user" ON "Character"("userId");
CREATE INDEX "idx_character_power" ON "Character"("powerLevel");
CREATE INDEX "idx_inventory_character" ON "InventoryItem"("characterId");
CREATE INDEX "idx_inventory_unique" ON "InventoryItem"("isUniqueItem");
CREATE INDEX "idx_inventory_permanent" ON "InventoryItem"("isPermanent");
CREATE INDEX "idx_room_code" ON "GameRoom"("roomCode");
CREATE INDEX "idx_room_status" ON "GameRoom"("status");
CREATE INDEX "idx_participant_room" ON "RoomParticipant"("roomId");
CREATE INDEX "idx_gameturn_room_turn" ON "GameTurn"("roomId", "turnNumber");
