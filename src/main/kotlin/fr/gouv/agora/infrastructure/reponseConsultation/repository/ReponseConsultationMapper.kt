package fr.gouv.agora.infrastructure.reponseConsultation.repository

import fr.gouv.agora.domain.ReponseConsultation
import fr.gouv.agora.domain.ReponseConsultationInserting
import fr.gouv.agora.domain.ResponseConsultationCount
import fr.gouv.agora.infrastructure.reponseConsultation.dto.ReponseConsultationDTO
import fr.gouv.agora.infrastructure.reponseConsultation.dto.ResponseConsultationCountDTO
import org.springframework.stereotype.Component
import java.util.*

@Component
class ReponseConsultationMapper {

    fun toDomain(dto: ReponseConsultationDTO): ReponseConsultation {
        return ReponseConsultation(
            id = dto.id.toString(),
            questionId = dto.questionId.toString(),
            choiceId = dto.choiceId.toString(),
            participationId = dto.participationId.toString(),
            userId = dto.userId.toString(),
            responseText = dto.responseText,
        )
    }

    fun toDomain(dto: ResponseConsultationCountDTO): ResponseConsultationCount {
        return ResponseConsultationCount(
            questionId = dto.questionId.toString(),
            choiceId = dto.choiceId.toString(),
            responseCount = dto.responseCount,
        )
    }

    fun toDto(
        consultationId: UUID,
        userId: UUID,
        participationId: UUID,
        domain: ReponseConsultationInserting,
    ): List<ReponseConsultationDTO> {
        return if (domain.choiceIds.isNullOrEmpty()) {
            listOfNotNull(
                toDto(
                    consultationId = consultationId,
                    userId = userId,
                    participationId = participationId,
                    domain = domain,
                    choiceId = null
                )
            )
        } else {
            domain.choiceIds.mapNotNull { choiceId ->
                toDto(
                    consultationId = consultationId,
                    userId = userId,
                    participationId = participationId,
                    domain = domain,
                    choiceId = choiceId
                )
            }
        }
    }

    private fun toDto(
        consultationId: UUID,
        userId: UUID,
        participationId: UUID,
        domain: ReponseConsultationInserting,
        choiceId: String?,
    ): ReponseConsultationDTO? {
        return try {
            ReponseConsultationDTO(
                id = UUID.randomUUID(),
                consultationId = consultationId,
                questionId = UUID.fromString(domain.questionId),
                choiceId = choiceId?.let { UUID.fromString(choiceId) },
                responseText = domain.responseText,
                participationId = participationId,
                participationDate = Date(),
                userId = userId,
            )
        } catch (e: IllegalStateException) {
            null
        }
    }
}