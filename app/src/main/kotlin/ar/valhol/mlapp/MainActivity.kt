package ar.valhol.mlapp

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import ar.valhol.mlapp.data.ApiSearch
import ar.valhol.mlapp.CategoriesFragment
import ar.valhol.mlapp.data.Category
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.squareup.picasso.Picasso
import org.jetbrains.anko.Orientation
import org.jetbrains.anko.find
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(),
        NavigationView.OnNavigationItemSelectedListener {

    lateinit var mApiInterface: ApiInterface

    companion object {
        val API_URL = "https://api.mercadolibre.com/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar?
        setSupportActionBar(toolbar)

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout?
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer?.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView?
        navigationView?.setNavigationItemSelectedListener(this)

        setRetrofit()
        setCategoriesFragment()
    }

    fun setCategoriesFragment() {
        supportFragmentManager
                .beginTransaction()
                .addToBackStack("")
                .replace(R.id.container, CategoriesFragment.newInstance())
                .commit()
    }

    fun showCategory(categoryId: String) {
        supportFragmentManager
                .beginTransaction()
                .addToBackStack("")
                .replace(R.id.container, ProductsFragment.newInstance(categoryId))
                .commit()
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout?
        if (drawer?.isDrawerOpen(GravityCompat.START)!!) {
            drawer?.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout?
        drawer?.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setRetrofit() {
        val retrofit = Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(JacksonConverterFactory.create(ObjectMapper().registerKotlinModule()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)))
                .build()

        mApiInterface = retrofit.create(ApiInterface::class.java)
    }



    private fun searchProductsByCategory(category: String) {
        mApiInterface.searchProducts(category).enqueue(object : Callback<ApiSearch?> {
            override fun onFailure(call: Call<ApiSearch?>?, t: Throwable?) {
                t?.printStackTrace()
            }

            override fun onResponse(call: Call<ApiSearch?>?, response: Response<ApiSearch?>?) {
                response?.body()?.results?.forEach { Log.d(javaClass.name, it.title) }
            }
        })
    }


}