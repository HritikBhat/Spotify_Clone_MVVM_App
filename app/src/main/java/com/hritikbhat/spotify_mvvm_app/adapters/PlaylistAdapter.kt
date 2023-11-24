package com.hritikbhat.spotify_mvvm_app.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.hritikbhat.spotify_mvvm_app.models.Song
import com.hritikbhat.spotify_mvvm_app.models.PlayListDetail
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.RetrofitHelper
import com.hritikbhat.spotify_mvvm_app.databinding.PlaylistItemHeaderBinding
import com.hritikbhat.spotify_mvvm_app.databinding.PlaylistItemRegularBinding
import com.hritikbhat.spotify_mvvm_app.ui.activities.PlayActivity
import kotlin.random.Random

class PlaylistAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val HEADER_VIEW_TYPE = 0
    private val ITEM_VIEW_TYPE = 1
    private var onItemClickListener: PlaylistAdapter.OnItemClickListener? = null
    private var isFav:Boolean = false
    private var ptype:Int = -1
    private var plid:String = ""


    interface OnItemClickListener {
        fun onItemClick()
        fun onFavPlaylistButtonClick(isFav: Boolean, plid: Int)
        fun onItemMoreOptionClick(plid:String,items: MutableList<Song>, i: Int, ptype: Int)
        fun onItemPlaylistMoreOptionClick(plid:String,pname:String,ptype:Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    private val items = mutableListOf<Song>()
    private lateinit var pName:String
    private lateinit var aName:String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return if (viewType == HEADER_VIEW_TYPE) {
            val binding: PlaylistItemHeaderBinding = DataBindingUtil.inflate(
                layoutInflater, R.layout.playlist_item_header, parent, false
            )
            HeaderViewHolder(binding)
        } else {
            val binding: PlaylistItemRegularBinding = DataBindingUtil.inflate(
                layoutInflater, R.layout.playlist_item_regular, parent, false
            )
            ItemViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {

            val binding = holder.binding
            binding.playlistMenuBtn.visibility=View.INVISIBLE
            val plURL = RetrofitHelper.BASE_URL +"data/img/playlist/${items[0].albumId}.jpg"

            if (ptype==3){
                binding.playlistFavBtn.visibility=View.INVISIBLE
                binding.playlistMenuBtn.visibility=View.VISIBLE
                binding.playlistImage.setImageResource(R.drawable.playlist_default_img)
                binding.playlistMenuBtn.setOnClickListener(View.OnClickListener {
                    onItemClickListener?.onItemPlaylistMoreOptionClick(plid,pName,ptype)
                })
            }

            else if (plid.equals("-1")){
                binding.playlistFavBtn.visibility= View.INVISIBLE
                binding.playlistImage.setImageResource(R.drawable.liked_song_playlist_img_bigpxl)
            }else{
                binding.playlistFavBtn.visibility= View.VISIBLE
                Glide.with(binding.root.context)
                    .load(plURL).thumbnail()
                    .into(binding.playlistImage)
            }

            binding.playlistShufflePlay.setOnClickListener(View.OnClickListener {
                val intent = Intent(binding.root.context, PlayActivity::class.java)
                val gson = Gson()

                val randomNumber = Random.nextInt(items.size)
                intent.putExtra("class", "ActivityOrAdapterPlaying")
                intent.putExtra("songList", gson.toJson(items))
                intent.putExtra("position",randomNumber)
                intent.putExtra("ptype",ptype)
                intent.putExtra("onShuffle",true)
                binding.root.context.startActivity(intent)
            })


            if (isFav){
                binding.playlistFavBtn.setBackgroundResource(R.drawable.ic_fav_selected_white)
            }

            binding.playlistFavBtn.setOnClickListener {
                    onItemClickListener?.onFavPlaylistButtonClick(isFav,items[0].albumId)
                    val img = if (isFav) R.drawable.ic_fav_unselected_white else R.drawable.ic_fav_selected_white
                    binding.playlistFavBtn.setBackgroundResource(img)
                    isFav = !isFav

            }


            binding.playlistNameTT.text = pName
            binding.playlistArtistTT.text = aName


            binding.playlistBackBtn.setOnClickListener {
                onItemClickListener?.onItemClick()
            }



        } else if (holder is ItemViewHolder) {
            val binding = holder.binding
            binding.playlistSongName.text = items[position-1].sname
            binding.playlistSongArtist.text = items[position-1].artist_name_arr.joinToString(", ")


            binding.songMoreOptionBtn.setOnClickListener(View.OnClickListener {
                //Song More Option Menu
                onItemClickListener?.onItemMoreOptionClick(plid,items,position-1,ptype)
            })

            binding.playlistSongItem.setOnClickListener {
                val intent = Intent(binding.root.context, PlayActivity::class.java)
                val gson = Gson()
                Log.d("CHEEZY NOTIFICATION","PType: $ptype")
                intent.putExtra("songList", gson.toJson(items))
                intent.putExtra("position", position-1)
                intent.putExtra("ptype",ptype)
                intent.putExtra("class", "ActivityOrAdapterPlaying")
                intent.putExtra("onShuffle",false)
                binding.root.context.startActivity(intent)
            }


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

    fun setSongFavStatus(pos:Int,flag:Boolean){
        items[pos].isFav=flag
    }

    fun setPlaylistItems(
        plid: String,
        pName: String,
        aName: String,
        ptype: Int,
        isFav: Boolean,
        playlistDetail: PlayListDetail
    ) {
        this.pName=pName
        this.aName=aName
        this.isFav=isFav
        this.ptype=ptype
        this.plid =plid
        items.clear()
        items.addAll(playlistDetail.songs)
        notifyDataSetChanged()
    }

    inner class HeaderViewHolder(val binding: PlaylistItemHeaderBinding) : RecyclerView.ViewHolder(binding.root)
    inner class ItemViewHolder(val binding: PlaylistItemRegularBinding) : RecyclerView.ViewHolder(binding.root)
}
