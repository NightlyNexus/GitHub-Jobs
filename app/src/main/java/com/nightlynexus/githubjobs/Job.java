package com.nightlynexus.githubjobs;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Job implements Serializable {

    @SerializedName("id") private final String id;
    @SerializedName("created_at") private final String createdAt;
    @SerializedName("title") private final String title;
    @SerializedName("location") private final String location;
    @SerializedName("type") private final String type;
    @SerializedName("description") private final String description;
    @SerializedName("how_to_apply") private final String howToApply;
    @SerializedName("company") private final String company;
    @SerializedName("company_url") private final String companyUrl;
    @SerializedName("company_logo") private final String companyLogo;
    @SerializedName("url") private final String url;

    public String getId() {
        return id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getHowToApply() {
        return howToApply;
    }

    public String getCompany() {
        return company;
    }

    public String getCompanyUrl() {
        return companyUrl;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public String getUrl() {
        return url;
    }

    public Job(final String id, final String createdAt, final String title, final String location,
               final String type, final String description, final String howToApply,
               final String company, final String companyUrl, final String companyLogo,
               final String url) {
        this.id = id;

        this.createdAt = createdAt;
        this.title = title;
        this.location = location;
        this.type = type;
        this.description = description;
        this.howToApply = howToApply;
        this.company = company;
        this.companyUrl = companyUrl;
        this.companyLogo = companyLogo;
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (this == o) return true;
        if (!(o instanceof Job)) return false;
        final Job other = (Job) o;
        return this.id.equals(other.id);
    }
}
