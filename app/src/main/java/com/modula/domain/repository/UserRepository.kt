package com.modula.domain.repository

import com.modula.domain.model.User
import com.modula.util.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUsers(
        pageSize: String,
        skip: String
    ): Flow<Resource<List<User>>>
}