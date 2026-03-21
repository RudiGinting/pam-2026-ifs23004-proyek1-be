package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object ApplicationTable : UUIDTable("applications") {
    val internshipId = uuid("internship_id")
    val studentId = uuid("student_id")
    val motivation = text("motivation")
    val cvUrl = text("cv_url").nullable()
    val status = varchar("status", 50).default("pending")
    val appliedAt = timestamp("applied_at")
    val updatedAt = timestamp("updated_at")
}