package me.brandonc.banking
import java.util.concurrent.TimeUnit
import java.lang.management.ManagementFactory

class DeadLockDetectWorker(intervalMilli: Int, exitApplicationWhenDetected: Boolean) extends Worker {
  def doTask = {
    detectDeadlock()
    TimeUnit.MILLISECONDS.sleep(intervalMilli)
  }

  def detectDeadlock() {
    val threadBean = ManagementFactory.getThreadMXBean();
    val threadIds = threadBean.findMonitorDeadlockedThreads();

    if (threadIds != null) {
      printf("Found %s deadlock\n", threadIds.length);
      val threadInfo = threadBean.getThreadInfo(threadIds, true, true);
      threadInfo.foreach(println)

      if (exitApplicationWhenDetected) {
        System.exit(-1)
      }
    }
  }
}
