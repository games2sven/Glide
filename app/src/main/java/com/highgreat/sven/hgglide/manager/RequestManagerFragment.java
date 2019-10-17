package com.highgreat.sven.hgglide.manager;

import com.highgreat.sven.hgglide.RequestManager;
import android.app.Fragment;

public class RequestManagerFragment extends Fragment {

    private final ActivityFragmentLifecycle lifecycle;
    private RequestManager requestManager;


    public RequestManagerFragment(){
        this.lifecycle = new ActivityFragmentLifecycle();
    }

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
