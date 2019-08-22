package com.task.system.activity

import android.os.Bundle
import com.task.system.Constans
import com.task.system.R
import com.task.system.api.API
import com.task.system.api.TaskService
import com.task.system.bean.AreaManagePublish
import com.task.system.bean.SimpleBeanInfo
import com.task.system.utils.TUtils
import com.yc.lib.api.ApiCallBackList
import com.yc.lib.api.ApiConfig

/**
 * FileName: LinkOrdersActivity.kt
 * Author: dengyancheng
 * Date: 2019-08-22 22:33
 * Description: 关联订单
 * History:
 */
class LinkOrdersActivity : BaseActivity() {


    private var taskItem: AreaManagePublish?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link_orders)

        taskItem = intent.getSerializableExtra(Constans.PASS_OBJECT) as AreaManagePublish

        setTitle("关联订单")
        if (taskItem==null){
           return
        }


        getLinkOrders()







    }


    private fun getLinkOrders(){
        var params = HashMap<String,String>()
        taskItem?.id?.let {
            params["task_id"] =it
        }
        var call = ApiConfig.getInstants().create(TaskService::class.java).getOperatOrderList(TUtils.getParams(params))

        API.getList(call,SimpleBeanInfo::class.java, object : ApiCallBackList<SimpleBeanInfo> {
            override fun onSuccess(msgCode: Int, msg: String?, data: MutableList<SimpleBeanInfo>?) {


            }

            override fun onFaild(msgCode: Int, msg: String?) {

            }
        })
    }
}
