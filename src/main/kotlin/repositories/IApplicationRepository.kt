package org.delcom.repositories

import org.delcom.entities.Application

interface IApplicationRepository {
    suspend fun getByStudent(studentId: String, page: Int, perPage: Int): List<Application>
    suspend fun getByInternship(internshipId: String): List<Application>
    suspend fun getById(applicationId: String): Application?
    suspend fun create(application: Application): String
    suspend fun update(applicationId: String, newApplication: Application): Boolean
    suspend fun delete(applicationId: String): Boolean
    suspend fun updateStatus(applicationId: String, status: String): Boolean
}