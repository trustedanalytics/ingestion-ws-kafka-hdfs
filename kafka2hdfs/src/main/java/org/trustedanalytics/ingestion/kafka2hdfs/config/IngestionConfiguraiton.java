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
import org.trustedanalytics.ingestion.kafka2hdfs.threading.TaskRunner;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.trustedanalytics.utils.hdfs.EnableHdfs;

import javax.annotation.Resource;
import java.util.List;

@Configuration
@EnableHdfs
public class IngestionConfiguraiton {

    @Resource(name = "tasks")
    private List<ConsumingTask> tasks;

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public TaskRunner taskRunner() {
        return new TaskRunner(tasks);
    }

}
