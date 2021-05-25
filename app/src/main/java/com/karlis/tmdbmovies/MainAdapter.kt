package com.karlis.tmdbmovies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class MainAdapter(private val moviesList: List<Movie>, private val listener: OnItemClickListener) : RecyclerView.Adapter<MainAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)
        return MovieViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val currentItem = moviesList[position]

        holder.titleTextView.text = currentItem.title
        Picasso.get().load(currentItem.poster_path).into(holder.imageView)
        holder.releaseDateTextView.text = currentItem.release_date.take(4)
        holder.rating.text = currentItem.vote_average.toDouble().toString()
    }

    override fun getItemCount() = moviesList.size

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener {
        val imageView: ImageView = itemView.findViewById(R.id.image_view)
        val titleTextView: TextView = itemView.findViewById(R.id.movie_title)
        val releaseDateTextView: TextView = itemView.findViewById(R.id.movie_release_date)
        val rating: TextView = itemView.findViewById(R.id.rating_value)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = this.bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }

        }

    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

}