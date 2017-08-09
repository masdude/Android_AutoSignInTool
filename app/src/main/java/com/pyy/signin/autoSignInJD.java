package com.pyy.signin;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.concurrent.TimeUnit;

import static com.pyy.signin.SignInService.autoCondition;
import static com.pyy.signin.SignInService.autoLock;
import static com.pyy.signin.Utils.delay;

/**
 * Created by pyy on 2017/8/9.
 */

public class autoSignInJD {
    private boolean found = false;
    final int VIP = 0;
    final int BEAN = 1;

    public void doJD(AccessibilityService service) {
        autoLock.lock();
        boolean ret;
        try {
            delay(6000);
            autoSignInJd(service.getRootInActiveWindow(), BEAN, service);
            ret = autoCondition.await(20, TimeUnit.SECONDS); // 20s
            if (!ret) {
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            }
            delay(2000);
            autoSignInJd(service.getRootInActiveWindow(), VIP, service);
            ret = autoCondition.await(20, TimeUnit.SECONDS); // 20s
            if (!ret) {
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            }
            delay(1000);
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            delay(1000);
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        autoLock.unlock();
        MainPage.condition.signal();
    }

    private void autoSignInJd(AccessibilityNodeInfo info, int type, AccessibilityService service) {
        if (BEAN == type && info.getChildCount() == 10) {
            if ("android.widget.GridView".equals(info.getClassName())) {
                AccessibilityNodeInfo child = info.getChild(6);
                if (child.isClickable() && "android.widget.RelativeLayout".equals(child.getClassName())
                        && "领京豆".equals(child.getChild(0).getText())) {
                    child.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    found = true;
                    return;
                }
            }
        } else if (VIP == type && info.getChildCount() == 0) {
            if ("android.widget.ImageView".equals(info.getClassName()) && info.isClickable()) {
                if ("我的".equals(info.getContentDescription())) {
                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    delay(5000);
                    iteratorJD(service.getRootInActiveWindow());
                    found = true;
                    return;
                }
            }
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if(info.getChild(i)!=null){
                    if (found) {
                        found = false;
                        break;
                    }
                    autoSignInJd(info.getChild(i), type, service);
                }
            }
        }
        return;
    }

    private void iteratorJD(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0 && info.getText() != null) {
            if (info.getText().toString().contains("会员") && !info.getText().equals("PLUS会员")) {
                AccessibilityNodeInfo parent = info.getParent();
                if ("android.widget.RelativeLayout".equals(parent.getClassName())
                        && parent.isClickable()) {
                    parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    found = true;
                    return ;
                }
            }
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if(info.getChild(i)!=null){
                    if (found) {
                        found  = false;
                        break;
                    }
                    iteratorJD(info.getChild(i));
                }
            }
        }
        return ;
    }
}
