package fr.gouv.agora.usecase.reponseConsultation.repository

import fr.gouv.agora.domain.DemographicInfoCount
import fr.gouv.agora.domain.DemographicInfoCountByChoices
import fr.gouv.agora.domain.ReponseConsultation
import fr.gouv.agora.domain.ResponseConsultationCount

interface GetConsultationResponseRepository {
    @Deprecated(message = "Should use getConsultationResponsesCount instead for better performances")
    fun getConsultationResponses(consultationId: String): List<ReponseConsultation>
    fun getConsultationResponsesCount(consultationId: String): List<ResponseConsultationCount>
    fun getParticipantDemographicInfo(consultationId: String): DemographicInfoCount
    fun getParticipantDemographicInfoByChoices(consultationId: String): DemographicInfoCountByChoices
}