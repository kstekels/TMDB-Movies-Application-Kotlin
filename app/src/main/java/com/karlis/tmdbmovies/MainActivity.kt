package com.karlis.tmdbmovies

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.karlis.tmdbmovies.databinding.ActivityMainBinding
import org.json.JSONException
import org.json.JSONObject


class MainActivity : AppCompatActivity(), MainAdapter.OnItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var responseResultString: String
    private var responseErrorString: String? = null
    private lateinit var movieList: ArrayList<Movie>
    private val apiKey = "73619d549f33ccdf0116452a1f3f9427"
    private val region = "en-US"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadMovies(40)
    }

    private fun loadMovies(loadPages: Int) {
        movieList = ArrayList()
        for (i in 1 until loadPages) {
            var urlString =
                "https://api.themoviedb.org/3/movie/popular?api_key=$apiKey&language=$region&page=$i"

            val stringRequest = StringRequest(
                Request.Method.GET, urlString, { response ->
                    // Response
                    this.responseResultString = response
                    // Json
                    val json = JSONObject(responseResultString).getJSONArray("results")
                    // Create Movie Object
                    val numberOfItemsInPage = json.length().toString().toInt()
                    for (index in 0 until numberOfItemsInPage) {
                        val item = json.getJSONObject(index)

                        val posterPath = item.getString("poster_path")
                        val title = item.getString("title")
                        var releaseDate = "No Data"
                        val id = item.getString("id")
                        val voteAverage = item.getString("vote_average")

                        try {
                            releaseDate = item.getString("release_date")
                        } catch (error: JSONException) {
                            Log.d("JSON Error", error.message.toString())
                        }


                        val movie = Movie(
                            title = title,
                            poster_path = "https://image.tmdb.org/t/p/w500$posterPath",
                            release_date = releaseDate,
                            id = id,
                            vote_average = voteAverage,
                        )
                        movieList.add(movie)
                    }

                    // Adapter
                    val adapter = MainAdapter(movieList, this)
                    binding.recyclerView.adapter = adapter
                    binding.recyclerView.layoutManager = LinearLayoutManager(this)
                    binding.recyclerView.setHasFixedSize(true)

                },
                { volleyError ->
                    // Error
                    this.responseErrorString = volleyError.message
                }
            )
            Volley.newRequestQueue(this).add(stringRequest)

        }
    }


    override fun onItemClick(position: Int) {
        val clickedItem: Movie = movieList[position]
        val i = Intent(this, DetailedView::class.java)
        i.putExtra("movieID", clickedItem.id)
        startActivity(i)

    }
}

