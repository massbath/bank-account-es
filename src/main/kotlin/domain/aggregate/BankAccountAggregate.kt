package domain.aggregate

import domain.AccountCreated
import domain.DomainEvent
import domain.MoneyDeposed
import domain.MoneyWithdrawn
import java.time.LocalDateTime

class BankAccountAggregate {

    val uncommittedChanges = mutableListOf<DomainEvent>()
    var state =  BankAccountState()

    constructor(events: List<DomainEvent>) {
        events.forEach { state = state.apply(it) }
    }

    constructor(accountNumber: AccountNumber, date: LocalDateTime) : this(emptyList()) {
        val event = AccountCreated(accountNumber, date)
        storeEvent(event)
    }

    fun deposit(amount: Amount, date: LocalDateTime) {
        val event = MoneyDeposed(state.accountNumber, amount, date)
        storeEvent(event)
    }


    fun withdraw(amount: Amount, date: LocalDateTime) {
        val event = MoneyWithdrawn(state.accountNumber, amount, date)
        storeEvent(event)
    }

    private fun storeEvent(event: DomainEvent) {
        uncommittedChanges.add(event)
        state = state.apply(event)
    }
}

data class BankAccountState(
    var balance: Amount = Amount(0.0),
    var accountNumber: AccountNumber = AccountNumber("UNKNOWN"),
    var dateCreation: LocalDateTime? = LocalDateTime.MIN,
) {

    fun apply(event: DomainEvent) =
        when (event) {
            is AccountCreated -> this.copy(accountNumber = event.accountNumber, dateCreation = event.date)
            is MoneyDeposed -> this.copy(balance = this.balance + event.amount)
            is MoneyWithdrawn -> this.copy(balance = this.balance - event.amount)
        }

}


data class Amount(val value: Double) {
    operator fun plus(otherAmount: Amount) = Amount(this.value + otherAmount.value)
    operator fun minus(otherAmount: Amount): Amount = Amount(this.value - otherAmount.value)
}

data class AccountNumber(val value: String)
