package com.hritikbhat.spotify_mvvm_app.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.hritikbhat.spotify_mvvm_app.models.FavPlaylistsX
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.databinding.AddToPlaylistItemHeaderBinding
import com.hritikbhat.spotify_mvvm_app.databinding.AddToPlaylistItemRegularBinding

class AddToPlaylistAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val HEADER_VIEW_TYPE = 0
    private val ITEM_VIEW_TYPE = 1
    private var onItemClickListener: AddToPlaylistAdapter.OnItemClickListener? = null
    private lateinit var sid:String


    interface OnItemClickListener {
        fun onSelectingAddToPlaylistItemClick(plid:String,sid:String)
        fun onSelectingBackButton()
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    private var items = emptyList<FavPlaylistsX>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return if (viewType == HEADER_VIEW_TYPE) {
            val binding: AddToPlaylistItemHeaderBinding = DataBindingUtil.inflate(
                layoutInflater, R.layout.add_to_playlist_item_header, parent, false
            )
            HeaderViewHolder(binding)
        } else {
            val binding: AddToPlaylistItemRegularBinding = DataBindingUtil.inflate(
                layoutInflater, R.layout.add_to_playlist_item_regular, parent, false
            )
            ItemViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            val binding = holder.binding

            binding.playlistBackBtn.setOnClickListener{
                onItemClickListener?.onSelectingBackButton()
            }


        } else if (holder is ItemViewHolder) {
            val binding = holder.binding

            binding.customPlaylistName.text = items[position-1].plname

            binding.customPlaylistItemLayout.setOnClickListener(View.OnClickListener {
                onItemClickListener?.onSelectingAddToPlaylistItemClick(items[position-1].plid.toString(),sid)
            })


        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            HEADER_VIEW_TYPE
        } else {
            ITEM_VIEW_TYPE
        }
    }

    override fun getItemCount(): Int {
        return items.size + 1
    }

    fun setPlaylistItems(favPlaylists: List<FavPlaylistsX>,sid:String) {
        items=favPlaylists
        this.sid = sid
        notifyDataSetChanged()
    }

    inner class HeaderViewHolder(val binding: AddToPlaylistItemHeaderBinding) : RecyclerView.ViewHolder(binding.root)
    inner class ItemViewHolder(val binding: AddToPlaylistItemRegularBinding) : RecyclerView.ViewHolder(binding.root)
}
