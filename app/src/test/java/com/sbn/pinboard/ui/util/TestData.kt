package com.sbn.pinboard.ui.util

import com.sbn.model.User

val links = User.Category.Links(
    "photos", "self"
)

val category = User.Category(
    1,
    links,
    2,
    "title"
)

val newList = User.Links(
    "download", "html", "self"
)

val urls = User.Urls(
    "full",
    "raw",
    "regular",
    "small",
    "thumb"
)

val userLinks = User.User.Links("", "", "", "")
val profileImages = User.User.ProfileImage("", "", "")

val users = User.User(
    "id",
    userLinks,
    "",
    profileImages,
    ""
)




fun getUser():User{
    return User(
        listOf(category), "#ffffff", "11-02-2020", 10, "1212132323", false, 12,
        newList,
        urls, users, 1
    )
}