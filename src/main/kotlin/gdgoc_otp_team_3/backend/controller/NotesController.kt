package gdgoc_otp_team_3.backend.controller

import gdgoc_otp_team_3.backend.dto.*
import gdgoc_otp_team_3.backend.security.CurrentUser
import gdgoc_otp_team_3.backend.security.User
import gdgoc_otp_team_3.backend.service.NoteService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/notes")
class NotesController(
  private val noteService: NoteService,
) {
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  fun uploadNote(
    @User user: CurrentUser,
    @RequestBody request: NoteUploadRequest,
  ): NoteUploadResponse = noteService.upload(user, request)

  @GetMapping
  fun listNotes(
    @RequestParam(required = false, defaultValue = "latest") sort: NoteSortType,
    @RequestParam(required = false) category: String?,
    @RequestParam(required = false) semester: String?,
    @RequestParam(required = false) major: String?,
    @RequestParam(required = false) subject: String?,
  ): List<NoteSummaryResponse> = noteService.list(sort, category, semester, major, subject)

  @GetMapping("/{noteId}")
  fun noteDetail(@PathVariable noteId: Long): NoteDetailResponse =
    noteService.getDetail(noteId)

  @PostMapping("/{noteId}/interaction")
  fun interact(
    @PathVariable noteId: Long,
    @RequestBody request: InteractionRequest,
  ) = noteService.interact(noteId, request)
}
