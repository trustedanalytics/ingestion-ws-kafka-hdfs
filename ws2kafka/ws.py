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
import websockets


class WebSocketConsumer(object):

    def init_on_topic(self, topic):
        pass

    def consume(self, topic, msg):
        pass


class StdOutWSConsumer(WebSocketConsumer):

    def init_on_topic(self, topic):
        print("init_on_topic: " + topic)

    def consume(self, topic, msg):
        print("consume({}):{}".format(topic, msg))


@asyncio.coroutine
def sender(url, iterable):
    websocket = yield from websockets.connect(url)
    for item in iterable:
        item = str(item)
        print("WS sending: " + item)
        yield from websocket.send(item)
        print("WS sent: " + item)


def _get_acceptor(consumer):
    def acceptor(websocket, path):
        topic = path.strip('/')
        consumer.init_on_topic(topic)
        while True:
            msg = yield from websocket.recv()
            if msg is None:
                return
            print("WS got topic: {} msg: {}".format(topic, msg))
            consumer.consume(topic, msg)
    return asyncio.coroutine(acceptor)


def start_server(host, port, consumer, **kwargs):
    print("Starting WebSocket server on: {} {}".format(host, port))
    acceptor = _get_acceptor(consumer)
    start_server = websockets.serve(acceptor, port=int(port), **kwargs)
    asyncio.get_event_loop().run_until_complete(start_server)


def start_sender(url, iterable):
    print("Starting WebSocket sender on: {}".format(url))
    sender_coroutine = sender(url, iterable)
    asyncio.get_event_loop().run_until_complete(sender_coroutine)