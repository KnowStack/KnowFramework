package com.didiglobal.logi.job.core.monitor;

import com.didiglobal.logi.job.core.beat.BeatManager;
import com.didiglobal.logi.job.utils.ThreadUtil;

import java.util.concurrent.TimeUnit;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * simple beat monitor.
 *
 * @author ds
 */
@Service
public class SimpleBeatMonitor implements BeatMonitor {
    private static final ILog logger     = LogFactory.getLog(SimpleTaskMonitor.class);
    private BeatManager beatManager;

    private Thread monitorThread;

    public static final long INTERVAL = 10L;

    @Autowired
    public SimpleBeatMonitor(BeatManager beatManager) {
        this.beatManager = beatManager;
    }

    @Override
    public void maintain() {
        beatManager.beat();

        monitorThread = new Thread(new BeatMonitorThread(), "BeatMonitorThread");
        monitorThread.start();
    }

    @Override
    public void stop() {
        logger.info("class=SimpleBeatMonitor||method=stop||msg=beat monitor stopByJobCode!!!");
        try {
            beatManager.stop();
            if (monitorThread != null && monitorThread.isAlive()) {
                monitorThread.interrupt();
            }
        } catch (Exception e) {
            logger.error("class=SimpleBeatMonitor||method=stop||msg=exception!", e);
        }
    }

    class BeatMonitorThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    // beat every INTERVAL seconds
                    ThreadUtil.sleep(INTERVAL, TimeUnit.SECONDS);

                    beatManager.beat();
                } catch (Exception e) {
                    logger.info("class=SimpleBeatMonitor||method=run||msg=exception!", e);
                }
            }
        }
    }
}
