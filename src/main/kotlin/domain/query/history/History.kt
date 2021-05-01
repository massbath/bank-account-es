package domain.query.history

import domain.aggregate.Amount
import domain.aggregate.Balance
import java.time.LocalDateTime

data class History(val operations: List<Operation>)

data class Operation(val type: OperationType, val amount: Amount, val balance: Balance, val date: LocalDateTime)

enum class OperationType {
    DEPOSIT,
    WITHDRAW
}

