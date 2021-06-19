package com.sharkawy.yelloemitter.presentation.features.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sharkawy.yelloemitter.databinding.ItemUserBinding
import com.sharkawy.yelloemitter.entities.users.UsersResponseItem

class UsersAdapter(
    private val itemClickListener: (item: UsersResponseItem) -> Unit,
    private val items: MutableList<UsersResponseItem>
) :
    RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, itemClickListener)
    }

    override fun getItemCount(): Int {
        return if (items.isNullOrEmpty()) 0 else items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class ViewHolder(
        private val binding: ItemUserBinding,
        private val itemClickListener: (item: UsersResponseItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(response: UsersResponseItem?) {
            binding.userNameTv.text = response?.username
            binding.userPhoneTv.text = response?.phone
            binding.userCityTv.text = response?.address?.city

            itemView.setOnClickListener {
                response?.let { response -> itemClickListener(response) }
            }
        }

        companion object {
            fun from(
                parent: ViewGroup,
                itemClickListener: (item: UsersResponseItem) -> Unit
            ): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemUserBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, itemClickListener)
            }
        }
    }


}