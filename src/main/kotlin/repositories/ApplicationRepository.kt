package org.delcom.repositories

import org.delcom.dao.ApplicationDAO
import org.delcom.entities.Application
import org.delcom.helpers.applicationDAOToModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.ApplicationTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import java.util.*
import org.jetbrains.exposed.sql.deleteWhere

class ApplicationRepository : IApplicationRepository {
    override suspend fun getByStudent(studentId: String, page: Int, perPage: Int): List<Application> = suspendTransaction {
        ApplicationDAO
            .find { ApplicationTable.studentId eq UUID.fromString(studentId) }
            .limit(perPage)
            .offset(((page - 1) * perPage).toLong())
            .map(::applicationDAOToModel)
    }

    override suspend fun getByInternship(internshipId: String): List<Application> = suspendTransaction {
        ApplicationDAO
            .find { ApplicationTable.internshipId eq UUID.fromString(internshipId) }
            .map(::applicationDAOToModel)
    }

    override suspend fun getById(applicationId: String): Application? = suspendTransaction {
        ApplicationDAO
            .find { ApplicationTable.id eq UUID.fromString(applicationId) }
            .limit(1)
            .map(::applicationDAOToModel)
            .firstOrNull()
    }

    override suspend fun create(application: Application): String = suspendTransaction {
        val applicationDAO = ApplicationDAO.new {
            internshipId = UUID.fromString(application.internshipId)
            studentId = UUID.fromString(application.studentId)
            motivation = application.motivation
            cvUrl = application.cvUrl
            status = application.status
            appliedAt = application.appliedAt
            updatedAt = application.updatedAt
        }
        applicationDAO.id.value.toString()
    }

    override suspend fun update(applicationId: String, newApplication: Application): Boolean = suspendTransaction {
        val applicationDAO = ApplicationDAO
            .find { ApplicationTable.id eq UUID.fromString(applicationId) }
            .limit(1)
            .firstOrNull()

        if (applicationDAO != null) {
            applicationDAO.motivation = newApplication.motivation
            applicationDAO.cvUrl = newApplication.cvUrl
            applicationDAO.status = newApplication.status
            applicationDAO.updatedAt = newApplication.updatedAt
            true
        } else {
            false
        }
    }

    override suspend fun delete(applicationId: String): Boolean = suspendTransaction {
        val rowsDeleted = ApplicationTable.deleteWhere {
            ApplicationTable.id eq UUID.fromString(applicationId)
        }
        rowsDeleted >= 1
    }

    override suspend fun updateStatus(applicationId: String, status: String): Boolean = suspendTransaction {
        val applicationDAO = ApplicationDAO
            .find { ApplicationTable.id eq UUID.fromString(applicationId) }
            .limit(1)
            .firstOrNull()

        if (applicationDAO != null) {
            applicationDAO.status = status
            true
        } else {
            false
        }
    }
}