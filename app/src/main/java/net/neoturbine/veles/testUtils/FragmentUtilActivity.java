package net.neoturbine.veles.testUtils;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

/* Adapted from http://stackoverflow.com/a/30974956/239003
* Must be in main source (as opposed to androidTest since ActivityTestRule
* requires activities to be declared in AndroidManifest */
public class FragmentUtilActivity extends Activity {
    private int mViewId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewId = View.generateViewId();

        FrameLayout view = new FrameLayout(this);
        view.setId(mViewId);
        setContentView(view);
    }

    public void addFragment(Fragment frag) {
        getFragmentManager().beginTransaction()
                .add(mViewId, frag)
                .commit();
    }
}
