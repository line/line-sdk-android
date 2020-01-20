package com.linecorp.linesdk.openchat

import org.json.JSONException
import org.json.JSONObject

class OpenChatParameters(
    private val name: String,
    private val description: String,
    private val creatorDisplayName: String,
    private val category: OpenChatCategory = OpenChatCategory.Game,
    private val isSearchable: Boolean = true
) {
    init {
        require(name.isNotEmpty() && name.length <= 50) { "String size needs to be less or equal to 50" }
        require(description.length <= 200) { "String size needs to be less or equal to 200" }
        require(creatorDisplayName.isNotEmpty() && creatorDisplayName.length <= 50) { "String size needs to be less or equal to 50" }
    }

    fun toJsonString(): String = try {
        JSONObject().apply {
            put("name", name)
            put("description", description)
            put("creatorDisplayName", creatorDisplayName)
            put("category", category.id)
            put("allowSearch", isSearchable)
        }.toString()
    } catch (exception: JSONException) {
        "{}"
    }
}
