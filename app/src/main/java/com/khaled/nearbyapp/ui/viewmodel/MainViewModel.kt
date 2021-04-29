package com.khaled.nearbyapp.ui.viewmodel

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khaled.nearbyapp.model.Venue
import com.khaled.nearbyapp.model.network.RetrofitClient
import com.khaled.nearbyapp.model.network.response.VenueListResponse
import com.khaled.nearbyapp.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel : ViewModel() {

    var venueList = MutableLiveData<MutableList<Venue>>()
        private set
    var showMessage = SingleLiveEvent<Void>()
        private set
    private var isSendRequestFinished = true

    fun onLocationChanged(location: Location) {
        if (isSendRequestFinished) {
            isSendRequestFinished = false
            viewModelScope.launch {
                try {
                    val offset = venueList.value?.size ?: 0
                    val latLong = "${location.latitude}, ${location.longitude}"
                    val venueListResponse =
                        RetrofitClient.moneyServiceApi.getVenueList(limit = LIMIT, offset = offset, latLong = latLong)
                    if (venueListResponse.isSuccessful) {
                        parseVenueListSuccessResponse(venueListResponse, offset)
                    } else {
                        parseVenueListErrorResponse()
                    }
                } catch (e: Exception) {
                    parseVenueListErrorResponse()
                }
                isSendRequestFinished = true
            }
        }
    }

    private fun parseVenueListErrorResponse() {
        showMessage.call()
    }

    private fun parseVenueListSuccessResponse(venueListResponse: Response<VenueListResponse>, offset: Int) {
        if (offset == 0) {
            venueList.value = venueListResponse.body()?.venueList!!.toMutableList()
        } else {
            venueList.value?.addAll(venueListResponse.body()?.venueList!!.toMutableList())
        }
    }

    companion object {
        private const val LIMIT = 10
    }
}