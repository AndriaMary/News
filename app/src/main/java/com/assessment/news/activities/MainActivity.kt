package com.assessment.news.activities

import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.assessment.news.R
import com.assessment.news.activities.adapters.MainPageAdapter
import com.assessment.news.data.model.MainContent
import com.assessment.news.databinding.ActivityMainBinding
import com.assessment.news.utils.ConnectivityReceiver
import com.assessment.news.viewModel.MainContentViewModel


class MainActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener
   {
    private lateinit var binding : ActivityMainBinding
    private lateinit var viewModel: MainContentViewModel
    private lateinit var newsAdapter : MainPageAdapter
       var newFilterList = listOf<String>()
       private var newList = ArrayList<MainContent>()


       override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerReceiver(
            ConnectivityReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )

        prepareRecyclerView()
        viewModel = ViewModelProvider(this)[MainContentViewModel::class.java]
        viewModel.getNews()
        viewModel.observeNewsLiveData().observe(this, Observer { newsList ->
            newList= newsList as ArrayList<MainContent>
            newsAdapter.setNewsList(newsList)
        })

        viewModel.observeFilterList().observe(this, Observer { filterList ->
            newFilterList= filterList
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item, filterList
            )
            binding.ivFilter.adapter = adapter
        })




        binding.ivFilter.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                newsAdapter.filter(newList,newFilterList[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                           }

        }
    }
        private fun prepareRecyclerView() {
            newsAdapter = MainPageAdapter()
            binding.recyclerView.apply {
                layoutManager = GridLayoutManager(applicationContext, 1)
                adapter = newsAdapter
            }
        }

        override fun onNetworkConnectionChanged(isConnected: Boolean) {
            showNetworkMessage(isConnected)
        }

        private fun showNetworkMessage(isConnected: Boolean) {
            if (!isConnected) {

                binding.toolbar.setBackgroundColor(resources.getColor(R.color.no_internet))
                binding.txtNews.text = "No Internet Connection Available"
            } else {
                binding.toolbar.setBackgroundColor(resources.getColor(R.color.toolbarColor))
                binding.txtNews.text = "News"
            }
        }

        override fun onResume() {
            super.onResume()
            ConnectivityReceiver.connectivityReceiverListener = this
        }



}