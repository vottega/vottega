package vottega.sse_server.dto

enum class ClientRole {
  USER,
  PARTICIPANT;

  val roleName: String
    get() = "ROLE_$name"
}