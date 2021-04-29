package domain.aggregate

import domain.AccountCreated
import domain.MoneyDeposed
import domain.MoneyWithdrawn
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class BankAccountAggregateTest {

    @Test
    fun `should create account`() {
        val today = LocalDateTime.now()
        val bankAccount = BankAccountAggregate(AccountNumber("12345678"), today)

        assertThat(bankAccount.uncommittedChanges).contains(AccountCreated(AccountNumber("12345678"), today))
        assertThat(bankAccount.state).isEqualTo(
            BankAccountState(
                accountNumber = AccountNumber("12345678"),
                dateCreation = today))
    }

    @Test
    fun `should make a deposit`() {
        val today = LocalDateTime.now()
        val bankAccount = BankAccountAggregate(AccountNumber("12345678"), today)

        bankAccount.deposit(Amount(10.0), today)

        assertThat(bankAccount.uncommittedChanges).contains(MoneyDeposed(AccountNumber("12345678"),
            Amount(10.0),
            today))
        assertThat(bankAccount.state).isEqualTo(
            BankAccountState(
                accountNumber = AccountNumber("12345678"),
                dateCreation = today,
                balance = Amount(10.0)))
    }


    @Test
    fun `should make a withdraw`() {
        val today = LocalDateTime.now()
        val bankAccount = BankAccountAggregate(AccountNumber("12345678"), today)

        bankAccount.withdraw(Amount(10.0), today)

        assertThat(bankAccount.uncommittedChanges).contains(MoneyWithdrawn(AccountNumber("12345678"),
            Amount(10.0),
            today))
        assertThat(bankAccount.state).isEqualTo(
            BankAccountState(
                accountNumber = AccountNumber("12345678"),
                dateCreation = today,
                balance = Amount(-10.0)))
    }
}







