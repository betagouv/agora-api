package fr.social.gouv.agora.infrastructure.moderatus.repository

import fr.social.gouv.agora.usecase.moderatus.repository.ModeratusQagLockRepository
import org.springframework.stereotype.Component

@Component
@Suppress("unused")
class ModeratusQagLockRepositoryImpl(
    private val cacheRepository: ModeratusQagLockCacheRepository
) : ModeratusQagLockRepository {

    override fun getLockedQagIds(): List<String> {
        return cacheRepository.getLockedQagIds()
    }

    override fun addLockedIds(lockedQagIds: List<String>) {
        cacheRepository.addLockedIds(lockedQagIds)
    }

    override fun removeLockedQagId(qagId: String) {
        cacheRepository.removeLockedQagId(qagId)
    }

}