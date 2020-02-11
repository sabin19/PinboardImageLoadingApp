package com.sbn.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    @SerializedName("categories")
    val categories: List<Category>,
    @SerializedName("color")
    val color: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("height")
    val height: Int,
    @SerializedName("id")
    val id: String,
    @SerializedName("liked_by_user")
    val likedByUser: Boolean,
    @SerializedName("likes")
    val likes: Int,
    @SerializedName("links")
    val links: Links,
    @SerializedName("urls")
    val urls: Urls,
    @SerializedName("user")
    val user: User,
    @SerializedName("width")
    val width: Int
) :
    Parcelable {
    fun like() = likes.toString()
    @Parcelize
    data class Category(
        @SerializedName("id")
        val id: Int,
        @SerializedName("links")
        val links: Links,
        @SerializedName("photo_count")
        val photoCount: Int,
        @SerializedName("title")
        val title: String
    ) : Parcelable {
        @Parcelize
        data class Links(
            @SerializedName("photos")
            val photos: String,
            @SerializedName("self")
            val self: String
        ) : Parcelable
    }

    @Parcelize
    data class Links(
        @SerializedName("download")
        val download: String,
        @SerializedName("html")
        val html: String,
        @SerializedName("self")
        val self: String
    ) : Parcelable

    @Parcelize
    data class Urls(
        @SerializedName("full")
        val full: String,
        @SerializedName("raw")
        val raw: String,
        @SerializedName("regular")
        val regular: String,
        @SerializedName("small")
        val small: String,
        @SerializedName("thumb")
        val thumb: String
    ) : Parcelable

    @Parcelize
    data class User(
        @SerializedName("id")
        val id: String,
        @SerializedName("links")
        val links: Links,
        @SerializedName("name")
        val name: String,
        @SerializedName("profile_image")
        val profileImage: ProfileImage,
        @SerializedName("username")
        val username: String
    ) : Parcelable {
        @Parcelize
        data class Links(
            @SerializedName("html")
            val html: String,
            @SerializedName("likes")
            val likes: String,
            @SerializedName("photos")
            val photos: String,
            @SerializedName("self")
            val self: String
        ) : Parcelable

        @Parcelize
        data class ProfileImage(
            @SerializedName("large")
            val large: String?,
            @SerializedName("medium")
            val medium: String?,
            @SerializedName("small")
            val small: String?
        ) : Parcelable
    }
}