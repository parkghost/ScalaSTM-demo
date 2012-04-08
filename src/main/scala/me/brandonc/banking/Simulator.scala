package me.brandonc.banking
import java.util.concurrent.Executors
import scala.collection.mutable.ListBuffer
import java.util.concurrent.TimeUnit
import java.util.concurrent.ExecutorService

class Simulator {

  var threadPool: ExecutorService = null
  val workers = ListBuffer[Worker]()

  def addWorker(worker: Worker) {
    workers += worker
  }

  def start() {
    threadPool = Executors.newCachedThreadPool()
    workers.foreach(_.start())
    workers.foreach(threadPool.submit(_))
  }

  def stop() {
    workers.foreach(_.stop())
    workers.clear()
    threadPool.shutdown()
    threadPool.awaitTermination(5, TimeUnit.SECONDS)
  }

}

abstract class Worker extends Runnable {
  private var running: Boolean = false

  def start() {
    running = true
  }

  def stop() {
    running = false
  }

  def isRunning() = running

  override def run() {
    while (running) {
      doTask
    }
  }

  def doTask: Unit

}