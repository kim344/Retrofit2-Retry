package com.kim344.retrofitretry.java;

import com.google.gson.annotations.SerializedName;

public class Model {

    //your Model Setting

    @SerializedName("has_more")
    boolean has_more;

    @SerializedName("quota_max")
    int quota_max;

    @SerializedName("quota_remaining")
    int quota_remaining;

    public boolean isHas_more() {
        return has_more;
    }

    public void setHas_more(boolean has_more) {
        this.has_more = has_more;
    }

    public int getQuota_max() {
        return quota_max;
    }

    public void setQuota_max(int quota_max) {
        this.quota_max = quota_max;
    }

    public int getQuota_remaining() {
        return quota_remaining;
    }

    public void setQuota_remaining(int quota_remaining) {
        this.quota_remaining = quota_remaining;
    }

}
