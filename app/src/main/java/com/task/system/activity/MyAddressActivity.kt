package com.task.system.activity

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.task.system.Constans
import com.task.system.R
import com.task.system.adapters.AddressAdapter
import com.task.system.api.API
import com.task.system.api.TaskService
import com.task.system.bean.AddressInfo
import com.task.system.utils.RecycleViewUtils
import com.task.system.utils.TUtils
import com.yc.lib.api.ApiCallBackList
import com.yc.lib.api.ApiConfig

class MyAddressActivity : BaseActivity() {

    private var adapter: AddressAdapter? = null

    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recycle)

        setTitle("收货地址")

        tvRightFunction.visibility = View.VISIBLE
        tvRightFunction.text = "新增"


        recyclerView = findViewById(R.id.recycle)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        adapter = AddressAdapter(R.layout.adapter_address, mutableListOf())
        recyclerView?.adapter = adapter

        adapter?.setOnItemClickListener { adapter, view, position ->

            var bundle = Bundle()
            bundle.putSerializable(Constans.PASS_OBJECT,adapter.getItem(position) as AddressInfo)
            ActivityUtils.startActivity(bundle,AddressDetailActivity::class.java)

        }

        tvRightFunction.setOnClickListener {
            ActivityUtils.startActivity(AddressDetailActivity::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        getAddressList()
    }


    private fun getAddressList() {
        showLoadingBar()
        val call = ApiConfig.getInstants().create(TaskService::class.java).getAddressList(
            TUtils.getParams()
        )
        API.getList(call, AddressInfo::class.java, object : ApiCallBackList<AddressInfo> {
            override fun onSuccess(msgCode: Int, msg: String, data: List<AddressInfo>) {
                dismissLoadingBar()
                if (data != null && data?.size > 0) {
                    adapter?.setNewData(data)
                } else {
                    adapter?.setNewData(mutableListOf())
                    adapter?.setEmptyView(
                        RecycleViewUtils.getEmptyView(
                            ApiConfig.context as Activity,
                            recyclerView
                        )
                    )
                }
            }

            override fun onFaild(msgCode: Int, msg: String) {
                dismissLoadingBar()
                adapter?.setNewData(mutableListOf())
                adapter?.setEmptyView(
                    RecycleViewUtils.getEmptyView(
                        ApiConfig.context as Activity,
                        recyclerView
                    )
                )
            }
        })

    }

}
