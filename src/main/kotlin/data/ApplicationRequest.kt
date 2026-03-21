package org.delcom.data

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import org.delcom.entities.Application

@Serializable
data class ApplicationRequest(
    var internshipId: String = "",
    var motivation: String = "",
    var cvUrl: String? = null,
){
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "internshipId" to internshipId,
            "motivation" to motivation,
            "cvUrl" to cvUrl
        )
    }

    fun toEntity(): Application {
        return Application(
            internshipId = internshipId,
            studentId = "", // akan diisi di service
            motivation = motivation,
            cvUrl = cvUrl,
            status = "pending",
            appliedAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
    }
}