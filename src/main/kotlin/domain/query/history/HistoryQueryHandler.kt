package domain.query.history

import domain.AccountCreated
import domain.MoneyDeposed
import domain.MoneyWithdrawn
import domain.query.HistoryQuery
import domain.query.Query
import domain.query.QueryHandler
import spi.EventStore

class HistoryQueryHandler(private val eventStore: EventStore) : QueryHandler {
    override fun accept(query: Query): Boolean =
        when (query) {
            is HistoryQuery -> true
        }

    override fun handle(query: Query): History? {
        val operations =
            eventStore.eventsOf(query.accountNumber)
                ?.sortedBy { it.date }
                ?.fold(mutableListOf<Operation>()) { operations, event ->
                    val currentBalance = if (operations.isNotEmpty()) operations.last().balance else Balance(0.0)
                    operations += when (event) {
                        is MoneyDeposed -> Operation(OperationType.DEPOSIT, currentBalance + event.amount, event.date)
                        is MoneyWithdrawn -> Operation(OperationType.WITHDRAW,
                            currentBalance - event.amount,
                            event.date)
                        is AccountCreated -> TODO("Nothing to do for other events")
                    }
                    operations
                }

        return operations?.let { History(operations) }
    }

}
