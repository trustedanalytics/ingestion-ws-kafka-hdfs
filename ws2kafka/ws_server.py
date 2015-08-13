#
# Copyright (c) 2015 Intel Corporation
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

import asyncio
import json
import os

import ws
import akafka


def get_cf_consumer(vcap):
    params = json.loads(vcap)
    uri = params['kafka'][0]['credentials']['uri']
    return akafka.KafkaWSConsumer(uri)


def get_consumer():
    vcap = os.getenv("VCAP_SERVICES")
    if vcap:  # we are in CF
        return get_cf_consumer(vcap)
    kafka_url = os.getenv("KAFKA")
    if kafka_url:
        return akafka.KafkaWSConsumer(kafka_url)
    return ws.StdOutWSConsumer()


def main():
    host = os.getenv("HOST", '0.0.0.0')
    port = os.getenv("VCAP_APP_PORT", 8765)
    consumer = get_consumer()
    print("Using consumer: {}".format(type(consumer)))
    ws.start_server(host, port, consumer)
    asyncio.get_event_loop().run_forever()


if __name__ == "__main__":
    main()
