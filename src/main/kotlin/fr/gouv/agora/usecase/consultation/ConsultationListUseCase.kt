package fr.gouv.agora.usecase.consultation

import fr.gouv.agora.domain.ConsultationStatus
import fr.gouv.agora.domain.ConsultationUpdate
import fr.gouv.agora.domain.Thematique
import fr.gouv.agora.infrastructure.utils.DateUtils.toLocalDateTime
import fr.gouv.agora.usecase.consultation.repository.ConsultationInfo
import fr.gouv.agora.usecase.consultation.repository.ConsultationInfoRepository
import fr.gouv.agora.usecase.consultationUpdate.repository.ConsultationUpdateRepository
import fr.gouv.agora.usecase.reponseConsultation.repository.UserAnsweredConsultationRepository
import fr.gouv.agora.usecase.thematique.repository.ThematiqueRepository
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDateTime

@Service
class ConsultationListUseCase(
    private val clock: Clock,
    private val consultationInfoRepository: ConsultationInfoRepository,
    private val thematiqueRepository: ThematiqueRepository,
    private val consultationUpdateRepository: ConsultationUpdateRepository,
    private val userAnsweredConsultationRepository: UserAnsweredConsultationRepository,
) {
    companion object {
        private const val START_OF_DESCRIPTION = "<body>"
        private const val START_OF_DESCRIPTION_WITH_ADDED_PREFIX =
            "$START_OF_DESCRIPTION🗳 La consultation est terminée !<br/>Les résultats sont en cours d’analyse. Vous serez notifié(e) dès que la synthèse sera disponible.<br/><br/>—<br/><br/>"
    }

    fun getConsultationList(userId: String): List<ConsultationWithThematiqueUpdateAndAnswered> {
        // TODO : tests
        val dateNow = LocalDateTime.now(clock)
        val consultationInfoList = consultationInfoRepository.getConsultations()
        val consultationInfoIds = consultationInfoList.map { consultationInfo -> consultationInfo.id }

        val thematiqueList = thematiqueRepository.getThematiqueList()
        val consultationUpdates = consultationUpdateRepository.getConsultationUpdates(consultationInfoIds)
        val hasAnswered = userAnsweredConsultationRepository.hasAnsweredConsultations(consultationInfoIds, userId)

        return consultationInfoList.mapNotNull { consultationInfo ->
            val thematique = thematiqueList.find { thematique -> consultationInfo.thematiqueId == thematique.id }
            val consultationUpdate = if (isOngoing(dateNow, consultationInfo)) {
                consultationUpdates.find { consultationUpdate -> consultationUpdate.consultationId == consultationInfo.id && consultationUpdate.status == ConsultationStatus.COLLECTING_DATA }
            } else {
                consultationUpdates.find { consultationUpdate -> consultationUpdate.consultationId == consultationInfo.id && consultationUpdate.status != ConsultationStatus.COLLECTING_DATA }
                    ?: generateTemporaryConsultationUpdate(consultationUpdates.find { consultationUpdate -> consultationUpdate.consultationId == consultationInfo.id })
            }

            if (thematique != null && consultationUpdate != null) {
                ConsultationWithThematiqueUpdateAndAnswered(
                    info = consultationInfo,
                    thematique = thematique,
                    update = consultationUpdate,
                    hasAnswered = hasAnswered[consultationInfo.id] ?: false,
                )
            } else null
        }
    }

    private fun isOngoing(dateNow: LocalDateTime, consultationInfo: ConsultationInfo) =
        isOngoing(
            dateNow = dateNow,
            startDate = consultationInfo.startDate.toLocalDateTime(),
            endDate = consultationInfo.endDate.toLocalDateTime(),
        )

    private fun isOngoing(dateNow: LocalDateTime, startDate: LocalDateTime, endDate: LocalDateTime) =
        (dateNow.isAfter(startDate) || dateNow == startDate) && dateNow.isBefore(endDate)

    private fun generateTemporaryConsultationUpdate(consultationUpdate: ConsultationUpdate?): ConsultationUpdate? {
        return consultationUpdate?.copy(
            description = consultationUpdate.description.replace(
                START_OF_DESCRIPTION,
                START_OF_DESCRIPTION_WITH_ADDED_PREFIX
            )
        )
    }

}

data class ConsultationWithThematiqueUpdateAndAnswered(
    val info: ConsultationInfo,
    val thematique: Thematique,
    val update: ConsultationUpdate,
    val hasAnswered: Boolean,
)