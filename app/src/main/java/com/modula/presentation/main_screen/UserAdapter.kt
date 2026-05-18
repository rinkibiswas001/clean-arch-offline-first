package com.modula.presentation.main_screen

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.modula.databinding.ItemUserBinding
import com.modula.domain.model.User

class UserAdapter : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var list: List<User> = emptyList()

    fun submitList(newList: List<User>) {
        val diffResult = DiffUtil.calculateDiff(UserDiffCallback(list, newList))
        list = newList
        diffResult.dispatchUpdatesTo(this)  // applies only the changed items
    }

    // DiffUtil.Callback — tells DiffUtil how to compare items
    class UserDiffCallback(
        private val oldList: List<User>,
        private val newList: List<User>
    ) : DiffUtil.Callback() {

        // total size of old list
        override fun getOldListSize() = oldList.size

        // total size of new list
        override fun getNewListSize() = newList.size

        // are these two items the same object? (compare by unique id)
        override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean {
            return oldList[oldPos].id == newList[newPos].id
        }

        // are the contents of the same item equal? (compare all fields)
        // only called if areItemsTheSame() returns true
        override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean {
            return oldList[oldPos] == newList[newPos]  // data class handles this
        }
    }

    class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.tvNameCode.text = when {
                user.firstName.isNotEmpty() && user.lastName.isNotEmpty() ->
                    user.firstName.substring(0, 1) + user.lastName.substring(0, 1)
                user.firstName.isNotEmpty() ->
                    user.firstName.substring(0, 2)
                else -> "-"
            }

            binding.tvNameCode.backgroundTintList =
                ColorStateList.valueOf(backgroundColors.random().toColorInt())

            binding.tvUserName.text = when {
                user.firstName.isNotEmpty() && user.lastName.isNotEmpty() ->
                    "${user.firstName} ${user.lastName}"
                user.firstName.isNotEmpty() -> user.firstName
                else -> "NA"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        UserViewHolder(
            ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: UserViewHolder, pos: Int) = holder.bind(list[pos])
    override fun getItemCount() = list.size

    companion object {
        private val backgroundColors = listOf(
            "#454545",
            "#933B3B",
            "#29B282"
        )
    }
}