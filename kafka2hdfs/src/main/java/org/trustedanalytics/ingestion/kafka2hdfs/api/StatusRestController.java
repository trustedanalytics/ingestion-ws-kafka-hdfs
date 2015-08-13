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
package org.trustedanalytics.ingestion.kafka2hdfs.api;

import org.trustedanalytics.ingestion.kafka2hdfs.core.ConsumingTask;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/status")
public class StatusRestController {

    @Resource(name = "tasks")
    private List<ConsumingTask> consumingTasks;

    @RequestMapping(value = "/stats", method = RequestMethod.GET)
    @ResponseBody
    public List<TopicMessageStats> allStats() {
        return consumingTasks.stream()
                .map(TopicMessageStats::fromConsumingTask)
                .collect(Collectors.toList());
    }
}
