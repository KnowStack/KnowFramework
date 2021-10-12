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

package com.didiglobal.logi.metrics.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.didiglobal.logi.metrics.Metric;
import com.didiglobal.logi.metrics.MetricsRecord;
import com.didiglobal.logi.metrics.MetricsTag;

/**
 * A metrics cache for sinks that don't support sparse updates.
 */
public class MetricsCache {

    private static final long         serialVersionUID = 1L;

    private final Map<String, RecMap> map              = new HashMap<String, RecMap>();

    static class RecMap extends HashMap<Collection<MetricsTag>, Record> {
        private static final long serialVersionUID = 1L;
    }

    /**
     * Cached record
     */
    public static class Record {
        final Map<String, String> tags    = new LinkedHashMap<String, String>();

        final Map<String, Metric> metrics = new LinkedHashMap<String, Metric>();

        /**
         * Get the tag value
         * @param key name of the tag
         * @return the tag value
         */
        public String getTag(String key) {
            return tags.get(key);
        }

        /**
         * Get the metric value
         * @param key name of the metric
         * @return the metric value
         */
        public Object getMetric(String key) {
            Metric metric = metrics.get(key);
            return metric != null ? metric.value() : null;
        }

        /**
         * Get the metric value
         * @param key name of the metric
         * @return the metric value
         */
        public Metric getMetricInstance(String key) {
            return metrics.get(key);
        }

        /**
         * @return entry set of metrics
         */
        public Set<Map.Entry<String, Object>> metrics() {
            Map<String, Object> map = new LinkedHashMap<String, Object>(metrics.size());
            for (Map.Entry<String, Metric> mapEntry : metrics.entrySet()) {
                map.put(mapEntry.getKey(), mapEntry.getValue().value());
            }
            return map.entrySet();
        }

        /**
         * @return entry set of metrics
         */
        public Set<Map.Entry<String, Metric>> metricsEntrySet() {
            return metrics.entrySet();
        }
    }

    /**
     * Update the cache and return the cached record
     * @param mr the update record
     * @param includingTags cache tag values (for later lookup by name) if true
     * @return the updated cached record
     */
    public Record update(MetricsRecord mr, boolean includingTags) {
        String name = mr.name();
        RecMap recMap = map.get(name);
        if (recMap == null) {
            recMap = new RecMap();
            map.put(name, recMap);
        }
        Collection<MetricsTag> tags = (Collection<MetricsTag>) mr.tags();
        Record rec = recMap.get(tags);
        if (rec == null) {
            rec = new Record();
            recMap.put(tags, rec);
        }
        for (Metric m : mr.metrics()) {
            rec.metrics.put(m.name(), m);
        }
        if (includingTags) {
            // mostly for some sinks that include tags as part of a dense schema
            for (MetricsTag t : mr.tags()) {
                rec.tags.put(t.name(), t.value());
            }
        }
        return rec;
    }

    public Record update(MetricsRecord mr) {
        return update(mr, false);
    }

    /**
     * Get the cached record
     * @param name of the record
     * @param tags of the record
     * @return the cached record or null
     */
    public Record get(String name, Collection<MetricsTag> tags) {
        RecMap tmap = map.get(name);
        if (tmap == null)
            return null;
        return tmap.get(tags);
    }
}
