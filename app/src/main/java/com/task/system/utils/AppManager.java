package com.task.system.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.Iterator;
import java.util.Stack;

public class AppManager {
    private static Stack<Activity> activityStack;
    private static AppManager instance;

    private AppManager() {
    }

    public static AppManager getAppManager() {
        if (instance == null) {
            instance = new AppManager();
        }

        return instance;
    }

    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack();
        }

        activityStack.add(activity);
    }

    public Activity currentActivity() {
        Activity activity = (Activity)activityStack.lastElement();
        return activity;
    }

    public void finishActivity() {
        Activity activity = (Activity)activityStack.lastElement();
        this.finishActivity(activity);
    }

    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }

    }

    public void finishActivity(Class<?> cls) {
        Iterator var2 = activityStack.iterator();

        while(var2.hasNext()) {
            Activity activity = (Activity)var2.next();
            if (activity.getClass().equals(cls)) {
                this.finishActivity(activity);
            }
        }

    }

    public void finishAllActivity() {
        int i = 0;

        for(int size = activityStack.size(); i < size; ++i) {
            if (null != activityStack.get(i)) {
                ((Activity)activityStack.get(i)).finish();
            }
        }

        activityStack.clear();
    }

    public void AppExit(Context context) {
        try {
            this.finishAllActivity();
            ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.restartPackage(context.getPackageName());
            System.exit(0);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public void finishAllActivity(boolean isExit, String mainClassName) {
        Iterator var3 = activityStack.iterator();

        while(var3.hasNext()) {
            Activity act = (Activity)var3.next();
            if (act != null) {
                if (!isExit) {
                    if (!act.getClass().getName().contains(mainClassName)) {
                        act.finish();
                    }
                } else {
                    act.finish();
                }
            }
        }

        if (isExit) {
            activityStack.clear();
        }

    }

    public void finishAllActivity(String mainClassName) {
        this.finishAllActivity(false, mainClassName);
    }
}