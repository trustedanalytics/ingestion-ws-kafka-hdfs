#
# Copyright (c) 2016 Intel Corporation
#
# Licensed under the Apache License, Version 2.0 (the 'License');
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an 'AS IS' BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

"""
This scripts automates deployment of kafka2hdfs application
(creates required service instances, configures environment variables).
"""

from app_deployment_helpers import cf_cli
from app_deployment_helpers import cf_helpers

APP_NAME = "kafka2hdfs"

PARSER = cf_helpers.get_parser(APP_NAME)
PARSER.add_argument('-t','--topics', type=str, required=True, help='Topics to listen on (separated with comas, without spaces)')
PARSER.add_argument('-cg','--consumer_group', type=str, required=True, help='Unique Kafka consumer group')

ARGS = PARSER.parse_args()

CF_INFO = cf_helpers.get_info(ARGS)
cf_cli.login(CF_INFO)

cf_cli.create_service('zookeeper', 'shared', 'zookeeper-inst')
cf_cli.create_service('hdfs', 'shared', 'hdfs-inst')
cf_cli.create_service('kerberos', 'shared', 'kerberos-inst')

PROJECT_DIR = ARGS.project_dir if ARGS.project_dir else \
    cf_helpers.get_project_dir()

#running script to prepare deployable jar
cf_cli.run_command(['./gradlew', 'check', 'assemble'], work_dir=PROJECT_DIR)

cf_helpers.push(work_dir=PROJECT_DIR, options="{0} -n {0} --no-start".format(ARGS.app_name))

cf_cli.set_env(APP_NAME, "TOPICS", ARGS.topics)
cf_cli.set_env(APP_NAME, "CONSUMER_GROUP", ARGS.consumer_group)

cf_cli.start(APP_NAME)

