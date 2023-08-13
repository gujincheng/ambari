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
package org.apache.ambari.logsearch.converter;

import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.solr.core.DefaultQueryParser;
import org.springframework.data.solr.core.query.SimpleQuery;

import static org.junit.Assert.assertEquals;

public class StringFieldFacetQueryConverterTest extends AbstractRequestConverterTest {
  private StringFieldFacetQueryConverter underTest;

  @Before
  public void setUp() {
    underTest = new StringFieldFacetQueryConverter();
  }

  @Test
  public void testConvert() {
    // GIVEN
    String field = "myfield";
    // WHEN
    SimpleQuery simpleQuery = underTest.convert(field);
    SolrQuery queryResult = new DefaultQueryParser().doConstructSolrQuery(simpleQuery);
    // THEN
    assertEquals("?q=*%3A*&rows=0&facet=true&facet.mincount=1&facet.limit=-1&facet.field=myfield",
      queryResult.toQueryString());
  }
}
