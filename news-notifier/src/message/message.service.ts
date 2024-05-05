import { Injectable } from '@nestjs/common';
import { InjectConnection, InjectModel } from '@nestjs/mongoose';
import { MessageSchema, Message } from '../schema/mesage.schema';
import { Model, Connection } from 'mongoose';
import axios from 'axios';
import { shuffle } from 'lodash';
import jwtDecode from 'jwt-decode';

@Injectable()
export class MessageService {
  constructor(
    @InjectModel('Message') private messageModel: Model<Message>,
    @InjectConnection() private readonly connection: Connection,
  ) {}

  async findNotif(token: string): Promise<any[]> {
    const url = `http://tpea23.voltevieira.fr:8087/gateway/subscription/subscribe`;
    const headers = {
      Authorization: `Bearer ${token}`,
    };

    const response = await axios.get(url, { headers });

    //response.data.map(({ topic }) => topic);
    let messages = [];
    const decodedToken: { [key: string]: any } = jwtDecode(token);
    const userId = decodedToken['preferred_username'];
    console.log(userId);

    // if (clientTopics) {
    // Pour chaque topic enregistré pour le client connecté
    for (const topicDetail of response.data) {
      // Récupérer les notifications non lues correspondantes
      console.log(topicDetail);
      const allMsg = await this.findAllByTopic(topicDetail.topic);
      console.log(allMsg);
     
      const messagesRead = await this.getMessagesByUser(userId);
      console.log(messagesRead);
      const notification = await allMsg.filter((msg) => {
        console.log(
          new Date(msg['datePublication']) >= new Date(topicDetail.date),
          new Date(msg['datePublication']),
          new Date(topicDetail.date),
        );
        return (
          new Date(msg['datePublication']) >= new Date(topicDetail.date) &&
          !messagesRead.some((readMsg) =>
            this.compareMessageWithJson(msg, readMsg),
          )
        );
      });
      console.log(notification);
      messages = messages.concat(notification);
      //   }
    }
    return messages;
  }
  async findAllBy(token: string): Promise<any[]> {
    const url = `http://tpea23.voltevieira.fr:8087/gateway/subscription/subscribe`;
    const headers = {
      Authorization: `Bearer ${token}`,
    };

    const response = await axios.get(url, { headers });

    const clientTopics = response.data.map(({ topic }) => topic);
    // ['finance', 'tpea'];
    //response.data.map(({ topic }) => topic);
    let messages = [];

    if (clientTopics) {
      // Pour chaque topic enregistré pour le client connecté
      for (const topic of clientTopics) {
        // Récupérer les notifications non lues correspondantes
        const notifications = await this.findAllByTopic(topic);
        messages = messages.concat(notifications);
      }
    }
    return shuffle(messages);
  }
  async findAllByTopic(topic: string): Promise<any[]> {
    const collection = this.connection.collection(topic);
    const data = await collection.find({}).toArray();
    return data;
  }
  compareMessageWithJson(message: any, json: Message) {
    // Convertir la charge utile du message en un objet JSON
    // const
    //payload = JSON.parse(message);
    // Comparer les propriétés de l'objet JSON avec les propriétés de l'objet à comparer
    return message.id == json.message_id;
  }
  async saveMessage(message): Promise<Message> {
    const messageObject = new this.messageModel({
      message_id: message.message_id,
      user_id: message.user_id,
      isRead: true,
    });
    await messageObject.save();
    return messageObject;
  }

  async getMessagesByUser(userId: string): Promise<Message[]> {
    const messages = await this.messageModel
      .find({ user_id: userId })
      .select('message_id user_id isRead')
      .exec();
    console.log(messages);

    return messages;
  }
}
