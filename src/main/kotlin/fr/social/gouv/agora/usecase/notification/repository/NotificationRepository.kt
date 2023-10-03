package fr.social.gouv.agora.usecase.notification.repository

import fr.social.gouv.agora.domain.Notification
import fr.social.gouv.agora.domain.NotificationInserting

interface NotificationRepository {
    fun insertNotifications(notification: NotificationInserting): NotificationInsertionResult
    fun getUserNotificationList(userId: String): List<Notification>
}

enum class NotificationInsertionResult {
    SUCCESS, FAILURE
}