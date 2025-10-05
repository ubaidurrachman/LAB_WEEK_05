package com.ubai.lab_week_05

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ubai.lab_week_05.api.CatApiService
import com.ubai.lab_week_05.model.ImageData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/v1/") // ✅ Diperbaiki: hapus spasi di akhir!
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    private val catApiService by lazy {
        retrofit.create(CatApiService::class.java)
    }

    private val apiResponseView: TextView by lazy {
        findViewById(R.id.api_response)
    }

    private val imageResultView: ImageView by lazy {
        findViewById(R.id.image_result)
    }

    private val imageLoader: ImageLoader by lazy {
        GlideLoader(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getCatImageResponse()
    }

    private fun getCatImageResponse() {
        val call = catApiService.searchImages(1, "full")
        call.enqueue(object : Callback<List<ImageData>> {
            override fun onFailure(call: Call<List<ImageData>>, t: Throwable) {
                Log.e(MAIN_ACTIVITY, "Failed to get response", t)
                apiResponseView.text = "Error: ${t.message}"
            }

            override fun onResponse(
                call: Call<List<ImageData>>,
                response: Response<List<ImageData>>
            ) {
                if (response.isSuccessful) {
                    val imageList = response.body()
                    val firstImage = imageList?.firstOrNull()

                    // ✅ TUGAS AKHIR: TAMPILKAN BREED, BUKAN URL!
                    val breedName = firstImage?.breeds?.firstOrNull()?.name ?: "Unknown"
                    apiResponseView.text = "Breed: $breedName"

                    // Tetap tampilkan gambarnya
                    val imageUrl = firstImage?.imageUrl.orEmpty()
                    if (imageUrl.isNotBlank()) {
                        imageLoader.loadImage(imageUrl, imageResultView)
                    } else {
                        Log.d(MAIN_ACTIVITY, "Missing image URL")
                    }
                } else {
                    Log.e(
                        MAIN_ACTIVITY,
                        "API Error: ${response.errorBody()?.string().orEmpty()}"
                    )
                    apiResponseView.text = "Failed to load cat"
                }
            }
        })
    }

    companion object {
        const val MAIN_ACTIVITY = "MAIN_ACTIVITY"
    }
}