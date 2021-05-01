package domain.query.detailedBalance

import domain.aggregate.Amount

data class DetailedBalance(val total: Amount, val credit: Amount, val debit: Amount)
