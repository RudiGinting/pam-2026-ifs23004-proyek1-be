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
import org.delcom.data.DataResponse
import org.delcom.data.InternshipRequest
import org.delcom.helpers.ServiceHelper
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IInternshipRepository
import org.delcom.repositories.IUserRepository
import java.io.File
import java.util.*

class InternshipService(
    private val userRepo: IUserRepository,
    private val internshipRepo: IInternshipRepository
) {
    suspend fun getAll(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
        val perPage = call.request.queryParameters["perPage"]?.toIntOrNull() ?: 10
        val category = call.request.queryParameters["category"]
        val location = call.request.queryParameters["location"]

        val internships = internshipRepo.getAll(search, page, perPage, category, location)

        val response = DataResponse(
            "success",
            "Berhasil mengambil daftar lowongan magang",
            mapOf(Pair("internships", internships))
        )
        call.respond(response)
    }

    suspend fun getById(call: ApplicationCall) {
        val internshipId = call.parameters["id"]
            ?: throw AppException(400, "ID lowongan tidak valid!")

        val internship = internshipRepo.getById(internshipId)
        if (internship == null) {
            throw AppException(404, "Lowongan magang tidak ditemukan!")
        }

        val response = DataResponse(
            "success",
            "Berhasil mengambil data lowongan",
            mapOf(Pair("internship", internship))
        )
        call.respond(response)
    }

    suspend fun post(call: ApplicationCall) {
        val user = ServiceHelper.getAuthUser(call, userRepo)

        val request = call.receive<InternshipRequest>()
        request.companyId = user.id

        val validator = ValidatorHelper(request.toMap())
        validator.required("title", "Judul lowongan tidak boleh kosong")
        validator.required("description", "Deskripsi tidak boleh kosong")
        validator.required("category", "Kategori tidak boleh kosong")
        validator.required("location", "Lokasi tidak boleh kosong")
        validator.required("duration", "Durasi tidak boleh kosong")
        validator.required("requirement", "Kualifikasi tidak boleh kosong")
        validator.required("deadline", "Deadline tidak boleh kosong")
        validator.validate()

        val internshipId = internshipRepo.create(request.toEntity())

        val response = DataResponse(
            "success",
            "Berhasil menambahkan lowongan magang",
            mapOf(Pair("internshipId", internshipId))
        )
        call.respond(response)
    }

    suspend fun put(call: ApplicationCall) {
        val internshipId = call.parameters["id"]
            ?: throw AppException(400, "ID lowongan tidak valid!")

        val user = ServiceHelper.getAuthUser(call, userRepo)

        val request = call.receive<InternshipRequest>()

        val validator = ValidatorHelper(request.toMap())
        validator.required("title", "Judul lowongan tidak boleh kosong")
        validator.required("description", "Deskripsi tidak boleh kosong")
        validator.required("category", "Kategori tidak boleh kosong")
        validator.required("location", "Lokasi tidak boleh kosong")
        validator.required("duration", "Durasi tidak boleh kosong")
        validator.required("requirement", "Kualifikasi tidak boleh kosong")
        validator.required("deadline", "Deadline tidak boleh kosong")
        validator.validate()

        val oldInternship = internshipRepo.getById(internshipId)
        if (oldInternship == null || oldInternship.companyId != user.id) {
            throw AppException(404, "Lowongan tidak ditemukan atau Anda bukan pemiliknya!")
        }

        request.cover = oldInternship.cover
        request.companyId = user.id

        val isUpdated = internshipRepo.update(internshipId, request.toEntity())
        if (!isUpdated) {
            throw AppException(400, "Gagal memperbarui lowongan magang!")
        }

        val response = DataResponse(
            "success",
            "Berhasil mengubah lowongan magang",
            null
        )
        call.respond(response)
    }

    suspend fun delete(call: ApplicationCall) {
        val internshipId = call.parameters["id"]
            ?: throw AppException(400, "ID lowongan tidak valid!")

        val user = ServiceHelper.getAuthUser(call, userRepo)

        val oldInternship = internshipRepo.getById(internshipId)
        if (oldInternship == null || oldInternship.companyId != user.id) {
            throw AppException(404, "Lowongan tidak ditemukan atau Anda bukan pemiliknya!")
        }

        val isDeleted = internshipRepo.delete(internshipId)
        if (!isDeleted) {
            throw AppException(400, "Gagal menghapus lowongan magang!")
        }

        if (oldInternship.cover != null) {
            val oldFile = File(oldInternship.cover!!)
            if (oldFile.exists()) {
                oldFile.delete()
            }
        }

        val response = DataResponse(
            "success",
            "Berhasil menghapus lowongan magang",
            null
        )
        call.respond(response)
    }

    suspend fun putCover(call: ApplicationCall) {
        val internshipId = call.parameters["id"]
            ?: throw AppException(400, "ID lowongan tidak valid!")

        val user = ServiceHelper.getAuthUser(call, userRepo)

        val request = InternshipRequest()
        request.companyId = user.id

        val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)
        multipartData.forEachPart { part ->
            when (part) {
                is PartData.FileItem -> {
                    val ext = part.originalFileName
                        ?.substringAfterLast('.', "")
                        ?.let { if (it.isNotEmpty()) ".$it" else "" }
                        ?: ""

                    val fileName = UUID.randomUUID().toString() + ext
                    val filePath = "uploads/internships/$fileName"

                    withContext(Dispatchers.IO) {
                        val file = File(filePath)
                        file.parentFile.mkdirs()
                        part.provider().copyAndClose(file.writeChannel())
                        request.cover = filePath
                    }
                }
                else -> {}
            }
            part.dispose()
        }

        if (request.cover == null) {
            throw AppException(404, "Cover lowongan tidak tersedia!")
        }

        val newFile = File(request.cover!!)
        if (!newFile.exists()) {
            throw AppException(404, "Cover lowongan gagal diunggah!")
        }

        val oldInternship = internshipRepo.getById(internshipId)
        if (oldInternship == null || oldInternship.companyId != user.id) {
            throw AppException(404, "Lowongan tidak ditemukan atau Anda bukan pemiliknya!")
        }

        request.title = oldInternship.title
        request.description = oldInternship.description
        request.category = oldInternship.category
        request.location = oldInternship.location
        request.duration = oldInternship.duration
        request.requirement = oldInternship.requirement
        request.benefit = oldInternship.benefit
        request.deadline = oldInternship.deadline

        val isUpdated = internshipRepo.update(internshipId, request.toEntity())
        if (!isUpdated) {
            throw AppException(400, "Gagal memperbarui cover lowongan!")
        }

        if (oldInternship.cover != null) {
            val oldFile = File(oldInternship.cover!!)
            if (oldFile.exists()) {
                oldFile.delete()
            }
        }

        val response = DataResponse(
            "success",
            "Berhasil mengubah cover lowongan",
            null
        )
        call.respond(response)
    }

    suspend fun getCover(call: ApplicationCall) {
        val internshipId = call.parameters["id"]
            ?: throw AppException(400, "ID lowongan tidak valid!")

        val internship = internshipRepo.getById(internshipId)
            ?: return call.respond(HttpStatusCode.NotFound)

        if (internship.cover == null) {
            throw AppException(404, "Lowongan belum memiliki cover")
        }

        val file = File(internship.cover!!)
        if (!file.exists()) {
            throw AppException(404, "Cover lowongan tidak tersedia")
        }

        call.respondFile(file)
    }
}