package me.brandonc.banking
import java.lang.management.ManagementFactory
object ThreadUtil {

  def detectDeadlock() {
    val threadBean = ManagementFactory.getThreadMXBean();
    val threadIds = threadBean.findMonitorDeadlockedThreads();

    if (threadIds != null) {
      println("number of deadlocked threads: " + threadIds.length);
    } else {
      println("deadlocked was not found");
    }
  }

}
