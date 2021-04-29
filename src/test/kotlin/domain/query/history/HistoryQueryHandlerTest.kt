package domain.query.history

import domain.MoneyDeposed
import domain.MoneyWithdrawn
import domain.aggregate.AccountNumber
import domain.aggregate.Amount
import domain.query.HistoryQuery
import domain.query.history.OperationType.DEPOSIT
import domain.query.history.OperationType.WITHDRAW
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import spi.EventStore
import java.time.LocalDateTime

class HistoryQueryHandlerTest {


    @Test
    internal fun `should accept HistoryQuery`() {
        //Given
        val eventStore = mockk<EventStore>(relaxed = true)
        val handler = HistoryQueryHandler(eventStore)

        //When
        val result = handler.accept(HistoryQuery(AccountNumber("12345")))

        //Then
        assertThat(result).isTrue
    }

    @Test
    internal fun `should return empty history`() {
        //Given
        val eventStore = mockk<EventStore> {
            every { eventsOf(AccountNumber("12345")) } returns emptyList()
        }
        val handler = HistoryQueryHandler(eventStore)

        //When
        val history = handler.handle(HistoryQuery(AccountNumber("12345")))

        //Then
        assertThat(history).isEqualTo(History(emptyList()))
    }

    @Test
    internal fun `should return history with one deposit`() {
        //Given
        val anAccountNumber = AccountNumber("12345")
        val today = LocalDateTime.now()
        val eventStore = mockk<EventStore> {
            every { eventsOf(anAccountNumber) } returns listOf(MoneyDeposed(anAccountNumber, Amount(10.0), today))
        }

        val handler = HistoryQueryHandler(eventStore)

        //When
        val history = handler.handle(HistoryQuery(anAccountNumber))

        //Then
        assertThat(history).isEqualTo(History(listOf(Operation(DEPOSIT, Balance(10.0), today))))
    }

    @Test
    internal fun `should return history with deposits`() {
        //Given
        val anAccountNumber = AccountNumber("12345")
        val today = LocalDateTime.now()
        val yesterday = today.minusDays(1)
        val eventStore = mockk<EventStore> {
            every { eventsOf(anAccountNumber) } returns listOf(MoneyDeposed(anAccountNumber, Amount(10.0), today),
                MoneyDeposed(anAccountNumber, Amount(10.0), yesterday))
        }

        val handler = HistoryQueryHandler(eventStore)

        //When
        val history = handler.handle(HistoryQuery(anAccountNumber))

        //Then
        assertThat(history).isEqualTo(History(listOf(
            Operation(DEPOSIT, Balance(10.0), yesterday),
            Operation(DEPOSIT, Balance(20.0), today)
        )))
    }

    @Test
    internal fun `should return history with one withdraw`() {
        //Given
        val anAccountNumber = AccountNumber("12345")
        val today = LocalDateTime.now()
        val eventStore = mockk<EventStore> {
            every { eventsOf(anAccountNumber) } returns listOf(MoneyWithdrawn(anAccountNumber, Amount(10.0), today))
        }

        val handler = HistoryQueryHandler(eventStore)

        //When
        val history = handler.handle(HistoryQuery(anAccountNumber))

        //Then
        assertThat(history).isEqualTo(History(listOf(Operation(WITHDRAW, Balance(-10.0), today))))
    }

    @Test
    internal fun `should return history with withdraws`() {
        //Given
        val anAccountNumber = AccountNumber("12345")
        val today = LocalDateTime.now()
        val yesterday = today.minusDays(1)

        val eventStore = mockk<EventStore> {
            every { eventsOf(anAccountNumber) } returns listOf(MoneyWithdrawn(anAccountNumber, Amount(10.0), today),
                MoneyWithdrawn(anAccountNumber, Amount(10.0), yesterday))
        }

        val handler = HistoryQueryHandler(eventStore)

        //When
        val history = handler.handle(HistoryQuery(anAccountNumber))

        //Then
        assertThat(history).isEqualTo(History(listOf(
            Operation(WITHDRAW, Balance(-10.0), yesterday),
            Operation(WITHDRAW, Balance(-20.0), today))))
    }

    @Test
    internal fun `should return history with deposit and withdraw`() {
        //Given
        val anAccountNumber = AccountNumber("12345")
        val today = LocalDateTime.now()
        val yesterday = today.minusDays(1)

        val eventStore = mockk<EventStore> {
            every { eventsOf(anAccountNumber) } returns listOf(MoneyWithdrawn(anAccountNumber, Amount(10.0), today),
                MoneyDeposed(anAccountNumber, Amount(10.0), yesterday))
        }

        val handler = HistoryQueryHandler(eventStore)

        //When
        val history = handler.handle(HistoryQuery(anAccountNumber))

        //Then
        assertThat(history).isEqualTo(History(listOf(
            Operation(DEPOSIT, Balance(10.0), yesterday),
            Operation(WITHDRAW, Balance(0.0), today))))
    }
}

