package domain.aggregate

import domain.AccountCreated
import domain.MoneyDeposed
import domain.MoneyWithdrawn
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class BankAccountAggregateTest {

    private val anAccountNumber = AccountNumber("12345678")
    private val today = LocalDateTime.now()

    @Test
    fun `should create account`() {
        //Given
        //When
        val bankAccount = BankAccountAggregate(anAccountNumber, today)

        //Then
        assertThat(bankAccount.uncommittedChanges).contains(AccountCreated(anAccountNumber, today))
        assertThat(bankAccount.state).isEqualTo(
            BankAccountState(
                balance = Balance(0.0),
                accountNumber = anAccountNumber,
                dateCreation = today))
    }

    @Test
    fun `should rebuild account from its events`() {
        //Given
        val yesterday = today.minusDays(1)

        //When
        val bankAccount = BankAccountAggregate(listOf(
            AccountCreated(anAccountNumber, yesterday),
            MoneyDeposed(anAccountNumber, Amount(5.0), today)))

        //Then
        assertThat(bankAccount.state).isEqualTo(
            BankAccountState(
                balance = Balance(5.0),
                accountNumber = anAccountNumber,
                dateCreation = yesterday))
    }

    @Test
    fun `should make a deposit`() {
        //Given
        val bankAccount = BankAccountAggregate(anAccountNumber, today)

        //When
        bankAccount.deposit(Amount(10.0), today)

        //Then
        assertThat(bankAccount.uncommittedChanges).contains(MoneyDeposed(anAccountNumber,
            Amount(10.0),
            today))
        assertThat(bankAccount.state).isEqualTo(
            BankAccountState(
                balance = Balance(10.0),
                accountNumber = anAccountNumber,
                dateCreation = today))
    }


    @Test
    fun `should make a withdraw`() {
        //Given
        val bankAccount = BankAccountAggregate(anAccountNumber, today)

        //When
        bankAccount.withdraw(Amount(10.0), today)

        //Then
        assertThat(bankAccount.uncommittedChanges).contains(MoneyWithdrawn(anAccountNumber,
            Amount(10.0),
            today))
        assertThat(bankAccount.state).isEqualTo(
            BankAccountState(
                balance = Balance(-10.0),
                accountNumber = anAccountNumber,
                dateCreation = today))
    }
}







