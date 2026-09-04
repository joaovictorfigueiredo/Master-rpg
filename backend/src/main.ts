import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { Logger } from '@nestjs/common';

async function bootstrap() {
  const logger = new Logger('Bootstrap');
  const app = await NestFactory.create(AppModule);

  app.enableCors({
    origin: '*',
    credentials: true,
  });

  const port = process.env.PORT || 3000;
  await app.listen(port);
  logger.log(`RPG AI Game Master Real-Time Server running on http://localhost:${port}`);
  logger.log(`WebSocket Gateway active at ws://localhost:${port}/game`);
}
bootstrap();
