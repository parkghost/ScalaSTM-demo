package me.brandonc.banking.locking

case class Account(val name: String, initialBalance: Int) {

  /**
   * 存款金額
   */
  private var balance = initialBalance

  /**
   * 交易次數
   */
  private var transactions = 0;

  def withdraw(amount: Int) = GlobalLock.synchronized {
    balance -= amount
    transactions += 1
  }

  def deposit(amount: Int) = GlobalLock.synchronized {
    balance += amount
    transactions += 1
  }

  def transferTo(to: Account, amount: Int) = GlobalLock.synchronized {
    this.withdraw(amount)
    to.deposit(amount)
  }

  def getBalance = GlobalLock.synchronized { balance }

  def getTransactions = GlobalLock.synchronized { transactions }
}