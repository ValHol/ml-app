package ar.valhol.mlapp

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ar.valhol.mlapp.data.ApiSearch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductsFragment: Fragment() {

    lateinit var mApiInterface : ApiInterface
    lateinit var mCategoryId : String

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
        searchProductsByCategory(mCategoryId)
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