"""
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
"""

import os

from resource_management.core.exceptions import Fail
from stacks.utils.RMFTestCase import RMFTestCase

import unittest

class TestJdbcDriverConfig(RMFTestCase):
  STACK_VERSION = "2.6"
  CONFIG_DIR = os.path.join(os.path.dirname(__file__), '../configs')

  def test_jdbc_type_0_12_0_2_0(self):
    self.executeScript("HIVE/0.12.0.2.0/package/scripts/hive_server.py",
                       classname="HiveServer",
                       command="configure",
                       target=RMFTestCase.TARGET_COMMON_SERVICES,
                       stack_version=self.STACK_VERSION,
                       config_file=os.path.join(self.CONFIG_DIR, "hive_default.json"))

  def test_unsupported_jdbc_type_throws_error_0_12_0_2_0(self):
    try:
      self.executeScript("HIVE/0.12.0.2.0/package/scripts/hive_server.py",
                       classname="HiveServer",
                       command="configure",
                       target=RMFTestCase.TARGET_COMMON_SERVICES,
                       stack_version=self.STACK_VERSION,
                       config_file=os.path.join(self.CONFIG_DIR, "hive_unsupported_jdbc_type.json"))
      self.fail("Expected 'Fail', but call completed without throwing")
    except Fail as e:
      pass
    except Exception as e:
      self.fail("Expected 'Fail', got {}".format(e))

