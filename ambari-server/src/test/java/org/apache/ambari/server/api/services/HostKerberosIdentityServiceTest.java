/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.apache.ambari.server.api.services;


import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import org.apache.ambari.server.api.resources.ResourceInstance;
import org.apache.ambari.server.api.services.parsers.RequestBodyParser;
import org.apache.ambari.server.api.services.serializers.ResultSerializer;

/**
 * Unit tests for HostKerberosIdentity.
 */
public class HostKerberosIdentityServiceTest extends BaseServiceTest {

  public List<ServiceTestInvocation> getTestInvocations() throws Exception {
    List<ServiceTestInvocation> listInvocations = new ArrayList<>();

    //getComponent
    HostKerberosIdentityService service = new TestHostKerberosIdentityService("clusterName", "hostName", "identityId");
    Method m = service.getClass().getMethod("getKerberosIdentity", String.class, HttpHeaders.class, UriInfo.class, String.class, String.class);
    Object[] args = new Object[] {null, getHttpHeaders(), getUriInfo(), "identityId", null};
    listInvocations.add(new ServiceTestInvocation(Request.Type.GET, service, m, args, null));

    //getComponents
    service = new TestHostKerberosIdentityService("clusterName", "hostName", null);
    m = service.getClass().getMethod("getKerberosIdentities", String.class, HttpHeaders.class, UriInfo.class, String.class);
    args = new Object[] {null, getHttpHeaders(), getUriInfo(), null};
    listInvocations.add(new ServiceTestInvocation(Request.Type.GET, service, m, args, null));

    return listInvocations;
  }

  private class TestHostKerberosIdentityService extends HostKerberosIdentityService {
    private String clusterId;
    private String hostId;
    private String identityId;

    private TestHostKerberosIdentityService(String clusterId, String hostId, String identityId) {
      super(clusterId, hostId);
      this.clusterId = clusterId;
      this.hostId = hostId;
      this.identityId = identityId;
    }

    @Override
    ResourceInstance createResource(String clusterId, String hostId, String identityId) {
      assertEquals(this.clusterId, clusterId);
      assertEquals(this.hostId, hostId);
      assertEquals(this.identityId, identityId);
      return getTestResource();
    }

    @Override
    RequestFactory getRequestFactory() {
      return getTestRequestFactory();
    }

    @Override
    protected RequestBodyParser getBodyParser() {
      return getTestBodyParser();
    }

    @Override
    protected ResultSerializer getResultSerializer() {
      return getTestResultSerializer();
    }
  }
}
