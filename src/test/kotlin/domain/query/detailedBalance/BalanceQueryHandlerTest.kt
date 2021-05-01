package domain.query.detailedBalance

import domain.MoneyDeposed
import domain.MoneyWithdrawn
import domain.aggregate.AccountNumber
import domain.aggregate.Amount
import domain.query.BalanceQuery
import domain.query.HistoryQuery
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class BalanceQueryHandlerTest {


    @Test
    internal fun `should accept BalanceQuery`() {
        //Given
        val balanceQueryHandler = BalanceQueryHandler(mockk())

        //When
        val isAccepted = balanceQueryHandler.accept(BalanceQuery(AccountNumber("12345")))

        //Then
        assertThat(isAccepted).isTrue
    }

    @Test
    internal fun `should refuse other Query`() {
        //Given
        val balanceQueryHandler = BalanceQueryHandler(mockk())

        //When
        val isAccepted = balanceQueryHandler.accept(HistoryQuery(AccountNumber("12345")))

        //Then
        assertThat(isAccepted).isFalse
    }

    @Test
    internal fun `should return no balance`() {
        //Given
        val balanceQueryHandler = BalanceQueryHandler(mockk(relaxed = true))

        //When
        val detailedBalance = balanceQueryHandler.handle(BalanceQuery(AccountNumber("12345")))

        //Then
        assertThat(detailedBalance).isNull()
    }

    @Test
    internal fun `should return a balance with credit`() {
        //Given
        val accountNumber = AccountNumber("12345")
        val today = LocalDateTime.now()
        val balanceQueryHandler = BalanceQueryHandler(mockk {
            every { eventsOf(accountNumber) } returns listOf(
                MoneyDeposed(accountNumber, Amount(5.0), today),
                MoneyDeposed(accountNumber, Amount(5.0), today)
            )
        })

        //When
        val detailedBalance = balanceQueryHandler.handle(BalanceQuery(accountNumber))

        //Then
        assertThat(detailedBalance).isEqualTo(
            DetailedBalance(total = Amount(10.0),
                credit = Amount(10.0),
                debit = Amount(0.0)))
    }

    @Test
    internal fun `should return a balance with debit`() {
        //Given
        val accountNumber = AccountNumber("12345")
        val today = LocalDateTime.now()
        val balanceQueryHandler = BalanceQueryHandler(mockk {
            every { eventsOf(accountNumber) } returns listOf(
                MoneyWithdrawn(accountNumber, Amount(5.0), today),
                MoneyWithdrawn(accountNumber, Amount(5.0), today)
            )
        })

        //When
        val detailedBalance = balanceQueryHandler.handle(BalanceQuery(accountNumber))

        //Then
        assertThat(detailedBalance).isEqualTo(
            DetailedBalance(total = Amount(-10.0),
                credit = Amount(0.0),
                debit = Amount(10.0)))
    }

    @Test
    internal fun `should return a balance with debit and credit`() {
        //Given
        val accountNumber = AccountNumber("12345")
        val today = LocalDateTime.now()
        val balanceQueryHandler = BalanceQueryHandler(mockk {
            every { eventsOf(accountNumber) } returns listOf(
                MoneyWithdrawn(accountNumber, Amount(5.0), today),
                MoneyDeposed(accountNumber, Amount(5.0), today)
            )
        })

        //When
        val detailedBalance = balanceQueryHandler.handle(BalanceQuery(accountNumber))

        //Then
        assertThat(detailedBalance).isEqualTo(
            DetailedBalance(total = Amount(0.0),
                credit = Amount(5.0),
                debit = Amount(5.0)))
    }
}
