package com.kim344.retrofitretry.kotlin

import com.google.gson.annotations.SerializedName

class Model {

    //your Model Setting

    @SerializedName("has_more")
    var has_more: Boolean = false

    @SerializedName("quota_max")
    var quota_max: Int = 0

    @SerializedName("quota_remaining")
    var quota_remaining: Int = 0

}