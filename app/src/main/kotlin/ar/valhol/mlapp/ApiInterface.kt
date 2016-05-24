package ar.valhol.mlapp

import ar.valhol.mlapp.data.ApiSearch
import ar.valhol.mlapp.data.Category
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {
    @GET("sites/MLA/search")
    fun searchProducts(@Query("category") category: String): Call<ApiSearch>

    @GET("categories/{categoryId}")
    fun getCategoryDetail(@Path("categoryId") categoryId: String): Call<Category>

    @GET("sites/MLA/categories")
    fun getCategories(): Call<List<Category>>
}