package com.hritikbhat.spotify_mvvm_app.adapters

import android.content.Intent
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.databinding.SearchItemBinding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.hritikbhat.spotify_mvvm_app.models.AllSearchItem
import com.hritikbhat.spotify_mvvm_app.models.Song
import com.hritikbhat.spotify_mvvm_app.ui.activities.PlayActivity
import com.hritikbhat.spotify_mvvm_app.utils.Retrofit.RetrofitHelper

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
    private var result :List<AllSearchItem> = emptyList()
    private var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onSearchItemClick(plid: Int, plname: String, aname: String, ptype: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<SearchItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.search_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = result.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(result[position])
    }
    inner class ViewHolder(private val binding: SearchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AllSearchItem) {
            // Bind your data to the layout using data binding

            var typeName= ""
            var typeImg= ""
            var intent:Intent
            when (item.type) {
                0 -> {
                    typeName="Artist"
                    typeImg= RetrofitHelper.BASE_URL +"data/img/artist/${item.id}.jpg"
                }
                1 -> {
                    typeName="Playlist • ${item.artistArr[0]}"
                    typeImg= RetrofitHelper.BASE_URL +"data/img/playlist/${item.id}.jpg"
                    binding.searchItemLayout.setOnClickListener {
                        //Toast.makeText(binding.root.context,"Clicked",Toast.LENGTH_LONG).show()
                        val plid = item.id
                        val pname = item.name
                        val ptype = item.type
                        val aname = item.artistArr[0]
                        onItemClickListener?.onSearchItemClick(plid,pname,aname,ptype)
                    }
                }
                2 -> {

                    typeName="Song • ${item.artistArr[0]}"
                    typeImg= RetrofitHelper.BASE_URL +"data/img/playlist/${item.albumId}.jpg"
                    binding.searchItemLayout.setOnClickListener {
                        intent = Intent(binding.albumImg.context, PlayActivity::class.java)
                        val gson = Gson()
                        val items:ArrayList<Song> = arrayListOf()
                        items.add(Song(item.id,item.albumId,item.name,item.artistArr,item.isFav))

                        intent.putExtra("songList", gson.toJson(items))
                        intent.putExtra("position", 0)
                        intent.putExtra("class", "ActivityOrAdapterPlaying")
//                        intent.putExtra("sid", item.id)
//                        intent.putExtra("sname", item.name)
//                        intent.putExtra("salbumId", item.albumId)
//                        intent.putExtra("artistArrString",item.artistArr[0])
//                        intent.putExtra("isFav",item.isFav)
                        binding.albumImg.context.startActivity(intent)
                    }
                }
            }

            Glide.with(binding.albumImg.context)
                .load(typeImg).thumbnail()
                .into(binding.albumImg)

            binding.searchItemName.text = item.name
            binding.searchItemTypeArtist.text = typeName

            //binding.albumImg = item

        }
    }

    //Update the items when the data changes
    fun updateItems(newItems: List<AllSearchItem>) {
        result = newItems
        notifyDataSetChanged()
    }
}
