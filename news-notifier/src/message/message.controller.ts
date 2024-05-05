import {
  Controller,
  Put,
  Param,
  Post,
  Body,
  Get,
  Headers,
  UseGuards,
} from '@nestjs/common';
import jwtDecode from 'jwt-decode';
import { MessageService } from './message.service';
import { Message } from 'src/schema/mesage.schema';
import { KafkaService } from '../kafka/kafka.service';
import { JwtAuthGuard } from '../guards/jwt-auth.guard';

@Controller('messages')
export class MessageController {
  constructor(
    private readonly messageService: MessageService,
    private readonly kafkaService: KafkaService,
  ) {}
  @Get('/all')
  async getAllUserMessages(
    @Headers('authorization') authHeader: string,
  ): Promise<any[]> {
    const token = authHeader.split(' ')[1]; // Récupérez le token JWT depuis l'en-tête Authorizat const decodedToken = jwt_decode(token); // Décodez le token JWT pour récupérer les informations de l'utilisateur
    // Décodez le token JWT pour récupérer les informations de l'utilisateur
    const decodedToken: { [key: string]: any } = jwtDecode(token);
    const userId = decodedToken['preferred_username']; // Décodez le token JWT pour récupérer les informations de l'utilisateur
    // Vérifiez si l'utilisateur existe dans votre système
    // const user = await this.usersService.findOneByUsername(decodedToken.username);
    // if (!user) {
    //    throw new HttpException('User not found', HttpStatus.NOT_FOUND);
    //  }
    // Récupérez tous les messages de l'utilisateur
    const messages = await this.messageService.findAllBy(token);

    return messages;
  }
  @Get('/notif')
  async getAllUserNotif(
    @Headers('authorization') authHeader: string,
  ): Promise<any[]> {
    console.log('1');
    const token = authHeader.split(' ')[1]; // Récupérez le token JWT depuis l'en-tête Authorization
    const decodedToken: { [key: string]: any } = jwtDecode(token);
    const userId = decodedToken['preferred_username']; // Décodez le token JWT pour récupérer les informations de l'utilisateur

    // Vérifiez si l'utilisateur existe dans votre système
    // const user = await this.usersService.findOneByUsername(decodedToken.username);
    // if (!user) {
    //    throw new HttpException('User not found', HttpStatus.NOT_FOUND);
    //  }
    // Récupérez tous les messages de l'utilisateur
    const messages = await this.messageService.findNotif(token);

    return messages;
  }

  @Get(':topic')
  async getMessagesByTopic(@Param('topic') topic: string): Promise<string[]> {
    const messages = await this.kafkaService.fetchMessages(topic);
    return messages;
  }

  @Post()
  async create(
    @Headers('authorization') authHeader: string,
    @Body() message: any,
  ) {
    const token = authHeader.split(' ')[1]; // Récupérez le token JWT depuis l'en-tête Authorization
    const decodedToken: { [key: string]: any } = jwtDecode(token);
    const userId = decodedToken['preferred_username'];
    const messages = new Message();
    messages.message_id = message.message_id;
    messages.user_id = userId;
    messages.isRead = message.isRead;

    await this.messageService.saveMessage(messages);
  }
}
