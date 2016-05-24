package ar.valhol.mlapp

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ar.valhol.mlapp.data.ApiSearch
import ar.valhol.mlapp.data.Category
import ar.valhol.mlapp.data.Product
import com.squareup.picasso.Picasso
import org.jetbrains.anko.find
import org.jetbrains.anko.onClick
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.find
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductsFragment: Fragment() {

    lateinit var mApiInterface : ApiInterface
    lateinit var mCategoryId : String
    lateinit var mRecyclerView : RecyclerView
    val mProductsList : MutableList<Product> = arrayListOf()

    companion object {
        val categoryArg = "CATEGORYID"
        fun newInstance(categoryId: String): ProductsFragment {
            val productsFragment = ProductsFragment()
            val bundle = Bundle()
            bundle.putCharSequence(categoryArg, categoryId)
            productsFragment.arguments = bundle
            return productsFragment
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val mainActivity = context as MainActivity
        mApiInterface = mainActivity.mApiInterface
        mCategoryId = arguments.getCharSequence(categoryArg).toString()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.products_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecyclerView = find<RecyclerView>(R.id.productsList)
        mRecyclerView.layoutManager = LinearLayoutManager(ctx)
        searchProductsByCategory(mCategoryId)
    }

    private fun searchProductsByCategory(category: String) {
        mProductsList.clear()
        find<TextView>(R.id.loadingProductsText).visibility = View.VISIBLE
        mApiInterface.searchProducts(category).enqueue(object : Callback<ApiSearch?> {
            override fun onFailure(call: Call<ApiSearch?>?, t: Throwable?) {
                t?.printStackTrace()
            }

            override fun onResponse(call: Call<ApiSearch?>?, response: Response<ApiSearch?>?) {
                mProductsList.addAll(response?.body()?.results ?: emptyList())
                setupRecyclerView()
            }
        })
    }

    private fun setupRecyclerView() {
        find<TextView>(R.id.loadingProductsText).visibility = View.GONE
        mRecyclerView.adapter = ProductsAdapter(mProductsList)
    }

    class ProductsAdapter(val productsItems: List<Product>) :
            RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val product = productsItems.get(position)
            holder?.productName?.text = product.title
            holder?.productPrice?.text = "$${product.price?.toString()}"
            Picasso.with(holder?.itemView?.context).load(Uri.parse(product.thumbnail)).into(holder?.imageHolder)
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder? {
            return ViewHolder(View.inflate(parent?.context, R.layout.product_item, null))
        }

        override fun getItemCount(): Int {
            return productsItems.size
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageHolder = itemView.find<ImageView>(R.id.imageHolder)
            val productName = itemView.find<TextView>(R.id.productName)
            val productPrice = itemView.find<TextView>(R.id.productPrice)
        }

    }
}