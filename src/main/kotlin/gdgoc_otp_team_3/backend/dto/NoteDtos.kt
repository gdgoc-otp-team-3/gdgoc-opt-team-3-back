package gdgoc_otp_team_3.backend.dto

import gdgoc_otp_team_3.backend.model.DifficultyLevel
import gdgoc_otp_team_3.backend.model.InteractionType
import java.time.LocalDateTime

enum class NoteSortType { latest, likes }

data class NoteUploadRequest(
  val fileName: String,
  val fileType: String,
  val semester: String,
  val major: String,
  val subject: String,
  val professor: String? = null,
  val description: String? = null,
  val aiSummary: String? = null,
  val difficulty: DifficultyLevel? = null,
  val estimatedTime: String? = null,
)

data class NoteUploadResponse(
  val noteId: Long,
  val presignedUrl: String,
)

data class NoteSummaryResponse(
  val id: Long,
  val title: String,
  val thumbnailUrl: String?,
  val major: String,
  val likes: Int,
  val createdAt: LocalDateTime,
)

data class NoteDetailResponse(
  val id: Long,
  val title: String,
  val semester: String,
  val professor: String?,
  val aiSummary: String?,
  val difficulty: DifficultyLevel?,
  val estimatedTime: String?,
  val uploader: NoteUploaderResponse,
  val stats: NoteStatsResponse,
  val createdAt: LocalDateTime,
  val link: String,
)

data class NoteUploaderResponse(
  val id: Long,
  val username: String,
)

data class NoteStatsResponse(
  val likes: Int,
  val dislikes: Int,
)

data class InteractionRequest(
  val type: InteractionType,
)
