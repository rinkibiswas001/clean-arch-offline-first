package com.modula.data.repository

import com.modula.data.local.UserDao
import com.modula.data.mapper.toDomain
import com.modula.data.mapper.toEntity
import com.modula.data.remote.ApiService
import com.modula.domain.model.User
import com.modula.domain.repository.UserRepository
import com.modula.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService, private val userDao: UserDao
) : UserRepository {

    override fun getUsers(
        pageSize: String, skip: String
    ): Flow<Resource<List<User>>> = flow {

        // Emit loading state
        emit(Resource.Loading)

        // Show cached data immediately on first load (refresh)
        if (skip == "0") {
            val cachedUsers = userDao.getUsers().first()
            if (cachedUsers.isNotEmpty()) {
                emit(Resource.Success(cachedUsers.map { it.toDomain() }))
            }
        }

        try {
            // Fetch fresh data from API
            val response = apiService.getUsers(pageSize, skip)

            if (response.isSuccessful) {
                val freshData = response.body()
                if (freshData != null) {
                    // Map to entities and filter nulls
                    val entities = freshData.userDataRes?.filterNotNull()
                        ?.map { it.toEntity() } ?: emptyList()

                    // Database Sync
                    if (skip == "0") {
                        userDao.clearUsers() // Clear only on first page (Refresh)
                    }
                    userDao.insertUsers(entities)

                    // Emit Full List from DB (Single Source of Truth)
                    val allUsersFromDb = userDao.getUsers().first()
                    emit(Resource.Success(allUsersFromDb.map { it.toDomain() }))
                } else {
                    emit(Resource.Error("Empty response body from server"))
                }
            } else {
                emit(Resource.Error("API Error: ${response.code()} ${response.message()}"))
            }

        } catch (e: HttpException) {
            emit(Resource.Error("Server error: ${e.code()}"))
        } catch (_: IOException) {
            emit(Resource.Error("No internet connection. Please check your network."))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unexpected error occurred"))
        }

    }.flowOn(Dispatchers.IO)
}