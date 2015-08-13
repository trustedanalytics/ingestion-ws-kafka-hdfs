/**
 * Copyright (c) 2015 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trustedanalytics.ingestion.kafka2hdfs.config;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Configuration
public class KafkaConfiguration {

    @Autowired
    private Environment env;

    @Value("${zookeeper}")
    private String zookeeper;

    @Value("${consumer.group}")
    private String consumerGroup;

    private List<String> trackedTopics() {
        String topics = env.getProperty("TOPICS");
        // TODO: could be list in form: topic1:4,topic2:3
        // where number stands for number of partitions
        return Arrays.stream(topics.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    @Bean
    public ConsumerConfig consumerConfig() {
        Properties props = new Properties();
        props.put("zookeeper.connect", zookeeper);
        props.put("group.id", consumerGroup);
        props.put("consumer.timeout.ms", "1000"); // it will throw ConsumerTimeoutException
        props.put("auto.offset.reset", "smallest"); // when there is no (valid) offset
        props.put("zookeeper.session.timeout.ms", "1000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        return new ConsumerConfig(props);
    }

    @Bean(destroyMethod = "shutdown")
    public ConsumerConnector consumer(ConsumerConfig consumerConfig) {
        return Consumer.createJavaConsumerConnector(consumerConfig);
    }

    @Bean
    public Map<String, KafkaStream<byte[], byte[]>> kafkaStreams(ConsumerConnector consumer) {
        Map<String, Integer> topicsCountMap = trackedTopics().stream()
                .collect(Collectors.toMap(topic -> topic, topic -> 1));
        Map<String, List<KafkaStream<byte[], byte[]>>> kafkaStreams =
                consumer.createMessageStreams(topicsCountMap);
        return kafkaStreams.keySet().stream()
                .collect(Collectors.toMap(
                        topic -> topic,
                        topic -> kafkaStreams.get(topic).get(0)));
    }

}
