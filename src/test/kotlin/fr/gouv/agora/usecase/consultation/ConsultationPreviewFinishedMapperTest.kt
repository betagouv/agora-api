package fr.gouv.agora.usecase.consultation

import fr.gouv.agora.TestUtils
import fr.gouv.agora.TestUtils.lenientGiven
import fr.gouv.agora.TestUtils.willReturn
import fr.gouv.agora.domain.ConsultationPreviewFinished
import fr.gouv.agora.domain.ConsultationStatus
import fr.gouv.agora.domain.Thematique
import fr.gouv.agora.infrastructure.utils.DateUtils.toDate
import fr.gouv.agora.usecase.consultation.repository.ConsultationWithUpdateInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.util.*

@ExtendWith(MockitoExtension::class)
class ConsultationPreviewFinishedMapperTest {

    private lateinit var mapper: ConsultationPreviewFinishedMapper

    companion object {
        @JvmStatic
        fun updateLabelTestCases() = arrayOf(
            input(
                testName = "when updateLabel is null - should return null updateLabel",
                serverDate = LocalDateTime.of(2024, Month.JANUARY, 2, 12, 30),
                updateDate = LocalDateTime.of(2024, Month.JANUARY, 1, 12, 30),
                updateLabel = null,
                expectedUpdateLabel = null,
            ),
            input(
                testName = "when serverDate < updateDate - should return null updateLabel",
                serverDate = LocalDateTime.of(2024, Month.JANUARY, 1, 12, 30),
                updateDate = LocalDateTime.of(2024, Month.JANUARY, 2, 12, 30),
                updateLabel = "New update !",
                expectedUpdateLabel = null,
            ),
            input(
                testName = "when updateDate < serverDate < updateDate+90d - should return updateLabel",
                serverDate = LocalDateTime.of(2024, Month.JANUARY, 7, 12, 30),
                updateDate = LocalDateTime.of(2024, Month.JANUARY, 1, 12, 30),
                updateLabel = "New update !",
                expectedUpdateLabel = "New update !",
            ),
            input(
                testName = "when updateDate < updateDate+90d < serverDate - should return null updateLabel",
                serverDate = LocalDateTime.of(2024, Month.APRIL, 10, 12, 30),
                updateDate = LocalDateTime.of(2024, Month.JANUARY, 1, 12, 30),
                updateLabel = "New update !",
                expectedUpdateLabel = null,
            ),
            input(
                testName = "when updateDate = serverDate - should return updateLabel",
                serverDate = LocalDateTime.of(2024, Month.JANUARY, 1, 12, 30),
                updateDate = LocalDateTime.of(2024, Month.JANUARY, 1, 12, 30),
                updateLabel = "New update !",
                expectedUpdateLabel = "New update !",
            ),
            input(
                testName = "when updateDate+90d = serverDate - should return updateLabel",
                serverDate = LocalDateTime.of(2024, Month.MARCH, 31, 12, 30),
                updateDate = LocalDateTime.of(2024, Month.JANUARY, 1, 12, 30),
                updateLabel = "New update !",
                expectedUpdateLabel = "New update !",
            ),
            input(
                testName = "when updateDate+90d + 1s = serverDate - should return null updateLabel",
                serverDate = LocalDateTime.of(2024, Month.MARCH, 31, 12, 30, 1),
                updateDate = LocalDateTime.of(2024, Month.JANUARY, 1, 12, 30),
                updateLabel = "New update !",
                expectedUpdateLabel = null,
            ),
        )

        private fun input(
            testName: String,
            serverDate: LocalDateTime,
            updateDate: LocalDateTime,
            updateLabel: String?,
            expectedUpdateLabel: String?,
        ) = arrayOf(testName, serverDate, updateDate, updateLabel, expectedUpdateLabel)
    }

    @Test
    fun `toConsultationPreviewFinished - when serverDate has not reached endDate - should return status COLLECTING_DATA`() {
        // Given
        mockCurrentDate(LocalDateTime.of(2024, Month.JANUARY, 1, 12, 30))
        val updateDate = LocalDate.of(2024, 12, 23)
        val consultationInfo = mock(ConsultationWithUpdateInfo::class.java).also {
            given(it.id).willReturn("consultationId")
            given(it.title).willReturn("title")
            given(it.coverUrl).willReturn("coverUrl")
            given(it.endDate).willReturn(LocalDateTime.of(2024, Month.JANUARY, 30, 18, 45).toDate())
            given(it.updateLabel).willReturn(null)
            given(it.updateDate).willReturn(updateDate.toDate())
        }
        val thematique = mock(Thematique::class.java)

        // When
        val result = mapper.toConsultationPreviewFinished(consultationInfo, thematique)

        // Then
        assertThat(result).isEqualTo(
            ConsultationPreviewFinished(
                id = "consultationId",
                title = "title",
                coverUrl = "coverUrl",
                thematique = thematique,
                step = ConsultationStatus.COLLECTING_DATA,
                updateLabel = null,
                updateDate = updateDate,
            )
        )
    }

    @Test
    fun `toConsultationPreviewFinished - when serverDate is greater than endDate - should return status POLITICAL_COMMITMENT`() {
        // Given
        mockCurrentDate(LocalDateTime.of(2024, Month.JANUARY, 10, 12, 30))
        val updateDate = LocalDate.of(2024, 12, 23)
        val consultationInfo = mock(ConsultationWithUpdateInfo::class.java).also {
            given(it.id).willReturn("consultationId")
            given(it.title).willReturn("title")
            given(it.coverUrl).willReturn("coverUrl")
            given(it.endDate).willReturn(LocalDateTime.of(2024, Month.JANUARY, 1, 4, 15).toDate())
            given(it.updateLabel).willReturn(null)
            given(it.updateDate).willReturn(updateDate.toDate())
        }
        val thematique = mock(Thematique::class.java)

        // When
        val result = mapper.toConsultationPreviewFinished(consultationInfo, thematique)

        // Then
        assertThat(result).isEqualTo(
            ConsultationPreviewFinished(
                id = "consultationId",
                title = "title",
                coverUrl = "coverUrl",
                thematique = thematique,
                step = ConsultationStatus.POLITICAL_COMMITMENT,
                updateLabel = null,
                updateDate = updateDate,
            )
        )
    }

    @Test
    fun `toConsultationPreviewFinished - when serverDate is equal to endDate - should return status POLITICAL_COMMITMENT`() {
        // Given
        mockCurrentDate(LocalDateTime.of(2024, Month.JANUARY, 1, 12, 30))
        val updateDate = LocalDate.of(2024, 12, 23)
        val consultationInfo = mock(ConsultationWithUpdateInfo::class.java).also {
            given(it.id).willReturn("consultationId")
            given(it.title).willReturn("title")
            given(it.coverUrl).willReturn("coverUrl")
            given(it.endDate).willReturn(LocalDateTime.of(2024, Month.JANUARY, 1, 12, 30).toDate())
            given(it.updateLabel).willReturn(null)
            given(it.updateDate).willReturn(updateDate.toDate())
        }
        val thematique = mock(Thematique::class.java)

        // When
        val result = mapper.toConsultationPreviewFinished(consultationInfo, thematique)

        // Then
        assertThat(result).isEqualTo(
            ConsultationPreviewFinished(
                id = "consultationId",
                title = "title",
                coverUrl = "coverUrl",
                thematique = thematique,
                step = ConsultationStatus.POLITICAL_COMMITMENT,
                updateLabel = null,
                updateDate = updateDate,
            )
        )
    }

    @ParameterizedTest(name = "toConsultationPreviewFinished - {0}")
    @MethodSource("updateLabelTestCases")
    fun `toConsultationPreviewFinished - should return expected`(
        @Suppress("UNUSED_PARAMETER")
        testName: String,
        serverDate: LocalDateTime,
        updateDate: LocalDateTime,
        updateLabel: String?,
        expectedUpdateLabel: String?,
    ) {
        // Given
        mockCurrentDate(serverDate)
        val consultationInfo = mock(ConsultationWithUpdateInfo::class.java).also {
            given(it.id).willReturn("consultationId")
            given(it.title).willReturn("title")
            given(it.coverUrl).willReturn("coverUrl")
            given(it.endDate).willReturn(Date(1))
            lenientGiven(it.updateDate).willReturn(updateDate.toDate())
            given(it.updateLabel).willReturn(updateLabel)
        }
        val thematique = mock(Thematique::class.java)

        // When
        val result = mapper.toConsultationPreviewFinished(consultationInfo = consultationInfo, thematique = thematique)

        // Then
        assertThat(result).isEqualTo(
            ConsultationPreviewFinished(
                id = "consultationId",
                title = "title",
                coverUrl = "coverUrl",
                thematique = thematique,
                step = ConsultationStatus.POLITICAL_COMMITMENT,
                updateLabel = expectedUpdateLabel,
                updateDate = updateDate.toLocalDate(),
            )
        )
    }

    private fun mockCurrentDate(serverDate: LocalDateTime) {
        mapper = ConsultationPreviewFinishedMapper(clock = TestUtils.getFixedClock(serverDate))
    }

}
