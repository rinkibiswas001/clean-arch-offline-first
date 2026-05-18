package com.modula.domain.usecase

import com.modula.domain.model.User
import com.modula.domain.repository.UserRepository
import com.modula.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(         // no suspend — Flow handles async
        pageSize: String,
        skip: String
    ): Flow<Resource<List<User>>> = repository.getUsers(pageSize, skip)
}