package domain.query

import domain.aggregate.AccountNumber

sealed class Query(open var accountNumber: AccountNumber)

data class HistoryQuery(override var accountNumber: AccountNumber) : Query(accountNumber)
