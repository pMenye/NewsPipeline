

## Description

[Nest](https://github.com/nestjs/nest) framework TypeScript starter repository.

## Installation


```bash
$ cd service_notification

$ npm install
```

## Running the app

```bash
# watch mode
$ npm run start:dev
```
## Endpoints
### POST /messages
acquitter un message lu
The body of the request should include the following fields:

```bash
curl -X POST -H "Content-Type: application/json" -d '{
    "message_id": "123456",
    "user_id": "johndoe",
    "isRead": true
}' http://localhost:3000/messages

```
### GET /messages/all
get all messages of topics of user
```bash
curl http://localhost:3000/messages/all


```
### GET /messages/topic
get all messages of topic 
```bash
curl http://localhost:3000/messages/finance


```
## Configuration

The following parameters can be configured using environment variables:

- `PORT` (number, default: 3000): the port on which the service will listen for requests
- `KAFKA_BROKERS` (string, required): the list of Kafka brokers to connect to
- `KAFKA_USERNAME` (string, required): the username to use when connecting to Kafka
- `KAFKA_PASSWORD` (string, required): the password to use when connecting to Kafka
- `MONGO_URI` (string, required): the URI of the MongoDB instance to connect to


## Test 
To test the websockets, you can create an index.html file as a client and use the following code snippet.
<!DOCTYPE html>
<html>
    <head>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/socket.io/4.6.1/socket.io.js"></script>
        <meta http-equiv="Access-Control-Allow-Origin" content='http://localhost:3003'>

        <script>
            const connection= io('http://localhost:3003', {
                transportOptions: {
                  polling: {
                    extraHeaders: {
                      Origin: ['http://localhost:3000','http://localhost:3003/socket.io/']
                    }
                  }
                }
              });           
             connection.on('notification', message=>{console.log(message);})
             socket.on('subscribeToNotifications', (data) => console.log(data))

        </script>
    </head>
    <body>

