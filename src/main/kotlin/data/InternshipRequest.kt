package org.delcom.data

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import org.delcom.entities.Internship

@Serializable
data class InternshipRequest(
    var companyId: String = "",
    var title: String = "",
    var description: String = "",
    var category: String = "",
    var location: String = "",
    var duration: String = "",
    var requirement: String = "",
    var benefit: String? = null,
    var deadline: String = "",
    var cover: String? = null,
){
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "companyId" to companyId,
            "title" to title,
            "description" to description,
            "category" to category,
            "location" to location,
            "duration" to duration,
            "requirement" to requirement,
            "benefit" to benefit,
            "deadline" to deadline,
            "cover" to cover
        )
    }

    fun toEntity(): Internship {
        return Internship(
            companyId = companyId,
            title = title,
            description = description,
            category = category,
            location = location,
            duration = duration,
            requirement = requirement,
            benefit = benefit,
            deadline = deadline,
            cover = cover,
            updatedAt = Clock.System.now()
        )
    }
}