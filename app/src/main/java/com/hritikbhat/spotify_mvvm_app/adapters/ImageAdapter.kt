package com.hritikbhat.spotify_mvvm_app.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hritikbhat.spotify_mvvm_app.Models.SignUpImage
import com.hritikbhat.spotify_mvvm_app.R

class ImageAdapter(private val images: List<SignUpImage>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position].image,images[position].text)
    }

    override fun getItemCount() = images.size

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val textView: TextView = itemView.findViewById(R.id.textView)

        fun bind(imageResId: Int,text:String) {
            imageView.setImageResource(imageResId)
            textView.text = text
        }
    }

}

