package com.modula.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserDataRes(

	@field:SerializedName("users")
	val userDataRes: List<UserDataResItem?>? = null

)

data class UserDataResItem(

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("firstName")
	val firstName: String? = null,

	@field:SerializedName("lastName")
	val lastName: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

)

