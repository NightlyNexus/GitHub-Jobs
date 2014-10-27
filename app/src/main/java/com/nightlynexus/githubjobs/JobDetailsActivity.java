package com.nightlynexus.githubjobs;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class JobDetailsActivity extends ActionBarActivity {

    public static final String EXTRA_JOB = "EXTRA_JOB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        if (savedInstanceState == null) {
            final Fragment fragment = new JobDetailsFragment();
            final Bundle bundle = new Bundle();
            bundle.putSerializable(JobDetailsFragment.KEY_JOB,
                    getIntent().getSerializableExtra(EXTRA_JOB));
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }
}
