import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { RoomsGateway } from './rooms/rooms.gateway';
import { RoomsService } from './rooms/rooms.service';
import { GeminiMasterService } from './ai/gemini-master.service';
import { AlchemyService } from './alchemy/alchemy.service';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
    }),
  ],
  providers: [RoomsGateway, RoomsService, GeminiMasterService, AlchemyService],
})
export class AppModule {}
