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
package org.trustedanalytics.ingestion.kafka2hdfs.core;

import kafka.consumer.ConsumerTimeoutException;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.Iterator;

import java.util.concurrent.atomic.AtomicLong;

public class ConsumingTask {

    private final static Logger LOGGER = LoggerFactory.getLogger(ConsumingTask.class);

    private final String topic;
    private final StreamConsumer streamConsumer;
    private final KafkaStream<byte[], byte[]> kafkaStream;

    private final AtomicLong consumedMessagesCount;
    private volatile boolean canRun;

    public ConsumingTask(String topic,
                         KafkaStream<byte[], byte[]> kafkaStream,
                         StreamConsumer streamConsumer) {
        this.topic = topic;
        this.streamConsumer = streamConsumer;
        this.kafkaStream = kafkaStream;
        consumedMessagesCount = new AtomicLong();
        canRun = true;
    }

    public void markForStop() {
        LOGGER.info("Marking for stop {}", this);
        canRun = false;
    }

    public void start() throws Exception {
        LOGGER.info("Initializing {}", this);
        streamConsumer.init();
        try {
            LOGGER.info("Getting kafka iterator {}", this);
            loopProcessing(kafkaStream.iterator());
        } finally {
            LOGGER.info("Started cleanup {}", this);
            streamConsumer.cleanUp();
        }
    }

    private void loopProcessing(Iterator<MessageAndMetadata<byte[], byte[]>> it) throws Exception {
        LOGGER.info("Starting processing {}", this);
        while (canRun) {
            try {
                while (canRun && it.hasNext() && canRun) {
                    // ^^^ I know that it looks stupid but I think it makes sense :)
                    processMessage(it.next());
                }
            } catch (ConsumerTimeoutException ex) {
                // We don't do anything here on purpose
            }
        }
        LOGGER.info("Done processing {}", this);
    }

    private void processMessage(MessageAndMetadata<byte[], byte[]> messageAndMetadata) throws Exception {
        byte[] message = messageAndMetadata.message();
        streamConsumer.consumeMessage(message);
        consumedMessagesCount.incrementAndGet();
    }

    public long consumedMessagesCount() {
        return consumedMessagesCount.get();
    }

    public String getTopic() {
        return topic;
    }

    @Override
    public String toString() {
        return String.format("ConsumingTask(%s)", topic);
    }
}
