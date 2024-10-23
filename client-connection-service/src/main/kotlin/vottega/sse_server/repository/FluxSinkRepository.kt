package vottega.sse_server.repository

import org.springframework.stereotype.Repository
import reactor.core.publisher.FluxSink
import java.util.concurrent.ConcurrentHashMap

@Repository
class FluxSinkRepository {
    private val clients = ConcurrentHashMap<Long, MutableList<FluxSink<String>>>()

    fun addClient(roomId: Long, sink: FluxSink<String>) {
        clients.computeIfAbsent(roomId) { mutableListOf() }.add(sink)
    }

    fun removeClient(roomId: Long, sink: FluxSink<String>) {
        clients[roomId]?.remove(sink)
    }

    fun getClients(roomId: Long): List<FluxSink<String>> {
        return clients[roomId] ?: emptyList()
    }

}