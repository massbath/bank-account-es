package domain.query

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class QueryHandlersTest {

    @Test
    internal fun `should delegate query to right handler`() {
        //Given
        val aQuery = mockk<Query>()
        val aQueryHandler = mockk<QueryHandler>(relaxed = true) {
            every { accept(aQuery) } returns (true)
        }
        val handlers = QueryHandlers(listOf(aQueryHandler))

        //When
        handlers.handle(aQuery)

        //Then
        verify {
            aQueryHandler.accept(aQuery)
            aQueryHandler.handle(aQuery)
        }
    }
}
