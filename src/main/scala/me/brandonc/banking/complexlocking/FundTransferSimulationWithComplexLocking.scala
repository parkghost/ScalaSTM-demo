package me.brandonc.banking.complexlocking

import java.util.concurrent.TimeUnit

import scala.concurrent.forkjoin.ThreadLocalRandom

import me.brandonc.banking.DeadLockDetectWorker
import me.brandonc.banking.Simulator
import me.brandonc.banking.Worker

object FundTransferSimulationWithComplexLocking extends App {

  val parameters = if (args.length == 4) args else "5 100 100 1".split("\\s")

  val (numberOfAccounts, initialBalance, rangeOfTransferValue, duration) = (parameters(0).toInt, parameters(1).toInt, parameters(2).toInt, parameters(3).toInt)

  val accounts = Range(1, numberOfAccounts + 1).map(x => Account(x.toString(), initialBalance)).toList

  val simulator = new Simulator()

  // 加入轉帳組合
  for (left <- accounts; right <- accounts) {
    if (left != right) {
      simulator.addWorker(new TransferWorker(left, right, rangeOfTransferValue))
    }
  }

  // 偵測 deadlock (每秒)
  simulator.addWorker(new DeadLockDetectWorker(1000, true))

  simulator.start()
  TimeUnit.MINUTES.sleep(duration)
  simulator.stop()

  println(snapshot(accounts))

  def snapshot(accounts: List[Account]) = {
    val buf = new StringBuilder
    for (acount <- accounts) {
      buf ++= "Account:%s balance:%3d transactions:%d\n".format(acount.name, acount.getBalance, acount.getTransactions)
    }

    buf.toString
  }
}

class TransferWorker(from: Account, to: Account, maxValue: Int) extends Worker {

  def doTask = {
    val amount = ThreadLocalRandom.current().nextInt(maxValue)

    var first: Account = null
    var second: Account = null

    val fromHash = System.identityHashCode(from)
    val toHash = System.identityHashCode(to)

    if (fromHash > toHash) {
      first = from
      second = to
    } else {
      first = to
      second = from
    }

    first.synchronized {
      second.synchronized {
        if (from.getBalance >= amount) {
          from.transferTo(to, amount)
        }
      }
    }

  }

}
