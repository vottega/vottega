package vottega.sse_server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SseServerApplication

fun main(args: Array<String>) {
	runApplication<SseServerApplication>(*args)
}
