package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.delcom.data.AppException
import org.delcom.data.ApplicationRequest
import org.delcom.data.DataResponse
import org.delcom.helpers.ServiceHelper
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IApplicationRepository
import org.delcom.repositories.IInternshipRepository
import org.delcom.repositories.IUserRepository
import java.io.File
import java.util.*

class ApplicationService(
    private val userRepo: IUserRepository,
    private val internshipRepo: IInternshipRepository,
    private val applicationRepo: IApplicationRepository
) {
    suspend fun getMyApplications(call: ApplicationCall) {
        val user = ServiceHelper.getAuthUser(call, userRepo)
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
        val perPage = call.request.queryParameters["perPage"]?.toIntOrNull() ?: 10

        val applications = applicationRepo.getByStudent(user.id, page, perPage)

        val response = DataResponse(
            "success",
            "Berhasil mengambil daftar lamaran saya",
            mapOf(Pair("applications", applications))
        )
        call.respond(response)
    }

    suspend fun getApplicationsByInternship(call: ApplicationCall) {
        val internshipId = call.parameters["internshipId"]
            ?: throw AppException(400, "ID lowongan tidak valid!")

        val applications = applicationRepo.getByInternship(internshipId)

        val response = DataResponse(
            "success",
            "Berhasil mengambil daftar pelamar",
            mapOf(Pair("applications", applications))
        )
        call.respond(response)
    }

    suspend fun post(call: ApplicationCall) {
        val user = ServiceHelper.getAuthUser(call, userRepo)

        val request = call.receive<ApplicationRequest>()
        request.studentId = user.id

        val validator = ValidatorHelper(request.toMap())
        validator.required("internshipId", "ID lowongan tidak boleh kosong")
        validator.required("motivation", "Motivasi tidak boleh kosong")
        validator.validate()

        // Cek apakah lowongan tersedia
        val internship = internshipRepo.getById(request.internshipId)
        if (internship == null) {
            throw AppException(404, "Lowongan magang tidak ditemukan!")
        }

        val applicationId = applicationRepo.create(request.toEntity())

        val response = DataResponse(
            "success",
            "Berhasil mengirim lamaran magang",
            mapOf(Pair("applicationId", applicationId))
        )
        call.respond(response)
    }

    suspend fun updateStatus(call: ApplicationCall) {
        val applicationId = call.parameters["id"]
            ?: throw AppException(400, "ID lamaran tidak valid!")

        val request = call.receive<ApplicationRequest>()

        val validator = ValidatorHelper(request.toMap())
        validator.required("status", "Status tidak boleh kosong")
        validator.validate()

        val isUpdated = applicationRepo.updateStatus(applicationId, request.status)
        if (!isUpdated) {
            throw AppException(400, "Gagal memperbarui status lamaran!")
        }

        val response = DataResponse(
            "success",
            "Berhasil mengubah status lamaran",
            null
        )
        call.respond(response)
    }

    suspend fun delete(call: ApplicationCall) {
        val applicationId = call.parameters["id"]
            ?: throw AppException(400, "ID lamaran tidak valid!")

        val user = ServiceHelper.getAuthUser(call, userRepo)

        val application = applicationRepo.getById(applicationId)
        if (application == null || application.studentId != user.id) {
            throw AppException(404, "Lamaran tidak ditemukan!")
        }

        val isDeleted = applicationRepo.delete(applicationId)
        if (!isDeleted) {
            throw AppException(400, "Gagal membatalkan lamaran!")
        }

        val response = DataResponse(
            "success",
            "Berhasil membatalkan lamaran",
            null
        )
        call.respond(response)
    }

    suspend fun uploadCV(call: ApplicationCall) {
        val applicationId = call.parameters["id"]
            ?: throw AppException(400, "ID lamaran tidak valid!")

        val user = ServiceHelper.getAuthUser(call, userRepo)

        var cvUrl: String? = null
        val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)
        multipartData.forEachPart { part ->
            when (part) {
                is PartData.FileItem -> {
                    val ext = part.originalFileName
                        ?.substringAfterLast('.', "")
                        ?.let { if (it.isNotEmpty()) ".$it" else "" }
                        ?: ""

                    val fileName = UUID.randomUUID().toString() + ext
                    val filePath = "uploads/cvs/$fileName"

                    withContext(Dispatchers.IO) {
                        val file = File(filePath)
                        file.parentFile.mkdirs()
                        part.provider().copyAndClose(file.writeChannel())
                        cvUrl = filePath
                    }
                }
                else -> {}
            }
            part.dispose()
        }

        if (cvUrl == null) {
            throw AppException(404, "File CV tidak tersedia!")
        }

        val newFile = File(cvUrl!!)
        if (!newFile.exists()) {
            throw AppException(404, "File CV gagal diunggah!")
        }

        val application = applicationRepo.getById(applicationId)
        if (application == null || application.studentId != user.id) {
            throw AppException(404, "Lamaran tidak ditemukan!")
        }

        val updatedApplication = application.copy(cvUrl = cvUrl)
        val isUpdated = applicationRepo.update(applicationId, updatedApplication)
        if (!isUpdated) {
            throw AppException(400, "Gagal mengupload CV!")
        }

        val response = DataResponse(
            "success",
            "Berhasil mengupload CV",
            null
        )
        call.respond(response)
    }
}