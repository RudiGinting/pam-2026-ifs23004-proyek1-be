package org.delcom.module

import org.delcom.repositories.*
import org.delcom.services.*
import org.koin.dsl.module

fun appModule(jwtSecret: String) = module {
    // User Repository
    single<IUserRepository> {
        UserRepository()
    }

    // User Service
    single {
        UserService(get(), get())
    }

    // Refresh Token Repository
    single<IRefreshTokenRepository> {
        RefreshTokenRepository()
    }

    // Auth Service
    single {
        AuthService(jwtSecret, get(), get())
    }

    // Internship Repository
    single<IInternshipRepository> {
        InternshipRepository()
    }

    // Internship Service
    single {
        InternshipService(get(), get())
    }

    // Application Repository
    single<IApplicationRepository> {
        ApplicationRepository()
    }

    // Application Service
    single {
        ApplicationService(get(), get(), get())
    }
}