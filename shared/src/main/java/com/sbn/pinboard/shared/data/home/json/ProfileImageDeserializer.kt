/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sbn.pinboard.shared.data.home.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.sbn.model.User
import java.lang.reflect.Type

/**
 * Deserializer for [User]s.
 */
class ProfileImageDeserializer : JsonDeserializer<User.User.ProfileImage> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): User.User.ProfileImage {
        val obj = json?.asJsonObject!!
        return User.User.ProfileImage(
            large = obj.get("large")?.asString,
            medium = obj.get("medium")?.asString,
            small = obj.get("small")?.asString
        )
    }
}
