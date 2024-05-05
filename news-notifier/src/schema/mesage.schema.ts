import { Schema, Prop, SchemaFactory } from '@nestjs/mongoose';

@Schema()
export class Message {
  @Prop({ required: true })
  message_id: string;

  @Prop({ required: true })
  user_id: string;

  @Prop({ default: false })
  isRead: boolean;
}

export const MessageSchema = SchemaFactory.createForClass(Message);
