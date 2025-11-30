package gdgoc_otp_team_3.backend.dto

import gdgoc_otp_team_3.backend.model.DifficultyLevel
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
)

data class NoteUploadResponse(
    val noteId: Long,
    val presignedUrl: String,
    val fileKey: String,
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
    val difficulty: DifficultyLevel,
    val estimatedTime: String?,
    val uploader: NoteUploaderResponse,
    val stats: NoteStatsResponse,
    val createdAt: LocalDateTime,
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

enum class InteractionType { like, dislike }
