package fr.social.gouv.agora.infrastructure.qag.repository

import fr.social.gouv.agora.domain.QagInserting
import fr.social.gouv.agora.domain.QagStatus
import fr.social.gouv.agora.infrastructure.qag.dto.QagDTO
import fr.social.gouv.agora.usecase.qag.repository.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest
internal class QagInfoRepositoryImplTest {

    @Autowired
    private lateinit var repository: QagInfoRepositoryImpl

    @MockBean
    private lateinit var databaseRepository: QagInfoDatabaseRepository

    @MockBean
    private lateinit var mapper: QagInfoMapper

    @Test
    fun `getQagInfoToModerateList - when has emptyList - should return emptyList`() {
        // Given
        given(databaseRepository.getQagToModerateList()).willReturn(emptyList())

        // When
        val result = repository.getQagInfoToModerateList()

        // Then
        assertThat(result).isEqualTo(emptyList<QagInfo>())
        then(databaseRepository).should(only()).getQagToModerateList()
        then(mapper).shouldHaveNoInteractions()
    }

    @Test
    fun `getQagInfoToModerateList - when has qags - should return mapped qags`() {
        // Given
        val qagDTO = mock(QagDTO::class.java)
        given(databaseRepository.getQagToModerateList()).willReturn(listOf(qagDTO))

        val qagInfo = mock(QagInfo::class.java)
        given(mapper.toDomain(qagDTO)).willReturn(qagInfo)

        // When
        val result = repository.getQagInfoToModerateList()

        // Then
        assertThat(result).isEqualTo(listOf(qagInfo))
        then(databaseRepository).should(only()).getQagToModerateList()
        then(mapper).should(only()).toDomain(qagDTO)
    }

    @Test
    fun `getDisplayedQagInfoList - when has null thematiqueId - should call database without thematique then return mapped qags`() {
        // Given
        val qagDTO = mock(QagDTO::class.java)
        given(databaseRepository.getDisplayedQagList()).willReturn(listOf(qagDTO))

        val qagInfo = mock(QagInfo::class.java)
        given(mapper.toDomain(qagDTO)).willReturn(qagInfo)

        // When
        val result = repository.getDisplayedQagInfoList(thematiqueId = null)

        // Then
        assertThat(result).isEqualTo(listOf(qagInfo))
        then(databaseRepository).should(only()).getDisplayedQagList()
        then(mapper).should(only()).toDomain(qagDTO)
    }

    @Test
    fun `getDisplayedQagInfoList - when has invalid thematiqueId UUID - should call database without thematique then return mapped qags`() {
        // Given
        val qagDTO = mock(QagDTO::class.java)
        given(databaseRepository.getDisplayedQagList()).willReturn(listOf(qagDTO))

        val qagInfo = mock(QagInfo::class.java)
        given(mapper.toDomain(qagDTO)).willReturn(qagInfo)

        // When
        val result = repository.getDisplayedQagInfoList(thematiqueId = "Invalid thematique UUID")

        // Then
        assertThat(result).isEqualTo(listOf(qagInfo))
        then(databaseRepository).should(only()).getDisplayedQagList()
        then(mapper).should(only()).toDomain(qagDTO)
    }

    @Test
    fun `getDisplayedQagInfoList - when has valid thematiqueId UUID - should call database with thematique then return mapped qags`() {
        // Given
        val thematiqueUUID = UUID.randomUUID()

        val qagDTO = mock(QagDTO::class.java)
        given(databaseRepository.getDisplayedQagList(thematiqueId = thematiqueUUID)).willReturn(listOf(qagDTO))

        val qagInfo = mock(QagInfo::class.java)
        given(mapper.toDomain(qagDTO)).willReturn(qagInfo)

        // When
        val result = repository.getDisplayedQagInfoList(thematiqueId = thematiqueUUID.toString())

        // Then
        assertThat(result).isEqualTo(listOf(qagInfo))
        then(databaseRepository).should(only()).getDisplayedQagList(thematiqueId = thematiqueUUID)
        then(mapper).should(only()).toDomain(qagDTO)
    }

    @Test
    fun `getUserQagInfoList - when invalid user UUID - should return emptyList`() {
        // When
        val result = repository.getUserQagInfoList(userId = "Invalid user UUID", thematiqueId = null)

        // Then
        assertThat(result).isEqualTo(emptyList<QagInfo>())
        then(databaseRepository).shouldHaveNoInteractions()
        then(mapper).shouldHaveNoInteractions()
    }

    @Test
    fun `getUserQagInfoList - when has null thematiqueId - should call database without thematique then return mapped qags`() {
        // Given
        val userUUID = UUID.randomUUID()

        val qagDTO = mock(QagDTO::class.java)
        given(databaseRepository.getUserQagList(userId = userUUID)).willReturn(listOf(qagDTO))

        val qagInfo = mock(QagInfo::class.java)
        given(mapper.toDomain(qagDTO)).willReturn(qagInfo)

        // When
        val result = repository.getUserQagInfoList(userId = userUUID.toString(), thematiqueId = null)

        // Then
        assertThat(result).isEqualTo(listOf(qagInfo))
        then(databaseRepository).should(only()).getDisplayedQagList()
        then(mapper).should(only()).toDomain(qagDTO)
    }

    @Test
    fun `getUserQagInfoList - when has invalid thematique UUID - should call database with thematique then return mapped qags`() {
        // Given
        val userUUID = UUID.randomUUID()

        val qagDTO = mock(QagDTO::class.java)
        given(databaseRepository.getUserQagList(userId = userUUID)).willReturn(listOf(qagDTO))

        val qagInfo = mock(QagInfo::class.java)
        given(mapper.toDomain(qagDTO)).willReturn(qagInfo)

        // When
        val result =
            repository.getUserQagInfoList(userId = userUUID.toString(), thematiqueId = "Invalid thematique UUID")

        // Then
        assertThat(result).isEqualTo(listOf(qagInfo))
        then(databaseRepository).should(only()).getDisplayedQagList()
        then(mapper).should(only()).toDomain(qagDTO)
    }

    @Test
    fun `getUserQagInfoList - when has valid thematiqueId UUID - should call database without thematique then return mapped qags`() {
        // Given
        val userUUID = UUID.randomUUID()
        val thematiqueUUID = UUID.randomUUID()

        val qagDTO = mock(QagDTO::class.java)
        given(databaseRepository.getUserQagList(userId = userUUID, thematiqueId = thematiqueUUID))
            .willReturn(listOf(qagDTO))

        val qagInfo = mock(QagInfo::class.java)
        given(mapper.toDomain(qagDTO)).willReturn(qagInfo)

        // When
        val result =
            repository.getUserQagInfoList(userId = userUUID.toString(), thematiqueId = thematiqueUUID.toString())

        // Then
        assertThat(result).isEqualTo(listOf(qagInfo))
        then(databaseRepository).should(only()).getDisplayedQagList()
        then(mapper).should(only()).toDomain(qagDTO)
    }

    @Test
    fun `getQagInfo - when invalid UUID - should return null`() {
        // When
        val result = repository.getQagInfo(qagId = "Invalid qag UUID")

        // Then
        assertThat(result).isEqualTo(null)
        then(databaseRepository).shouldHaveNoInteractions()
        then(mapper).shouldHaveNoInteractions()
    }

    @Test
    fun `getQagInfo - when valid UUID - should return mapped result from database`() {
        // Given
        val qagUUID = UUID.randomUUID()
        val qagDTO = mock(QagDTO::class.java)
        given(databaseRepository.getQagById(qagId = qagUUID)).willReturn(qagDTO)

        val qagInfo = mock(QagInfo::class.java)
        given(mapper.toDomain(qagDTO)).willReturn(qagInfo)

        // When
        val result = repository.getQagInfo(qagId = qagUUID.toString())

        // Then
        assertThat(result).isEqualTo(qagInfo)
        then(databaseRepository).should(only()).getQagById(qagId = qagUUID)
        then(mapper).should(only()).toDomain(qagDTO)
    }

    @Test
    fun `insertQagInfo - when mapper returns null - should return FAILURE`() {
        // Given
        val qagInserting = mock(QagInserting::class.java)
        given(mapper.toDto(qagInserting)).willReturn(null)

        // When
        val result = repository.insertQagInfo(qagInserting)

        // Then
        assertThat(result).isEqualTo(QagInsertionResult.Failure)
        then(databaseRepository).shouldHaveNoInteractions()
        then(mapper).should(only()).toDto(qagInserting)
    }

    @Test
    fun `insertQagInfo - when mapper returns DTO - should return SUCCESS`() {
        // Given
        val qagInserting = mock(QagInserting::class.java)
        val qagDTO = mock(QagDTO::class.java)
        given(mapper.toDto(qagInserting)).willReturn(qagDTO)

        val savedQagDTO = mock(QagDTO::class.java)
        given(databaseRepository.save(qagDTO)).willReturn(savedQagDTO)

        val qagInfo = mock(QagInfo::class.java)
        given(mapper.toDomain(savedQagDTO)).willReturn(qagInfo)

        // When
        val result = repository.insertQagInfo(qagInserting)

        // Then
        assertThat(result).isEqualTo(QagInsertionResult.Success(qagInfo = qagInfo))
        then(databaseRepository).should(only()).save(qagDTO)
    }

    @Test
    fun `updateQagStatus - when invalid qag UUID - should return Failure`() {
        // When
        val result = repository.updateQagStatus(qagId = "Invalid QaG UUID", newQagStatus = QagStatus.MODERATED_ACCEPTED)

        // Then
        assertThat(result).isEqualTo(QagUpdateResult.Failure)
        then(databaseRepository).shouldHaveNoInteractions()
        then(mapper).shouldHaveNoInteractions()
    }

    @Test
    fun `updateQagStatus - when valid qag UUID but updates nothing - should return Failure`() {
        // Given
        val qagUUID = UUID.randomUUID()
        given(mapper.toIntStatus(qagStatus = QagStatus.MODERATED_REJECTED)).willReturn(49)
        given(databaseRepository.updateQagStatus(qagId = qagUUID, newStatus = 49)).willReturn(0)

        // When
        val result = repository.updateQagStatus(qagId = qagUUID.toString(), newQagStatus = QagStatus.MODERATED_REJECTED)

        // Then
        assertThat(result).isEqualTo(QagUpdateResult.Failure)
        then(databaseRepository).should(only()).updateQagStatus(qagId = qagUUID, newStatus = 49)
        then(mapper).shouldHaveNoInteractions()
    }

    @Test
    fun `updateQagStatus - when valid qag UUID, updates something but getQag is null - should return Failure`() {
        // Given
        val qagUUID = UUID.randomUUID()
        given(mapper.toIntStatus(qagStatus = QagStatus.MODERATED_REJECTED)).willReturn(6)
        given(databaseRepository.updateQagStatus(qagId = qagUUID, newStatus = 6)).willReturn(0)

        given(databaseRepository.getQagById(qagId = qagUUID)).willReturn(null)

        // When
        val result = repository.updateQagStatus(qagId = qagUUID.toString(), newQagStatus = QagStatus.OPEN)

        // Then
        assertThat(result).isEqualTo(QagUpdateResult.Failure)
        then(databaseRepository).should().updateQagStatus(qagId = qagUUID, newStatus = 6)
        then(databaseRepository).should().getQagById(qagId = qagUUID)
        then(databaseRepository).shouldHaveNoMoreInteractions()
        then(mapper).shouldHaveNoInteractions()
    }

    @Test
    fun `updateQagStatus - when valid qag UUID and getQag returns qag - should return Success`() {
        // Given
        given(mapper.toIntStatus(qagStatus = QagStatus.ARCHIVED)).willReturn(77)
        val qagUUID = UUID.randomUUID()
        val qagDTO = mock(QagDTO::class.java)
        given(databaseRepository.getQagById(qagId = qagUUID)).willReturn(qagDTO)

        val qagInfo = mock(QagInfo::class.java)
        given(mapper.toDomain(qagDTO)).willReturn(qagInfo)

        // When
        val result = repository.updateQagStatus(qagId = qagUUID.toString(), newQagStatus = QagStatus.ARCHIVED)

        // Then
        assertThat(result).isEqualTo(QagUpdateResult.Success(updatedQagInfo = qagInfo))
        then(databaseRepository).should().updateQagStatus(qagId = qagUUID, newStatus = 77)
        then(databaseRepository).should().getQagById(qagId = qagUUID)
        then(databaseRepository).shouldHaveNoMoreInteractions()
        then(mapper).should(only()).toDomain(qagDTO)
    }

    @Test
    fun `selectQagForResponse - when invalid qag UUID - should return Failure`() {
        // When
        val result = repository.selectQagForResponse(qagId = "Invalid QaG UUID")

        // Then
        assertThat(result).isEqualTo(QagUpdateResult.Failure)
        then(databaseRepository).shouldHaveNoInteractions()
        then(mapper).shouldHaveNoInteractions()
    }

    @Test
    fun `selectQagForResponse - when valid qag UUID but updates nothing - should return Failure`() {
        // Given
        val qagUUID = UUID.randomUUID()
        given(databaseRepository.selectQagForResponse(qagId = qagUUID)).willReturn(0)

        // When
        val result = repository.selectQagForResponse(qagId = qagUUID.toString())

        // Then
        assertThat(result).isEqualTo(QagUpdateResult.Failure)
        then(databaseRepository).should(only()).selectQagForResponse(qagId = qagUUID)
        then(mapper).shouldHaveNoInteractions()
    }

    @Test
    fun `selectQagForResponse - when valid qag UUID, updated something but getQag is null - should return Failure`() {
        // Given
        val qagUUID = UUID.randomUUID()
        given(databaseRepository.getQagById(qagId = qagUUID)).willReturn(null)

        // When
        val result = repository.selectQagForResponse(qagId = qagUUID.toString())

        // Then
        assertThat(result).isEqualTo(QagUpdateResult.Failure)
        then(databaseRepository).should().selectQagForResponse(qagId = qagUUID)
        then(databaseRepository).should().getQagById(qagId = qagUUID)
        then(databaseRepository).shouldHaveNoMoreInteractions()
        then(mapper).shouldHaveNoInteractions()
    }

    @Test
    fun `selectQagForResponse - when valid qag UUID and getQag returns qag - should return Success`() {
        // Given
        val qagUUID = UUID.randomUUID()
        val qagDTO = mock(QagDTO::class.java)
        given(databaseRepository.getQagById(qagId = qagUUID)).willReturn(qagDTO)

        val qagInfo = mock(QagInfo::class.java)
        given(mapper.toDomain(qagDTO)).willReturn(qagInfo)

        // When
        val result = repository.selectQagForResponse(qagId = qagUUID.toString())

        // Then
        assertThat(result).isEqualTo(QagUpdateResult.Success(updatedQagInfo = qagInfo))
        then(databaseRepository).should().selectQagForResponse(qagId = qagUUID)
        then(databaseRepository).should().getQagById(qagId = qagUUID)
        then(databaseRepository).shouldHaveNoMoreInteractions()
        then(mapper).should(only()).toDomain(qagDTO)
    }

    @Test
    fun `archiveOldQags - should call database archive and anonymize then clear cache`() {
        // Given
        val resetDate = mock(Date::class.java)

        // When
        repository.archiveOldQags(resetDate)

        // Then
        then(databaseRepository).should().archiveQagsBeforeDate(resetDate)
        then(databaseRepository).should().anonymizeRejectedQagsBeforeDate(resetDate)
        then(databaseRepository).shouldHaveNoMoreInteractions()
    }

    @Test
    fun `deleteQag - when invalid qag UUID - should return FAILURE`() {
        // When
        val result = repository.deleteQag(qagId = "Invalid qag UUID")

        // Then
        assertThat(result).isEqualTo(QagDeleteResult.Failure)
        then(databaseRepository).shouldHaveNoInteractions()
    }

    @Test
    fun `deleteQag - when valid qag UUID, but does not exist in Database - should return Failure`() {
        // Given
        val qagUUID = UUID.randomUUID()
        given(databaseRepository.getQagById(qagId = qagUUID)).willReturn(null)

        // When
        val result = repository.deleteQag(qagId = qagUUID.toString())

        // Then
        assertThat(result).isEqualTo(QagDeleteResult.Failure)
        then(databaseRepository).should(only()).getQagById(qagId = qagUUID)
    }

    @Test
    fun `deleteQag - when valid qag UUID, exists in database but deletes nothing - should return FAILURE`() {
        // Given
        val qagUUID = UUID.randomUUID()
        given(databaseRepository.getQagById(qagId = qagUUID)).willReturn(mock(QagDTO::class.java))
        given(databaseRepository.deleteQagById(qagId = qagUUID)).willReturn(0)

        // When
        val result = repository.deleteQag(qagId = qagUUID.toString())

        // Then
        assertThat(result).isEqualTo(QagDeleteResult.Failure)
        then(databaseRepository).should(only()).deleteQagById(qagId = qagUUID)
    }

    @Test
    fun `deleteQag - when valid qag UUID, exists in database and deletes something - should return Success`() {
        // Given
        val qagUUID = UUID.randomUUID()
        val qagDTO = mock(QagDTO::class.java)
        given(databaseRepository.getQagById(qagId = qagUUID)).willReturn(mock(QagDTO::class.java))
        given(databaseRepository.deleteQagById(qagId = qagUUID)).willReturn(1)

        val qagInfo = mock(QagInfo::class.java)
        given(mapper.toDomain(qagDTO)).willReturn(qagInfo)

        // When
        val result = repository.deleteQag(qagId = qagUUID.toString())

        // Then
        assertThat(result).isEqualTo(QagDeleteResult.Success(qagInfo))
        then(databaseRepository).should(only()).deleteQagById(qagId = qagUUID)
    }

    @Test
    fun `deleteQag - when valid UUID for qagID, deletes something AND exists in Database - should return SUCCESS`() {
        // Given
        val qagUUID = UUID.randomUUID()
        given(databaseRepository.deleteQagById(qagId = qagUUID)).willReturn(1)

        val qagDTO = mock(QagDTO::class.java)
        given(databaseRepository.getQagById(qagId = qagUUID)).willReturn(null)

        val qagInfo = mock(QagInfo::class.java)
        given(mapper.toDomain(qagDTO)).willReturn(qagInfo)

        // When
        val result = repository.deleteQag(qagId = qagUUID.toString())

        // Then
        assertThat(result).isEqualTo(QagDeleteResult.Success(deletedQagInfo = qagInfo))
        then(databaseRepository).should(only()).deleteQagById(qagId = qagUUID)
    }

}