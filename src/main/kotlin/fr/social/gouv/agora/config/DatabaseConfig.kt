package fr.social.gouv.agora.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.URI
import javax.sql.DataSource

@Configuration
@Suppress("unused")
class DatabaseConfig {

    companion object {
        private const val DEFAULT_DB_MAX_POOL_SIZE = 5
    }

    @Bean
    fun dataSource(hikariConfig: HikariConfig): DataSource {
        return HikariDataSource(hikariConfig)
    }

    @Bean
    fun hikariConfig(): HikariConfig {
        return HikariConfig().apply {
            System.getenv("DATABASE_URL")?.let { databaseUrl ->
                try {
                    val databaseURI = URI.create(databaseUrl)
                    jdbcUrl = formatJdbcUrl(databaseUrl = databaseUrl, userInfo = databaseURI.userInfo)
                    val userInfo = databaseURI.userInfo.split(":")
                    username = userInfo[0]
                    password = userInfo[1]
                } catch (e: IllegalArgumentException) {
                    println("Invalid Database URL: $databaseUrl")
                }
                maximumPoolSize = System.getenv("DATABASE_MAX_POOL_SIZE").toIntOrNull() ?: DEFAULT_DB_MAX_POOL_SIZE
            }
        }
    }

    private fun formatJdbcUrl(databaseUrl: String, userInfo: String): String {
        val alteredDatabaseUrl = databaseUrl
            .replace("postgres://", "postgresql://")
            .replace("$userInfo@", "")
        return "jdbc:$alteredDatabaseUrl"
    }
}