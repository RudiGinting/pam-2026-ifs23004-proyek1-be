package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object InternshipTable : UUIDTable("internships") {
    val companyId = uuid("company_id")
    val companyName = varchar("company_name", 200)
    val companyEmail = varchar("company_email", 100)
    val title = varchar("title", 200)
    val description = text("description")
    val category = varchar("category", 100)
    val location = varchar("location", 100)
    val duration = varchar("duration", 50)
    val requirement = text("requirement")
    val benefit = text("benefit").nullable()
    val deadline = varchar("deadline", 50)
    val status = varchar("status", 20).default("Open")
    val applicantsCount = integer("applicants_count").default(0)
    val submissionDate = varchar("submission_date", 50)
    val cover = text("cover").nullable()
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}