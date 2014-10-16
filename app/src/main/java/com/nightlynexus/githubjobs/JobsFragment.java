package com.nightlynexus.githubjobs;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class JobsFragment extends Fragment {

    private static final String ENDPOINT = "https://jobs.github.com";

    private static final String KEY_DESCRIPTION_PARAM = "KEY_DESCRIPTION_PARAM";
    private static final String KEY_LOCATION_PARAM = "KEY_LOCATION_PARAM";
    private static final String KEY_DESCRIPTION = "KEY_DESCRIPTION";
    private static final String KEY_LOCATION = "KEY_LOCATION";
    private static final String KEY_PAGE = "KEY_PAGE";
    private static final String KEY_JOB_LISTINGS = "KEY_JOB_LISTINGS";
    private static final String KEY_DEPLETED = "KEY_DEPLETED";

    private JobsListService mRestService;
    private ListView mListView;
    private EditText mEtDescription;
    private EditText mEtLocation;
    private Button mBtnSearch;
    private View mLoadingMoreBar;
    private View mLoadingFullView;
    private JobsAdapter mAdapter;
    private ArrayList<Job> mJobsList;
    private String mDescriptionParam;
    private String mLocationParam;
    private int mPage;
    private boolean mDepleted;
    private boolean mIsFetching;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .build();
        mRestService = restAdapter.create(JobsListService.class);
        if (savedInstanceState == null) {
            mDescriptionParam = "";
            mLocationParam = "";
            mDepleted = false;
            mPage = 0;
            mJobsList = new ArrayList<Job>();
        } else {
            mDescriptionParam = savedInstanceState.getString(KEY_DESCRIPTION_PARAM);
            mLocationParam = savedInstanceState.getString(KEY_LOCATION_PARAM);
            mDepleted = savedInstanceState.getBoolean(KEY_DEPLETED);
            mPage = savedInstanceState.getInt(KEY_PAGE);
            mJobsList = (ArrayList<Job>) savedInstanceState.getSerializable(KEY_JOB_LISTINGS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_jobs, container, false);
        mListView = (ListView) rootView.findViewById(android.R.id.list);
        mEtDescription = (EditText) rootView.findViewById(R.id.et_description);
        mEtLocation = (EditText) rootView.findViewById(R.id.et_location);
        mBtnSearch = (Button) rootView.findViewById(R.id.btn_search);
        mLoadingMoreBar = rootView.findViewById(R.id.loading_more_bar);
        mLoadingFullView = rootView.findViewById(R.id.loading_full);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            mEtDescription.setText("");
            mEtLocation.setText("");
        } else {
            mEtDescription.setText(savedInstanceState.getCharSequence(KEY_DESCRIPTION));
            mEtLocation.setText(savedInstanceState.getCharSequence(KEY_LOCATION));
        }
        mAdapter = new JobsAdapter(getActivity(), mJobsList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Intent intent = new Intent(getActivity(), JobDetailsActivity.class);
                intent.putExtra(JobDetailsActivity.EXTRA_JOB, mJobsList.get(position));
                startActivity(intent);
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mJobsList.get(position).getCompanyUrl()));
                startActivity(intent);
                return true;
            }
        });
        final TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (actionId == EditorInfo.IME_NULL
                        && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    doNewSearch();
                    // close soft keyboard
                    final InputMethodManager inputManager = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }
                return false;
            }
        };
        mEtDescription.setOnEditorActionListener(onEditorActionListener);
        mEtLocation.setOnEditorActionListener(onEditorActionListener);
        mBtnSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                doNewSearch();
                // close soft keyboard from btton click
                final InputMethodManager inputManager
                        = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                View v = getActivity().getCurrentFocus();
                if (v != null) {
                    inputManager.hideSoftInputFromWindow(v.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
        mListView.setOnScrollListener(mOnScrollListener);
        mIsFetching = false;
        if (mJobsList.isEmpty()) {
            requestJobs();
        }
    }

    private void doNewSearch() {
        mDepleted = false;
        mPage = 0;
        mJobsList.clear();
        mAdapter.notifyDataSetChanged();
        mDescriptionParam = mEtDescription.getText().toString();
        mLocationParam = mEtLocation.getText().toString();
        requestJobs();
    }

    private void requestJobs() {
        if (mIsFetching || mDepleted) return;
        startFetching();
        mRestService.getJobsList(mDescriptionParam, mLocationParam, mPage, mCallBack);
    }

    private final AbsListView.OnScrollListener mOnScrollListener
            = new AbsListView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                             int totalItemCount) {
            if (firstVisibleItem + visibleItemCount + 5 >= totalItemCount) {
                requestJobs();
            }
        }
    };

    private final Callback<List<Job>> mCallBack = new Callback<List<Job>>() {

        @Override
        public void success(List<Job> jobs, Response response) {
            if (isDetached()) return;
            if (jobs.isEmpty()) {
                mDepleted = true;
            } else {
                mJobsList.addAll(jobs);
                mAdapter.notifyDataSetChanged();
                mPage++;
            }
            stopFetching();
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            if (isDetached()) return;
            stopFetching();
            Toast.makeText(getActivity(), R.string.try_again, Toast.LENGTH_SHORT).show();
        }
    };

    private void startFetching() {
        mIsFetching = true;
        mBtnSearch.setEnabled(false);
        if (mPage > 0) {
            showLoadingBar(true);
        } else {
            showLoadingFull(true);
        }
    }

    private void stopFetching() {
        mIsFetching = false;
        mBtnSearch.setEnabled(true);
        showLoadingBar(false);
        showLoadingFull(false);
    }

    private void showLoadingBar(final boolean show) {
        if (show) {
            mLoadingMoreBar.setVisibility(View.VISIBLE);
        } else {
            mLoadingMoreBar.setVisibility(View.GONE);
        }
    }

    private void showLoadingFull(final boolean show) {
        if (show) {
            mListView.setVisibility(View.GONE);
            mLoadingFullView.setVisibility(View.VISIBLE);
        } else {
            mLoadingFullView.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_DESCRIPTION_PARAM, mDescriptionParam);
        outState.putString(KEY_LOCATION_PARAM, mLocationParam);
        outState.putCharSequence(KEY_DESCRIPTION, mEtDescription.getText());
        outState.putCharSequence(KEY_LOCATION, mEtLocation.getText());
        outState.putInt(KEY_PAGE, mPage);
        outState.putSerializable(KEY_JOB_LISTINGS, mJobsList);
        outState.putBoolean(KEY_DEPLETED, mDepleted);
        super.onSaveInstanceState(outState);
    }
}
