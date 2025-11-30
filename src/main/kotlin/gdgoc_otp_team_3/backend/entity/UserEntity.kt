package gdgoc_otp_team_3.backend.entity

import jakarta.persistence.*

@Entity
@Table(name = "users")
class UserEntity(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id: Long? = null,

  @Column(nullable = false, unique = true)
  var email: String,

  @Column(nullable = false, unique = true)
  var username: String,

  @Column(nullable = false)
  var password: String,

  @Column(nullable = false)
  var createdAt: java.time.LocalDateTime = java.time.LocalDateTime.now(),
)
