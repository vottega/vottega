package vottega.auth_server.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vottega.auth_server.jwt.JwtUtil

@RestController
class KeyController (private val jwtUtil: JwtUtil) {

    @GetMapping("/public-key")
    fun getPublicKey(): String {
        return jwtUtil.getPublicKey()
    }

}
