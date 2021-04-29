package domain.query.history

import domain.aggregate.Amount
import java.time.LocalDateTime

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
