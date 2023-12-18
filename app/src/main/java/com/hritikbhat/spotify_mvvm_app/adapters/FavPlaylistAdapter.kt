package com.hritikbhat.spotify_mvvm_app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hritikbhat.spotify_mvvm_app.models.FavPlaylistsX
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.RetrofitHelper
import com.hritikbhat.spotify_mvvm_app.databinding.FavPlaylistItemBinding

class FavPlaylistAdapter : RecyclerView.Adapter<FavPlaylistAdapter.ViewHolder>() {
    private var result :List<FavPlaylistsX> = emptyList()
    private var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(plid: Int, plname: String, ptype: Int, aname: String)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<FavPlaylistItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.fav_playlist_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = result.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(result[position])
    }
    inner class ViewHolder(private val binding: FavPlaylistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FavPlaylistsX) {
            // Bind your data to the layout using data binding

            binding.favPlaylistText.text = item.plname

            if (item.pltype==3){
                binding.favPlaylistImg.setImageResource(R.drawable.playlist_default_img)
            }

            else if (item.plid==-2){

                binding.favPlaylistImg.setImageResource(R.drawable.create_custom_playlist_img)
            }
            else if(item.plid==-1){
                    //Goes to Liked Songs
                binding.favPlaylistImg.setImageResource(R.drawable.liked_song_playlist_img)
            }
            else{
                val typeImg= RetrofitHelper.BASE_URL +"data/img/playlist/${item.plid}.jpg"
                Glide.with(binding.favPlaylistImg.context)
                    .load(typeImg).thumbnail()
                    .into(binding.favPlaylistImg)
            }

            binding.favPlaylistItemLayout.setOnClickListener {
                val plid = item.plid
                val pname = item.plname
                val aname = item.aname
                val ptype = item.pltype
                onItemClickListener?.onItemClick(plid,pname,ptype,aname)
            }

        }
    }

    //Update the items when the data changes
    fun updateItems(newItems: List<FavPlaylistsX>) {
        result = newItems
        notifyDataSetChanged()
    }
}