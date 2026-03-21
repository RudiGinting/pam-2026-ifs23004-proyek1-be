package org.delcom.helpers

import kotlinx.coroutines.Dispatchers
import org.delcom.dao.RefreshTokenDAO
import org.delcom.dao.UserDAO
import org.delcom.dao.InternshipDAO  // Tambahkan import
import org.delcom.dao.ApplicationDAO // Tambahkan import
import org.delcom.entities.RefreshToken
import org.delcom.entities.User
import org.delcom.entities.Internship  // Tambahkan import
import org.delcom.entities.Application // Tambahkan import
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

// HAPUS fungsi technicianDAOToModel ini:
// fun technicianDAOToModel(dao: TechnicianDAO) = Technician(...)

// TAMBAHKAN fungsi untuk Internship
fun internshipDAOToModel(dao: InternshipDAO) = Internship(
    id = dao.id.value.toString(),
    companyId = dao.companyId.toString(),
    title = dao.title,
    description = dao.description,
    category = dao.category,
    location = dao.location,
    duration = dao.duration,
    requirement = dao.requirement,
    benefit = dao.benefit,
    deadline = dao.deadline,
    cover = dao.cover,
    createdAt = dao.createdAt,
    updatedAt = dao.updatedAt
)

// TAMBAHKAN fungsi untuk Application
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