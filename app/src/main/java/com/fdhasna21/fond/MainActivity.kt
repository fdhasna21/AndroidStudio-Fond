package com.fdhasna21.fond

import android.app.SearchManager
import android.content.Context
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fdhasna21.fond.base.BaseActivity
import com.fdhasna21.fond.databinding.ActivityMainBinding
import com.fdhasna21.fond.model.response.ItemResto
import com.fdhasna21.fond.model.response.PlacesResponse
import com.fdhasna21.fond.utility.Utils
import com.google.gson.Gson

/**
 * Created by Fernanda Hasna on 26/10/2024.
 */

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate),
    SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    private var restoList = arrayListOf<ItemResto>()
    private lateinit var itemAdapter: ItemRestoAdapter
    private lateinit var layoutManager : LinearLayoutManager

    override fun setupData() {
        /** == Setup Dummy as Initial Data == **/
        val textfile = Utils.convertStreamToString(resources.assets.open("placesdummy.json"))
        val dummyData = Gson().fromJson(textfile, PlacesResponse::class.java)
        restoList.apply {
            clear()
            addAll(dummyData.results ?: arrayListOf())
            if(::itemAdapter.isInitialized){
            itemAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun setupUI() {
        binding.apply {
            /**== Setup Search View ==**/
            val searchManager : SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            searchView.setOnQueryTextListener(this@MainActivity)

            /**== Setup Swipe Refresh ==**/
            refreshRecyclerView.setOnRefreshListener(this@MainActivity)

            /**== Setup Recycler View ==**/
            itemAdapter = ItemRestoAdapter( this@MainActivity, restoList)
            layoutManager = LinearLayoutManager(this@MainActivity)

            recyclerView.apply {
                layoutManager = this@MainActivity.layoutManager
                adapter = itemAdapter
                addItemDecoration(object : DividerItemDecoration(this@MainActivity, VERTICAL) {})
                setHasFixedSize(true)
                itemAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onRefresh() {
        TODO("Not yet implemented")
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (currentFocus != null && imm.isAcceptingText){
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun getData(){

    }
}