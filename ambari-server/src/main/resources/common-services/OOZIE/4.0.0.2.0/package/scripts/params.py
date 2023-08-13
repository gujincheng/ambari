#!/usr/bin/env python
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
from ambari_commons import OSCheck
from resource_management.libraries.functions.default import default
from resource_management.libraries.functions.expect import expect
from resource_management.libraries.functions.copy_tarball import get_sysprep_skip_copy_tarballs_hdfs

if OSCheck.is_windows_family():
  from params_windows import *
else:
  from params_linux import *

java_home = config['ambariLevelParams']['java_home']
java_version = expect("/ambariLevelParams/java_version", int)


host_sys_prepped = default("/ambariLevelParams/host_sys_prepped", False)

# By default, copy the tarballs to HDFS. If the cluster is sysprepped, then set based on the config.
sysprep_skip_copy_oozie_share_lib_to_hdfs = False
if host_sys_prepped:
  sysprep_skip_copy_oozie_share_lib_to_hdfs = default("/configurations/cluster-env/sysprep_skip_copy_oozie_share_lib_to_hdfs", False)
sysprep_skip_oozie_schema_create = host_sys_prepped and default("/configurations/cluster-env/sysprep_skip_oozie_schema_create", False)
