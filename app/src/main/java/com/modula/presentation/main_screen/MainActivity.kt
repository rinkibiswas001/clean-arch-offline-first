package com.modula.presentation.main_screen

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.modula.databinding.ActivityMainBinding
import com.modula.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: UserViewModel by viewModels()
    private val adapter = UserAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.adapter = adapter

        setupScrollListener()
        observeData()

    }

    private fun setupScrollListener() {
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

                val isAtBottom = (visibleItemCount + firstVisibleItem) >= totalItemCount - 2
                // -2 means load next page when 2 items from bottom

                if (totalItemCount > 0 && isAtBottom && !viewModel.isLoading && !viewModel.isLastPage) {
                    viewModel.fetchUsers()
                }
            }
        })
    }

    private fun observeData(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    when (state) {
                        is Resource.Loading -> {
                            binding.progressBar.isVisible = true
                            binding.recyclerView.isVisible = false
                        }
                        is Resource.Success -> {
                            binding.progressBar.isVisible = false
                            binding.recyclerView.isVisible = true
                            adapter.submitList(state.data)
                        }
                        is Resource.Error -> {
                            binding.progressBar.isVisible = false
                            binding.recyclerView.isVisible = true
                            Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

}