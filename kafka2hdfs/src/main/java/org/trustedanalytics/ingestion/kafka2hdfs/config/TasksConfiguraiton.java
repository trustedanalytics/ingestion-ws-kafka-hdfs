/**
 * Copyright (c) 2016 Intel Corporation
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

import org.trustedanalytics.ingestion.kafka2hdfs.core.ConsumingTask;
import org.trustedanalytics.ingestion.kafka2hdfs.hdfs.ToHdfsStreamConsumer;
import org.trustedanalytics.utils.hdfs.HdfsConfig;

import kafka.consumer.KafkaStream;
import org.apache.hadoop.fs.FileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class TasksConfiguraiton {

    public final static String FOLDER = "from_kafka/";

    @Autowired
    private HdfsConfig hdfsConfig;

    @Resource(name = "kafkaStreams")
    private Map<String, KafkaStream<byte[], byte[]>> kafkaStreams;

    @Bean
    public List<ConsumingTask> tasks() {
        FileSystem fs = hdfsConfig.getFileSystem();
        return kafkaStreams.entrySet().stream()
                .map(entry -> new ConsumingTask(
                        entry.getKey(),
                        entry.getValue(),
                        new ToHdfsStreamConsumer(fs, FOLDER + entry.getKey())
                ))
                .collect(Collectors.toList());
    }
}
