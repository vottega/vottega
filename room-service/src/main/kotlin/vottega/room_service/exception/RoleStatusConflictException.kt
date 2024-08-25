package vottega.room_service.exception

class RoleStatusConflictException(role : String) : RuntimeException("$role : Duplicate Role Name")