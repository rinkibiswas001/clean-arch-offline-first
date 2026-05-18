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

    private val currentList = mutableListOf<User>()
    var isLastPage = false
    var isLoading = false
    private val currentPage = 10
    private var currentSkip = 0

    init {
        fetchUsers()
    }

    fun fetchUsers(
        pageSize: String = currentPage.toString(),
        skip: String = currentSkip.toString()
    ) {
        if (isLoading || isLastPage) return
        
        val requestSkip = skip.toIntOrNull() ?: 0
        isLoading = true

        getUsersUseCase(pageSize, skip)
            .onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        // Only show loading state if the list is empty
                        if (currentList.isEmpty()) {
                            _uiState.value = Resource.Loading
                        }
                    }

                    is Resource.Success -> {
                        val allItems = result.data

                        // Update isLastPage based on total count vs expected count
                        isLastPage = allItems.size < requestSkip + currentPage

                        if (requestSkip == 0) {
                            // First page/Refresh: Replace current list
                            currentList.clear()
                            currentList.addAll(allItems)
                        } else {
                            // Pagination: Add only unique new items
                            val unique = allItems.filter { new ->
                                currentList.none { it.id == new.id }
                            }
                            currentList.addAll(unique)
                        }

                        // Set skip for the NEXT call based on our local count
                        currentSkip = currentList.size
                        _uiState.value = Resource.Success(currentList.toList())
                    }

                    is Resource.Error -> {
                        if (currentList.isEmpty()) {
                            _uiState.value = Resource.Error(result.message)
                        }
                    }
                }
            }
            .onCompletion {
                isLoading = false
            }
            .launchIn(viewModelScope)
    }

}