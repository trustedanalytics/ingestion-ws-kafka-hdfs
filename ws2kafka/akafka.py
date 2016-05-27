#
# Copyright (c) 2016 Intel Corporation
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

from kafka import KafkaClient, SimpleProducer, KafkaConsumer
from retry.api import retry_call

from ws import WebSocketConsumer


class KafkaWSConsumer(WebSocketConsumer):

    def __init__(self, url):
        print("creating KafkaClient and SimpleProducer on " + url)
        client = KafkaClient(url)
        self.producer = SimpleProducer(client)

    def init_on_topic(self, topic):
        # self.producer.client.ensure_topic_exists(topic)
        # ^^^ won't work as sending message creates the topic
        pass

    def consume(self, topic, msg):
        print("Message of length {} to Kafka({})".format(len(msg), topic))
        msg = bytes(msg, "UTF-8")
        retry_call(self.producer.send_messages, fargs=[topic, msg],
                   tries=3,delay=0.5)


def topic_iterator(kafka_url, topic, group_id='SomeDefaultGroupId'):
    return KafkaConsumer(topic, metadata_broker_list=[kafka_url],
                         group_id=group_id)

