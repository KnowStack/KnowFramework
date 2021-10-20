package com.didiglobal.logi.elasticsearch.client.response.model.ingest;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;
import java.util.Map;

/**
 * author weizijun
 * date：2019-11-01
 */
public class PipelineStats {
    private long count;
    private long time_in_millis;
    private long current;
    private long failed;

    @JSONField(name = "processors")
    private List<Map<String, IngestProcessorStats>> processors;

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getTime_in_millis() {
        return time_in_millis;
    }

    public void setTime_in_millis(long time_in_millis) {
        this.time_in_millis = time_in_millis;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public long getFailed() {
        return failed;
    }

    public void setFailed(long failed) {
        this.failed = failed;
    }

    public List<Map<String, IngestProcessorStats>> getProcessors() {
        return processors;
    }

    public void setProcessors(List<Map<String, IngestProcessorStats>> processors) {
        this.processors = processors;
    }
}
