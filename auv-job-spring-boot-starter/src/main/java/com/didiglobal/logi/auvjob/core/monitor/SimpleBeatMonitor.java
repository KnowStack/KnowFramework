package com.didiglobal.logi.auvjob.core.monitor;

import com.didiglobal.logi.auvjob.core.beat.BeatManager;
import com.didiglobal.logi.auvjob.utils.ThreadUtil;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * simple beat monitor.
 *
 * @author dengshan
 */
@Service
public class SimpleBeatMonitor implements BeatMonitor {

  private BeatManager beatManager;

  private Thread monitorThread;

  @Autowired
  public SimpleBeatMonitor(BeatManager beatManager) {
    this.beatManager = beatManager;
  }

  @Override
  public void maintain() {
    monitorThread = new Thread(new BeatMonitorThread(), "BeatMonitorThread");
    monitorThread.start();
  }

  @Override
  public void stop() {
    if (monitorThread != null && monitorThread.isAlive()) {
      monitorThread.interrupt();
    }
  }

  class BeatMonitorThread implements Runnable {
    private static final long INTERVAL = 5L;

    @Override
    public void run() {
      while (true) {
        beatManager.beat();

        // beat every INTERVAL seconds
        ThreadUtil.sleep(INTERVAL, TimeUnit.SECONDS);
      }
    }
  }
}
