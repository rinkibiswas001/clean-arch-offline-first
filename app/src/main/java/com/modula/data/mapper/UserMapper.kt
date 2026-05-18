package com.modula.data.mapper

import com.modula.data.local.entity.UserEntity
import com.modula.data.remote.dto.UserDataResItem
import com.modula.domain.model.User
import kotlin.toString

// DTO(Data Transfer Object) → Domain (API response to your app's model)
fun UserDataResItem.toDomain(): User = User(
    id = id.toString(),
    firstName = firstName.toString(),
    lastName = lastName.toString()
)

// Entity → Domain (DB row to your app's model)
fun UserEntity.toDomain(): User = User(
    id = id,
    firstName = firstName,
    lastName = lastName
)

// Domain → Entity (your app's model to DB row)
fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    firstName = firstName,
    lastName = lastName
)

// DTO → Entity (API response directly to DB row)
fun UserDataResItem.toEntity(): UserEntity = UserEntity(
    id = id.toString(),
    firstName = firstName.toString(),
    lastName = lastName.toString()
)