package vottega.user_service.domain

import jakarta.persistence.*

@Entity
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 15)
    var username: String,

    @Column(nullable = false, length = 15)
    var password: String,

    @Column(nullable = false, length = 30)
    var email: String,

    @Column(nullable = false, length = 10)
    var phonenumber: String
)