package com.hritikbhat.spotify_mvvm_app.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.databinding.ExploreItemBinding
import com.hritikbhat.spotify_mvvm_app.models.Playlist
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.RetrofitHelper

class PlaylistGridItemAdapter(private val myContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null
    private var playlistArray:ArrayList<Playlist> = arrayListOf()

    interface OnItemClickListener {
        fun onPlaylistItemClick(plid: Int, plname: String, aname: String, ptype: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding: ExploreItemBinding = DataBindingUtil.inflate(
            layoutInflater, R.layout.explore_item, parent, false
        )

        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val binding = holder.binding

            val playlistData = playlistArray[position]

            binding.exploreItemLayout.setOnClickListener{
                Log.d("Grid Clicked","Clicked")
                val plid = playlistData.plid
                val pname = playlistData.plname
                val ptype = playlistData.pltype
                val aname = playlistData.aname
                onItemClickListener?.onPlaylistItemClick(plid,pname,aname,ptype)
            }

            val typeImg= RetrofitHelper.BASE_URL +"data/img/playlist/${playlistData.plid}.jpg"

            Glide.with(myContext)
                .load(typeImg).thumbnail()
                .transform(CenterCrop(), RoundedCorners(15))
                .into(binding.explorePhotoImg)
            binding.exploreSongName.text = playlistData.plname

        }
    }


    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun getItemCount(): Int {
        return playlistArray.size
    }

    fun setExplorePlaylistItems(
        playlistArrayList: List<Playlist>
    ) {
        playlistArray = playlistArrayList as ArrayList<Playlist>
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(val binding: ExploreItemBinding) : RecyclerView.ViewHolder(binding.root)
}