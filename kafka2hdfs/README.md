# Kafka to HDFS

This CF application listens on predefined list of Kafka topics and writes their content into files on HDFS. Content of one topic gets written into one file named after the topic. Those files are created in folder ```from_kafka``` in path that is given in HDFS URI.


## Building

You can build deplyable jar with:

```sh
./gradlew assemble
```

## Deploying

You need two service-instances:

- zookeeper - Zookeper is used to recievie Kafka urls and it stores offset for topics that you read from
- hdfs - broker will create dedicated folder for you and will provide all needed parameters (NameNode, URI, etc.)

There is sample CF manifest in file **manifest.yml** that assumes that those services are named **zookeeper-inst** and **hdfs-inst**.

You also need to specify topics that you would like to listen on. You do this by setting environment variable TOPICS. You can provide many of them, just separate themy with comma and don't add any spaces.

Also you should set variable **CONSUMER_GROUP** into some unique Kafka consumer group. If someone else uses the same value only one of you will recieve messages.

We suggest that you just modify **manifest.yml** to suit your needs and run:

```sh
cf push
```


## Running it locally

This project uses [hadoop-spring-utils](https://github.com/trustedanalytics/hadoop-spring-utils) and so it inherits its HDFS configuration options (see its README).

You can run **kafka2hdfs** application locally if you have access to Kafka. In case of default local Kafka setup you can run this app with:

```sh
SPRING_PROFILES_ACTIVE=hdfs-local,local HDFS_USER=hdfs HDFS_URI=hdfs://localhost:8020/some/hdfs/path/to/destination/folder ZOOKEEPER=localhost:2181 CONSUMER_GROUP=some_consumer_group KAFKA=localhost:9092 TOPICS=someTopicA,someTopicB ./gradlew clean bootRun
```


## Monitoring

You can make REST call to see what topics are tracked and how many messages were consumed since application start. In case of local deployment just call:

```sh
curl http://localhost:8080/status/stats
```

## TODOS

* Currently messages are separated by new line chars. It could be changed so that this is default, but you can also specify it using environment variable, like **MESSAGE_SEPARATOR**. That way you could set it to be empty and have another way for uploading files :)
* Parametrize destination folder

