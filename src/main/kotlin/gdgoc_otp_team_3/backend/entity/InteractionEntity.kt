package gdgoc_otp_team_3.backend.entity

import gdgoc_otp_team_3.backend.model.InteractionType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
  name = "interactions",
  uniqueConstraints = [
    UniqueConstraint(name = "uk_interactions_user_note", columnNames = ["user_id", "note_id"]),
  ],
  indexes = [
    Index(name = "idx_interactions_note", columnList = "note_id"),
    Index(name = "idx_interactions_user", columnList = "user_id"),
  ],
)
class InteractionEntity(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id: Long? = null,

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
    name = "user_id",
    nullable = false,
    foreignKey = ForeignKey(name = "fk_interactions_users"),
  )
  var user: UserEntity,

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
    name = "note_id",
    nullable = false,
    foreignKey = ForeignKey(name = "fk_interactions_notes"),
  )
  var note: NoteEntity,

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  var type: InteractionType,

  @Column(nullable = false)
  var createdAt: LocalDateTime = LocalDateTime.now(),

  @Column(nullable = false)
  var updatedAt: LocalDateTime = LocalDateTime.now(),
) {
  @PrePersist
  fun onCreate() {
    val now = LocalDateTime.now()
    createdAt = now
    updatedAt = now
  }

  @PreUpdate
  fun onUpdate() {
    updatedAt = LocalDateTime.now()
  }
}
