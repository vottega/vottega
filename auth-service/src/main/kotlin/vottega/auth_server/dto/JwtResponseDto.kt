package vottega.auth_server.dto


// JWT 토큰 응답 DTO
sealed class JwtResponseDto {

    data class JwtParticipantResponseDto(
        val uuid: String,    // Participant 고유 ID
        val roomId: Long     // Room 고유 ID
    ) : JwtResponseDto()

    data class JwtUserResponseDto(
        val userId: String    // User 고유 ID
    ) : JwtResponseDto()
}