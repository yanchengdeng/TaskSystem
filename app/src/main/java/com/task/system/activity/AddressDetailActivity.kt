package com.task.system.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.blankj.utilcode.util.RegexUtils
import com.task.system.Constans
import com.task.system.R
import com.task.system.api.API
import com.task.system.api.TaskService
import com.task.system.bean.AddressInfo
import com.task.system.bean.SimpleBeanInfo
import com.task.system.utils.TUtils
import com.yc.lib.api.ApiCallBack
import com.yc.lib.api.ApiConfig
import com.yc.lib.api.utils.SysUtils

class AddressDetailActivity : BaseActivity() {

    private var editName: EditText? = null
    private var etPhone: EditText? = null
    private var etAddess: EditText? = null

    private var addressInfo: AddressInfo? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_detail)
        title = "收货信息"

        editName = findViewById(R.id.et_post_name)
        etPhone = findViewById(R.id.et_post_phone)
        etAddess = findViewById(R.id.et_post_address)

        if (intent != null && intent.getSerializableExtra(Constans.PASS_OBJECT) != null) {
            addressInfo = intent.getSerializableExtra(Constans.PASS_OBJECT) as AddressInfo?
            tvRightFunction.visibility = View.VISIBLE
            tvRightFunction.text = "删除"


            editName?.setText(addressInfo?.contact_name)
            etPhone?.setText(addressInfo?.contact_mobile)
            etAddess?.setText(addressInfo?.address)
        }


        findViewById<TextView>(R.id.tv_submit).setOnClickListener {
            if (TextUtils.isEmpty(editName?.editableText.toString())) {
                SysUtils.showToast("请输入收件人姓名")
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(etPhone?.editableText.toString())) {
                SysUtils.showToast("请输入收件人电话")
                return@setOnClickListener
            }

            if (!RegexUtils.isMobileSimple(etPhone?.editableText.toString())) {
                SysUtils.showToast("请输入正确号码")
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(etAddess?.editableText.toString())) {
                SysUtils.showToast("请输入收件人地址")
                return@setOnClickListener
            }


            if (addressInfo != null) {
                doSubmitAddress()
            } else {
                addAddress()
            }
        }


        tvRightFunction.setOnClickListener {
            deleteAddress()
        }


    }

    //修改地址
    private fun doSubmitAddress() {

        showLoadingBar()

        var maps = HashMap<String, String>()
        maps.put("address", etAddess?.editableText.toString())
        maps.put("contact_name", editName?.editableText.toString())
        maps.put("contact_mobile", etPhone?.editableText.toString())
        maps.put("id", addressInfo?.id.toString())
        val call = ApiConfig.getInstants().create(TaskService::class.java).setAddress(
            TUtils.getParams(maps)
        )
        API.getObjectIgnoreBody(call, object : ApiCallBack<SimpleBeanInfo> {
            override fun onSuccess(msgCode: Int, msg: String?, data: SimpleBeanInfo?) {
                dismissLoadingBar()
                SysUtils.showToast(msg + "")
            }

            override fun onFaild(msgCode: Int, msg: String?) {
                dismissLoadingBar()
                SysUtils.showToast(msg + "")

            }
        })
    }

    //添加地址
    fun addAddress() {

        showLoadingBar()

        var maps = HashMap<String, String>()
        maps.put("address", etAddess?.editableText.toString())
        maps.put("contact_name", editName?.editableText.toString())
        maps.put("contact_mobile", etPhone?.editableText.toString())
        val call = ApiConfig.getInstants().create(TaskService::class.java).addAddress(
            TUtils.getParams(maps)
        )
        API.getObjectIgnoreBody(call,  object : ApiCallBack<SimpleBeanInfo> {
            override fun onSuccess(msgCode: Int, msg: String?, data: SimpleBeanInfo?) {
                dismissLoadingBar()
                SysUtils.showToast(msg + "")
                finish()
            }

            override fun onFaild(msgCode: Int, msg: String?) {
                dismissLoadingBar()
                SysUtils.showToast(msg + "")

            }
        })


    }

    //删除地址
    fun deleteAddress() {
        showLoadingBar()

        var maps = HashMap<String, String>()
        maps.put("id", addressInfo?.id.toString())
        val call = ApiConfig.getInstants().create(TaskService::class.java).delAddress(
            TUtils.getParams(maps)
        )
        API.getObjectIgnoreBody(call, object : ApiCallBack<SimpleBeanInfo> {
            override fun onSuccess(msgCode: Int, msg: String?, data: SimpleBeanInfo?) {
                dismissLoadingBar()
                SysUtils.showToast(msg + "")
                finish()
            }

            override fun onFaild(msgCode: Int, msg: String?) {
                dismissLoadingBar()
                SysUtils.showToast(msg + "")

            }
        })

    }
}
