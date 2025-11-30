package gdgoc_otp_team_3.backend.entity

import gdgoc_otp_team_3.backend.model.DifficultyLevel
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "notes",
    indexes = [
        Index(name = "idx_notes_likes", columnList = "likes"),
        Index(name = "idx_notes_created_at", columnList = "createdAt"),
        Index(name = "idx_notes_major", columnList = "major"),
    ],
)
class NoteEntity(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id: Long? = null,

  @Column(nullable = false)
  var title: String,

  @Column(nullable = false)
  var fileName: String,

  @Column(nullable = false)
  var semester: String,

  @Column(nullable = false)
  var major: String,

  @Column(nullable = false)
  var subject: String,

  var professor: String? = null,

  @Column(columnDefinition = "TEXT")
  var description: String? = null,

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
    name = "uploader_id",
    nullable = false,
    foreignKey = ForeignKey(name = "fk_notes_users"),
  )
  var uploader: UserEntity,

  @Column(nullable = false)
  var fileKey: String,

  @Column(columnDefinition = "TEXT")
  var aiSummary: String? = null,

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  var difficulty: DifficultyLevel = DifficultyLevel.Medium,

  var estimatedTime: String? = null,

  @Column(nullable = false)
  var createdAt: LocalDateTime = LocalDateTime.now(),

  @Column(nullable = false)
  var likes: Int = 0,

  @Column(nullable = false)
  var dislikes: Int = 0,

  var thumbnailUrl: String? = null,

  @Version
  var version: Long? = null,
)
