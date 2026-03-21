package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Application

@Serializable
data class ApplicationRequest(
    var internshipId: String = "",
    var studentId: String = "",
    var motivation: String = "",
    var cvUrl: String? = null,
    var status: String = "pending",
){
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "internshipId" to internshipId,
            "studentId" to studentId,
            "motivation" to motivation,
            "cvUrl" to cvUrl,
            "status" to status
        )
    }

    fun toEntity(): Application {
        return Application(
            internshipId = internshipId,
            studentId = studentId,
            motivation = motivation,
            cvUrl = cvUrl,
            status = status
        )
    }
}