package com.hritikbhat.spotify_mvvm_app.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.hritikbhat.spotify_mvvm_app.R

class GridItemAdapter(context: Context, private val genres: List<String>) : ArrayAdapter<String>(context, 0, genres) {

    private var bgColorIndex:Int = 0


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var gridItemView = convertView
        if (gridItemView == null) {
            gridItemView = LayoutInflater.from(context).inflate(R.layout.genre_grid_item, parent, false)
        }

        val cardView: CardView = gridItemView!!.findViewById(R.id.genreCardView)
        val itemText: TextView = gridItemView!!.findViewById(R.id.genreName)
        val gridConstraintLayout: ConstraintLayout = gridItemView!!.findViewById(R.id.gridConstraintLayout)

        val colorArr = arrayListOf(
            R.color.genre_green,
            R.color.genre_lavender,
            R.color.genre_blue,
            R.color.genre_orange,
            R.color.genre_brown
        )
        //val randomNo = Random.nextInt(colorArr.size)
        if (bgColorIndex>=colorArr.size){
            bgColorIndex=0
        }
        val backgroundColor = ContextCompat.getColor(context, colorArr[bgColorIndex++])
        ViewCompat.setBackgroundTintList(gridConstraintLayout, ColorStateList.valueOf(backgroundColor))

        cardView.setOnClickListener {
            Toast.makeText(context, "Selected Genre: ${genres[position]}", Toast.LENGTH_LONG).show()
        }

        val genre = genres[position]
        itemText.text = genre

        // You can set the image resource or load images here
        // itemImage.setImageResource(R.drawable.your_image_resource)

        return gridItemView
    }
}
