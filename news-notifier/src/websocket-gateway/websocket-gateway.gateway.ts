import { MessageService } from '../message/message.service';
import { KafkaMessage } from '@nestjs/microservices/external/kafka.interface';
import {
  MessageBody,
  OnGatewayConnection,
  SubscribeMessage,
  WebSocketGateway,
  WebSocketServer,
  WsResponse,
} from '@nestjs/websockets';
import { from, fromEvent, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Server } from 'socket.io';
import { KafkaService } from '../kafka/kafka.service';
import { Message } from '../schema/mesage.schema';
import axios from 'axios';

const url = `http://tpea23.voltevieira.fr:8087/gateway/subscription/subscribe`;

@WebSocketGateway(3003, {
  cors: {
    origin: 'null',
  },
})
export class WebsocketGatewayGateway implements OnGatewayConnection {
  constructor(
    private readonly kafkaService: KafkaService,
    private readonly dbService: MessageService,
  ) {} // injection du contrôleur
  @WebSocketServer() server: Server;

  async handleConnection(client: any, ...args: any[]) {
    const response = await axios.get(url);

    const clientTopics = response.data.map(({ topic }) => topic);
    if (clientTopics) {
      // Pour chaque topic enregistré pour le client connecté
      for (const topic of clientTopics) {
        // Récupérer les notifications non lues correspondantes

        const allMsg = await this.kafkaService.fetchMessages(topic);
        console.log(allMsg);
        const messagesRead = await this.dbService.getMessagesByUser('3');
        const notification = allMsg.filter((msg) => {
          return !messagesRead.some((readMsg) =>
            this.compareMessageWithJson(msg, readMsg),
          );
        });

        if (notification) {
          // Envoyer les notifications non lues au client connecté
          notification.forEach((notif) => {
            client.emit('notification', notif);
          });
        }
      }
    }
  }

  compareMessageWithJson(message: string, json: Message) {
    // Convertir la charge utile du message en un objet JSON
    const payload = JSON.parse(message);
    // Comparer les propriétés de l'objet JSON avec les propriétés de l'objet à comparer
    return payload.id == json.message_id;
  }
}
