package org.delcom.helpers

import kotlinx.coroutines.Dispatchers
import org.delcom.dao.RefreshTokenDAO
import org.delcom.dao.UserDAO
import org.delcom.dao.InternshipDAO
import org.delcom.dao.ApplicationDAO
import org.delcom.entities.RefreshToken
import org.delcom.entities.User
import org.delcom.entities.Internship
import org.delcom.entities.Application
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)

fun userDAOToModel(dao: UserDAO) = User(
    dao.id.value.toString(),
    dao.name,
    dao.username,
    dao.password,
    dao.photo,
    dao.about,
    dao.createdAt,
    dao.updatedAt
).apply { about = dao.about }

fun refreshTokenDAOToModel(dao: RefreshTokenDAO) = RefreshToken(
    dao.id.value.toString(),
    dao.userId.toString(),
    dao.refreshToken,
    dao.authToken,
    dao.createdAt,
)

// PERBAIKAN: Tambahkan semua parameter yang diperlukan untuk Internship
fun internshipDAOToModel(dao: InternshipDAO) = Internship(
    id = dao.id.value.toString(),
    companyId = dao.companyId.toString(),
    companyName = dao.companyName,
    companyEmail = dao.companyEmail,
    title = dao.title,
    description = dao.description,
    category = dao.category,
    location = dao.location,
    duration = dao.duration,
    requirement = dao.requirement,
    benefit = dao.benefit,
    deadline = dao.deadline,
    status = dao.status,
    applicantsCount = dao.applicantsCount,
    submissionDate = dao.submissionDate,
    cover = dao.cover,
    createdAt = dao.createdAt,
    updatedAt = dao.updatedAt
)

fun applicationDAOToModel(dao: ApplicationDAO) = Application(
    id = dao.id.value.toString(),
    internshipId = dao.internshipId.toString(),
    studentId = dao.studentId.toString(),
    motivation = dao.motivation,
    cvUrl = dao.cvUrl,
    status = dao.status,
    appliedAt = dao.appliedAt,
    updatedAt = dao.updatedAt
)