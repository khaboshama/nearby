package com.khaled.nearbyapp.model.network.response

import com.fasterxml.jackson.databind.JsonNode
import com.khaled.nearbyapp.model.Venue

interface VenueParser {

    fun getVenue(jsonNodeVenue: JsonNode): Venue {
        val id = jsonNodeVenue.get("id").asText()
        val name = jsonNodeVenue.get("name").asText()
        val location: JsonNode? = jsonNodeVenue.get("location")
        val address = if (location == null) "" else location.get("address").asText()
        return Venue(id, name, address)
    }
}