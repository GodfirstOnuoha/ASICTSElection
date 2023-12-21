package ng.org.asicts.election.model

data class Aspirants(
    val name: String? = null,
    val department: String? = null,
    val level: String? = null,
    val image: Int? = 0,
    val position: String? = null,
    var votes: Int = 0
)
