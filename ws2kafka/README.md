# WebSockets to Kafka

This simple Python (3.4.1) application is a WebSocket server that will put all incomming messages into Kafka topic. Topic is selected by the URI path, so if you connect to *ws://localhost:12345/someTopic* all your messages will go into topic ```someTopic```. *ws://* results in unencrypted communication and works well localy. When deployed you should use *wss://* as it uses encryption and *ws://* might not be allowed.

If you need more advanced system then probably go for [Gateway](https://github.com/trustedanalytics/gateway) as **this project currently doesn't support any authorization**.

## Deployment

### Manual deployment
There is already maninfest that you could use. Just review it and run:

```sh
cf push
```
It will deploy all Python files which is not needed in this case.

### Automated deployment
* Switch to `deploy` directory: `cd deploy`
* Install tox: `sudo -E pip install --upgrade tox`
* Run: `tox`
* Activate virtualenv with installed dependencies: `. .tox/py27/bin/activate`
* Run deployment script: `python deploy.py` providing required parameters when running script (`python deploy.py -h` to check script parameters with their descriptions).

## Running it localy

NOTE: We recommend using [Virtualenv](https://virtualenv.pypa.io/en/latest/) for local deployment. It is a tool to create isolated Python environments.

Install requirements :

```sh
pip install -r requirements.txt
```

and run:
```sh
KAFKA=url_to_kafka python -m ws_server
```

If you don't provide KAFKA environment variable then all messages will be printed into stdout.

## Sending messages

Just run:

```sh
wscat -c ws://name_of_ws2kafka_app.rest_of_domain.com/myFavouriteKafkaTopic
```

and you are able to send messages that will be pushed to Kafka topic *myFavouriteKafkaTopic*.

## TODOs

* authorization
* proper logging instead of prints


