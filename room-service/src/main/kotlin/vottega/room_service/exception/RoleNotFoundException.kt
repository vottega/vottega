package vottega.room_service.exception

class RoleNotFoundException(role : String?) : RuntimeException("$role role not found")