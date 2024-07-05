package fr.gouv.agora.infrastructure.question.repository

import fr.gouv.agora.domain.AgoraFeature
import fr.gouv.agora.domain.Question
import fr.gouv.agora.domain.Questions
import fr.gouv.agora.infrastructure.consultation.repository.ConsultationDatabaseRepository
import fr.gouv.agora.infrastructure.utils.UuidUtils.toUuidOrNull
import fr.gouv.agora.usecase.featureFlags.repository.FeatureFlagsRepository
import fr.gouv.agora.usecase.question.repository.QuestionRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
@Suppress("unused")
class QuestionRepositoryImpl(
    private val consultationDatabaseRepository: ConsultationDatabaseRepository,
    private val questionDatabaseRepository: QuestionDatabaseRepository,
    private val choixPossibleDatabaseRepository: ChoixPossibleDatabaseRepository,
    private val featureFlagsRepository: FeatureFlagsRepository,
    private val questionMapper: QuestionMapper,
) : QuestionRepository {

    override fun getConsultationQuestions(consultationId: String): Questions {
        val consultationUUID = consultationId.toUuidOrNull()

        if (consultationUUID != null) {
            return consultationDatabaseRepository.getConsultation(consultationUUID)?.let { consultationDTO ->
                Questions(
                    questionCount = consultationDTO.questionCountNumber,
                    questions = getConsultationQuestions(consultationUUID),
                )
            } ?: Questions(questionCount = 0, questions = emptyList())
        }

        if (!featureFlagsRepository.isFeatureEnabled(AgoraFeature.StrapiConsultations))
            return Questions(questionCount = 0, questions = emptyList())

        // todo STRAPI
        return Questions(questionCount = 0, questions = emptyList())
    }

    override fun getConsultationQuestionList(consultationId: String): List<Question> {
        val consultationUUID = consultationId.toUuidOrNull()

        if (consultationUUID != null) {
            return getConsultationQuestions(consultationUUID)
        }

        if (!featureFlagsRepository.isFeatureEnabled(AgoraFeature.StrapiConsultations))
            return emptyList()

        // todo STRAPI
        return emptyList()
    }

    private fun getConsultationQuestions(consultationUUID: UUID): List<Question> {
        val questions = questionDatabaseRepository.getQuestionConsultation(consultationUUID) ?: emptyList()
        return questions.map { questionDTO ->
            questionMapper.toDomain(
                dto = questionDTO,
                questionDTOList = questions,
                choixPossibleDTOList = choixPossibleDatabaseRepository.getChoixPossibleQuestion(questionDTO.id)
                    ?: emptyList(),
            )
        }
    }

}
