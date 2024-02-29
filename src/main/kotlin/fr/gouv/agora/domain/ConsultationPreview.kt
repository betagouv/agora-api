package fr.gouv.agora.domain

import java.util.*

sealed class ConsultationPreview {
    abstract val id: String
    abstract val title: String
    abstract val coverUrl: String
    abstract val thematique: Thematique
}

data class ConsultationPreviewOngoing(
    override val id: String,
    override val title: String,
    override val coverUrl: String,
    override val thematique: Thematique,
    val endDate: Date,
    val highlightLabel: String?,
) : ConsultationPreview()

data class ConsultationPreviewFinished(
    override val id: String,
    override val title: String,
    override val coverUrl: String,
    override val thematique: Thematique,
    val step: ConsultationStatus,
    val updateLabel: String?,
) : ConsultationPreview()

data class ConsultationPreviewAnswered(
    override val id: String,
    override val title: String,
    override val coverUrl: String,
    override val thematique: Thematique,
    val step: ConsultationStatus,
    val updateLabel: String?,
) : ConsultationPreview()

