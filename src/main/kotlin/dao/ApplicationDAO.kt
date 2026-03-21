package org.delcom.dao

import org.delcom.tables.ApplicationTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import java.util.UUID

class ApplicationDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, ApplicationDAO>(ApplicationTable)

    var internshipId by ApplicationTable.internshipId
    var studentId by ApplicationTable.studentId
    var motivation by ApplicationTable.motivation
    var cvUrl by ApplicationTable.cvUrl
    var status by ApplicationTable.status
    var appliedAt by ApplicationTable.appliedAt
    var updatedAt by ApplicationTable.updatedAt
}