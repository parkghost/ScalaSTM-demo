package me.brandonc.banking
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

import scala.collection.mutable.ListBuffer

class Simulator {

  var threadPool: ExecutorService = null
  val workers = ListBuffer[Worker]()

  def addWorker(worker: Worker) {
    workers += worker
  }

  def start() {
    println("starting simulation")
    threadPool = Executors.newCachedThreadPool()
    workers.foreach(_.start())
    workers.foreach(threadPool.submit(_))
  }

  def stop() {
    println("stopping simulation")
    workers.foreach(_.stop())
    workers.clear()
    threadPool.shutdown()
    threadPool.awaitTermination(5, TimeUnit.SECONDS)
    val pendingTasks = threadPool.shutdownNow()
    if (pendingTasks.size() > 0) {
      println("remaining " + pendingTasks.size() + " tasks in work queue")
    }
  }

}

abstract class Worker extends Runnable {

  @volatile
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

      //avoid liveness problem
      Thread.`yield`
    }
  }

  def doTask: Unit

}