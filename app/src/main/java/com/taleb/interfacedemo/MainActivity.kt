package com.taleb.interfacedemo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainRecyclerView {

    override fun onItemClicked(data: String) {
        Toast.makeText(this,data,Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val iphoneList = ArrayList<IphoneModel>()
        iphoneList.add(IphoneModel("iPhone 11 Pro",999))
        iphoneList.add(IphoneModel("iPhone 11",699))
        iphoneList.add(IphoneModel("iPhone XR",599))
        iphoneList.add(IphoneModel("iPhone 8",449))
        iphoneList.add(IphoneModel("iPhone 7",208))


        val adapter = MainRecyclerAdapter(iphoneList,this)
        mainRecyclerView.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        mainRecyclerView.adapter = adapter
    }
}
