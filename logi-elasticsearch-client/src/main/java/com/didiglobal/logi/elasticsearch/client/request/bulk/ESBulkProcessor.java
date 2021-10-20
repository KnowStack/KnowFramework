package com.didiglobal.logi.elasticsearch.client.request.bulk;

import com.didiglobal.logi.elasticsearch.client.ESClient;
import com.didiglobal.logi.elasticsearch.client.request.batch.BatchNode;
import com.didiglobal.logi.elasticsearch.client.request.batch.BatchType;
import com.didiglobal.logi.elasticsearch.client.request.batch.ESBatchRequest;
import com.didiglobal.logi.elasticsearch.client.response.batch.ESBatchResponse;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.util.concurrent.EsExecutors;
import org.elasticsearch.common.util.concurrent.FutureUtils;

import java.io.Closeable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;


public class ESBulkProcessor implements Closeable {


    public interface Listener {


        void beforeBulk(long executionId, ESBatchRequest request);


        void afterBulk(long executionId, ESBatchRequest request, ESBatchResponse response);


        void afterBulk(long executionId, ESBatchRequest request, Throwable failure);
    }


    public static class Builder {

        private final ESClient client;
        private final Listener listener;

        private String name;
        private int concurrentRequests = 1;
        private int bulkActions = 1000;
        private ByteSizeValue bulkSize = new ByteSizeValue(5, ByteSizeUnit.MB);
        private TimeValue flushInterval = null;
        private BackoffPolicy backoffPolicy = BackoffPolicy.exponentialBackoff();


        public Builder(ESClient client, Listener listener) {
            this.client = client;
            this.listener = listener;
        }


        public ESBulkProcessor.Builder setName(String name) {
            this.name = name;
            return this;
        }


        public ESBulkProcessor.Builder setConcurrentRequests(int concurrentRequests) {
            this.concurrentRequests = concurrentRequests;
            return this;
        }


        public ESBulkProcessor.Builder setBulkActions(int bulkActions) {
            this.bulkActions = bulkActions;
            return this;
        }


        public ESBulkProcessor.Builder setBulkSize(ByteSizeValue bulkSize) {
            this.bulkSize = bulkSize;
            return this;
        }


        public ESBulkProcessor.Builder setFlushInterval(TimeValue flushInterval) {
            this.flushInterval = flushInterval;
            return this;
        }


        public ESBulkProcessor.Builder setBackoffPolicy(BackoffPolicy backoffPolicy) {
            if (backoffPolicy == null) {
                throw new NullPointerException("'backoffPolicy' must not be null. To disable backoff, pass BackoffPolicy.noBackoff()");
            }
            this.backoffPolicy = backoffPolicy;
            return this;
        }


        public ESBulkProcessor build() {
            return new ESBulkProcessor(client, backoffPolicy, listener, name, concurrentRequests, bulkActions, bulkSize, flushInterval);
        }
    }

    public static ESBulkProcessor.Builder builder(ESClient client, Listener listener) {
        if (client == null) {
            throw new NullPointerException("The client you specified while building a ESBulkProcessor is null");
        }

        return new ESBulkProcessor.Builder(client, listener);
    }

    private final int bulkActions;
    private final long bulkSize;


    private final ScheduledThreadPoolExecutor scheduler;
    private final ScheduledFuture scheduledFuture;

    private final AtomicLong executionIdGen = new AtomicLong();

    private ESBatchRequest bulkRequest;
    private final BulkRequestHandler bulkRequestHandler;

    private volatile boolean closed = false;

    ESBulkProcessor(ESClient client, BackoffPolicy backoffPolicy, Listener listener, @Nullable String name, int concurrentRequests, int bulkActions, ByteSizeValue bulkSize, @Nullable TimeValue flushInterval) {
        this.bulkActions = bulkActions;
        this.bulkSize = bulkSize.bytes();

        this.bulkRequest = new ESBatchRequest();
        this.bulkRequestHandler = (concurrentRequests == 0) ? BulkRequestHandler.syncHandler(client, backoffPolicy, listener) : BulkRequestHandler.asyncHandler(client, backoffPolicy, listener, concurrentRequests);

        if (flushInterval != null) {
            this.scheduler = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1, EsExecutors.daemonThreadFactory(Settings.EMPTY, (name != null ? "[" + name + "]" : "") + "bulk_processor"));
            this.scheduler.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
            this.scheduler.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
            this.scheduledFuture = this.scheduler.scheduleWithFixedDelay(new ESBulkProcessor.Flush(), flushInterval.millis(), flushInterval.millis(), TimeUnit.MILLISECONDS);
        } else {
            this.scheduler = null;
            this.scheduledFuture = null;
        }
    }


    @Override
    public void close() {
        try {
            awaitClose(0, TimeUnit.NANOSECONDS);
        } catch(InterruptedException exc) {
            Thread.currentThread().interrupt();
        }
    }


    public synchronized boolean awaitClose(long timeout, TimeUnit unit) throws InterruptedException {
        if (closed) {
            return true;
        }
        closed = true;
        if (this.scheduledFuture != null) {
            FutureUtils.cancel(this.scheduledFuture);
            this.scheduler.shutdown();
        }
        if (bulkRequest.numberOfActions() > 0) {
            execute();
        }
        return this.bulkRequestHandler.awaitClose(timeout, unit);
    }

    public boolean ensureFlush(long timeout, TimeUnit unit) throws InterruptedException {
        return this.bulkRequestHandler.awaitClose(timeout, unit);
    }

    public ESBulkProcessor add(BatchType batchType, String index, String type, String content) {
        return add(batchType, index, type, null, content);
    }

    public ESBulkProcessor add(BatchType batchType, String index, String type, String id, String content) {
        internalAdd(new BatchNode(batchType, index, type, id, content));
        return this;
    }

    public ESBulkProcessor add(BatchNode batchNode) {
        internalAdd(batchNode);
        return this;
    }

    boolean isOpen() {
        return closed == false;
    }

    protected void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("bulk process already closed");
        }
    }

    private synchronized void internalAdd(BatchNode request) {
        ensureOpen();
        bulkRequest.addNode(request);
        executeIfNeeded();
    }

    private void executeIfNeeded() {
        ensureOpen();
        if (!isOverTheLimit()) {
            return;
        }
        execute();
    }


    private void execute() {
        final ESBatchRequest bulkRequest = this.bulkRequest;
        final long executionId = executionIdGen.incrementAndGet();

        this.bulkRequest = new ESBatchRequest();
        this.bulkRequestHandler.execute(bulkRequest, executionId);
    }

    private boolean isOverTheLimit() {
        if (bulkActions != -1 && bulkRequest.numberOfActions() >= bulkActions) {
            return true;
        }
        if (bulkSize != -1 && bulkRequest.estimatedSizeInBytes() >= bulkSize) {
            return true;
        }
        return false;
    }


    public synchronized void flush() {
        ensureOpen();
        if (bulkRequest.numberOfActions() > 0) {
            execute();
        }
    }

    class Flush implements Runnable {

        @Override
        public void run() {
            synchronized (ESBulkProcessor.this) {
                if (closed) {
                    return;
                }
                if (bulkRequest.numberOfActions() == 0) {
                    return;
                }
                execute();
            }
        }
    }
}
