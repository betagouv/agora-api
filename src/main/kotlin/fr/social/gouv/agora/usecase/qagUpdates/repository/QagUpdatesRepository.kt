package fr.social.gouv.agora.usecase.qagUpdates.repository

import fr.social.gouv.agora.domain.QagUpdates

interface QagUpdatesRepository {
    fun setQagUpdates(qagUpdates: QagUpdates)
}

