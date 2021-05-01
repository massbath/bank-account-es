package domain

import domain.adapter.EventStoreInMemory
import domain.aggregate.AccountNumber
import domain.aggregate.Amount
import domain.aggregate.Balance
import domain.command.CommandHandler
import domain.command.DepositCommand
import domain.command.WithdrawCommand
import domain.query.BalanceQuery
import domain.query.HistoryQuery
import domain.query.QueryHandlers
import domain.query.detailedBalance.BalanceQueryHandler
import domain.query.detailedBalance.DetailedBalance
import domain.query.history.History
import domain.query.history.HistoryQueryHandler
import domain.query.history.Operation
import domain.query.history.OperationType.DEPOSIT
import domain.query.history.OperationType.WITHDRAW
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class BankAccountAcceptanceTest {
    @Test
    internal fun `should make deposit and withdraw operations and retrieve them in historic`() {
        //Given
        val eventStore = EventStoreInMemory()
        val queryHandlers = QueryHandlers(listOf(HistoryQueryHandler(eventStore)))
        val commandHandler = CommandHandler(eventStore)
        val today = LocalDateTime.now()
        val yesterday = today.minusDays(1)
        val aWeekAgo = today.minusWeeks(1)
        val accountNumber = AccountNumber("12345")

        //When
        commandHandler.handle(DepositCommand(accountNumber, Amount(10.0), aWeekAgo))
        commandHandler.handle(DepositCommand(accountNumber, Amount(5.5), yesterday))
        commandHandler.handle(WithdrawCommand(accountNumber, Amount(10.0), today))

        //Then
        val historic = queryHandlers.handle(HistoryQuery(accountNumber))

        assertThat(historic).isEqualTo(History(listOf(
            Operation(DEPOSIT, Balance(10.0), aWeekAgo, Amount(10.0)),
            Operation(DEPOSIT, Balance(15.5), yesterday, Amount(5.5)),
            Operation(WITHDRAW, Balance(5.5), today, Amount(10.0)))
        ))
    }

    @Test
    internal fun `should be able to retrieve detailed balance`() {
        //Given
        val eventStore = EventStoreInMemory()
        val queryHandlers = QueryHandlers(listOf(BalanceQueryHandler(eventStore)))
        val commandHandler = CommandHandler(eventStore)
        val today = LocalDateTime.now()
        val yesterday = today.minusDays(1)
        val aWeekAgo = today.minusWeeks(1)
        val accountNumber = AccountNumber("12345")

        //When
        commandHandler.handle(DepositCommand(accountNumber, Amount(10.0), aWeekAgo))
        commandHandler.handle(WithdrawCommand(accountNumber, Amount(10.0), today))

        //Then
        val distribution = queryHandlers.handle(BalanceQuery(accountNumber))

        assertThat(distribution).isEqualTo(DetailedBalance(total = Amount(0.0),
            credit = Amount(10.0),
            debit = Amount(10.0)))

    }
}
