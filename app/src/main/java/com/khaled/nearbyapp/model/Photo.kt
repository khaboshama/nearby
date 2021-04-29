package com.khaled.nearbyapp.model

class Photo(var prefix: String? = null, var suffix: String? = null) {

    fun getUrl() = prefix + "200x200" + suffix
}