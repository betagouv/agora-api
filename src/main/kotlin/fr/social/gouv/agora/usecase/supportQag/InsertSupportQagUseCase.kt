package fr.social.gouv.agora.usecase.supportQag

import fr.social.gouv.agora.domain.QagStatus
import fr.social.gouv.agora.domain.SupportQagInserting
import fr.social.gouv.agora.usecase.qag.repository.QagInfo
import fr.social.gouv.agora.usecase.qag.repository.QagInfoRepository
import fr.social.gouv.agora.usecase.qagPreview.repository.QagPreviewPageRepository
import fr.social.gouv.agora.usecase.responseQag.repository.ResponseQagRepository
import fr.social.gouv.agora.usecase.supportQag.repository.GetSupportQagRepository
import fr.social.gouv.agora.usecase.supportQag.repository.SupportQagRepository
import fr.social.gouv.agora.usecase.supportQag.repository.SupportQagResult
import org.springframework.stereotype.Service

@Service
class InsertSupportQagUseCase(
    private val responseQagRepository: ResponseQagRepository,
    private val qagInfoRepository: QagInfoRepository,
    private val getSupportQagRepository: GetSupportQagRepository,
    private val supportQagRepository: SupportQagRepository,
    private val previewPageRepository: QagPreviewPageRepository,
) {
    fun insertSupportQag(supportQagInserting: SupportQagInserting): SupportQagResult {
        if (responseQagRepository.getResponseQag(supportQagInserting.qagId) != null) return SupportQagResult.FAILURE
        if (getSupportQagRepository.getSupportQag(
                qagId = supportQagInserting.qagId,
                userId = supportQagInserting.userId,
            )?.isSupportedByUser == true
        ) return SupportQagResult.FAILURE

        val qagInfo = qagInfoRepository.getQagInfo(supportQagInserting.qagId)
        return when (qagInfo?.status) {
            null, QagStatus.ARCHIVED, QagStatus.MODERATED_REJECTED, QagStatus.SELECTED_FOR_RESPONSE -> SupportQagResult.FAILURE
            QagStatus.OPEN, QagStatus.MODERATED_ACCEPTED -> {
                val supportResult = supportQagRepository.insertSupportQag(supportQagInserting)
                if (supportResult == SupportQagResult.SUCCESS) {
                    updateCache(qagInfo, supportQagInserting.userId)
                }
                supportResult
            }
        }
    }

    private fun updateCache(qagInfo: QagInfo, userId: String) {
        val popularQagsNoThematique = previewPageRepository.getQagPopularList(thematiqueId = null)
        if (popularQagsNoThematique?.any { it.qagInfo.id == qagInfo.id } == true) {
            previewPageRepository.evictQagPopularList(thematiqueId = null)
        }

        val popularQagsWithThematique = previewPageRepository.getQagPopularList(thematiqueId = qagInfo.thematiqueId)
        if (popularQagsWithThematique?.any { it.qagInfo.id == qagInfo.id } == true) {
            previewPageRepository.evictQagPopularList(thematiqueId = qagInfo.thematiqueId)
        }

        previewPageRepository.evictQagSupportedList(userId = userId, thematiqueId = null)
        previewPageRepository.evictQagSupportedList(userId = userId, thematiqueId = qagInfo.thematiqueId)
    }
}