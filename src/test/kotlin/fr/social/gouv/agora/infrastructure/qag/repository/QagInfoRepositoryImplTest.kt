package fr.social.gouv.agora.infrastructure.qag.repository

import fr.social.gouv.agora.domain.QagInserting
import fr.social.gouv.agora.infrastructure.qag.dto.QagDTO
import fr.social.gouv.agora.usecase.qag.repository.QagInfo
import fr.social.gouv.agora.usecase.qag.repository.QagInsertionResult
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.cache.annotation.EnableCaching
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest
@EnableCaching
@ImportAutoConfiguration(
    CacheAutoConfiguration::class,
    RedisAutoConfiguration::class,
)
internal class QagInfoRepositoryImplTest {

    @Autowired
    private lateinit var repository: QagInfoRepositoryImpl

    @MockBean
    private lateinit var databaseRepository: QagInfoDatabaseRepository

    @MockBean
    private lateinit var cacheRepository: QagCacheRepository

    @MockBean
    private lateinit var mapper: QagInfoMapper

    @Nested
    inner class GetAllQagInfoTestCases {

        @Test
        fun `getQag - when cache is not initialized - should initialize cache with database then return mapped results`() {
            // Given
            given(cacheRepository.isInitialized()).willReturn(false)
            val qagDTO = mock(QagDTO::class.java)
            given(databaseRepository.getAllQagList()).willReturn(listOf(qagDTO))

            val qagInfo = mock(QagInfo::class.java)
            given(mapper.toDomain(qagDTO)).willReturn(qagInfo)

            // When
            val result = repository.getAllQagInfo()

            // Then
            assertThat(result).isEqualTo(listOf(qagInfo))
            inOrder(cacheRepository, databaseRepository, mapper).also {
                then(cacheRepository).should(it).isInitialized()
                then(databaseRepository).should(it).getAllQagList()
                then(cacheRepository).should(it).initializeCache(listOf(qagDTO))
                then(mapper).should(it).toDomain(qagDTO)
                it.verifyNoMoreInteractions()
            }
        }

        @Test
        fun `getQag - when cache is initialized - should return mapped result`() {
            // Given
            given(cacheRepository.isInitialized()).willReturn(true)
            val qagDTO = mock(QagDTO::class.java)
            val allQagDTO = listOf(qagDTO)
            given(cacheRepository.getAllQagList()).willReturn(allQagDTO)

            val qagInfo = mock(QagInfo::class.java)
            given(mapper.toDomain(qagDTO)).willReturn(qagInfo)

            // When
            val result = repository.getAllQagInfo()

            // Then
            assertThat(result).isEqualTo(listOf(qagInfo))
            inOrder(cacheRepository, databaseRepository, mapper).also {
                then(cacheRepository).should(it).isInitialized()
                then(cacheRepository).should(it).getAllQagList()
                then(mapper).should(it).toDomain(qagDTO)
                it.verifyNoMoreInteractions()
            }
            then(databaseRepository).shouldHaveNoInteractions()
        }

    }

    @Nested
    inner class GetQagInfoTestCases {
        @Test
        fun `getQag - when invalid UUID - should return null`() {
            // When
            val result = repository.getQagInfo(qagId = "invalid UUID")

            // Then
            assertThat(result).isEqualTo(null)
            then(cacheRepository).shouldHaveNoInteractions()
            then(databaseRepository).shouldHaveNoInteractions()
            then(mapper).shouldHaveNoInteractions()
        }

        @Test
        fun `getQag - when cache is not initialized and has no result - should initialize cache with database then return null`() {
            // Given
            val qagId = UUID.randomUUID()
            given(cacheRepository.isInitialized()).willReturn(false)
            val allQagDTO = listOf(
                createMockQagDTO(qagId = UUID.randomUUID()),
            )
            given(databaseRepository.getAllQagList()).willReturn(allQagDTO)

            // When
            val result = repository.getQagInfo(qagId = qagId.toString())

            // Then
            assertThat(result).isEqualTo(null)
            inOrder(cacheRepository, databaseRepository).also {
                then(cacheRepository).should(it).isInitialized()
                then(databaseRepository).should(it).getAllQagList()
                then(cacheRepository).should(it).initializeCache(allQagDTO)
                it.verifyNoMoreInteractions()
            }
            then(mapper).shouldHaveNoInteractions()
        }

        @Test
        fun `getQag - when cache is initialized and has result - should return mapped result`() {
            // Given
            val qagId = UUID.randomUUID()
            given(cacheRepository.isInitialized()).willReturn(true)
            val qagDTO = createMockQagDTO(qagId = qagId)
            val allQagDTO = listOf(
                qagDTO,
                createMockQagDTO(qagId = UUID.randomUUID()),
            )
            given(cacheRepository.getAllQagList()).willReturn(allQagDTO)

            val qagInfo = mock(QagInfo::class.java)
            given(mapper.toDomain(qagDTO)).willReturn(qagInfo)

            // When
            val result = repository.getQagInfo(qagId = qagId.toString())

            // Then
            assertThat(result).isEqualTo(qagInfo)
            inOrder(cacheRepository, databaseRepository, mapper).also {
                then(cacheRepository).should(it).isInitialized()
                then(cacheRepository).should(it).getAllQagList()
                then(mapper).should(it).toDomain(qagDTO)
                it.verifyNoMoreInteractions()
            }
            then(databaseRepository).shouldHaveNoInteractions()
        }

        private fun createMockQagDTO(qagId: UUID) = mock(QagDTO::class.java).also {
            given(it.id).willReturn(qagId)
        }
    }

    @Nested
    inner class InsertQagInfoTestCases {
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
            then(cacheRepository).shouldHaveNoInteractions()
        }

        @Test
        fun `insertQagInfo - when mapper returns DTO - should return SUCCESS`() {
            // Given
            val qagInserting = mock(QagInserting::class.java)
            val qagDTO = mock(QagDTO::class.java)
            given(mapper.toDto(qagInserting)).willReturn(qagDTO)

            val savedQagId = UUID.randomUUID()
            val savedQagDTO = mock(QagDTO::class.java).also {
                given(it.id).willReturn(savedQagId)
            }
            given(databaseRepository.save(qagDTO)).willReturn(savedQagDTO)

            // When
            val result = repository.insertQagInfo(qagInserting)

            // Then
            assertThat(result).isEqualTo(QagInsertionResult.Success(qagId = savedQagId))
            then(databaseRepository).should(only()).save(qagDTO)
            then(cacheRepository).should(only()).insertQag(qagDTO = savedQagDTO)
        }
    }
}