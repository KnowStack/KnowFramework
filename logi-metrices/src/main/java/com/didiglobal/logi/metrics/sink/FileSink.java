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

package com.didiglobal.logi.metrics.sink;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import com.didiglobal.logi.metrics.MetricsTag;
import org.apache.commons.configuration.SubsetConfiguration;

import com.didiglobal.logi.metrics.Metric;
import com.didiglobal.logi.metrics.MetricsException;
import com.didiglobal.logi.metrics.MetricsRecord;
import com.didiglobal.logi.metrics.MetricsSink;

/**
 * 
 */
public class FileSink implements MetricsSink {

    private static final String FILENAME_KEY = "filename";
    private PrintWriter         writer;

    @Override
    public void init(SubsetConfiguration conf) {
        String filename = conf.getString(FILENAME_KEY);
        try {
            writer = filename == null ? new PrintWriter(new BufferedOutputStream(System.out))
                : new PrintWriter(new FileWriter(new File(filename), true));
        } catch (Exception e) {
            throw new MetricsException("Error creating " + filename, e);
        }
    }

    @Override
    public void putMetrics(MetricsRecord record) {
        writer.print(record.timestamp());
        writer.print(" ");
        writer.print(record.context());
        writer.print(".");
        writer.print(record.name());
        String separator = ": ";
        for (MetricsTag tag : record.tags()) {
            writer.print(separator);
            separator = ", ";
            writer.print(tag.name());
            writer.print("=");
            writer.print(String.valueOf(tag.value()));
        }
        for (Metric metric : record.metrics()) {
            writer.print(separator);
            separator = ", ";
            writer.print(metric.name());
            writer.print("=");
            writer.print(metric.value());
        }
        writer.println();
    }

    @Override
    public void flush() {
        writer.flush();
    }

}
