package com.task.system.utils

import android.app.Activity
import android.content.Intent


/**
 * Copyright (C), 2016-2019, 福建大道之行有限公司
 * FileName: KUtils
 * Author: dengyc
 * Date: 2019-08-15 01:10
 * Description:
 * History:
 */

class KUtils {
    companion object{


       fun startActivty(context:Activity,clazz : Class<Activity> ){
            context?.startActivity(Intent(context,clazz))
        }


    }
}