package domain.adapter

import domain.DomainEvent
import domain.aggregate.AccountNumber
import spi.EventStore

class EventStoreInMemory(private val events: MutableMap<AccountNumber, MutableList<DomainEvent>> = mutableMapOf()) :
    EventStore {

    override fun eventsOf(accountNumber: AccountNumber) = events[accountNumber]

    override fun save(events: List<DomainEvent>) =
        events.forEach {
            if (this.events.containsKey(it.accountNumber).not()) this.events[it.accountNumber] = mutableListOf()
            this.events[it.accountNumber]?.add(it)
        }

}
