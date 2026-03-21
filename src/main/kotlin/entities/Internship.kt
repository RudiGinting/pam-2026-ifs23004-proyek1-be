package org.delcom.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Internship(
    var id: String = UUID.randomUUID().toString(),
    var companyId: String,
    var companyName: String,
    var companyEmail: String,
    var title: String,
    var description: String,
    var category: String,
    var location: String,
    var duration: String,
    var requirement: String,
    var benefit: String? = null,
    var deadline: String,
    var status: String = "Open",
    var applicantsCount: Int = 0,
    var submissionDate: String,
    var cover: String? = null,

    @Contextual
    val createdAt: Instant = Clock.System.now(),
    @Contextual
    var updatedAt: Instant = Clock.System.now(),
)