package vottega.auth_server.dto;

sealed class JwtResponseDto {

    data class JwtParticipantResponseDto(
        val uuid: String,
        val roomId: Long
    ) : JwtResponseDto()

    data class JwtUserResponseDto(
        val id: Long,
        val userId: String
    ) : JwtResponseDto()
}
