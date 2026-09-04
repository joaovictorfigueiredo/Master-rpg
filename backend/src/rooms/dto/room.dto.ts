export class CreateRoomDto {
  mode: 'SOLO' | 'MULTIPLAYER';
  difficulty?: 'ADAPTIVE' | 'APPRENTICE' | 'HEROIC' | 'NIGHTMARE';
  hostCharacter: {
    id: string;
    name: string;
    class: string;
    level: number;
    hp: number;
    maxHp: number;
    mana: number;
    maxMana: number;
    powerLevel: number;
    uniqueEquipment: string[];
  };
}

export class JoinRoomDto {
  roomCode: string;
  character: {
    id: string;
    name: string;
    class: string;
    level: number;
    hp: number;
    maxHp: number;
    mana: number;
    maxMana: number;
    powerLevel: number;
    uniqueEquipment: string[];
  };
}

export class SubmitActionDto {
  roomCode: string;
  characterId: string;
  actionText: string;
  actionType: 'ATTACK' | 'CAST_SPELL' | 'USE_ITEM' | 'ALCHEMY_COMBINE' | 'EXPLORE' | 'FLEE';
  targetMonsterId?: string;
  d20Roll: number;
  attributeModifier: number;
}

export class CombineAlchemyDto {
  roomCode: string;
  characterId: string;
  item1Id: string;
  item1Name: string;
  item2Id: string;
  item2Name: string;
}
