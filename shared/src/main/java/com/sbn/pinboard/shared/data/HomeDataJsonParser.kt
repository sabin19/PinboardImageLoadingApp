package com.sbn.pinboard.shared.data

import com.google.gson.GsonBuilder
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.sbn.model.User
import com.sbn.pinboard.shared.data.home.json.ProfileImageDeserializer


object HomeDataJsonParser {
    @Throws(JsonIOException::class, JsonSyntaxException::class, IllegalStateException::class)
    fun parseHomeData(json: String): List<User> {
        val gson = GsonBuilder()
            .registerTypeAdapter(User.User.ProfileImage::class.java, ProfileImageDeserializer())
            .create()
        val listType = object : TypeToken<List<User>>() {}.type
        return gson.fromJson(json, listType)
    }
}
