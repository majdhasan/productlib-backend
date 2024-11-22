package life.majd.servicelib.services

data class ServiceRequest(
    val name: String,
    val description: String,
    val cost: Double,
    val duration: Int,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val userId: Long
)