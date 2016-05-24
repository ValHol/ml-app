package ar.valhol.mlapp

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import ar.valhol.mlapp.ApiInterface
import ar.valhol.mlapp.MainActivity
import ar.valhol.mlapp.R
import ar.valhol.mlapp.data.ApiSearch
import ar.valhol.mlapp.data.Category
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.find
import org.jetbrains.anko.onClick
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.find
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class CategoriesFragment : Fragment() {

    companion object {
        fun newInstance(): CategoriesFragment {
            return CategoriesFragment()
        }
    }

    lateinit var mApiInterface: ApiInterface
    lateinit var mRecyclerView: RecyclerView
    lateinit var mProgressBar: ProgressBar

    lateinit var mMainActivity: MainActivity

    var mCategorySize: Int = 0

    var mRetrievedItems: Int by Delegates.observable(0) {
        d, old, new ->
        if (mCategorySize != 0) {
            mProgressBar.progress = new / mCategorySize * 100
            if (new == mCategorySize) setupRecyclerView()
        }
    }

    val mCategoryList: MutableList<Category> = arrayListOf()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mMainActivity = context as MainActivity
        mApiInterface = mMainActivity.mApiInterface
    }

    override fun onResume() {
        super.onResume()
        getCategories()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.content_main, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecyclerView = find<RecyclerView>(R.id.categoryList)
        mRecyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        mProgressBar = find<ProgressBar>(R.id.progressBar)
    }

    private fun setupRecyclerView() {
        mRecyclerView.adapter = CategoryAdapter(mCategoryList, mMainActivity)
        mProgressBar.visibility = View.INVISIBLE
        find<TextView>(R.id.loadingText).visibility = View.INVISIBLE
    }

    private fun getCategories() {
        mCategoryList.clear()
        mCategorySize = 0
        mRetrievedItems = 0
        find<TextView>(R.id.loadingText).visibility = View.VISIBLE
        mProgressBar.visibility = View.VISIBLE

        mApiInterface.getCategories().enqueue(object : Callback<List<Category>?> {
            override fun onResponse(call: Call<List<Category>?>?, response: Response<List<Category>?>?) {
                mCategorySize = response?.body()?.size ?: 0
                response?.body()?.forEach {
                    getCategoryDetail(it.id)
                }
            }

            override fun onFailure(call: Call<List<Category>?>?, t: Throwable?) {
                t?.printStackTrace()
            }
        })
    }

    private fun getCategoryDetail(id: String) {
        mApiInterface.getCategoryDetail(id).enqueue(object : Callback<Category?> {
            override fun onResponse(call: Call<Category?>?, response: Response<Category?>?) {
                if (response?.body() != null) mCategoryList.add(response?.body()!!)
                mRetrievedItems++
            }

            override fun onFailure(call: Call<Category?>?, t: Throwable?) {
                mRetrievedItems++
                t?.printStackTrace()
            }
        })
    }

    class CategoryAdapter(val categoryItems: List<Category>,
                          val mainActivity: MainActivity) :
            RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val category = categoryItems.get(position)
            holder?.categoryName?.text = category.name
            Picasso.with(mainActivity).load(Uri.parse(category.picture)).into(holder?.backgroundImage)
            holder?.itemView?.onClick {
                mainActivity.showCategory(category.id)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder? {
            return ViewHolder(View.inflate(parent?.context, R.layout.category_item, null))
        }

        override fun getItemCount(): Int {
            return categoryItems.size
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var backgroundImage: ImageView = itemView.find<ImageView>(R.id.background)
            var categoryName: TextView = itemView.find<TextView>(R.id.categoryName)
        }

    }

}