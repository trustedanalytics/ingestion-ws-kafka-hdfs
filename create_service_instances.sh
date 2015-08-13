#!/bin/bash
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


set -e

# cf cs -> create service instance
# this will be used by ws2kafka
cf cs kafka shared kafka-inst

# to read from kafka we need only properly pointed zookeeper
cf cs zookeeper shared zookeeper-inst

# we need hdfs to store files
cf cs hdfs free hdfs-inst

