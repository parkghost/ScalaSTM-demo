package me.brandonc.banking.locking
import net.jcip.annotations.ThreadSafe
import net.jcip.annotations.GuardedBy

@ThreadSafe
case class Account(val name: String, initialBalance: Int) {

  /**
   * 存款金額
   */
  @GuardedBy("me.brandonc.banking.locking.Lock")
  private var balance = initialBalance

  /**
   * 交易次數
   */
  @GuardedBy("me.brandonc.banking.locking.Lock")
  private var transactions = 0;

  def withdraw(amount: Int) = Lock.synchronized {
    balance -= amount
    transactions += 1
  }

  def deposit(amount: Int) = Lock.synchronized {
    balance += amount
    transactions += 1
  }

  def transferTo(to: Account, amount: Int) = Lock.synchronized {
    this.withdraw(amount)
    to.deposit(amount)
  }

  def getBalance = Lock.synchronized { balance }

  def getTransactions = Lock.synchronized { transactions }
}