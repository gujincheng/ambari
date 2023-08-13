/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

var App = require('app');

/**
 * @class
 *
 * This is a view for showing HBase RegionServer store files
 *
 * @extends App.ChartServiceMetricsAMS_RegionServerBaseView
 * @extends Ember.Object
 * @extends Ember.View
 */
App.ChartServiceMetricsAMS_RegionServerStoreFiles = App.ChartServiceMetricsAMS_RegionServerBaseView.extend({
  id: "service-metrics-ambari-metrics-region-server-store-files",
  title: Em.I18n.t('services.service.info.metrics.ambariMetrics.regionServer.storeFiles'),
  renderer: 'line',
  ajaxIndex: 'service.metrics.ambari_metrics.region_server.store_files',

  loadGroup: {
    name: 'service.metrics.ambari_metrics.aggregated',
    fields: ['metrics/hbase/regionserver/storefiles']
  },

  regionServerName: 'storefiles',
  displayName: Em.I18n.t('services.service.info.metrics.ambariMetrics.regionServer.displayNames.storeFilesCount')
});