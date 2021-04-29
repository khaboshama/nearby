package com.khaled.nearbyapp.model.network.response

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.khaled.nearbyapp.model.Photo

@JsonDeserialize(using = VenuePhotosResponseDeserializer::class)
data class VenuePhotosResponse(val photo: Photo)
