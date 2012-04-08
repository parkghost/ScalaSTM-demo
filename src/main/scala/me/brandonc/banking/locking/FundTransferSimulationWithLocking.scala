package me.brandonc.banking.locking

import java.util.concurrent.TimeUnit

import scala.concurrent.forkjoin.ThreadLocalRandom

import me.brandonc.banking.Simulator
import me.brandonc.banking.ThreadUtil
import me.brandonc.banking.Worker

object FundTransferSimulationWithLocking extends App {

  val parameters = if (args.length == 4) args else "5 100 100 1".split("\\s")

  val (numberOfAccounts, initialBalance, rangeOftransferValue, duration) = (parameters(0).toInt, parameters(1).toInt, parameters(2).toInt, parameters(3).toInt)

  val accounts = Range(1, numberOfAccounts + 1).map(x => Account(x.toString(), initialBalance)).toList

  val simulator = new Simulator()

  // 加入轉帳組合
  for (left <- accounts; right <- accounts) {
    if (left != right) {
      simulator.addWorker(new TransferWorker(left, right, rangeOftransferValue))
    }
  }

  // 監控各 account 存款與交易次數(每秒)
  simulator.addWorker(new SnapshotWorker(1000, accounts))

  simulator.start()
  TimeUnit.MINUTES.sleep(duration)
  simulator.stop()

  // 偵測 deadlock 
  ThreadUtil.detectDeadlock()

}

class TransferWorker(from: Account, to: Account, maxValue: Int) extends Worker {

  def doTask = {
    GlobalLock.synchronized {
      val amount = ThreadLocalRandom.current().nextInt(maxValue)
      if (from.getBalance >= amount) {
        from.transferTo(to, amount)
      }
    }
    Thread.`yield`
  }

}

class SnapshotWorker(intervalMilli: Int, accounts: List[Account]) extends Worker {

  def doTask = {
    println(snapshot)
    TimeUnit.MILLISECONDS.sleep(intervalMilli)
  }

  def snapshot = GlobalLock.synchronized {
    val buf = new StringBuilder
    for (acount <- accounts) {
      buf ++= "Account:%s balance:%3d transactions:%d\n".format(acount.name, acount.getBalance, acount.getTransactions)
    }

    buf.toString
  }
}
