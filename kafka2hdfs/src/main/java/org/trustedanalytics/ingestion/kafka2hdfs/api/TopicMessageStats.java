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
package org.trustedanalytics.ingestion.kafka2hdfs.api;

import org.trustedanalytics.ingestion.kafka2hdfs.core.ConsumingTask;

public class TopicMessageStats {

    private String topic;
    private Long consumedMessages;

    public TopicMessageStats(String topic, Long consumedMessages) {
        this.topic = topic;
        this.consumedMessages = consumedMessages;
    }

    public String getTopic() {
        return topic;
    }

    public Long getConsumedMessages() {
        return consumedMessages;
    }

    public static TopicMessageStats fromConsumingTask(ConsumingTask task) {
        return new TopicMessageStats(task.getTopic(), task.consumedMessagesCount());
    }
}
