import {
  WebSocketGateway,
  SubscribeMessage,
  MessageBody,
  ConnectedSocket,
  WebSocketServer,
  OnGatewayConnection,
  OnGatewayDisconnect,
} from '@nestjs/websockets';
import { Server, Socket } from 'socket.io';
import { Logger } from '@nestjs/common';
import { RoomsService } from './rooms.service';
import { CreateRoomDto, JoinRoomDto, SubmitActionDto, CombineAlchemyDto } from './dto/room.dto';

@WebSocketGateway({
  cors: {
    origin: '*',
  },
  namespace: 'game',
})
export class RoomsGateway implements OnGatewayConnection, OnGatewayDisconnect {
  @WebSocketServer()
  server: Server;

  private readonly logger = new Logger(RoomsGateway.name);

  constructor(private readonly roomsService: RoomsService) {}

  handleConnection(client: Socket) {
    this.logger.log(`Client connected: ${client.id}`);
  }

  handleDisconnect(client: Socket) {
    this.logger.log(`Client disconnected: ${client.id}`);
  }

  /**
   * CREATE ROOM (Solo or Multiplayer)
   */
  @SubscribeMessage('create_room')
  handleCreateRoom(@ConnectedSocket() client: Socket, @MessageBody() dto: CreateRoomDto) {
    try {
      const room = this.roomsService.createRoom(dto, client.id);
      client.join(room.roomCode);

      const serializedRoom = {
        ...room,
        participants: Array.from(room.participants.values()),
      };

      client.emit('room_created', {
        success: true,
        roomCode: room.roomCode,
        room: serializedRoom,
      });

      this.logger.log(`Room [${room.roomCode}] created by client ${client.id}`);
    } catch (error: any) {
      client.emit('error', { message: error.message });
    }
  }

  /**
   * JOIN MULTIPLAYER ROOM
   */
  @SubscribeMessage('join_room')
  handleJoinRoom(@ConnectedSocket() client: Socket, @MessageBody() dto: JoinRoomDto) {
    try {
      const room = this.roomsService.joinRoom(dto, client.id);
      client.join(dto.roomCode);

      const serializedRoom = {
        ...room,
        participants: Array.from(room.participants.values()),
      };

      // Notify the joining player
      client.emit('room_joined', {
        success: true,
        room: serializedRoom,
      });

      // Broadcast to all other players in the room
      client.to(dto.roomCode).emit('player_joined', {
        newPlayer: dto.character,
        room: serializedRoom,
      });

      this.logger.log(`Player [${dto.character.name}] joined room ${dto.roomCode}`);
    } catch (error: any) {
      client.emit('error', { message: error.message });
    }
  }

  /**
   * SUBMIT PLAYER ACTION (Processed by Gemini AI Game Master & Broadcast in Real-Time)
   */
  @SubscribeMessage('submit_action')
  async handleSubmitAction(@ConnectedSocket() client: Socket, @MessageBody() dto: SubmitActionDto) {
    try {
      this.logger.log(`Action received in room ${dto.roomCode}: "${dto.actionText}"`);

      // Notify all players that the AI Game Master is thinking
      this.server.to(dto.roomCode).emit('gm_thinking', {
        actingCharacterId: dto.characterId,
        actionType: dto.actionType,
      });

      // Process with AI Game Master
      const result = await this.roomsService.processAction(dto);

      const serializedRoom = {
        ...result.room,
        participants: Array.from(result.room.participants.values()),
      };

      // Broadcast the cinematic outcome instantly to all participants via WebSocket
      this.server.to(dto.roomCode).emit('turn_resolved', {
        turnResult: result.turnResult,
        updatedRoom: serializedRoom,
      });
    } catch (error: any) {
      this.logger.error(`Error resolving turn for room ${dto.roomCode}`, error);
      client.emit('error', { message: error.message });
    }
  }

  /**
   * COMBINE ITEMS (Alchemy System generating permanent Unique Items)
   */
  @SubscribeMessage('alchemy_combine')
  async handleAlchemyCombine(@ConnectedSocket() client: Socket, @MessageBody() dto: CombineAlchemyDto) {
    try {
      const result = await this.roomsService.combineAlchemy(dto);

      // Notify the player with their new permanent unique item
      client.emit('alchemy_success', {
        craftedItem: result.craftedUnique,
        inventory: result.updatedInventory,
      });

      // Notify other players about the legendary synthesis
      client.to(dto.roomCode).emit('party_alchemy_event', {
        characterId: dto.characterId,
        craftedItemName: result.craftedUnique.name,
      });
    } catch (error: any) {
      client.emit('error', { message: error.message });
    }
  }
}
