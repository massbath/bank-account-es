package domain

import domain.aggregate.AccountNumber
import domain.aggregate.Amount
import java.time.LocalDateTime

sealed class DomainEvent(open val accountNumber: AccountNumber, open val date: LocalDateTime)


data class AccountCreated(override val accountNumber: AccountNumber, override val date: LocalDateTime) :
    DomainEvent(accountNumber, date)

data class MoneyDeposed(
    override val accountNumber: AccountNumber,
    val amount: Amount,
    override val date: LocalDateTime,
) :
    DomainEvent(accountNumber, date)

data class MoneyWithdrawn(
    override val accountNumber: AccountNumber,
    val amount: Amount,
    override val date: LocalDateTime,
) :
    DomainEvent(accountNumber, date)
