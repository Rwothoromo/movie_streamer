package com.moviestreamer.di

import com.moviestreamer.domain.usecase.GetPopularMoviesUseCase
import com.moviestreamer.domain.usecase.GetPublicDomainMoviesUseCase
import com.moviestreamer.domain.usecase.GetTopRatedMoviesUseCase
import com.moviestreamer.domain.usecase.SearchMoviesUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetPopularMoviesUseCase(get()) }
    factory { GetTopRatedMoviesUseCase(get()) }
    factory { GetPublicDomainMoviesUseCase(get()) }
    factory { SearchMoviesUseCase() }
}
