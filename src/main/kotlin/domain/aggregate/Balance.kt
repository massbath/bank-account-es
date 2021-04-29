package domain.aggregate

data class Balance(val value: Double) {
    operator fun plus(amount: Amount) = Balance(value + amount.value)
    operator fun minus(amount: Amount) = Balance(value - amount.value)
}
