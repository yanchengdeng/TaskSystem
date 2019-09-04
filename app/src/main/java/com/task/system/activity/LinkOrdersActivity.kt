package com.task.system.activity

import android.os.Bundle
import com.task.system.Constans
import com.task.system.R
import com.task.system.api.API
import com.task.system.api.TaskService
import com.task.system.bean.AreaManageOrder
import com.task.system.bean.OperateOrderList
import com.task.system.utils.TUtils
import com.yc.lib.api.ApiCallBack
import com.yc.lib.api.ApiConfig

/**
 * FileName: LinkOrdersActivity.kt
 * Author: dengyancheng
 * Date: 2019-08-22 22:33
 * Description: 关联订单
 * History:
 */
class LinkOrdersActivity : BaseActivity() {


    private var taskItem: AreaManageOrder?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link_orders)

        taskItem = intent.getSerializableExtra(Constans.PASS_OBJECT) as AreaManageOrder

        setTitle("关联订单")
        if (taskItem==null){
           return
        }


        getLinkOrders()







    }


    private fun getLinkOrders(){
        var params = HashMap<String,String>()
        taskItem?.task_id?.let {
            params["task_id"] =it
        }
        var call = ApiConfig.getInstants().create(TaskService::class.java).getOperatOrderList(TUtils.getParams(params))

        API.getObject(call, OperateOrderList::class.java, object : ApiCallBack<OperateOrderList> {
            override fun onSuccess(msgCode: Int, msg: String?, data: OperateOrderList?) {


            }

            override fun onFaild(msgCode: Int, msg: String?) {

            }
        })
    }
}
