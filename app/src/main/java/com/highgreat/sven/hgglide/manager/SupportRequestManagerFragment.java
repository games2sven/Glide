package com.highgreat.sven.hgglide.manager;


import android.support.v4.app.Fragment;

import com.highgreat.sven.hgglide.RequestManager;


/**
 * 生命周期回调管理类
 */
public class SupportRequestManagerFragment extends Fragment {

    private final ActivityFragmentLifecycle lifecycle;

    private RequestManager requestManager;

    public SupportRequestManagerFragment( ) {
        this.lifecycle = new ActivityFragmentLifecycle();
    }

    /**
     * 当前的请求管理类
     *
     * @param requestManager
     */
    public void setRequestManager(RequestManager requestManager) {
        this.requestManager = requestManager;
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }

    ActivityFragmentLifecycle getGlideLifecycle() {
        return lifecycle;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        lifecycle.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        lifecycle.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        lifecycle.onStop();
    }


}
