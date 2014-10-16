package com.nightlynexus.githubjobs;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class JobsAdapter extends ArrayAdapter<Job> {

    public JobsAdapter(final Context context, final List<Job> jobsList) {
        super(context, 0, jobsList);
    }

    private static class ViewHolder {

        private final TextView jobTitle;
        private final TextView jobCompany;
        private final TextView jobLocation;
        private final TextView jobCreatedAt;

        private ViewHolder(final View view) {
            jobTitle = (TextView) view.findViewById(R.id.job_title);
            jobCompany = (TextView) view.findViewById(R.id.job_company);
            jobLocation = (TextView) view.findViewById(R.id.job_location);
            jobCreatedAt = (TextView) view.findViewById(R.id.job_created_at);
        }
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        final ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.job_row, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Job job = getItem(position);
        final String jobTitle = job.getTitle();
        final String jobCompany = job.getCompany();
        final String jobLocation = job.getLocation();
        final CharSequence jobCreatedAt = getReadableCreatedAt(job.getCreatedAt());
        holder.jobTitle.setText(jobTitle);
        holder.jobCompany.setText(jobCompany);
        holder.jobLocation.setText(jobLocation);
        holder.jobCreatedAt.setText(jobCreatedAt);
        return convertView;
    }

    private CharSequence getReadableCreatedAt(String dateStr) {
        final DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
        final Date date;
        try {
            date = df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        if (date.after(new Date())) {
            return getContext().getString(R.string.just_now);
        }
        return DateUtils.getRelativeTimeSpanString(date.getTime());
    }
}
