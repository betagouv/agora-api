package fr.social.gouv.agora.usecase.qag

import fr.social.gouv.agora.domain.QagPreview
import fr.social.gouv.agora.usecase.qag.repository.QagSupportedListRepository
import fr.social.gouv.agora.usecase.supportQag.repository.GetSupportQagRepository
import fr.social.gouv.agora.usecase.thematique.repository.ThematiqueRepository
import org.springframework.stereotype.Service

@Service
class GetQagSupportedPreviewListUseCase(
    private val thematiqueRepository: ThematiqueRepository,
    private val supportRepository: GetSupportQagRepository,
    private val qagSupportedListRepository: QagSupportedListRepository,
) {
    fun getQagSupportedPreviewList(userId: String, thematiqueId: String?): List<QagPreview> {
        return qagSupportedListRepository.getQagSupportedList(
            thematiqueId = thematiqueId.takeUnless { it.isNullOrBlank() },
            userId = userId,
        ).mapNotNull { qagInfo ->
            thematiqueRepository.getThematique(qagInfo.thematiqueId)?.let { thematique ->
                supportRepository.getSupportQag(qagId = qagInfo.id, userId = userId)?.let { supportQag ->
                    QagPreview(
                        id = qagInfo.id,
                        thematique = thematique,
                        title = qagInfo.title,
                        username = qagInfo.username,
                        date = qagInfo.date,
                        support = supportQag,
                    )
                }
            }
        }.sortedByDescending { it.date }
    }
}
