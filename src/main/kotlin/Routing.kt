package org.delcom

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.delcom.data.AppException
import org.delcom.data.ErrorResponse
import org.delcom.helpers.JWTConstants
import org.delcom.helpers.parseMessageToMap
import org.delcom.services.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val internshipService: InternshipService by inject()
    val applicationService: ApplicationService by inject()
    val authService: AuthService by inject()
    val userService: UserService by inject()

    install(StatusPages) {
        exception<AppException> { call, cause ->
            val dataMap: Map<String, List<String>> = parseMessageToMap(cause.message)

            call.respond(
                status = HttpStatusCode.fromValue(cause.code),
                message = ErrorResponse(
                    status = "fail",
                    message = if (dataMap.isEmpty()) cause.message else "Data yang dikirimkan tidak valid!",
                    data = if (dataMap.isEmpty()) null else dataMap.toString()
                )
            )
        }

        exception<Throwable> { call, cause ->
            call.respond(
                status = HttpStatusCode.fromValue(500),
                message = ErrorResponse(
                    status = "error",
                    message = cause.message ?: "Unknown error",
                    data = ""
                )
            )
        }
    }

    routing {
        get("/") {
            call.respondText("Internship Management API - PAM Project")
        }

        // Route Auth
        route("/auth") {
            post("/login") {
                authService.postLogin(call)
            }
            post("/register") {
                authService.postRegister(call)
            }
            post("/refresh-token") {
                authService.postRefreshToken(call)
            }
            post("/logout") {
                authService.postLogout(call)
            }
        }

        // Route Internships (Public - tanpa auth untuk melihat)
        route("/internships") {
            get {
                internshipService.getAll(call)
            }
            get("/{id}") {
                internshipService.getById(call)
            }

            // Protected routes untuk create, update, delete
            authenticate(JWTConstants.NAME) {
                post {
                    internshipService.post(call)
                }
                put("/{id}") {
                    internshipService.put(call)
                }
                put("/{id}/cover") {
                    internshipService.putCover(call)
                }
                delete("/{id}") {
                    internshipService.delete(call)
                }
            }
        }

        // Route Applications (hanya untuk user yang login)
        authenticate(JWTConstants.NAME) {
            route("/applications") {
                get("/my") {
                    applicationService.getMyApplications(call)
                }
                post {
                    applicationService.post(call)
                }
                put("/{id}/cv") {
                    applicationService.uploadCV(call)
                }
                delete("/{id}") {
                    applicationService.delete(call)
                }
            }
        }

        // Route Users
        authenticate(JWTConstants.NAME) {
            route("/users") {
                get("/me") {
                    userService.getMe(call)
                }
                put("/me") {
                    userService.putMe(call)
                }
                put("/me/password") {
                    userService.putMyPassword(call)
                }
                put("/me/photo") {
                    userService.putMyPhoto(call)
                }
            }
        }

        // Public routes for images
        route("/images") {
            get("users/{id}") {
                userService.getPhoto(call)
            }
            get("internships/{id}") {
                internshipService.getCover(call)
            }
        }
    }
}