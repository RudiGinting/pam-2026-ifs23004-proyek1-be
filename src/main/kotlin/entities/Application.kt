package org.delcom.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Application(
    var id: String = UUID.randomUUID().toString(),
    var internshipId: String,
    var studentId: String,
    var motivation: String,
    var cvUrl: String? = null,
    var status: String = "pending",
    var appliedAt: Instant = Clock.System.now(),

    @Contextual
    var updatedAt: Instant = Clock.System.now(),
)