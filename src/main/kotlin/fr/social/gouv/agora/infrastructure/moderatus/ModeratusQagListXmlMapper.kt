package fr.social.gouv.agora.infrastructure.moderatus

import fr.social.gouv.agora.domain.ModeratusQag
import fr.social.gouv.agora.infrastructure.profile.repository.DateMapper
import org.springframework.stereotype.Component

@Component
class ModeratusQagListXmlMapper(private val dateMapper: DateMapper) {

    fun toXml(qags: List<ModeratusQag>): ModeratusQagListXml {
        return ModeratusQagListXml(
            qagToModerateCount = qags.size,
            qagsToModerate = qags.map(::toXml),
        )
    }

    private fun toXml(qag: ModeratusQag): ModeratusQagXml {
        return ModeratusQagXml(
            qagId = qag.qagId,
            postDate = dateMapper.toFormattedDate(qag.date),
            userId = qag.userId,
            username = qag.username,
            title = qag.title,
            description = qag.description,
        )
    }

}