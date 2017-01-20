package com.landscape.alipayrobred;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class AlipayRobService extends AccessibilityService {

    private AccessibilityEvent retryEvent = null;

    public AlipayRobService() {
    }

    @Override
    public void onAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {
        int eventType = accessibilityEvent.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                List<AccessibilityNodeInfo> nodeInfos = accessibilityEvent.getSource().findAccessibilityNodeInfosByText("再来一次");
                for (AccessibilityNodeInfo accessibilityNodeInfo :nodeInfos) {
                    accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
                nodeInfos = accessibilityEvent.getSource().findAccessibilityNodeInfosByText("点击重试");
                for (AccessibilityNodeInfo accessibilityNodeInfo :nodeInfos) {
                    recycle(accessibilityNodeInfo);
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                retryEvent = accessibilityEvent;
                delayHandler.removeCallbacks(delayRun);
                delayHandler.postDelayed(delayRun, 500);
                break;
        }
    }

    Runnable delayRun = new Runnable() {
        @Override
        public void run() {
            if (retryEvent != null) {
                List<AccessibilityNodeInfo> retryInfos = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.alipay.mobile.scan.arplatform:id/cover_click_button");
                for (AccessibilityNodeInfo accessibilityNodeInfo :retryInfos) {
                    recycle(accessibilityNodeInfo);
                }
            }
        }
    };

    Handler delayHandler = new Handler();

    public void recycle(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) {
            if(info.getText() != null){
                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                AccessibilityNodeInfo parent = info.getParent();
                while(parent != null){
                    if(parent.isClickable()){
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        break;
                    }
                    parent = parent.getParent();
                }
            }

        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if(info.getChild(i)!=null){
                    recycle(info.getChild(i));
                }
            }
        }
    }

    @Override
    public void onInterrupt() {

    }
}
