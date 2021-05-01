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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import spi.EventStore
import java.time.LocalDateTime

class BankAccountAcceptanceTest {
    private lateinit var eventStore: EventStore
    private lateinit var commandHandler: CommandHandler
    private lateinit var queryHandlers: QueryHandlers
    private lateinit var today: LocalDateTime
    private lateinit var accountNumber: AccountNumber

    @BeforeEach
    internal fun setUp() {
        eventStore = EventStoreInMemory()
        queryHandlers = QueryHandlers(listOf(HistoryQueryHandler(eventStore), BalanceQueryHandler(eventStore)))
        commandHandler = CommandHandler(eventStore)
        today = LocalDateTime.now()
        accountNumber = AccountNumber("12345")
    }


    @Test
    internal fun `should make deposit and withdraw operations and retrieve them in history`() {
        //Given
        val yesterday = today.minusDays(1)
        val aWeekAgo = today.minusWeeks(1)

        //When
        commandHandler.handle(DepositCommand(accountNumber, Amount(10.0), aWeekAgo))
        commandHandler.handle(DepositCommand(accountNumber, Amount(5.5), yesterday))
        commandHandler.handle(WithdrawCommand(accountNumber, Amount(10.0), today))

        //Then
        val history = queryHandlers.handle(HistoryQuery(accountNumber))

        assertThat(history).isEqualTo(History(listOf(
            Operation(DEPOSIT, Amount(10.0), Balance(10.0), aWeekAgo),
            Operation(DEPOSIT, Amount(5.5), Balance(15.5), yesterday),
            Operation(WITHDRAW, Amount(10.0), Balance(5.5), today))
        ))
    }

    @Test
    internal fun `should be able to retrieve detailed balance`() {
        //Given
        //When
        commandHandler.handle(DepositCommand(accountNumber, Amount(10.0), today))
        commandHandler.handle(WithdrawCommand(accountNumber, Amount(10.0), today))

        //Then
        val distribution = queryHandlers.handle(BalanceQuery(accountNumber))

        assertThat(distribution).isEqualTo(
            DetailedBalance(total = Amount(0.0),
                credit = Amount(10.0),
                debit = Amount(10.0)))

    }
}
