package fr.social.gouv.agora.usecase.responseQag.repository

import fr.social.gouv.agora.domain.ResponseQag

interface ResponseQagRepository {
    fun getResponseQag(qagId: String): ResponseQag?
}