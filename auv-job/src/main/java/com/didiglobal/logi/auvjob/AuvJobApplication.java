package com.didiglobal.logi.auvjob;

import com.didiglobal.logi.auvjob.core.SimpleWorkerFactory;
import com.didiglobal.logi.auvjob.core.Worker;
import com.didiglobal.logi.auvjob.core.WorkerFactory;

import java.io.IOException;

/**
 * @author dengshan
 */
public class AuvJobApplication {

  public static void main(String[] args) throws IOException {
    final WorkerFactory workerFactory = new SimpleWorkerFactory();
    final Worker worker = workerFactory.instantiate();
    worker.startup();
  }
}
