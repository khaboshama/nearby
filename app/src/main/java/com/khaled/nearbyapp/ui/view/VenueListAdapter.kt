package com.khaled.nearbyapp.ui.view

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.khaled.nearbyapp.R
import com.khaled.nearbyapp.constant.Constants
import com.khaled.nearbyapp.databinding.ListItemVenueBinding
import com.khaled.nearbyapp.model.Venue

class VenueListAdapter : RecyclerView.Adapter<VenueListAdapter.VenueViewHolder>() {

    private var venueList: List<Venue> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VenueViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_venue, parent, false)
        )

    override fun onBindViewHolder(holder: VenueViewHolder, position: Int) {
        val venue = venueList[position]
        holder.bindViewHolder(venue)
    }

    override fun getItemCount() = venueList.size

    fun setVenueList(venueList: List<Venue>) {
        this.venueList = venueList
        notifyDataSetChanged()
    }

    class VenueViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding: ListItemVenueBinding? = DataBindingUtil.bind(itemView)

        fun bindViewHolder(venue: Venue) {
            binding?.nameTextView?.text = venue.name
            binding?.addressTextView?.text = venue.address
            venue.photo?.let { photo ->
                Glide.with(itemView.context)
                    .load(photo.getUrl()).override(Target.SIZE_ORIGINAL)
                    .apply(RequestOptions.timeoutOf(Constants.TIMEOUT))
                    .listener(object : RequestListener<Drawable?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding?.imageProgressBarLoading?.visibility = View.GONE
                            binding?.imageError?.visibility = View.VISIBLE
                            binding?.icVenue?.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding?.imageProgressBarLoading?.visibility = View.GONE
                            binding?.imageError?.visibility = View.GONE
                            binding?.icVenue?.visibility = View.VISIBLE
                            return false
                        }
                    })
                    .into(binding?.icVenue!!)
            }
        }
    }
}