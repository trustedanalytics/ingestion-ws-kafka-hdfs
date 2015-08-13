# WebSockets to Kafka

This simple Python application is a WebSocket server that will put all incomming messages into Kafka topic. Topic is selected by the URI path, so if you connect to *ws://localhost:12345/someTopic* all your messages will go into topic ```someTopic```. *ws://* results in unencrypted communication and works well localy. When deployed you should use *wss://* as it uses encryption and *ws://* might not be allowed.

If you need more advanced system then probably go for [Gateway](https://github.com/trustedanalytics/gateway) as **this project currently doesn't support any authorization**.

## Deployment

There is already maninfest that you could use. Just review it and run:

```sh
cf push
```
It will deploy all Python files which is not needed in this case.


## Running it localy

Install requirements (we recommend using Virtualenv for this):

```sh
pip install -r requirements.txt
```

and run:
```sh
KAFKA=url_to_kafka python -m ws_server
```

If you don't provide KAFKA environment variable then all messages will be printed into stdout.

## Sending messages

Currently there is a problem in Cloud Foundry with routing WebSocket connections. It will work only with encrypted ones (WSS). This is why using provided ```ws_sender.py``` might work only localy. More advanced is [wscat](https://www.npmjs.com/package/wscat) which seems to work fine. Taken this all into account you just run:

```sh
wscat -c wss://name_of_ws2kafka_app.rest_of_domain.com/myFavouriteKafkaTopic
```

and you are able to send messages that will be pushed to Kafka topic *myFavouriteKafkaTopic*.


## TODOs

* authorization
* proper logging instead of prints


