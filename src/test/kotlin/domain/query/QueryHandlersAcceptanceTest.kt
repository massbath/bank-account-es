package domain.query

import domain.MoneyDeposed
import domain.MoneyWithdrawn
import domain.adapter.EventStoreInMemory
import domain.aggregate.AccountNumber
import domain.aggregate.Amount
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
        //When
        val historyQueryHandler = HistoryQueryHandler(eventStore)
        val queryHandlers = QueryHandlers(listOf(historyQueryHandler))
        val history = queryHandlers.handle(HistoryQuery(anAccount))

        //Then
        assertThat(history).isEqualTo(
            History(
                listOf(Operation(type = OperationType.DEPOSIT, balance = Balance(5.0), date = aWeekAgo),
                    Operation(type = OperationType.DEPOSIT, balance = Balance(10.0), date = yesterday),
                    Operation(type = OperationType.WITHDRAW, balance = Balance(2.0), date = today)
                )))
    }
}

data class History(val operations: List<Operation>)

data class Operation(val type: OperationType, val balance: Balance, val date: LocalDateTime)

enum class OperationType {
    DEPOSIT,
    WITHDRAW
}

data class Balance(val value: Double) {
    operator fun plus(amount: Amount) = Balance(value + amount.value)
    operator fun minus(amount: Amount) = Balance(value - amount.value)
}

class QueryHandlers(private val handlers: List<QueryHandler>) {
    fun handle(query: Query) = handlers.first { it.accept(query) }.handle(query)
}


interface QueryHandler {
    fun accept(query: Query): Boolean
    fun handle(query: Query): Any?
}


sealed class Query(open var accountNumber: AccountNumber)

data class HistoryQuery(override var accountNumber: AccountNumber) : Query(accountNumber)
