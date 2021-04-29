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

    private lateinit var currentLocation: Location
    var venueListLiveData = MutableLiveData<MutableList<Venue>>()
        private set
    var notifyVenueList = SingleLiveEvent<Int>()
        private set
    var venueListProgressBarEndlessLoading = SingleLiveEvent<Boolean>()
        private set
    var showMessage = SingleLiveEvent<Void>()
        private set
    private var isSendRequestFinished = true

    fun onLocationChanged(location: Location) {
        currentLocation = location
        if (isSendRequestFinished) {
            sendVenueListRequest()
        }
    }

    private fun sendVenueListRequest() {
        viewModelScope.launch {
            isSendRequestFinished = false
            try {
                val offset = venueListLiveData.value?.size ?: 0
                if (offset != 0) venueListProgressBarEndlessLoading.value = true
                val latLong = "${currentLocation.latitude}, ${currentLocation.longitude}"
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
            venueListProgressBarEndlessLoading.value = false
            isSendRequestFinished = true
        }
    }

    private fun parseVenueListErrorResponse() {
        showMessage.call()
    }

    private fun parseVenueListSuccessResponse(venueListResponse: Response<VenueListResponse>, offset: Int) {
        if (offset == 0) {
            venueListLiveData.value = venueListResponse.body()?.venueList!!.toMutableList()
        } else {
            venueListLiveData.value?.addAll(venueListResponse.body()?.venueList!!.toMutableList())
            venueListLiveData.value = venueListLiveData.value
        }
        sendVenuePhotoDetailsRequest(venueListResponse.body()?.venueList)
    }

    private fun sendVenuePhotoDetailsRequest(venueList: List<Venue>?) {
        viewModelScope.launch {
            try {
                venueList?.forEach { venue ->
                    val venuePhotosResponse = RetrofitClient.moneyServiceApi.getVenuePhotos(venueId = venue.id)
                    if (venuePhotosResponse.isSuccessful) {
                        venue.photo = venuePhotosResponse.body()?.photo
                        notifyVenueList.value = venueListLiveData.value?.indexOf(venue)
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    fun loadNextPage() {
        if (isSendRequestFinished.not()) return
        sendVenueListRequest()
    }

    companion object {
        private const val LIMIT = 10
    }
}