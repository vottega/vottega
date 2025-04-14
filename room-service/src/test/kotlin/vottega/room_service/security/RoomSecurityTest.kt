package vottega.room_service.security

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.transaction.Transactional
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import vottega.room_service.adaptor.RoomProducer
import vottega.room_service.dto.ClientRole
import vottega.room_service.dto.CreateRoomRequestDTO
import vottega.room_service.dto.UpdateRoomRequestDTO
import vottega.room_service.service.RoomService

@SpringBootTest()
@ActiveProfiles("test")
@EnableAutoConfiguration(exclude = [KafkaAutoConfiguration::class])
@EnableMethodSecurity
@AutoConfigureMockMvc
@Transactional
class RoomSecurityTest {

  @Autowired
  private lateinit var mockMvc: MockMvc

  @Autowired
  private lateinit var roomService: RoomService

  @Autowired
  private lateinit var objectMapper: ObjectMapper

  @MockBean
  private lateinit var roomProducer: RoomProducer

  @BeforeEach
  fun setUp() {
    val body1 = CreateRoomRequestDTO(roomName = "test", ownerId = 1L, participantRoleList = listOf())
    mockMvc.post("/api/room")
    {
      headers {
        this["X-Client-Role"] = ClientRole.USER.name
        this["X-User-Id"] = "1"
      }
      contentType = MediaType.APPLICATION_JSON
      content = objectMapper.writeValueAsString(body1)
    }
  }

  @Test()
  fun `방은 USER가 아니면 만들 수 없다`() {
    val body = CreateRoomRequestDTO(roomName = "test", ownerId = 1L, participantRoleList = listOf())
    mockMvc.post("/api/room")
    {
      contentType = MediaType.APPLICATION_JSON
      content = objectMapper.writeValueAsString(body)
    }
      .andExpect {
        status { isUnauthorized() }
      }
  }

  @Test()
  fun `방은 USER만 만들 수 있다`() {
    val body = CreateRoomRequestDTO(roomName = "test1", ownerId = 1L, participantRoleList = listOf())
    mockMvc.post("/api/room")
    {
      headers {
        this["X-Client-Role"] = ClientRole.USER.name
        this["X-User-Id"] = "1"
      }
      contentType = MediaType.APPLICATION_JSON
      content = objectMapper.writeValueAsString(body)
    }
      .andExpect {
        status { isCreated() }
      }
  }

  @Test()
  fun `방 update는 해당 ROOM의 주인 USER만 가능하다`() {
    val body = UpdateRoomRequestDTO(roomName = "test123", status = null)
    mockMvc.patch("/api/room/1")
    {
      headers {
        this["X-Client-Role"] = ClientRole.USER.name
        this["X-User-Id"] = "1"
      }
      contentType = MediaType.APPLICATION_JSON
      content = objectMapper.writeValueAsString(body)
    }
      .andExpect {
        status { isOk() }
      }
  }

  @Test()
  fun `방 update는 해당 ROOM 주인이 아니면 할 수 없다`() {
    val body2 = UpdateRoomRequestDTO(roomName = "test123", status = null)
    mockMvc.patch("/api/room/1")
    {
      headers {
        this["X-Client-Role"] = ClientRole.USER.name
        this["X-User-Id"] = "2"
      }
      contentType = MediaType.APPLICATION_JSON
      content = objectMapper.writeValueAsString(body2)
    }
      .andExpect {
        status { isForbidden() }
      }
  }

  @Test
  fun `방 update는 인증이 되지 않은 USER는 할 수 없다`() {
    val body3 = UpdateRoomRequestDTO(roomName = "test123", status = null)
    mockMvc.patch("/api/room/1")
    {
      contentType = MediaType.APPLICATION_JSON
      content = objectMapper.writeValueAsString(body3)
    }
      .andExpect {
        status { isUnauthorized() }
      }
  }


  @Test()
  fun `방에 대한 정보는 참가자와 방장만 얻을 수 있다`() {
    mockMvc.get("/api/room/1")
    {
      headers {
        this["X-Client-Role"] = ClientRole.USER.name
        this["X-User-Id"] = "1"
      }
    }
      .andExpect {
        status { isOk() }
      }

    mockMvc.get("/api/room/1")
    {
      headers {
        this["X-Client-Role"] = ClientRole.PARTICIPANT.name
        this["X-Participant-Id"] = "1"
      }
    }
      .andExpect {
        status { isOk() }
      }

    mockMvc.get("/api/room/1")
    {
      headers {
        this["X-Client-Role"] = ClientRole.PARTICIPANT.name
        this["X-Participant-Id"] = "2"
      }
    }
      .andExpect {
        status { isForbidden() }
      }

    mockMvc.get("/api/room/1")
    {
      headers {
        this["X-Client-Role"] = ClientRole.USER.name
        this["X-User-Id"] = "2"
      }
    }
      .andExpect {
        status { isForbidden() }
      }
  }


}