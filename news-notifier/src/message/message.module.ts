import { Module } from '@nestjs/common';
import { MessageController } from './message.controller';
import { MessageService } from './message.service';
import { MongooseModule } from '@nestjs/mongoose';
import { Message, MessageSchema } from '../schema/mesage.schema';
import { KafkaModule } from 'src/kafka/kafka.module';
import { KafkaService } from '../kafka/kafka.service';

@Module({
  imports: [
    MongooseModule.forFeature([{ name: Message.name, schema: MessageSchema }]),
    KafkaModule,
  ],

  controllers: [MessageController],
  providers: [MessageService, KafkaService],
})
export class MessageModule {}
