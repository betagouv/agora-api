package fr.social.gouv.agora.infrastructure.profile.repository

import fr.social.gouv.agora.domain.Profile
import fr.social.gouv.agora.domain.ProfileInserting
import fr.social.gouv.agora.infrastructure.profile.dto.ProfileDTO
import fr.social.gouv.agora.infrastructure.profile.repository.ProfileCacheRepository.CacheResult
import fr.social.gouv.agora.usecase.profile.repository.ProfileEditResult
import fr.social.gouv.agora.usecase.profile.repository.ProfileRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
class ProfileRepositoryImpl(
    private val databaseRepository: ProfileDatabaseRepository,
    private val cacheRepository: ProfileCacheRepository,
    private val mapper: ProfileMapper,
) : ProfileRepository {

    override fun getProfile(userId: String): Profile? {
        return try {
            val userUUID = UUID.fromString(userId)
            when (val cacheResult = cacheRepository.getProfile(userUUID)) {
                CacheResult.CacheNotInitialized -> getProfileFromDatabase(userUUID)
                CacheResult.CachedProfileNotFound -> null
                is CacheResult.CachedProfile -> cacheResult.profileDTO
            }?.let { profileDTO -> mapper.toDomain(profileDTO) }
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    override fun insertProfile(profileInserting: ProfileInserting): ProfileEditResult {
        mapper.toDto(profileInserting)?.let { profileDTO ->
            val savedProfileDTO = databaseRepository.save(profileDTO)
            cacheRepository.insertProfile(userUUID = savedProfileDTO.userId, profileDTO = savedProfileDTO)
            return ProfileEditResult.SUCCESS
        }
        return ProfileEditResult.FAILURE
    }

    override fun updateProfile(profileInserting: ProfileInserting): ProfileEditResult {
        return mapper.toDto(profileInserting)?.let { newProfileDTO ->
            val oldProfileDTO = when (val cacheResult = cacheRepository.getProfile(newProfileDTO.userId)) {
                CacheResult.CacheNotInitialized -> databaseRepository.getProfile(newProfileDTO.userId)
                CacheResult.CachedProfileNotFound -> null
                is CacheResult.CachedProfile -> cacheResult.profileDTO
            }

            if (oldProfileDTO != null) {
                val updatedProfileDTO = mapper.updateProfile(
                    oldProfileDTO = oldProfileDTO,
                    newProfileDTO = newProfileDTO,
                )
                val savedProfileDTO = databaseRepository.save(updatedProfileDTO)
                cacheRepository.insertProfile(savedProfileDTO.userId, savedProfileDTO)
                ProfileEditResult.SUCCESS
            } else ProfileEditResult.FAILURE
        } ?: ProfileEditResult.FAILURE
    }

    private fun getProfileFromDatabase(userUUID: UUID): ProfileDTO? {
        val profileDTO = databaseRepository.getProfile(userUUID)
        cacheRepository.insertProfile(userUUID, profileDTO)
        return profileDTO
    }
}
