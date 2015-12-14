# A sample application for ingesting data through TAP and Kafka

This repository contains a sample application on how to setup ingestion pipeline on Trusted Analytics platform, as the following shows:

![](docs/ingestion_ws2kafka2hdfs.png)

* External device/system pushes data through WebSockets (WS) application that is hosted in TAP. Lets call this application **ws2kafka**.
* **ws2kafka** is pushing recieved data into Kafka. Topic is chosen based on WS URL to which the connection is made, so calls looks like: ```wss://ws2kafka.some_domain.com/topic1```. For this to work, Kafka needs to be configured to automatically create topics. **ws2kafka** is horizontally scalable so you can have multiple instances.
* **kafka2hdfs** is an application that is started with a predefined list of Kafka topics that it should track. For each topic it will ensure that corresponding file exists and append newly added data to it. Since it is not safe to do multiple concurrent appends to a single HDFS file, you should not try to have multiple instances listening on the same topic.


Instructions on how to deploy each application is included in its folder with more detailed notes. There is a small utility script ```create_service_instances.sh``` that will create service instances.

The provided **ws2kafka** application is very simple and doesn't provide authorization. If you need something more advanced or you just feel adventurous, you may consider using [Gateway](https://github.com/trustedanalytics/gateway) but note that it enforces some kafka messages format. A handy feature of this pipeline is that you can replace one part of it with your own. Their common part is Kafka and its topics.


