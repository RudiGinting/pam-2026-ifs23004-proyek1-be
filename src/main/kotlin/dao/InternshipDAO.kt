package org.delcom.dao

import org.delcom.tables.InternshipTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import java.util.UUID

class InternshipDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, InternshipDAO>(InternshipTable)

    var companyName by InternshipTable.companyName
    var companyEmail by InternshipTable.companyEmail
    var title by InternshipTable.title
    var description by InternshipTable.description
    var category by InternshipTable.category
    var location by InternshipTable.location
    var duration by InternshipTable.duration
    var requirement by InternshipTable.requirement
    var benefit by InternshipTable.benefit
    var deadline by InternshipTable.deadline
    var status by InternshipTable.status
    var applicantsCount by InternshipTable.applicantsCount
    var submissionDate by InternshipTable.submissionDate
    var cover by InternshipTable.cover
    var createdAt by InternshipTable.createdAt
    var updatedAt by InternshipTable.updatedAt
}