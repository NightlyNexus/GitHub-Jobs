package com.nightlynexus.githubjobs;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class StartActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new JobsFragment())
                    .commit();
        }
    }
}
