package gdgoc_otp_team_3.backend.service

import gdgoc_otp_team_3.backend.dto.*
import gdgoc_otp_team_3.backend.entity.InteractionEntity
import gdgoc_otp_team_3.backend.entity.NoteEntity
import gdgoc_otp_team_3.backend.repository.InteractionRepository
import gdgoc_otp_team_3.backend.repository.NoteRepository
import gdgoc_otp_team_3.backend.repository.UserRepository
import gdgoc_otp_team_3.backend.security.CurrentUser
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class NoteService(
  private val noteRepository: NoteRepository,
  private val interactionRepository: InteractionRepository,
  private val userService: UserService,
  private val userRepository: UserRepository,
) {
  @Transactional
  fun upload(user: CurrentUser, request: NoteUploadRequest): NoteUploadResponse {
    val uploader = userService.getUser(user.id)
      ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다.")
    val fileKey = "notes/${UUID.randomUUID()}/${request.fileName}"
    val initial = NoteEntity(
      title = request.subject.ifBlank { request.fileName },
      fileName = request.fileName,
      semester = request.semester,
      major = request.major,
      subject = request.subject,
      professor = request.professor,
      description = request.description,
      uploader = uploader,
      fileKey = fileKey,
      aiSummary = request.aiSummary,
      difficulty = request.difficulty,
      estimatedTime = request.estimatedTime,
      thumbnailUrl = null,
    )
    val saved = noteRepository.save(initial)
    val presignedUrl = "presignedUrl" // TODO
    return NoteUploadResponse(noteId = saved.id ?: -1, presignedUrl = presignedUrl)
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
      NoteSortType.likes -> Sort.unsorted()
      NoteSortType.latest -> Sort.by(Sort.Direction.DESC, "createdAt")
    }
    val candidates = when {
      !category.isNullOrBlank() || !major.isNullOrBlank() || !semester.isNullOrBlank() || !subject.isNullOrBlank() ->
        noteRepository.search(category ?: major, semester, subject, sortOrder)

      else -> noteRepository.findAll(sortOrder)
    }
    val counts = countsByNoteIds(candidates.mapNotNull { it.id })
    val summaries = candidates.map { note ->
      val count = counts[note.id] ?: InteractionCount(0, 0)
      toSummary(note, count.likes)
    }
    return if (sort == NoteSortType.likes) {
      summaries.sortedByDescending { it.likes }
    } else summaries
  }

  @Transactional(readOnly = true)
  fun getDetail(noteId: Long): NoteDetailResponse {
    val note = noteRepository.findById(noteId)
      .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "노트를 찾을 수 없습니다.") }
    val presignedUrl = "presignedUrl" // TODO
    val interactionCount = countsByNoteIds(listOf(noteId))[noteId] ?: InteractionCount(0, 0)
    return NoteDetailResponse(
      id = note.id ?: -1,
      title = note.title,
      semester = note.semester,
      professor = note.professor,
      aiSummary = note.aiSummary,
      difficulty = note.difficulty,
      estimatedTime = note.estimatedTime,
      uploader = NoteUploaderResponse(id = note.uploader.id ?: -1, username = note.uploader.username),
      stats = NoteStatsResponse(likes = interactionCount.likes, dislikes = interactionCount.dislikes),
      createdAt = note.createdAt,
      link = presignedUrl
    )
  }

  @Transactional
  fun interact(noteId: Long, request: InteractionRequest, user: CurrentUser) {
    val note = noteRepository.getReferenceById(noteId)
    val actor = userRepository.getReferenceById(user.id)

    val existing = interactionRepository.findByUserIdAndNoteId(actor.id!!, noteId)
    if (existing == null) {
      interactionRepository.save(
        InteractionEntity(
          user = actor,
          note = note,
          type = request.type,
        ),
      )
      return
    }
    existing.type = request.type
    interactionRepository.save(existing)
  }

  @Transactional(readOnly = true)
  fun findByUser(userId: Long): List<NoteSummaryResponse> =
    noteRepository.findAllByUploaderIdOrderByCreatedAtDesc(userId).let { notes ->
      val counts = countsByNoteIds(notes.mapNotNull { it.id })
      notes.map { note -> toSummary(note, counts[note.id]?.likes ?: 0) }
    }

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

  private fun countsByNoteIds(noteIds: List<Long>): Map<Long, InteractionCount> {
    if (noteIds.isEmpty()) return emptyMap()
    return interactionRepository.aggregateCountsByNoteIds(noteIds)
      .associate { it.getNoteId() to InteractionCount(it.getLikes().toInt(), it.getDislikes().toInt()) }
  }

  private fun toSummary(note: NoteEntity, likes: Int = 0): NoteSummaryResponse = NoteSummaryResponse(
    id = note.id ?: -1,
    title = note.title,
    thumbnailUrl = note.thumbnailUrl ?: "https://placehold.co/600x400?text=${note.subject}",
    major = note.major,
    likes = likes,
    createdAt = note.createdAt,
  )
}

private data class InteractionCount(val likes: Int, val dislikes: Int)
