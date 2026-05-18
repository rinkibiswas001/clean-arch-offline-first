package com.modula.data.remote

import com.modula.data.remote.dto.UserDataRes
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("users")
    suspend fun getUsers(
        @Query("limit") pageSize: String,
        @Query("skip") skip: String
    ): Response<UserDataRes>
}