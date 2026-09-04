import { Injectable, Logger, NotFoundException } from '@nestjs/common';
import { GeminiMasterService } from '../ai/gemini-master.service';
import { AlchemyService } from '../alchemy/alchemy.service';
import { CreateRoomDto, JoinRoomDto, SubmitActionDto, CombineAlchemyDto } from './dto/room.dto';

export interface ActiveParticipant {
  socketId: string;
  character: CreateRoomDto['hostCharacter'];
  currentHp: number;
  currentMana: number;
  isReady: boolean;
  inventory: Array<{
    id: string;
    name: string;
    isUniqueItem: boolean;
    isPermanent: boolean;
    isCommonDungeonItem: boolean;
    description?: string;
  }>;
}

export interface ActiveRoomState {
  roomCode: string;
  mode: 'SOLO' | 'MULTIPLAYER';
  status: 'LOBBY' | 'IN_DUNGEON' | 'COMPLETED' | 'WIPED';
  floorNumber: number;
  roomTitle: string;
  environmentNarration: string;
  participants: Map<string, ActiveParticipant>; // characterId -> ActiveParticipant
  activeMonsters: Array<{
    id: string;
    name: string;
    hp: number;
    maxHp: number;
    attack: number;
  }>;
  turnNumber: number;
  currentTurnCharacterId: string;
  turnHistory: any[];
}

@Injectable()
export class RoomsService {
  private readonly logger = new Logger(RoomsService.name);
  private rooms = new Map<string, ActiveRoomState>();

  constructor(
    private readonly geminiMaster: GeminiMasterService,
    private readonly alchemyService: AlchemyService
  ) {}

  /**
   * Create a new Solo or Multiplayer Game Room.
   */
  createRoom(dto: CreateRoomDto, socketId: string): ActiveRoomState {
    const roomCode = Math.random().toString(36).substring(2, 8).toUpperCase();

    const initialInventory = [
      {
        id: 'item-dng-1',
        name: 'Faca Comum de Ferro',
        isUniqueItem: false,
        isPermanent: false,
        isCommonDungeonItem: true,
        description: 'Uma lâmina comum encontrada no chão da tumba. Se morrer, será perdida.',
      },
      {
        id: 'item-dng-2',
        name: 'Poção Menor de Cura',
        isUniqueItem: false,
        isPermanent: false,
        isCommonDungeonItem: true,
        description: 'Frasco de vidro com extrato de ervas medicinais.',
      },
    ];

    const hostParticipant: ActiveParticipant = {
      socketId,
      character: dto.hostCharacter,
      currentHp: dto.hostCharacter.hp,
      currentMana: dto.hostCharacter.mana,
      isReady: true,
      inventory: initialInventory,
    };

    const participants = new Map<string, ActiveParticipant>();
    participants.set(dto.hostCharacter.id, hostParticipant);

    const room: ActiveRoomState = {
      roomCode,
      mode: dto.mode,
      status: dto.mode === 'SOLO' ? 'IN_DUNGEON' : 'LOBBY',
      floorNumber: 1,
      roomTitle: 'O Sepulcro dos Ecos Ancestrais',
      environmentNarration:
        'Gotas de água gotejam do teto de pedra calcária, ecoando na escuridão. O ar cheira a mofo e enxofre antigo.',
      participants,
      activeMonsters: [
        {
          id: 'mon-1',
          name: 'Necrófago das Criptas',
          hp: 35 + dto.hostCharacter.level * 5,
          maxHp: 35 + dto.hostCharacter.level * 5,
          attack: 8 + dto.hostCharacter.level * 2,
        },
      ],
      turnNumber: 1,
      currentTurnCharacterId: dto.hostCharacter.id,
      turnHistory: [],
    };

    this.rooms.set(roomCode, room);
    this.logger.log(`Room created: ${roomCode} (${dto.mode}) by ${dto.hostCharacter.name}`);
    return room;
  }

  /**
   * Join an existing multiplayer room.
   */
  joinRoom(dto: JoinRoomDto, socketId: string): ActiveRoomState {
    const room = this.rooms.get(dto.roomCode);
    if (!room) {
      throw new NotFoundException(`Sala com código ${dto.roomCode} não encontrada.`);
    }

    if (room.mode !== 'MULTIPLAYER') {
      throw new Error('Esta sala foi configurada como modo Solo e não aceita novos jogadores.');
    }

    const participant: ActiveParticipant = {
      socketId,
      character: dto.character,
      currentHp: dto.character.hp,
      currentMana: dto.character.mana,
      isReady: true,
      inventory: [
        {
          id: `item-${Date.now()}`,
          name: 'Tocha Acesa',
          isUniqueItem: false,
          isPermanent: false,
          isCommonDungeonItem: true,
        },
      ],
    };

    room.participants.set(dto.character.id, participant);

    // Adaptive monster scaling: when more party members join, scale dungeon monsters
    const totalPartyPower = Array.from(room.participants.values()).reduce(
      (sum, p) => sum + p.character.powerLevel,
      0
    );

    if (room.participants.size > 1 && room.activeMonsters.length < 2) {
      room.activeMonsters.push({
        id: `mon-${Date.now()}`,
        name: 'Gárgula Pétrea Vigia',
        hp: Math.floor(totalPartyPower * 0.4),
        maxHp: Math.floor(totalPartyPower * 0.4),
        attack: 10,
      });
      room.environmentNarration += ' O peso da presença do grupo fez ruir a estátua que agora desperta!';
    }

    return room;
  }

  /**
   * Process a turn action via Gemini AI Game Master.
   */
  async processAction(dto: SubmitActionDto) {
    const room = this.rooms.get(dto.roomCode);
    if (!room) throw new NotFoundException('Sala não encontrada.');

    const participant = room.participants.get(dto.characterId);
    if (!participant) throw new NotFoundException('Personagem não encontrado nesta sala.');

    const partyMembers = Array.from(room.participants.values()).map((p) => ({
      id: p.character.id,
      name: p.character.name,
      class: p.character.class,
      level: p.character.level,
      hp: p.currentHp,
      maxHp: p.character.maxHp,
      mana: p.currentMana,
      maxMana: p.character.maxMana,
      powerLevel: p.character.powerLevel,
      uniqueEquipment: p.inventory.filter((i) => i.isUniqueItem).map((i) => i.name),
    }));

    // Invoke Gemini AI Game Master
    const turnResult = await this.geminiMaster.processTurn({
      gameMode: room.mode,
      floorNumber: room.floorNumber,
      roomTitle: room.roomTitle,
      party: partyMembers,
      activeMonsters: room.activeMonsters,
      actingPlayer: {
        id: participant.character.id,
        name: participant.character.name,
        actionText: dto.actionText,
        actionType: dto.actionType,
        d20Roll: dto.d20Roll,
        attributeModifier: dto.attributeModifier,
        targetName: room.activeMonsters[0]?.name,
      },
    });

    // Apply HP/Mana Deltas
    for (const hpDelta of turnResult.partyHpDeltas) {
      const targetP = room.participants.get(hpDelta.characterId);
      if (targetP) {
        targetP.currentHp = Math.max(0, Math.min(targetP.character.maxHp, targetP.currentHp + hpDelta.delta));
      }
    }

    for (const manaDelta of turnResult.partyManaDeltas) {
      const targetP = room.participants.get(manaDelta.characterId);
      if (targetP) {
        targetP.currentMana = Math.max(0, Math.min(targetP.character.maxMana, targetP.currentMana + manaDelta.delta));
      }
    }

    // Apply Monster HP Updates
    for (const mUp of turnResult.monsterUpdates) {
      const mon = room.activeMonsters.find((m) => m.id === mUp.monsterId);
      if (mon) {
        mon.hp = mUp.remainingHp;
      }
    }
    // Remove dead monsters
    room.activeMonsters = room.activeMonsters.filter((m) => m.hp > 0);

    // Apply Inventory Changes
    for (const invChange of turnResult.inventoryChanges) {
      const p = room.participants.get(invChange.characterId);
      if (p) {
        if (invChange.action === 'ADD' || invChange.action === 'UPGRADE_TO_UNIQUE') {
          p.inventory.push({
            id: `item-${Date.now()}`,
            name: invChange.itemName,
            isUniqueItem: invChange.isUniqueItem,
            isPermanent: invChange.isPermanent,
            isCommonDungeonItem: !invChange.isUniqueItem,
            description: invChange.customDescription,
          });
        }
      }
    }

    // Check party wipe rule: Purge common dungeon items, keep permanent unique items!
    const allDead = Array.from(room.participants.values()).every((p) => p.currentHp <= 0);
    if (allDead) {
      room.status = 'WIPED';
      this.handlePartyWipe(room);
    }

    room.turnNumber++;
    room.currentTurnCharacterId = turnResult.nextTurnActorCharacterId;
    room.turnHistory.push(turnResult);

    return {
      room,
      turnResult,
    };
  }

  /**
   * Synthesize Alchemy combination in room.
   */
  async combineAlchemy(dto: CombineAlchemyDto) {
    const room = this.rooms.get(dto.roomCode);
    if (!room) throw new NotFoundException('Sala não encontrada.');

    const participant = room.participants.get(dto.characterId);
    if (!participant) throw new NotFoundException('Personagem não encontrado.');

    const item1 = participant.inventory.find((i) => i.id === dto.item1Id || i.name === dto.item1Name);
    const item2 = participant.inventory.find((i) => i.id === dto.item2Id || i.name === dto.item2Name);

    const craftedUnique = await this.alchemyService.synthesizeUniqueItem({
      characterId: dto.characterId,
      characterAlchemySkill: participant.character.level * 2,
      item1: item1 || { id: '1', name: dto.item1Name, type: 'WEAPON', isPermanent: false, isCommonDungeonItem: true },
      item2: item2 || { id: '2', name: dto.item2Name, type: 'CONSUMABLE', isPermanent: false, isCommonDungeonItem: true },
    });

    // Remove consumed items from inventory
    participant.inventory = participant.inventory.filter(
      (i) => i.name !== dto.item1Name && i.name !== dto.item2Name
    );

    // Add synthesized unique item (Permanente que viaja com o personagem!)
    participant.inventory.push({
      id: `unique-${Date.now()}`,
      name: craftedUnique.name,
      isUniqueItem: true,
      isPermanent: true,
      isCommonDungeonItem: false,
      description: craftedUnique.description,
    });

    return {
      craftedUnique,
      updatedInventory: participant.inventory,
    };
  }

  /**
   * Rule Implementation: On Wipe/Death, common dungeon items are destroyed, but unique items survive!
   */
  private handlePartyWipe(room: ActiveRoomState) {
    this.logger.warn(`Party wiped in room ${room.roomCode}! Purging non-permanent items.`);
    for (const p of room.participants.values()) {
      // Retém apenas os itens permanentes/únicos
      p.inventory = p.inventory.filter((i) => i.isPermanent || i.isUniqueItem);
    }
  }

  getRoom(roomCode: string): ActiveRoomState | undefined {
    return this.rooms.get(roomCode);
  }
}
