package domain

import domain.aggregate.AccountNumber
import domain.aggregate.Amount
import java.time.LocalDateTime

sealed class DomainEvent(open val accountNumber: AccountNumber)

data class AccountCreated(override val accountNumber: AccountNumber, val date: LocalDateTime) :
    DomainEvent(accountNumber)

data class MoneyDeposed(override val accountNumber: AccountNumber, val amount: Amount, val date: LocalDateTime) :
    DomainEvent(accountNumber)

data class MoneyWithdrawn(override val accountNumber: AccountNumber, val amount: Amount, val date: LocalDateTime) :
    DomainEvent(accountNumber)
