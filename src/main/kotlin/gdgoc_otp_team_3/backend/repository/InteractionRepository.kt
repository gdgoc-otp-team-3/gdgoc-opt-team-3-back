package gdgoc_otp_team_3.backend.repository

import gdgoc_otp_team_3.backend.entity.InteractionEntity
import gdgoc_otp_team_3.backend.model.InteractionType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface InteractionRepository : JpaRepository<InteractionEntity, Long> {
  fun findByUserIdAndNoteId(userId: Long, noteId: Long): InteractionEntity?

  fun countByNoteIdAndType(noteId: Long, type: InteractionType): Long

  @Query(
    "select i.note.id as noteId, " +
      "sum(case when i.type = gdgoc_otp_team_3.backend.model.InteractionType.LIKE then 1 else 0 end) as likes, " +
      "sum(case when i.type = gdgoc_otp_team_3.backend.model.InteractionType.DISLIKE then 1 else 0 end) as dislikes " +
      "from InteractionEntity i " +
      "where i.note.id in :noteIds " +
      "group by i.note.id"
  )
  fun aggregateCountsByNoteIds(@Param("noteIds") noteIds: List<Long>): List<InteractionCountProjection>
}

interface InteractionCountProjection {
  fun getNoteId(): Long
  fun getLikes(): Long
  fun getDislikes(): Long
}
