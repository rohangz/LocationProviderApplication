package com.rohangz.location.platform.adapters

import android.annotation.SuppressLint
import android.app.Application
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.rohangz.location.R
import com.rohangz.location.databinding.ItemLocationBinding
import com.rohangz.location.platform.model.LocationItemUIModel
import com.rohangz.location.platform.viewholders.BaseViewHolder
import com.rohangz.location.repository.models.LocationRepoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class LocationListAdapter @Inject constructor(
    list: ArrayList<Any>,
    val application: Application,
) : BaseAdapter(list) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding =
            ItemLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemLocationViewHolder(binding)
    }

    inner class ItemLocationViewHolder(
        private val _binding: ItemLocationBinding
    ) : BaseViewHolder(_binding.root) {
        override fun bindData(position: Int, data: Any) {
            when (data) {
                is LocationRepoModel -> {
                    val dateFormat = application.getString(R.string.app_date_format)
                    val timeFormat = application.getString(R.string.app_time_format)

                    val date = Date(data.timeStamp)
                    val formattedDate =
                        SimpleDateFormat(dateFormat, Locale.US).format(date).toString()
                    val formattedTime =
                        SimpleDateFormat(timeFormat, Locale.US).format(date).toString()

                    _binding.modelData = LocationItemUIModel(
                        latitude = application.getString(
                            R.string.app_latitude,
                            data.latitude.toString()
                        ),
                        longitude = application.getString(
                            R.string.app_longitude,
                            data.longitude.toString()
                        ),
                        time = application.getString(
                            R.string.app_date_time,
                            formattedDate,
                            formattedTime
                        )

                    )
                }
            }
        }
    }

    fun updateList(newList: List<Any>) {
        itemList.clear()
        itemList.addAll(newList)
        notifyDataSetChanged()
    }
}