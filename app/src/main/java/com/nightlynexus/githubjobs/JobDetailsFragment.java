package com.nightlynexus.githubjobs;

import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class JobDetailsFragment extends Fragment {

    public static final String KEY_JOB = "KEY_JOB";

    private WebView mWebViewHowToApply;
    private WebView mWebViewDescription;
    private Job mJob;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mJob = (Job) getArguments().getSerializable(KEY_JOB);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_job_details, container, false);
        mWebViewHowToApply = (WebView) rootView.findViewById(R.id.web_view_how_to_apply);
        mWebViewDescription = (WebView) rootView.findViewById(R.id.web_view_description);

        String howto64 = Base64.encodeToString(mJob.getHowToApply().getBytes(), Base64.DEFAULT);
        mWebViewHowToApply.loadData(howto64, "text/html; charset=utf-8", "base64");

        String descr64 = Base64.encodeToString(mJob.getDescription().getBytes(), Base64.DEFAULT);
        mWebViewDescription.loadData(descr64, "text/html; charset=utf-8", "base64");

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupActionBar();
    }

    private void setupActionBar() {
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().setTitle(mJob.getTitle());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.job_details, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        ShareActionProvider shareActionProvider = (ShareActionProvider)
                MenuItemCompat.getActionProvider(menuItem);
        shareActionProvider.setShareIntent(getShareIntent());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Intent getShareIntent() {
        final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, mJob.getTitle());
        intent.putExtra(Intent.EXTRA_TEXT, mJob.getUrl());
        return intent;
    }
}
