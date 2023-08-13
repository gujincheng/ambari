/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.ambari.logsearch.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeListResponse extends SearchResponse {

  @ApiModelProperty
  protected List<NodeData> vNodeList = new ArrayList<NodeData>();

  public List<NodeData> getvNodeList() {
    return vNodeList;
  }

  public void setvNodeList(List<NodeData> vNodeList) {
    this.vNodeList = vNodeList;
  }

  @Override
  public int getListSize() {
    if (vNodeList == null) {
      return 0;
    }
    return vNodeList.size();
  }
}
