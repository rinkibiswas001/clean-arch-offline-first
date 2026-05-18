package com.modula.presentation.main_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.modula.domain.model.User
import com.modula.domain.usecase.GetUsersUseCase
import com.modula.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<Resource<List<User>>>(Resource.Loading)
    val uiState: StateFlow<Resource<List<User>>> = _uiState.asStateFlow()

    private val currentList = mutableListOf<User>()  // holds all loaded items
    var isLastPage = false                            // stop loading when no more data
    var isLoading = false                             // prevent duplicate calls
    var page = 10
    private var currentSkip = 0

    init {
        fetchUsers()
    }      //auto fetch on screen open

    fun fetchUsers(
        pageSize: String = page.toString(),
        skip: String = currentSkip.toString()
    ) {
        if (isLoading || isLastPage) return
        isLoading = true

        val requestSkip = skip.toIntOrNull() ?: 0

        getUsersUseCase(pageSize, skip)
            .onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.value = Resource.Loading
                    }

                    is Resource.Success -> {
                        val newItems = result.data

                        // Update last page status based on the items received
                        isLastPage = newItems.size < page

                        if (requestSkip == 0) {
                            // first page → replace list (handles refresh too)
                            currentList.clear()
                            currentList.addAll(newItems)
                        } else {
                            // next pages → append only new unique items
                            val unique = newItems.filter { new ->
                                currentList.none { it.id == new.id }
                            }
                            currentList.addAll(unique)
                        }

                        currentSkip = currentList.size
                        _uiState.value = Resource.Success(currentList.toList())
                    }

                    is Resource.Error -> {
                        _uiState.value = Resource.Error(result.message)
                    }
                }
            }
            .onCompletion {
                isLoading = false
            }
            .launchIn(viewModelScope)
    }

}