package com.khaled.nearbyapp.model.network.response

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.khaled.nearbyapp.model.Venue

class VenueListResponseDeserializer : JsonDeserializer<VenueListResponse>(), VenueParser {

    override fun deserialize(jsonParser: JsonParser?, context: DeserializationContext?): VenueListResponse {
        val venueList: MutableList<Venue> = ArrayList()
        val objectCodec: ObjectCodec? = jsonParser?.codec
        val jsonNode: JsonNode? = objectCodec?.readTree(jsonParser)
        val jsonNodeResponse: JsonNode? = jsonNode?.get("response")
        jsonNodeResponse?.let {
            val jsonNodeGroups: JsonNode? = it.get("groups")
            jsonNodeGroups?.let {
                jsonNodeGroups.forEach { jsonGroup ->
                    val jsonNodeItems: JsonNode? = jsonGroup.get("items")
                    jsonNodeItems?.let { jsonNodeItem ->
                        jsonNodeItem.forEach { jsonItem ->
                            val jsonNodeVenue: JsonNode? = jsonItem.get("venue")
                            jsonNodeVenue?.let { jsonVenue ->
                                val venue = getVenue(jsonVenue)
                                if (venue.address.isNotEmpty()) venueList.add(venue)
                            }
                        }
                    }
                }
            }
        }
        return VenueListResponse(venueList)
    }
}
