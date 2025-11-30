package gdgoc_otp_team_3.backend.service

import gdgoc_otp_team_3.backend.dto.*
import gdgoc_otp_team_3.backend.entity.NoteEntity
import gdgoc_otp_team_3.backend.model.DifficultyLevel
import gdgoc_otp_team_3.backend.repository.NoteRepository
import gdgoc_otp_team_3.backend.security.CurrentUser
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
import kotlin.math.max

@Service
class NoteService(
  private val noteRepository: NoteRepository,
  private val userService: UserService,
) {
  @Transactional
  fun upload(user: CurrentUser, request: NoteUploadRequest): NoteUploadResponse {
    val uploader = userService.getUser(user.id)
      ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다.")
    val initial = NoteEntity(
      title = request.subject.ifBlank { request.fileName },
      fileName = request.fileName,
      semester = request.semester,
      major = request.major,
      subject = request.subject,
      professor = request.professor,
      description = request.description,
      uploader = uploader,
      fileKey = "",
      aiSummary = "이 필기는 '${request.subject}' 강의를 요약한 내용입니다.",
      difficulty = DifficultyLevel.Medium,
      estimatedTime = "20분 내외",
      createdAt = LocalDateTime.now(),
      likes = 0,
      dislikes = 0,
      thumbnailUrl = null,
    )
    val saved = noteRepository.save(initial)
    val fileKey = "notes/${user.id}/${saved.id}-${request.fileName}"
    saved.fileKey = fileKey
    val updated = noteRepository.save(saved)
    val presignedUrl = "https://upload.example.com/$fileKey" //TODO
    return NoteUploadResponse(noteId = updated.id ?: -1, presignedUrl = presignedUrl, fileKey = fileKey)
  }

  @Transactional(readOnly = true)
  fun list(
    sort: NoteSortType,
    category: String?,
    semester: String?,
    major: String?,
    subject: String?,
  ): List<NoteSummaryResponse> {
    val sortOrder = when (sort) {
      NoteSortType.likes -> Sort.by(Sort.Direction.DESC, "likes")
      NoteSortType.latest -> Sort.by(Sort.Direction.DESC, "createdAt")
    }
    val candidates = when {
      !category.isNullOrBlank() || !major.isNullOrBlank() || !semester.isNullOrBlank() || !subject.isNullOrBlank() ->
        noteRepository.search(category ?: major, semester, subject, sortOrder)

      else -> noteRepository.findAll(sortOrder)
    }
    return candidates.map { toSummary(it) }
  }

  @Transactional(readOnly = true)
  fun getDetail(noteId: Long): NoteDetailResponse {
    val note = noteRepository.findById(noteId)
      .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "노트를 찾을 수 없습니다.") }
    val uploader = userService.getUser(note.uploader.id!!)
      ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "업로더 정보를 찾을 수 없습니다.")
    return NoteDetailResponse(
      id = note.id ?: -1,
      title = note.title,
      semester = note.semester,
      professor = note.professor,
      aiSummary = note.aiSummary,
      difficulty = note.difficulty,
      estimatedTime = note.estimatedTime,
      uploader = NoteUploaderResponse(id = uploader.id ?: -1, username = uploader.username),
      stats = NoteStatsResponse(likes = note.likes, dislikes = note.dislikes),
      createdAt = note.createdAt,
    )
  }

  @Transactional
  fun interact(noteId: Long, request: InteractionRequest) {
    val note = noteRepository.findByIdWithLock(noteId)
      .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "노트를 찾을 수 없습니다.") }
    when (request.type) {
      InteractionType.like -> note.likes += 1
      InteractionType.dislike -> note.dislikes += 1
    }
    noteRepository.save(note)
  }

  @Transactional(readOnly = true)
  fun findByUser(userId: Long): List<NoteSummaryResponse> =
    noteRepository.findAllByUploaderIdOrderByCreatedAtDesc(userId)
      .map { toSummary(it) }

  @Transactional(readOnly = true)
  fun topContributors(): List<TopContributorResponse> {
    val rows = noteRepository.findTopContributors()
    return rows.mapIndexed { index, row ->
      TopContributorResponse(
        userId = row.getUserId(),
        username = row.getUsername(),
        uploadCount = row.getUploadCount().toInt(),
        rank = index + 1,
      )
    }
  }

  private fun toSummary(note: NoteEntity): NoteSummaryResponse = NoteSummaryResponse(
    id = note.id ?: -1,
    title = note.title,
    thumbnailUrl = note.thumbnailUrl ?: "https://placehold.co/600x400?text=${note.subject}",
    major = note.major,
    likes = max(note.likes, 0),
    createdAt = note.createdAt,
  )
}
