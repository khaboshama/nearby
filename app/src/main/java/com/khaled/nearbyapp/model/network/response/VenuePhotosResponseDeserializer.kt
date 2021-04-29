package com.khaled.nearbyapp.model.network.response

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.khaled.nearbyapp.model.Photo

class VenuePhotosResponseDeserializer : JsonDeserializer<VenuePhotosResponse>(), VenuePhotoParser {

    override fun deserialize(jsonParser: JsonParser?, context: DeserializationContext?): VenuePhotosResponse {
        var photo = Photo()
        val objectCodec: ObjectCodec? = jsonParser?.codec
        val jsonNode: JsonNode? = objectCodec?.readTree(jsonParser)
        val jsonNodeResponse: JsonNode? = jsonNode?.get("response")
        jsonNodeResponse?.let {
            val jsonNodePhotos: JsonNode? = it.get("photos")
            jsonNodePhotos?.let {
                val jsonNodeItems: JsonNode? = jsonNodePhotos.get("items")
                jsonNodeItems?.forEach { jsonNodeItem -> photo = getVenuePhoto(jsonNodeItem) }
            }
        }
        return VenuePhotosResponse(photo)
    }
}
