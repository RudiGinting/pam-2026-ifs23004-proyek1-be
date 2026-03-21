package org.delcom.data

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import org.delcom.entities.Internship

@Serializable
data class InternshipRequest(
    var companyId: String = "",
    var companyName: String = "",
    var companyEmail: String = "",
    var title: String = "",
    var description: String = "",
    var category: String = "",
    var location: String = "",
    var duration: String = "",
    var requirement: String = "",
    var benefit: String? = null,
    var deadline: String = "",
    var status: String = "Open",
    var submissionDate: String = "",
    var cover: String? = null,
){
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "companyId" to companyId,
            "companyName" to companyName,
            "companyEmail" to companyEmail,
            "title" to title,
            "description" to description,
            "category" to category,
            "location" to location,
            "duration" to duration,
            "requirement" to requirement,
            "benefit" to benefit,
            "deadline" to deadline,
            "status" to status,
            "submissionDate" to submissionDate,
            "cover" to cover
        )
    }

    fun toEntity(): Internship {
        return Internship(
            companyId = companyId,
            companyName = companyName,
            companyEmail = companyEmail,
            title = title,
            description = description,
            category = category,
            location = location,
            duration = duration,
            requirement = requirement,
            benefit = benefit,
            deadline = deadline,
            status = status,
            applicantsCount = 0,
            submissionDate = submissionDate,
            cover = cover,
            updatedAt = Clock.System.now()
        )
    }
}