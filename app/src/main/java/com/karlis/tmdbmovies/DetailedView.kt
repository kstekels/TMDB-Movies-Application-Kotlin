package com.karlis.tmdbmovies

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.android.volley.Request
import com.karlis.tmdbmovies.databinding.DetailedViewBinding
import com.squareup.picasso.Picasso

import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView
import org.json.JSONObject

class DetailedView : AppCompatActivity() {
    private lateinit var binding: DetailedViewBinding
    private lateinit var responseString: String
    private lateinit var genresList: ArrayList<String>
    private lateinit var movieID: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DetailedViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideCards()
        loadMovieDescription()
    }

    private fun hideCards() {
        binding.cardViewBackdropPicture.isVisible = false
        binding.cardViewInfo.isVisible = false
        binding.cardViewOverview.isVisible = false
        binding.sectionProductionCompanies.isVisible = false
    }

    private fun loadMovieDescription() {
        this.genresList = ArrayList()
        this.movieID = intent.getStringExtra("movieID").toString()
        val apiKey = "73619d549f33ccdf0116452a1f3f9427"
        val urlString = "https://api.themoviedb.org/3/movie/$movieID?api_key=$apiKey"
        Log.d("URL - > #:", urlString)
        val strRequest = StringRequest(
            Request.Method.GET,
            urlString,
            { responseStr ->
                // Response
                this.responseString = responseStr
                //Json
                val resultJson = JSONObject(responseString)

                val title = resultJson.getString("title")
                this.title = title
                val backdropPath = resultJson.getString("backdrop_path")
                val posterPath = resultJson.getString("poster_path")
                val releaseDate = resultJson.getString("release_date")
                val overview = resultJson.getString("overview")
                val voteAverage = resultJson.getString("vote_average")
                val tagline = resultJson.getString("tagline")
                val status = resultJson.getString("status")

                var productionCompany = ""
                for (index in 1 until resultJson.getJSONArray("production_companies").length()) {
                    val company =
                        resultJson.getJSONArray("production_companies").getJSONObject(index)
                            .getString("name")
                    productionCompany += "$company\n"
                }

                val numberOfGenres = resultJson.getJSONArray("genres").length()
                for (genreIndex in 0 until numberOfGenres) {
                    val newGenre = resultJson.getJSONArray("genres").getJSONObject(genreIndex)
                        .getString("name")
                    genresList.add(newGenre)
                }

                if (tagline == "") {
                    binding.tagline.isVisible = false
                } else {
                    binding.tagline.isVisible = true
                    binding.tagline.text = tagline
                }

                // Image
                Picasso.get().load("https://image.tmdb.org/t/p/w500$backdropPath").into(binding.backdropPicture)
                Picasso.get().load("https://image.tmdb.org/t/p/w500$posterPath").into(binding.posterPicture)

                // text
                binding.movieReleaseDate.text = releaseDate
                if (tagline == "") {
                    binding.tagline.isVisible = false
                } else {
                    binding.tagline.isVisible = true
                    binding.tagline.text = tagline
                }
                binding.title.text = title
                binding.overview.text = overview
                binding.ratingValue.text = voteAverage.toDouble().toString()
                binding.genre.text = genresList.joinToString(", ")
                binding.status.text = status

                if (productionCompany == ""){
                    binding.sectionProductionCompanies.isVisible= false
                } else {
                    binding.sectionProductionCompanies.isVisible= true
                    binding.companies.text = productionCompany
                }
            },
            { volleyError ->
                // Error
                Log.d("Error Volle", volleyError.message.toString())
            }
        )
        Volley.newRequestQueue(this).add(strRequest)
        showCards()
    }

    private fun showCards() {
        Thread.sleep(100)
        binding.cardViewBackdropPicture.isVisible = true
        binding.cardViewInfo.isVisible = true
        binding.cardViewOverview.isVisible = true
    }

    private fun goToTmdbHomepageWithMovieId(movieID: String?) {
        try {
            val tmdbHomepage = "https://www.themoviedb.org/movie/$movieID"
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse(tmdbHomepage)
            startActivity(openURL)
            Toast.makeText(this, "Redirecting you to -> $tmdbHomepage", Toast.LENGTH_SHORT)
                .show()
        } catch (error: ActivityNotFoundException) {
            Log.d("Error Message:", error.message.toString())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        goToTmdbHomepageWithMovieId(movieID = movieID)
        return super.onOptionsItemSelected(item)
    }


}