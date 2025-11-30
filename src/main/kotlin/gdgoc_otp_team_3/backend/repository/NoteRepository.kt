package gdgoc_otp_team_3.backend.repository

import gdgoc_otp_team_3.backend.entity.NoteEntity
import jakarta.persistence.LockModeType
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface NoteRepository : JpaRepository<NoteEntity, Long> {
    fun findAllByUploaderIdOrderByCreatedAtDesc(uploaderId: Long): List<NoteEntity>

    @Query(
        "select n from NoteEntity n where " +
            "(:major is null or n.major = :major) and " +
            "(:semester is null or n.semester = :semester) and " +
            "(:subject is null or n.subject = :subject)"
    )
    fun search(
        @Param("major") major: String?,
        @Param("semester") semester: String?,
        @Param("subject") subject: String?,
        sort: Sort,
    ): List<NoteEntity>

    @Query(
        "select u.id as userId, u.username as username, count(n.id) as uploadCount " +
            "from NoteEntity n join n.uploader u " +
            "group by u.id, u.username " +
            "order by count(n.id) desc"
    )
    fun findTopContributors(): List<TopContributorProjection>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select n from NoteEntity n where n.id = :id")
    fun findByIdWithLock(@Param("id") id: Long): Optional<NoteEntity>
}
