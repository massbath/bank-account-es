package spi

import domain.DomainEvent
import domain.aggregate.AccountNumber

interface EventStore {
    fun eventsOf(accountNumber: AccountNumber): List<DomainEvent>?
    fun save(events: List<DomainEvent>)
}
