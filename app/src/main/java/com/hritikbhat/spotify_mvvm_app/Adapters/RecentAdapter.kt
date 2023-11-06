package com.hritikbhat.spotify_mvvm_app.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.hritikbhat.spotify_mvvm_app.Models.Song
import com.hritikbhat.spotify_mvvm_app.R
import com.hritikbhat.spotify_mvvm_app.Utils.Retrofit.RetrofitHelper
import com.hritikbhat.spotify_mvvm_app.databinding.RecentItemBinding
import com.hritikbhat.spotify_mvvm_app.ui.Activities.PlayActivity

class RecentAdapter : RecyclerView.Adapter<RecentAdapter.ViewHolder>() {
    private var result :List<Song> = emptyList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<RecentItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.recent_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = result.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(result[position])
    }
    inner class ViewHolder(private val binding: RecentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Song) {
            // Bind your data to the layout using data binding

            var typeName= ""
            var typeImg= ""
            var intent: Intent
                    typeName="Song • ${item.artist_name_arr[0]}"
                    typeImg= RetrofitHelper.BASE_URL +"data/img/playlist/${item.albumId}.jpg"
                    binding.recentItemLayout.setOnClickListener {
                        intent = Intent(binding.root.context, PlayActivity::class.java)

                        val gson = Gson()
                        val items:ArrayList<Song> = arrayListOf()
                        items.add(item)

                        intent.putExtra("songList", gson.toJson(items))
                        intent.putExtra("position", 0)
                        intent.putExtra("class", "ActivityOrAdapterPlaying")

//                        intent.putExtra("sid", item.sid)
//                        intent.putExtra("sname", item.sname)
//                        intent.putExtra("salbumId", item.albumId)
//                        intent.putExtra("artistArrString",item.artist_name_arr[0])
//                        intent.putExtra("isFav",item.isFav)
                        binding.recentPhotoImg.context.startActivity(intent)
                    }

            Glide.with(binding.recentPhotoImg.context)
                .load(typeImg).thumbnail()
                .into(binding.recentPhotoImg)

            binding.recentSongName.text = item.sname

            //binding.albumImg = item

        }
    }

    //Update the items when the data changes
    fun updateItems(newItems: List<Song>) {
        result = newItems
        notifyDataSetChanged()
    }
}