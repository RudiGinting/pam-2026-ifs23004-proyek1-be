package org.delcom.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Internship(
    var id: String = UUID.randomUUID().toString(),
    var companyId: String, // ID perusahaan yang membuka lowongan
    var title: String,
    var description: String,
    var category: String, // Bidang: IT, Marketing, Finance, dll
    var location: String, // On-site, Remote, Hybrid
    var duration: String, // Durasi magang (3 bulan, 6 bulan, dll)
    var requirement: String, // Kualifikasi yang dibutuhkan
    var benefit: String?, // Benefit yang didapat
    var deadline: String, // Tanggal deadline pendaftaran
    var cover: String?,

    @Contextual
    val createdAt: Instant = Clock.System.now(),
    @Contextual
    var updatedAt: Instant = Clock.System.now(),
)