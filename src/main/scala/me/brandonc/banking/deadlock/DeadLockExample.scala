package me.brandonc.banking.deadlock

import java.util.concurrent.TimeUnit

import scala.concurrent.forkjoin.ThreadLocalRandom

import me.brandonc.banking.DeadLockDetectWorker
import me.brandonc.banking.Simulator
import me.brandonc.banking.Worker

object DeadLockExample extends App {

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

  // 偵測 deadlock (每秒)
  simulator.addWorker(new DeadLockDetectWorker(1000, true))

  simulator.start()
  TimeUnit.MINUTES.sleep(duration)
  simulator.stop()

}

class TransferWorker(from: Account, to: Account, maxValue: Int) extends Worker {

  def doTask = {

    val amount = ThreadLocalRandom.current().nextInt(maxValue)
    from.synchronized {
      to.synchronized {
        if (from.getBalance >= amount) {
          from.transferTo(to, amount)
        }
      }
    }

    Thread.`yield`
  }

}
