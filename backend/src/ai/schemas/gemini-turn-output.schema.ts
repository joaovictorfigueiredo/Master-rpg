// Gemini API Structured Output Schema
// Used with responseSchema in Google Gen AI SDK to guarantee 100% type-safe JSON returns.

export interface TurnHpDelta {
  characterId: string;
  characterName: string;
  delta: number; // e.g. -14 for damage, +20 for healing
  reason: string;
}

export interface TurnManaDelta {
  characterId: string;
  delta: number;
}

export interface InventoryChangeItem {
  action: 'ADD' | 'REMOVE' | 'UPGRADE_TO_UNIQUE';
  characterId: string;
  itemName: string;
  isUniqueItem: boolean;
  isPermanent: boolean;
  customDescription?: string;
  bonusModifiers?: Record<string, number | string>;
}

export interface MonsterStateUpdate {
  monsterId: string;
  monsterName: string;
  hpDelta: number;
  remainingHp: number;
  statusCondition?: string; // "STUNNED", "BURNING", "DEAD", etc.
}

export interface GeminiTurnOutput {
  narrativeText: string;
  tacticalSummary: string;
  rulesEvaluation: {
    actionType: string;
    dcApplied: number;
    playerRoll: number;
    modifier: number;
    totalResult: number;
    successLevel: 'CRITICAL_FAILURE' | 'FAILURE' | 'SUCCESS' | 'CRITICAL_SUCCESS';
  };
  partyHpDeltas: TurnHpDelta[];
  partyManaDeltas: TurnManaDelta[];
  monsterUpdates: MonsterStateUpdate[];
  inventoryChanges: InventoryChangeItem[];
  dungeonEvent: {
    roomCleared: boolean;
    bossDefeated: boolean;
    partyWiped: boolean;
    secretDiscovered?: string;
  };
  suggestedNextActions: string[];
  nextTurnActorCharacterId: string;
}

// JSON Schema definition for Gemini Structured Outputs
export const GEMINI_TURN_JSON_SCHEMA = {
  type: 'object',
  properties: {
    narrativeText: {
      type: 'string',
      description: 'Immersive, atmospheric RPG Master storytelling in response to the players action.'
    },
    tacticalSummary: {
      type: 'string',
      description: 'Quick single-line combat or exploratory outcome summary.'
    },
    rulesEvaluation: {
      type: 'object',
      properties: {
        actionType: { type: 'string' },
        dcApplied: { type: 'integer' },
        playerRoll: { type: 'integer' },
        modifier: { type: 'integer' },
        totalResult: { type: 'integer' },
        successLevel: {
          type: 'string',
          enum: ['CRITICAL_FAILURE', 'FAILURE', 'SUCCESS', 'CRITICAL_SUCCESS']
        }
      },
      required: ['actionType', 'dcApplied', 'playerRoll', 'modifier', 'totalResult', 'successLevel']
    },
    partyHpDeltas: {
      type: 'array',
      items: {
        type: 'object',
        properties: {
          characterId: { type: 'string' },
          characterName: { type: 'string' },
          delta: { type: 'integer' },
          reason: { type: 'string' }
        },
        required: ['characterId', 'characterName', 'delta', 'reason']
      }
    },
    partyManaDeltas: {
      type: 'array',
      items: {
        type: 'object',
        properties: {
          characterId: { type: 'string' },
          delta: { type: 'integer' }
        },
        required: ['characterId', 'delta']
      }
    },
    monsterUpdates: {
      type: 'array',
      items: {
        type: 'object',
        properties: {
          monsterId: { type: 'string' },
          monsterName: { type: 'string' },
          hpDelta: { type: 'integer' },
          remainingHp: { type: 'integer' },
          statusCondition: { type: 'string' }
        },
        required: ['monsterId', 'monsterName', 'hpDelta', 'remainingHp']
      }
    },
    inventoryChanges: {
      type: 'array',
      items: {
        type: 'object',
        properties: {
          action: { type: 'string', enum: ['ADD', 'REMOVE', 'UPGRADE_TO_UNIQUE'] },
          characterId: { type: 'string' },
          itemName: { type: 'string' },
          isUniqueItem: { type: 'boolean' },
          isPermanent: { type: 'boolean' },
          customDescription: { type: 'string' }
        },
        required: ['action', 'characterId', 'itemName', 'isUniqueItem', 'isPermanent']
      }
    },
    dungeonEvent: {
      type: 'object',
      properties: {
        roomCleared: { type: 'boolean' },
        bossDefeated: { type: 'boolean' },
        partyWiped: { type: 'boolean' },
        secretDiscovered: { type: 'string' }
      },
      required: ['roomCleared', 'bossDefeated', 'partyWiped']
    },
    suggestedNextActions: {
      type: 'array',
      items: { type: 'string' },
      description: '3-4 tactical suggestions for the player or group to do next.'
    },
    nextTurnActorCharacterId: {
      type: 'string',
      description: 'The character ID whose turn it is next (or same character in solo mode).'
    }
  },
  required: [
    'narrativeText',
    'tacticalSummary',
    'rulesEvaluation',
    'partyHpDeltas',
    'partyManaDeltas',
    'monsterUpdates',
    'inventoryChanges',
    'dungeonEvent',
    'suggestedNextActions',
    'nextTurnActorCharacterId'
  ]
};
