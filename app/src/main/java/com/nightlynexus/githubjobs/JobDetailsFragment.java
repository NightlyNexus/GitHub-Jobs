package com.nightlynexus.githubjobs;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.graphics.PaletteItem;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ShareActionProvider;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class JobDetailsFragment extends Fragment {

    public static final String KEY_JOB = "KEY_JOB";

    private WebView mWebViewHowToApply;
    private WebView mWebViewDescription;
    private Job mJob;
    private Target mTarget; // keep global so target doesn't get garbage collected

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
        mWebViewHowToApply.loadData(mJob.getHowToApply(), "text/html", "UTF-8");
        mWebViewDescription.loadData(mJob.getDescription(), "text/html", "UTF-8");
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupActionBar();
    }

    private void setupActionBar() {
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().setTitle(mJob.getTitle());
        mTarget = new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if (isDetached()) return;
                getActivity().getActionBar().setIcon(new BitmapDrawable(getResources(), bitmap));
                Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {

                    @Override
                    public void onGenerated(Palette palette) {
                        if (isDetached()) return;
                        final PaletteItem pi = palette.getLightVibrantColor();
                        if (pi == null) setCrazyActionBarColor();
                        else setAbColorSafe(pi.getRgb());
                    }
                });
            }

            @Override
            public void onBitmapFailed(Drawable drawable) {
                if (isDetached()) return;
                setCrazyActionBarColor();
            }

            @Override
            public void onPrepareLoad(Drawable drawable) {
                if (isDetached()) return;
            }
        };
        getActivity().getActionBar().setIcon(android.R.color.transparent);
        if (mJob.getCompanyLogo() == null) setCrazyActionBarColor();
        else Picasso.with(getActivity()).load(mJob.getCompanyLogo()).into(mTarget);
    }

    private void setCrazyActionBarColor() {
        final int crazyCode = mJob.getTitle().hashCode();
        setAbColorSafe(crazyCode);
    }

    private void setAbColorSafe(final int color) {
        final int actionBarColor = - Math.abs(color % Color.BLACK);
        if (actionBarColor > Color.rgb(20, 20, 20) && actionBarColor < Color.rgb(241, 241, 241)) {
            getActivity().getActionBar().setBackgroundDrawable(new ColorDrawable(actionBarColor));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.job_details, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        final ShareActionProvider shareActionProvider = (ShareActionProvider) menuItem.getActionProvider();
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
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.putExtra(Intent.EXTRA_SUBJECT, mJob.getTitle());
        intent.putExtra(Intent.EXTRA_TEXT, mJob.getUrl());
        return intent;
    }
}
