package org.delcom.repositories

import org.delcom.dao.InternshipDAO
import org.delcom.entities.Internship
import org.delcom.helpers.internshipDAOToModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.InternshipTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.*
import org.jetbrains.exposed.sql.or

class InternshipRepository : IInternshipRepository {
    override suspend fun getAll(
        search: String,
        page: Int,
        perPage: Int,
        category: String?,
        location: String?
    ): List<Internship> = suspendTransaction {
        val query = if (search.isBlank()) {
            InternshipDAO.find {
                var op: org.jetbrains.exposed.sql.Op<Boolean> = InternshipTable.title.isNotNull()
                if (category != null) op = op and (InternshipTable.category eq category)
                if (location != null) op = op and (InternshipTable.location eq location)
                op
            }
        } else {
            val keyword = "%${search.lowercase()}%"
            InternshipDAO.find {
                val titleCondition = InternshipTable.title.lowerCase() like keyword
                val descriptionCondition = InternshipTable.description.lowerCase() like keyword
                var op = titleCondition or descriptionCondition
                if (category != null) op = op and (InternshipTable.category eq category)
                if (location != null) op = op and (InternshipTable.location eq location)
                op
            }
        }

        query.orderBy(InternshipTable.createdAt to SortOrder.DESC)
            .limit(perPage)
            .offset(((page - 1) * perPage).toLong())
            .map(::internshipDAOToModel)
    }

    override suspend fun getById(internshipId: String): Internship? = suspendTransaction {
        InternshipDAO
            .find { InternshipTable.id eq UUID.fromString(internshipId) }
            .limit(1)
            .map(::internshipDAOToModel)
            .firstOrNull()
    }

    override suspend fun create(internship: Internship): String = suspendTransaction {
        val internshipDAO = InternshipDAO.new {
            companyName = internship.companyName
            companyEmail = internship.companyEmail
            title = internship.title
            description = internship.description
            category = internship.category
            location = internship.location
            duration = internship.duration
            requirement = internship.requirement
            benefit = internship.benefit
            deadline = internship.deadline
            status = internship.status
            applicantsCount = internship.applicantsCount
            submissionDate = internship.submissionDate
            cover = internship.cover
            createdAt = internship.createdAt
            updatedAt = internship.updatedAt
        }
        internshipDAO.id.value.toString()
    }

    override suspend fun update(internshipId: String, newInternship: Internship): Boolean = suspendTransaction {
        val internshipDAO = InternshipDAO
            .find { InternshipTable.id eq UUID.fromString(internshipId) }
            .limit(1)
            .firstOrNull()

        if (internshipDAO != null) {
            internshipDAO.companyName = newInternship.companyName
            internshipDAO.companyEmail = newInternship.companyEmail
            internshipDAO.title = newInternship.title
            internshipDAO.description = newInternship.description
            internshipDAO.category = newInternship.category
            internshipDAO.location = newInternship.location
            internshipDAO.duration = newInternship.duration
            internshipDAO.requirement = newInternship.requirement
            internshipDAO.benefit = newInternship.benefit
            internshipDAO.deadline = newInternship.deadline
            internshipDAO.status = newInternship.status
            internshipDAO.submissionDate = newInternship.submissionDate
            internshipDAO.cover = newInternship.cover
            internshipDAO.updatedAt = newInternship.updatedAt
            true
        } else {
            false
        }
    }

    override suspend fun delete(internshipId: String): Boolean = suspendTransaction {
        val rowsDeleted = InternshipTable.deleteWhere {
            InternshipTable.id eq UUID.fromString(internshipId)
        }
        rowsDeleted >= 1
    }
}