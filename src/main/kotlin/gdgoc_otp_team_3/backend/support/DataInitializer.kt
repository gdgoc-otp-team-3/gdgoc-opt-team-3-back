package gdgoc_otp_team_3.backend.support

import gdgoc_otp_team_3.backend.entity.InteractionEntity
import gdgoc_otp_team_3.backend.entity.NoteEntity
import gdgoc_otp_team_3.backend.entity.UserEntity
import gdgoc_otp_team_3.backend.model.DifficultyLevel
import gdgoc_otp_team_3.backend.model.InteractionType
import gdgoc_otp_team_3.backend.repository.InteractionRepository
import gdgoc_otp_team_3.backend.repository.NoteRepository
import gdgoc_otp_team_3.backend.repository.UserRepository
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class DataInitializer(
    private val userRepository: UserRepository,
    private val noteRepository: NoteRepository,
    private val interactionRepository: InteractionRepository,
    private val passwordEncoder: org.springframework.security.crypto.password.PasswordEncoder,
) {
    @PostConstruct
    fun seed() {
        if (userRepository.count() > 0 || noteRepository.count() > 0) return

        val alice = userRepository.save(
            UserEntity(
                email = "alice@yonsei.ac.kr",
                username = "alice",
                password = passwordEncoder.encode("password1"),
            ),
        )
        val bob = userRepository.save(
            UserEntity(
                email = "bob@yonsei.ac.kr",
                username = "bob",
                password = passwordEncoder.encode("password2"),
            ),
        )
        val carol = userRepository.save(
            UserEntity(
                email = "carol@yonsei.ac.kr",
                username = "carol",
                password = passwordEncoder.encode("password3"),
            ),
        )

        val notes = listOf(
            NoteEntity(
                title = "운영체제 1주차 요약",
                fileName = "os_week1.pdf",
                semester = "2024-1",
                major = "000-컴퓨터과학과",
                subject = "운영체제",
                professor = "김연세",
                description = "프로세스와 스레드의 기초",
                uploader = alice,
                fileKey = "notes/${alice.id}/os_week1.pdf",
                aiSummary = "프로세스/스레드 개념과 상태 전이 요약",
                difficulty = DifficultyLevel.Medium,
                estimatedTime = "25분",
                createdAt = LocalDateTime.now().minusDays(2),
                thumbnailUrl = "https://placehold.co/600x400?text=OS",
            ),
            NoteEntity(
                title = "자료구조 중간 대비",
                fileName = "ds_midterm.pdf",
                semester = "2024-1",
                major = "002-소프트웨어학과",
                subject = "자료구조",
                professor = "박소프트",
                description = "트리/그래프 핵심 정리",
                uploader = bob,
                fileKey = "notes/${bob.id}/ds_midterm.pdf",
                aiSummary = "트리 순회와 그래프 탐색 핵심 정리",
                difficulty = DifficultyLevel.Hard,
                estimatedTime = "40분",
                createdAt = LocalDateTime.now().minusDays(4),
                thumbnailUrl = "https://placehold.co/600x400?text=DS",
            ),
            NoteEntity(
                title = "캡스톤디자인 노트",
                fileName = "capstone.pdf",
                semester = "2023-2",
                major = "000-컴퓨터과학과",
                subject = "캡스톤디자인",
                professor = "최프로",
                description = "팀 프로젝트 운영 체크리스트",
                uploader = alice,
                fileKey = "notes/${alice.id}/capstone.pdf",
                aiSummary = "프로젝트 일정관리, 리스크 관리 요약",
                difficulty = DifficultyLevel.Easy,
                estimatedTime = "15분",
                createdAt = LocalDateTime.now().minusWeeks(1),
                thumbnailUrl = "https://placehold.co/600x400?text=Capstone",
            ),
        )

        noteRepository.saveAll(notes)

        val interactions = listOf(
            InteractionEntity(user = alice, note = notes[0], type = InteractionType.LIKE),
            InteractionEntity(user = bob, note = notes[0], type = InteractionType.LIKE),
            InteractionEntity(user = carol, note = notes[0], type = InteractionType.DISLIKE),
            InteractionEntity(user = alice, note = notes[1], type = InteractionType.LIKE),
            InteractionEntity(user = bob, note = notes[1], type = InteractionType.LIKE),
            InteractionEntity(user = carol, note = notes[1], type = InteractionType.LIKE),
            InteractionEntity(user = alice, note = notes[2], type = InteractionType.LIKE),
        )
        interactionRepository.saveAll(interactions)
    }
}
