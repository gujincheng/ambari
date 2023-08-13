/**
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

package org.apache.ambari.view;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Map;

/**
 * Provider of a URL connection.
 */
public interface URLConnectionProvider {
  /**
   * Get the HttpURLConnection specified by the given URL spec.
   *
   * @param spec           the String to parse as a URL
   * @param requestMethod  the HTTP method (GET,POST,PUT,etc.).
   * @param body           the body of the request; may be null
   * @param headers        the headers of the request; may be null
   *
   * @return the HttpURLConnection to read the input stream from
   *
   * @throws java.io.IOException if an error occurred connecting to the server
   */
  public HttpURLConnection getConnection(String spec, String requestMethod, String body, Map<String, String> headers)
      throws IOException;

  /**
   * Get the HttpURLConnection specified by the given URL spec.
   *
   * @param spec           the String to parse as a URL
   * @param requestMethod  the HTTP method (GET,POST,PUT,etc.).
   * @param body           the body of the request; may be null
   * @param headers        the headers of the request; may be null
   *
   * @return the HttpURLConnection to read the input stream from
   *
   * @throws IOException if an error occurred connecting to the server
   */
  public HttpURLConnection getConnection(String spec, String requestMethod, InputStream body, Map<String, String> headers)
      throws IOException;

  /**
   * Get the HttpURLConnection specified by the
   * given URL spec as the given user.  This method sets the
   * "doAs" user header to impersonate the user over the request.
   *
   * @param spec           the String to parse as a URL
   * @param requestMethod  the HTTP method (GET,POST,PUT,etc.).
   * @param body           the body of the request; may be null
   * @param headers        the headers of the request; may be null
   * @param userName       the "doAs" user name
   *
   * @return the HttpURLConnection to read the input stream from
   *
   * @throws IOException if an error occurred connecting to the server
   */
  public HttpURLConnection getConnectionAs(String spec, String requestMethod, String body, Map<String, String> headers,
                                           String userName)
      throws IOException;

  /**
   * Get the HttpURLConnection specified by the given URL
   * spec as the given user.  This method sets the
   * "doAs" user header to impersonate the user over the request.
   *
   * @param spec           the String to parse as a URL
   * @param requestMethod  the HTTP method (GET,POST,PUT,etc.).
   * @param body           the body of the request; may be null
   * @param headers        the headers of the request; may be null
   * @param userName       the "doAs" user name
   *
   * @return the HttpURLConnection to read the input stream from
   *
   * @throws IOException if an error occurred connecting to the server
   */
  public HttpURLConnection getConnectionAs(String spec, String requestMethod, InputStream body, Map<String, String> headers,
                                           String userName) throws IOException;

  /**
   * Get the HttpURLConnection specified by the given URL
   * spec as the current user.  This method sets the "doAs" user header to impersonate
   * the user over the request.
   *
   * @param spec           the String to parse as a URL
   * @param requestMethod  the HTTP method (GET,POST,PUT,etc.).
   * @param body           the body of the request; may be null
   * @param headers        the headers of the request; may be null
   *
   * @return the HttpURLConnection to read the input stream from
   *
   * @throws IOException if an error occurred connecting to the server
   */
  public HttpURLConnection getConnectionAsCurrent(String spec, String requestMethod, String body, Map<String, String> headers)
      throws IOException;

  /**
   * Get the HttpURLConnection specified by the given URL spec
   * as the current user.  This method sets the "doAs" user header to impersonate
   * the user over the request.
   *
   * @param spec           the String to parse as a URL
   * @param requestMethod  the HTTP method (GET,POST,PUT,etc.).
   * @param body           the body of the request; may be null
   * @param headers        the headers of the request; may be null
   *
   * @return the HttpURLConnection to read the input stream from
   *
   * @throws IOException if an error occurred connecting to the server
   */
  public HttpURLConnection getConnectionAsCurrent(String spec, String requestMethod, InputStream body, Map<String, String> headers)
      throws IOException;
}
