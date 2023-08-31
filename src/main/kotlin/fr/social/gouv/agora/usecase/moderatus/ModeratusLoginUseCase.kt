package fr.social.gouv.agora.usecase.moderatus

import fr.social.gouv.agora.domain.UserAuthorization
import fr.social.gouv.agora.domain.UserInfo
import fr.social.gouv.agora.infrastructure.login.DecodeResult
import fr.social.gouv.agora.infrastructure.login.LoginTokenGenerator
import fr.social.gouv.agora.usecase.login.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class ModeratusLoginUseCase(
    private val loginTokenGenerator: LoginTokenGenerator,
    private val userRepository: UserRepository,
) {

    fun login(loginToken: String): ModeratusLoginResult {
        return when (val decodeResult = loginTokenGenerator.decodeLoginToken(loginToken)) {
            DecodeResult.Failure -> ModeratusLoginResult.Failure
            is DecodeResult.Success -> userRepository
                .getUserById(decodeResult.loginTokenData.userId)
                ?.takeIf { userInfo -> canModerateQag(userInfo) }
                ?.let { userInfo -> ModeratusLoginResult.Success(userInfo.userId) }
                ?: ModeratusLoginResult.Failure
        }
    }

    private fun canModerateQag(userInfo: UserInfo) = userInfo.authorizationList.contains(UserAuthorization.MODERATE_QAG)

}

sealed class ModeratusLoginResult {
    object Failure : ModeratusLoginResult()
    data class Success(val userId: String) : ModeratusLoginResult()
}