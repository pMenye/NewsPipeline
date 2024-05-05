import { Injectable, OnModuleInit } from '@nestjs/common';
import { Kafka, Consumer } from 'kafkajs';
import { config } from 'dotenv';

config();

@Injectable()
export class KafkaService {
  private kafka: Kafka;
  private consumer;

  constructor() {
    this.kafka = new Kafka({
      clientId: process.env.KAFKA_CLIENT_ID,
      brokers: [process.env.KAFKA_BROKERS],
      retry: {
        maxRetryTime: 30000,
        retries: 10,
      },
      connectionTimeout: 60000,
    });
    this.consumer = this.kafka.consumer({
      groupId: 'my-group1',
      sessionTimeout: 60000,
    });
  }
  async fetchMessages(topic: string): Promise<string[]> {
    const messages: string[] = [];

    let isEachBatchDone = false;

    await this.consumer.connect();
    await this.consumer.subscribe({ topic: topic, fromBeginning: true });

    await this.consumer.run({
      eachBatch: async ({ batch, resolveOffset, heartbeat }) => {
        for (const message of batch.messages) {
          messages.push(message.value.toString());
          console.log(messages);
        }

        await resolveOffset(batch.messages[batch.messages.length - 1].offset);
        await heartbeat();

        isEachBatchDone = true;
      },
    });

    await new Promise<void>((resolve) => {
      const checkEachBatchDone = () => {
        if (isEachBatchDone) {
          resolve();
        } else {
          setTimeout(checkEachBatchDone, 100);
        }
      };
      checkEachBatchDone();
      // Attendre 10 secondes au maximum
      setTimeout(() => {
        if (!isEachBatchDone) {
          resolve();
        }
      }, 100000);
    });

    await this.consumer.disconnect();

    return messages;
  }
}
