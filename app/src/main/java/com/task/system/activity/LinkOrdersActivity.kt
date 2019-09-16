package com.task.system.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.task.system.Constans
import com.task.system.R
import com.task.system.adapters.AreaManageOrdersAdapter
import com.task.system.api.API
import com.task.system.api.TaskService
import com.task.system.bean.AreaManageOrder
import com.task.system.bean.OperateOrderList
import com.task.system.utils.TUtils
import com.yc.lib.api.ApiCallBack
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


    private var taskItem: String? = null

    private var page = 1
    private val sort: String? = null

    private var taskOrderAdapter: AreaManageOrdersAdapter? = null

    private var recycleview: RecyclerView? = null
    private var smartRefresh: SmartRefreshLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link_orders)

        taskItem = intent.getStringExtra(Constans.PASS_STRING)

        recycleview = findViewById<RecyclerView>(R.id.recycle)
        smartRefresh = findViewById(R.id.smartRefresh)
        setTitle("关联订单")
        if (taskItem == null) {
            return
        }


        getLinkOrders()


        taskOrderAdapter = AreaManageOrdersAdapter(R.layout.adapter_operate_publish_item)
        recycleview?.setLayoutManager(LinearLayoutManager(ApiConfig.context))
        recycleview?.setAdapter(taskOrderAdapter)

        taskOrderAdapter?.setOnLoadMoreListener(BaseQuickAdapter.RequestLoadMoreListener {
            page++
            getLinkOrders()
        }, recycleview)

        smartRefresh?.setOnRefreshListener(OnRefreshListener {
            page = 1
            getLinkOrders()
        })


        taskOrderAdapter?.setOnItemClickListener(BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
            val bundle = Bundle()
            bundle.putString(Constans.PASS_NAME, "任务详情")
            bundle.putString(Constans.PASS_STRING, taskOrderAdapter?.getData()!![position].order_id)
            ActivityUtils.startActivity(bundle, OrderDetailActivity::class.java)
        })


        taskOrderAdapter?.setOnItemChildClickListener(BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
            //查看原因
            //                TextView funLookReason = helper.getView(R.id.tv_look_for_reason);
            //取消任务
            //                TextView funCancleTask = helper.getView(R.id.tv_cancle_task);
            //                再次编辑
            //                TextView funEditAgain = helper.getView(R.id.tv_edit_again);
            //                订单数据
            //                View  funOrderData = helper.getView(R.id.tv_order_data);


            when (view.id) {
                R.id.tv_look_for_reason -> {
                    val about = Bundle()
                    about.putString(Constans.PASS_NAME, "审核理由")
                    about.putString(Constans.ARTICAL_TYPE, Constans.ORDER_ROOLBACK_REASON)
                    about.putString(
                        Constans.PASS_STRING,
                        taskOrderAdapter?.getData()!![position].order_id
                    )
                    ActivityUtils.startActivity(about, OpenWebViewActivity::class.java)
                }
                R.id.tv_cancle_task -> cancleTask(position, taskOrderAdapter?.getData()!![position])
                R.id.tv_edit_again -> {
                    val bundle = Bundle()
                    bundle.putString(Constans.PASS_NAME, "编辑任务")
                    bundle.putString(
                        Constans.PASS_STRING,
                        taskOrderAdapter?.getData()!![position].edit_url
                    )
                    ActivityUtils.startActivity(bundle, OpenWebViewActivity::class.java)
                }
                R.id.tv_order_data -> {
                    val orderdatas = Bundle()
                    orderdatas.putString(
                        Constans.PASS_STRING,
                        taskOrderAdapter?.getData()!![position].task_id
                    )
                    ActivityUtils.startActivity(orderdatas, LinkOrdersActivity::class.java)
                }
            }
        })
    }

    //放弃任务
    private fun cancleTask(position: Int, orderInfo: AreaManageOrder) {
        showLoadingBar("取消任务..")
        val maps = java.util.HashMap<String, String>()
        maps["task_id"] = orderInfo.task_id
        //要变更的状态，中止任务和取消任务的值均为0
        maps["status"] = "0"
        val call = ApiConfig.getInstants().create(TaskService::class.java)
            .operatorTaskStatus(TUtils.getParams(maps))

        API.getList(call, String::class.java, object : ApiCallBackList<String> {
            override fun onSuccess(msgCode: Int, msg: String, data: List<String>) {
                ToastUtils.showShort("" + msg)
                taskOrderAdapter?.remove(position)

                //                EventBus.getDefault().post(new RefreshUnreadCountEvent());
                if (taskOrderAdapter?.getData()!!.size == 0) {
                    TUtils.dealNoReqestData(taskOrderAdapter, recycleview, smartRefresh)
                }
                dismissLoadingBar()
            }

            override fun onFaild(msgCode: Int, msg: String) {
                ToastUtils.showShort("" + msg)
                dismissLoadingBar()
            }
        })
    }


//    private fun showSmartSort() {
//
//        if (quickPopupSmart == null) {
//            quickPopupSmart = BubblePopupSingle(ApiConfig.context)
//            val recyclerView = quickPopupSmart?.getContentView()
//            val layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                (ScreenUtils.getScreenHeight() * 0.5).toInt()
//            )
//            recyclerView?.layoutParams = layoutParams
//            recyclerView?.layoutManager = LinearLayoutManager(ApiConfig.context)
//            recyclerView?.addItemDecoration(RecycleViewUtils.getItemDecorationHorizontal())
//            recyclerView?.adapter = menuAdapter
//            quickPopupSmart?.setOnDismissListener(object : BasePopupWindow.OnDismissListener() {
//                override fun onDismiss() {
//                    iv_smart_sort.setImageResource(R.mipmap.icon_arrow_down)
//                }
//            })
//            menuAdapter?.setOnItemClickListener(BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
//                for (item in menuAdapter?.getData()!!) {
//                    item.isSelected = false
//                }
//                menuAdapter?.data?.get(position)?.isSelected = true
//                menuAdapter?.notifyDataSetChanged()
//                sort_id = menuAdapter?.getItem(position)!!.sort
//                quickPopupSmart?.dismiss()
//                tv_smart_sort.setText("" + menuAdapter?.getItem(position)!!.title)
//                tv_smart_sort.setTextColor(resources.getColor(R.color.red))
//                iv_smart_sort.setImageResource(R.mipmap.icon_arrow_down)
//                if (viewpage.adapter != null && viewpage.adapter is FragmentPagerItemAdapter) {
//                    val fragment =
//                        (viewpage.adapter as FragmentPagerItemAdapter).getItem(viewpage.currentItem) as TaskListAreaOrdersFragment
//                    fragment.setSortRefresh()
//                }
//            })
//        }
//
//        //
//        if (quickPopupSmart?.isShowing()!!) {
//            quickPopupSmart?.dismiss()
//            iv_smart_sort.setImageResource(R.mipmap.icon_arrow_down)
//        } else {
//            quickPopupSmart?.showPopupWindow(ll_smart_sort)
//            iv_smart_sort.setImageResource(R.mipmap.arrwo_up_red)
//        }
//    }


    private fun getLinkOrders() {
        var params = HashMap<String, String>()
        taskItem?.let { it1 -> params.put("task_id", it1) }
        var call = ApiConfig.getInstants().create(TaskService::class.java)
            .getOperatOrderList(TUtils.getParams(params))

        API.getObject(call, OperateOrderList::class.java, object : ApiCallBack<OperateOrderList> {
            override fun onSuccess(msgCode: Int, msg: String?, data: OperateOrderList?) {
                if (ApiConfig.context != null) {
                    (ApiConfig.context as BaseActivity).dismissLoadingBar()
                }

                TUtils.dealReqestData(taskOrderAdapter, recycleview, data?.list, page, smartRefresh)

            }

            override fun onFaild(msgCode: Int, msg: String?) {
                if (ApiConfig.context != null) {
                    (ApiConfig.context as BaseActivity).dismissLoadingBar()
                }

                TUtils.dealNoReqestData(taskOrderAdapter, recycleview, smartRefresh)
            }
        })
    }
}
