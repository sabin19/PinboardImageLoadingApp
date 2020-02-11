package com.sbn.pinboard.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sbn.model.User
import com.sbn.netwoking.ImageFetcher
import com.sbn.pinboard.databinding.FooterLoadingViewBinding
import com.sbn.pinboard.databinding.UserItemBinding
import com.sbn.pinboard.shared.result.Result
import com.sbn.pinboard.util.ViewType

class HomeAdapter(
    private val imageLoader: ImageFetcher,
    private var listener: OnUserItemClickedListener?
) :
    PagedListAdapter<User, RecyclerView.ViewHolder>(
        UserItemDiff
    ) {
    private var state: Result<Boolean>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.DATA_VIEW_TYPE.key -> UserViewHolder(
                UserItemBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
            else -> FooterItemViewHolder(
                FooterLoadingViewBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < super.getItemCount()) ViewType.DATA_VIEW_TYPE.key else ViewType.FOOTER_VIEW_TYPE.key
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let { (holder as UserViewHolder).bind(it, listener) }
    }

    fun setState(state: Result<Boolean>) {
        this.state = state
        notifyItemChanged(super.getItemCount())
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (state == Result.Loading) 1 else 0
    }

    inner class UserViewHolder(private val binding: UserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User, listener: OnUserItemClickedListener?) {
            binding.apply {
                this.user = user
                this.imageLoader = this@HomeAdapter.imageLoader
                this.listener = listener
                executePendingBindings()
            }
        }
    }

    inner class FooterItemViewHolder(private val binding: FooterLoadingViewBinding) :
        RecyclerView.ViewHolder(binding.root)
}

object UserItemDiff : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(
        oldItem: User,
        newItem: User
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: User,
        newItem: User
    ) = oldItem == newItem
}