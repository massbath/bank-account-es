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
                balance = Balance(0.0),
                accountNumber = AccountNumber("12345678"),
                dateCreation = today))
    }

    @Test
    fun `should rebuild account from its events`() {
        //Given
        val today = LocalDateTime.now()
        val yesterday = today.minusDays(1)
        val accountNumber = AccountNumber("12345678")

        //When
        val bankAccount = BankAccountAggregate(listOf(
            AccountCreated(accountNumber, yesterday),
            MoneyDeposed(accountNumber, Amount(5.0), today)))

        //Then
        assertThat(bankAccount.state).isEqualTo(
            BankAccountState(
                balance = Balance(5.0),
                accountNumber = accountNumber,
                dateCreation = yesterday))
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
                balance = Balance(10.0),
                accountNumber = AccountNumber("12345678"),
                dateCreation = today))
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
                balance = Balance(-10.0),
                accountNumber = AccountNumber("12345678"),
                dateCreation = today))
    }
}







