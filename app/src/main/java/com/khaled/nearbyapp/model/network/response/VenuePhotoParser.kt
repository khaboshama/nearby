package com.khaled.nearbyapp.model.network.response

import com.fasterxml.jackson.databind.JsonNode
import com.khaled.nearbyapp.model.Photo

interface VenuePhotoParser {

    fun getVenuePhoto(jsonNode: JsonNode) = Photo().apply {
        prefix = jsonNode.get("prefix").asText()
        suffix = jsonNode.get("suffix").asText()
    }
}