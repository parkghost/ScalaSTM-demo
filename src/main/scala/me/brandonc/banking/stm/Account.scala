package me.brandonc.banking.stm

import scala.concurrent.stm._

case class Account(val name: String, initialBalance: Int) {

  /**
   * 存款金額
   */
  private val balance = Ref(initialBalance);

  /**
   * 交易次數
   */
  private val transactions = Ref(0);

  def withdraw(amount: Int) = atomic { implicit txn =>
    balance -= amount
    transactions += 1
  }

  def deposit(amount: Int) = atomic { implicit txn =>
    balance += amount
    transactions += 1
  }

  def transferTo(to: Account, amount: Int) = atomic { implicit txn =>
    this.withdraw(amount)
    to.deposit(amount)
  }

  def getBalance = balance.single.get

  def getTransactions = transactions.single.get
}