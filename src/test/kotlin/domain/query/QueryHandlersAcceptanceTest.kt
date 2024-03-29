package domain.query

import domain.MoneyDeposed
import domain.MoneyWithdrawn
import domain.adapter.EventStoreInMemory
import domain.aggregate.AccountNumber
import domain.aggregate.Amount
import domain.aggregate.Balance
import domain.query.history.History
import domain.query.history.HistoryQueryHandler
import domain.query.history.Operation
import domain.query.history.OperationType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class QueryHandlersAcceptanceTest {

    @Test
    internal fun `should retrieve account operation history`() {
        //Given
        val eventStore = EventStoreInMemory()
        val today = LocalDateTime.now()
        val aWeekAgo = today.minusDays(7)
        val yesterday = today.minusDays(1)
        val anAccount = AccountNumber("12345")
        eventStore.save(listOf(
            MoneyDeposed(anAccount, Amount(5.0), aWeekAgo),
            MoneyDeposed(anAccount, Amount(5.0), yesterday),
            MoneyWithdrawn(anAccount, Amount(8.0), today))
        )
        val historyQueryHandler = HistoryQueryHandler(eventStore)
        val queryHandlers = QueryHandlers(listOf(historyQueryHandler))

        //When
        val history = queryHandlers.handle(HistoryQuery(anAccount))

        //Then
        assertThat(history).isEqualTo(
            History(
                listOf(Operation(type = OperationType.DEPOSIT,
                    amount = Amount(5.0),
                    balance = Balance(5.0),
                    date = aWeekAgo),
                    Operation(type = OperationType.DEPOSIT,
                        amount = Amount(5.0),
                        balance = Balance(10.0),
                        date = yesterday),
                    Operation(type = OperationType.WITHDRAW,
                        amount = Amount(8.00),
                        balance = Balance(2.0),
                        date = today)
                )))
    }
}
