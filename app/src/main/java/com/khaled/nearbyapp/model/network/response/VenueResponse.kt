package com.khaled.nearbyapp.model.network.response

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.khaled.nearbyapp.model.Venue

@JsonDeserialize(using = VenueListResponseDeserializer::class)
data class VenueListResponse(val venueList: List<Venue>)
