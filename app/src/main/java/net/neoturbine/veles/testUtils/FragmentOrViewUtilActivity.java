package net.neoturbine.veles.testUtils;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

/* Adapted from http://stackoverflow.com/a/30974956/239003
* Must be in main source (as opposed to androidTest since ActivityTestRule
* requires activities to be declared in AndroidManifest */
public class FragmentOrViewUtilActivity extends Activity {
    private FrameLayout mFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFrame = new FrameLayout(this);
        mFrame.setId(View.generateViewId());
        setContentView(mFrame);
    }

    public void addFragment(Fragment frag) {
        getFragmentManager().beginTransaction()
                .add(mFrame.getId(), frag)
                .commit();
    }

    public void addView(View view) {
        mFrame.addView(view);
    }
}
