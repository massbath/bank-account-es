package domain.command

import domain.aggregate.BankAccountAggregate
import spi.EventStore

class CommandHandler(private val eventStore: EventStore) {

    fun handle(command: Command) {
        val accountAggregate = eventStore.eventsOf(command.accountNumber)
            ?.let { BankAccountAggregate(it) }
            ?: run { BankAccountAggregate(command.accountNumber, command.date) }

        command.apply(accountAggregate)
        eventStore.save(accountAggregate.uncommittedChanges)
    }
}
