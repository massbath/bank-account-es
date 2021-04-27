package domain.command.command

import domain.aggregate.AccountNumber
import domain.aggregate.Amount
import domain.aggregate.BankAccountAggregate
import java.time.LocalDateTime

abstract class Command(open var accountNumber: AccountNumber, open var date: LocalDateTime) {
    abstract fun apply(bankAccount: BankAccountAggregate)
}


data class DepositCommand(
    override var accountNumber: AccountNumber,
    val amount: Amount,
    override var date: LocalDateTime,
) :
    Command(accountNumber, date) {
    override fun apply(bankAccount: BankAccountAggregate) {
        bankAccount.deposit(amount, date)
    }
}

data class WithdrawCommand(
    override var accountNumber: AccountNumber,
    val amount: Amount,
    override var date: LocalDateTime,
) :
    Command(accountNumber, date) {
    override fun apply(bankAccount: BankAccountAggregate) {
        bankAccount.withdraw(amount, date)
    }

}
