#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

$newVersion = $($args[0])

if ($newVersion -ne '${defaultStackVersion}')
{
  $separator = "-"
  $option = [System.StringSplitOptions]::RemoveEmptyEntries
  $newStackParts = $newVersion.Split($separator, $option)

  $newStackVersion = $($newStackParts[0]) + "-" + $($newStackParts[1])
  $newLocalStackVersion = $($newStackParts[0]) + "Local-" + $($newStackParts[1])
  "Setting default stack to '$($newStackParts[0])' and version to '$($newStackParts[1])'"
  Get-Content public/javascripts/app.js -ReadCount 1000 | %{$_ -replace "App.defaultStackVersion.*=.*;","App.defaultStackVersion = '$newStackVersion';"} | Set-Content public/javascripts/tmp.js
  Move-Item ./public/javascripts/tmp.js ./public/javascripts/app.js -force
  Get-Content public/javascripts/app.js -ReadCount 1000 | %{$_ -replace "App.defaultLocalStackVersion.*=.*;","App.defaultLocalStackVersion = '$newLocalStackVersion';"} | Set-Content public/javascripts/tmp.js
  Move-Item ./public/javascripts/tmp.js ./public/javascripts/app.js -force
}
