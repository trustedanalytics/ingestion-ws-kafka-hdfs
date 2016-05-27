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
package org.trustedanalytics.ingestion.kafka2hdfs.threading;

import org.trustedanalytics.ingestion.kafka2hdfs.core.ConsumingTask;
import org.trustedanalytics.ingestion.kafka2hdfs.utils.ThrowingConsumer;

import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class TaskRunner {

    private final static Logger LOGGER = LoggerFactory.getLogger(TaskRunner.class);

    private ExecutorService executor;
    private List<ConsumingTask> tasks;

    public TaskRunner(List<ConsumingTask> tasks) {
        this.tasks = tasks;
        executor = Executors.newFixedThreadPool(tasks.size());
    }

    private Runnable taskToRunnable(ConsumingTask task) {
        return () -> safeConsumer(ConsumingTask::start).accept(task);
    }

    public void start() {
        LOGGER.info("Starting tasks");
        tasks.forEach(task -> executor.submit(taskToRunnable(task)));
    }

    public void shutdown() throws InterruptedException {
        LOGGER.info("Stopping tasks");
        tasks.forEach(safeConsumer(ConsumingTask::markForStop));
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        LOGGER.info("Finished stopping tasks");
    }

    private Consumer<ConsumingTask> safeConsumer(ThrowingConsumer<ConsumingTask> consumer) {
        return task -> {
            try {
                consumer.consume(task);
            } catch (Exception e) {
                LOGGER.error("FAILED executing task: {}", task, e);
                Throwables.propagate(e);
            }
        };
    }
}
