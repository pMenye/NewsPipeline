import { Module } from '@nestjs/common';
import { MessageController } from './message/message.controller';
import { MessageService } from './message/message.service';
import { MongooseModule } from '@nestjs/mongoose';
import { Message, MessageSchema } from './schema/mesage.schema';
import { MessageModule } from './message/message.module';
//import { WebsocketGatewayGateway } from './websocket-gateway/websocket-gateway.gateway';
import { KafkaService } from './kafka/kafka.service';
import { KafkaModule } from './kafka/kafka.module';
import { WebsocketGatewayGateway } from './websocket-gateway/websocket-gateway.gateway';

@Module({
  imports: [
    KafkaModule,
    MongooseModule.forRoot(process.env.MONGO_URI),
    MongooseModule.forFeature([{ name: Message.name, schema: MessageSchema }]),
    MessageModule,
  ],
  controllers: [],
  providers: [WebsocketGatewayGateway, MessageService, KafkaService],
})
export class AppModule {}
