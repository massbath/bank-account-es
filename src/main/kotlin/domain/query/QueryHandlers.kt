package domain.query

class QueryHandlers(private val handlers: List<QueryHandler>) {
    fun handle(query: Query) = handlers.first { it.accept(query) }.handle(query)
}

interface QueryHandler {
    fun accept(query: Query): Boolean
    fun handle(query: Query): Any?
}
