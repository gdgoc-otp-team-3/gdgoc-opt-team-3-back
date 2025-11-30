package gdgoc_otp_team_3.backend.controller

import gdgoc_otp_team_3.backend.dto.NoteSummaryResponse
import gdgoc_otp_team_3.backend.dto.TopContributorResponse
import gdgoc_otp_team_3.backend.service.NoteService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val noteService: NoteService,
) {
    @GetMapping("/{userId}/notes")
    fun notesByUser(@PathVariable userId: Long): List<NoteSummaryResponse> =
        noteService.findByUser(userId)

    @GetMapping("/top-contributors")
    fun topContributors(): List<TopContributorResponse> =
        noteService.topContributors()
}
