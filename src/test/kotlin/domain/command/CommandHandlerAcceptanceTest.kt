package domain.command

import domain.AccountCreated
import domain.MoneyDeposed
import domain.MoneyWithdrawn
import domain.adapter.EventStoreInMemory
import domain.aggregate.AccountNumber
import domain.aggregate.Amount
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import spi.EventStore
import java.time.LocalDateTime

class CommandHandlerAcceptanceTest {

    private lateinit var today: LocalDateTime
    private lateinit var eventStore: EventStore
    private lateinit var commandHandler: CommandHandler

    @BeforeEach
    internal fun setUp() {
        today = LocalDateTime.now()
        eventStore = EventStoreInMemory()
        commandHandler = CommandHandler(eventStore)
    }

    @Test
    internal fun `should create account if not existing`() {
        commandHandler.handle(DepositCommand(AccountNumber("123456"), Amount(10.0), today))

        assertThat(eventStore.eventsOf(AccountNumber("123456")))
            .contains(AccountCreated(AccountNumber("123456"), today))
    }

    @Test
    internal fun `should make a deposit`() {
        commandHandler.handle(DepositCommand(AccountNumber("123456"), Amount(10.0), today))

        assertThat(eventStore.eventsOf(AccountNumber("123456")))
            .contains(MoneyDeposed(AccountNumber("123456"), Amount(10.0), today))
    }

    @Test
    internal fun `should make a withdraw`() {
        commandHandler.handle(WithdrawCommand(AccountNumber("123456"), Amount(10.0), today))

        assertThat(eventStore.eventsOf(AccountNumber("123456")))
            .contains(MoneyWithdrawn(AccountNumber("123456"), Amount(10.0), today))
    }
}

