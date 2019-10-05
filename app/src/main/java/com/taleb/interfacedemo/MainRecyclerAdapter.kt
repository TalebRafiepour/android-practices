package com.taleb.interfacedemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_ihpone_model_layout.view.*

class MainRecyclerAdapter(private val mainRecyclerList: ArrayList<IphoneModel>,private val delegate: MainRecyclerView): RecyclerView.Adapter<MainRecyclerAdapter.MainRecyclerItemHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MainRecyclerItemHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.item_ihpone_model_layout,p0,false)
        return MainRecyclerItemHolder(view)
    }

    override fun getItemCount(): Int = mainRecyclerList.size

    override fun onBindViewHolder(p0: MainRecyclerItemHolder, p1: Int) {
        p0.onBind(mainRecyclerList[p1])
    }


    inner class MainRecyclerItemHolder(view: View): RecyclerView.ViewHolder(view) {

        fun onBind(iphoneModel: IphoneModel){
            itemView.iphoneModel.text = iphoneModel.name
            itemView.iphonePrice.text = "$${iphoneModel.price}"
            itemView.setOnClickListener {
                delegate.onItemClicked(iphoneModel.name)
            }
        }
    }
}