package com.task.system.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.task.system.Constans
import com.task.system.R
import com.task.system.adapters.FragmentPagerItemAdapter
import com.task.system.adapters.MenuAdapter
import com.task.system.api.API
import com.task.system.api.TaskService
import com.task.system.bean.AreaManageIitem
import com.task.system.bean.HomeMenu
import com.task.system.fragments.TaskListAreaOrdersFragment
import com.task.system.utils.PerfectClickListener
import com.task.system.utils.RecycleViewUtils
import com.task.system.utils.TUtils
import com.task.system.views.BubblePopupSingle
import com.task.system.views.FragmentPagerItem
import com.task.system.views.FragmentPagerItems
import com.yc.lib.api.ApiCallBackList
import com.yc.lib.api.ApiConfig
import com.yc.lib.api.utils.SysUtils
import kotlinx.android.synthetic.main.task_fragment.*
import razerdp.basepopup.BasePopupWindow

/**
 * FileName: OrderManageActivity.kt
 * Author: dengyancheng
 * Date: 2019-08-22 23:18
 * Description: 订单管理
 * History:
 */
class OrderManageActivity : BaseActivity() {

    lateinit  var etKey: EditText
    private var sort_id: String? = ""
    private var quickPopupSmart: BubblePopupSingle? = null
    private var menuAdapter: MenuAdapter? = null

    private var selectPostion: Int = 0
    private var tabs: List<AreaManageIitem>? = null


    private var tvCount: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.task_fragment)
        tv_title_top.visibility = View.GONE
        setTitle("订单/管理")

        selectPostion = intent.getIntExtra(Constans.PASS_STRING, 0)
        tabs = intent.getSerializableExtra(Constans.PASS_OBJECT) as List<AreaManageIitem>
        if (tabs == null || tabs?.size == 0) {
            return
        }
        val pages = getPagerItems(FragmentPagerItems(ApiConfig.context))
        val adapter = FragmentPagerItemAdapter(
            supportFragmentManager, pages
        )
        viewpage.setAdapter(adapter)
        tabs?.size?.let { viewpage.setOffscreenPageLimit(it) }

        tablayout.setupWithViewPager(viewpage)

        iv_search.visibility = View.VISIBLE
        etKey = findViewById(R.id.et_keys)
        et_keys.visibility = View.VISIBLE


        iv_search.setOnClickListener {
            if (TextUtils.isEmpty(et_keys.editableText.toString())) {
                SysUtils.showToast("请输入任务名/任务ID")
                return@setOnClickListener
            }
            if (viewpage.adapter != null && viewpage.adapter is FragmentPagerItemAdapter) {
                val fragment =
                    (viewpage.adapter as FragmentPagerItemAdapter).getItem(viewpage.currentItem) as TaskListAreaOrdersFragment
                fragment.setSortRefresh()
            }

        }



        menuAdapter = MenuAdapter(R.layout.adapter_drop_menu_item)

        getSmartSort()

        //要放到 setsetupWithViewPager houmian
        for (i in 0 until tabs?.size!!) {
            val tab = tablayout.getTabAt(i)
            val inflate = View.inflate(ApiConfig.context, R.layout.tab_layout, null)
            val textView = inflate.findViewById<TextView>(R.id.tv_tab_name)
            val tvMessage = inflate.findViewById<TextView>(R.id.tv_message_num)
            if (!TextUtils.isEmpty(tabs?.get(i)?.count)) {
                tvMessage.visibility = View.VISIBLE
                tvMessage.setText(tabs?.get(i)?.count)
                tvCount = tvMessage
            } else {
                tvMessage.visibility = View.GONE
            }
            //
            textView.text = tabs?.get(i)?.title
            tab!!.setCustomView(inflate)
        }

        viewpage.currentItem = selectPostion
        viewpage.offscreenPageLimit = tabs?.size!!


        ll_smart_sort_ui.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View) {
                showSmartSort()
            }
        })


    }

    //智能分类
    private fun getSmartSort() {

        val call = ApiConfig.getInstants().create(TaskService::class.java)
            .getOperateTaskSorts(TUtils.getParams())

        API.getList(call, HomeMenu::class.java, object : ApiCallBackList<HomeMenu> {
            override fun onSuccess(msgCode: Int, msg: String, datas: List<HomeMenu>?) {
                if (datas != null) {
                    menuAdapter?.setNewData(datas)
                }
            }

            override fun onFaild(msgCode: Int, msg: String) {
                ToastUtils.showShort(msg)
            }
        })
    }

    private fun showSmartSort() {

        if (quickPopupSmart == null) {
            quickPopupSmart = BubblePopupSingle(ApiConfig.context)
            val recyclerView = quickPopupSmart?.getContentView()
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (ScreenUtils.getScreenHeight() * 0.5).toInt()
            )
            recyclerView?.layoutParams = layoutParams
            recyclerView?.layoutManager = LinearLayoutManager(ApiConfig.context)
            recyclerView?.addItemDecoration(RecycleViewUtils.getItemDecorationHorizontal())
            recyclerView?.adapter = menuAdapter
            quickPopupSmart?.setOnDismissListener(object : BasePopupWindow.OnDismissListener() {
                override fun onDismiss() {
                    iv_smart_sort.setImageResource(R.mipmap.icon_arrow_down)
                }
            })
            menuAdapter?.setOnItemClickListener(BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
                for (item in menuAdapter?.getData()!!) {
                    item.isSelected = false
                }
                menuAdapter?.data?.get(position)?.isSelected = true
                menuAdapter?.notifyDataSetChanged()
                sort_id = menuAdapter?.getItem(position)!!.sort
                quickPopupSmart?.dismiss()
                tv_smart_sort.setText("" + menuAdapter?.getItem(position)!!.title)
                tv_smart_sort.setTextColor(resources.getColor(R.color.red))
                iv_smart_sort.setImageResource(R.mipmap.icon_arrow_down)
                if (viewpage.adapter != null && viewpage.adapter is FragmentPagerItemAdapter) {
                    val fragment =
                        (viewpage.adapter as FragmentPagerItemAdapter).getItem(viewpage.currentItem) as TaskListAreaOrdersFragment
                    fragment.setSortRefresh()
                }
            })
        }

        //
        if (quickPopupSmart?.isShowing()!!) {
            quickPopupSmart?.dismiss()
            iv_smart_sort.setImageResource(R.mipmap.icon_arrow_down)
        } else {
            quickPopupSmart?.showPopupWindow(ll_smart_sort)
            iv_smart_sort.setImageResource(R.mipmap.arrwo_up_red)
        }
    }

    private fun getPagerItems(pages: FragmentPagerItems): FragmentPagerItems {
        for (item in this!!.tabs!!) {
            val bundle = Bundle()
            bundle.putString(Constans.PASS_STRING, item?.status)
            pages.add(
                FragmentPagerItem.of(
                    item.title,
                    TaskListAreaOrdersFragment::class.java,
                    bundle
                )
            )
        }

        return pages
    }
}
