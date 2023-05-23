package fr.social.gouv.agora.usecase.profile

import fr.social.gouv.agora.domain.Profile
import fr.social.gouv.agora.usecase.profile.repository.ProfileRepository
import org.springframework.stereotype.Service

@Service
class GetProfileUseCase(
    private val profileRepository: ProfileRepository,
) {
    fun getProfile(userId: String): Profile? {
        return profileRepository.getProfile(userId)
    }
}
