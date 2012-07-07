package me.brandonc.banking.complexlocking
import net.jcip.annotations.GuardedBy
import net.jcip.annotations.ThreadSafe

@ThreadSafe
case class Account(val name: String, init: Int) {

  /**
   * 存款金額
   */
  @GuardedBy("this")
  private var balance = init

  /**
   * 交易次數
   */
  @GuardedBy("this")
  private var transactions = 0;

  def withdraw(amount: Int) = synchronized {
    balance -= amount
    transactions += 1
  }

  def deposit(amount: Int) = synchronized {
    balance += amount
    transactions += 1
  }

  def transferTo(to: Account, amount: Int) = synchronized {
    this.withdraw(amount)
    to.deposit(amount)
  }

  def getBalance = synchronized { balance }

  def getTransactions = synchronized { transactions }
}