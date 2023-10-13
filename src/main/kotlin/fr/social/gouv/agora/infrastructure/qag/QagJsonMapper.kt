package fr.social.gouv.agora.infrastructure.qag

import fr.social.gouv.agora.domain.Qag
import fr.social.gouv.agora.domain.QagInserting
import fr.social.gouv.agora.domain.QagStatus
import fr.social.gouv.agora.domain.SupportQag
import fr.social.gouv.agora.infrastructure.profile.repository.DateMapper
import fr.social.gouv.agora.infrastructure.thematique.ThematiqueJsonMapper
import fr.social.gouv.agora.infrastructure.utils.StringUtils
import fr.social.gouv.agora.usecase.qag.repository.QagInsertionResult
import org.springframework.stereotype.Component
import java.util.*

@Component
class QagJsonMapper(
    private val thematiqueJsonMapper: ThematiqueJsonMapper,
    private val dateMapper: DateMapper,
) {

    fun toJson(qag: Qag, userId: String): QagJson {
        return QagJson(
            id = qag.id,
            thematique = thematiqueJsonMapper.toNoIdJson(qag.thematique),
            title = qag.title,
            description = qag.description,
            date = dateMapper.toFormattedDate(qag.date),
            username = qag.username,
            canShare = qag.status == QagStatus.MODERATED_ACCEPTED || qag.status == QagStatus.SELECTED_FOR_RESPONSE,
            canSupport = qag.status == QagStatus.OPEN || qag.status == QagStatus.MODERATED_ACCEPTED,
            canDelete = qag.userId == userId,
            support = buildSupportQagJson(qag),
            isAuthor = qag.userId == userId,
            response = buildResponseQagJson(qag),
        )
    }

    fun toJson(supportQag: SupportQag): SupportQagJson {
        return SupportQagJson(
            supportCount = supportQag.supportCount,
            isSupportedByUser = supportQag.isSupportedByUser,
        )
    }

    fun toJson(qagInsertionResult: QagInsertionResult.Success): QagInsertionResultJson {
        return QagInsertionResultJson(qagId = qagInsertionResult.qagId.toString())
    }

    fun toDomain(json: QagInsertingJson, userId: String): QagInserting {
        return QagInserting(
            thematiqueId = json.thematiqueId,
            title = json.title,
            description = json.description,
            date = Calendar.getInstance().time,
            status = QagStatus.OPEN,
            username = json.author,
            userId = userId,
        )
    }

    private fun buildSupportQagJson(qag: Qag): SupportQagJson {
        return SupportQagJson(
            supportCount = qag.support.supportCount,
            isSupportedByUser = qag.support.isSupportedByUser,
        )
    }

    private fun buildResponseQagJson(qag: Qag): ResponseQagJson? {
        return qag.response?.let { response ->
            ResponseQagJson(
                author = response.author,
                authorDescription = response.authorDescription,
                responseDate = dateMapper.toFormattedDate(response.responseDate),
                videoUrl = response.videoUrl,
                videoWidth = response.videoWidth,
                videoHeight = response.videoHeight,
                transcription = StringUtils.unescapeLineBreaks(response.transcription),
                feedbackStatus = qag.feedback,
                feedbackResults = if (qag.feedbackResults == null) null else {
                    val feedbackList = qag.feedbackResults
                    var positiveRatio = 0
                    var negativeRatio = 0
                    if (feedbackList.isNotEmpty()) {
                        positiveRatio =
                            feedbackList.count { feedbackQag -> feedbackQag.isHelpful } * 100 / feedbackList.size
                        negativeRatio = 100 - positiveRatio
                    }
                    FeedbackResultsJson(
                        positiveRatio = positiveRatio,
                        negativeRatio = negativeRatio,
                        count = feedbackList.size,
                    )
                }
            )
        }
    }
}
