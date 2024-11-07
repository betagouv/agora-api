package fr.gouv.agora.infrastructure.thematique.dto

import jakarta.persistence.*
import org.hibernate.Hibernate
import java.io.Serializable
import java.util.*

// TODO
@Entity(name = "thematiques")
data class ThematiqueDatabaseDTO(
    @Id
    @Column(columnDefinition = "TEXT")
    val id: String,
    @Column(columnDefinition = "TEXT")
    val label: String,
    @Column(columnDefinition = "TEXT")
    val picto: String,
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as ThematiqueDatabaseDTO

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , label = $label , picto = $picto )"
    }
}
