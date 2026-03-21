package org.delcom.repositories

import org.delcom.entities.Internship

interface IInternshipRepository {
    suspend fun getAll(
        search: String,
        page: Int,
        perPage: Int,
        category: String?,
        location: String?
    ): List<Internship>

    suspend fun getById(internshipId: String): Internship?
    suspend fun create(internship: Internship): String
    suspend fun update(internshipId: String, newInternship: Internship): Boolean
    suspend fun delete(internshipId: String): Boolean
}