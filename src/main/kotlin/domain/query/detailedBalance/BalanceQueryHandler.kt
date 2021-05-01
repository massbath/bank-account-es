package domain.query.detailedBalance

import domain.MoneyDeposed
import domain.MoneyWithdrawn
import domain.aggregate.Amount
import domain.query.BalanceQuery
import domain.query.Query
import domain.query.QueryHandler
import spi.EventStore

class BalanceQueryHandler(private val eventStore: EventStore) : QueryHandler {
    override fun accept(query: Query): Boolean = query is BalanceQuery

    override fun handle(query: Query): DetailedBalance? {
        val events = eventStore.eventsOf(query.accountNumber)
            ?.filter { it is MoneyDeposed || it is MoneyWithdrawn }

        if (events?.isEmpty() == true)
            return null

        return events
            ?.let {
                val creditAmount = events.filterIsInstance<MoneyDeposed>().totalCreditAmount()
                val debitAmount = events.filterIsInstance<MoneyWithdrawn>().totalDebitAmount()

                val totalAmount = creditAmount - debitAmount

                DetailedBalance(total = totalAmount, credit = creditAmount, debit = debitAmount)
            }
    }

    private fun List<MoneyDeposed>.totalCreditAmount() =
        this.fold(Amount(0.0)) { amount, event -> amount + event.amount }

    private fun List<MoneyWithdrawn>.totalDebitAmount() =
        this.fold(Amount(0.0)) { amount, event -> amount + event.amount }
}
