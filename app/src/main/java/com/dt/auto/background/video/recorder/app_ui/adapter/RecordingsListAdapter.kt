package com.dt.auto.background.video.recorder.app_ui.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dt.auto.background.video.recorder.R
import com.dt.auto.background.video.recorder.databinding.RecordingsListRowBinding
import com.dt.auto.background.video.recorder.domain.RecordingsDto
import com.dt.auto.background.video.recorder.app_ui.activity.video_recording.RecordedVideoScreen.Companion.RECORDINGS_LIST
import com.dt.auto.background.video.recorder.helpers.utils.gone
import com.dt.auto.background.video.recorder.helpers.utils.setOnClickListenerCoolDown
import com.dt.auto.background.video.recorder.helpers.utils.visible
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.BaseRequestOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target


import java.util.*


class RecordingsListAdapter(private val categoriesAfterChipsAdapterOnClickInterface: OnItemClickListener? = null) :
    ListAdapter<RecordingsDto, RecordingsListAdapter.HorizontalViewHolder>(
        ListCheckDiffCallback()
    ),
    Filterable {

    var mFilteredList: List<RecordingsDto>? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalViewHolder {
        return HorizontalViewHolder.from(
            parent,
            categoriesAfterChipsAdapterOnClickInterface
        )
    }

    override fun onBindViewHolder(holder: HorizontalViewHolder, position: Int) {
        when (holder) {
            is HorizontalViewHolder -> {
                holder.bind(getItem(position), holder.itemView)
            }
        }
    }


    class HorizontalViewHolder private constructor(
        view: View,
        private val binding: RecordingsListRowBinding,
        private var OnItemClickListener: OnItemClickListener?
    ) : RecyclerView.ViewHolder(view) {

        fun bind(item: RecordingsDto?, view: View) {
            item?.let { recordingItems ->

                view.apply {

                    val with: RequestManager = Glide.with(context)
                    with.load("file:///" + recordingItems.filepath)
                        .listener(object : RequestListener<Drawable?> {
                            override fun onLoadFailed(
                                glideException: GlideException?,
                                obj: Any?,
                                target: Target<Drawable?>,
                                z: Boolean
                            ): Boolean {
                                binding.ivPlay.gone()
                                return false
                            }

                            override fun onResourceReady(
                                drawable: Drawable?,
                                obj: Any?,
                                target: Target<Drawable?>,
                                dataSource: DataSource?,
                                z: Boolean
                            ): Boolean {
                                binding.ivPlay.visible()
                                return false
                            }
                        }).apply(
                            RequestOptions().placeholder(R.drawable.ic_loader)
                                .error(R.drawable.ic_error) as BaseRequestOptions<*>
                        ).transform(CenterCrop(), RoundedCorners(20))
                        .into(binding.ivVideoThumbnail)

                    binding.tvVideoName.text = recordingItems.filename
                    "Duration : ${recordingItems.duration}".also { binding.tvVideoDuration.text = it }
                    binding.tvVideoSize.text = recordingItems.size
                    binding.tvVideoDateTime.text = recordingItems.datetime

                    binding.container.setOnClickListenerCoolDown {
                        if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                            OnItemClickListener?.setOnItemPlayClickListener(
                                absoluteAdapterPosition,
                                item
                            )
                        }
                    }
                    binding.ivDelete.setOnClickListenerCoolDown {
                        if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                            OnItemClickListener?.setOnItemDeleteClickListener(
                                absoluteAdapterPosition,
                                item
                            )
                        }
                    }
                    binding.ivShare.setOnClickListenerCoolDown {
                        if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                            OnItemClickListener?.setOnItemShareClickListener(
                                absoluteAdapterPosition,
                                item
                            )
                        }
                    }
                }
            }

        }

        companion object {
            fun from(
                parent: ViewGroup,
                OnItemClickListener: OnItemClickListener?
            ): HorizontalViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecordingsListRowBinding.inflate(layoutInflater, parent, false)
                return HorizontalViewHolder(
                    binding.root,
                    binding,
                    OnItemClickListener
                )
            }
        }
    }


    class ListCheckDiffCallback : DiffUtil.ItemCallback<RecordingsDto>() {
        override fun areItemsTheSame(
            oldItem: RecordingsDto,
            newItem: RecordingsDto
        ): Boolean {
            return oldItem.filename == newItem.filename
        }

        override fun areContentsTheSame(
            oldItem: RecordingsDto,
            newItem: RecordingsDto
        ): Boolean {
            return oldItem == newItem
        }
    }

    interface OnItemClickListener {
        fun setOnItemPlayClickListener(position: Int, item: RecordingsDto)
        fun setOnItemDeleteClickListener(position: Int, item: RecordingsDto)
        fun setOnItemShareClickListener(position: Int, item: RecordingsDto)
    }


    /*
    * https://stackoverflow.com/questions/58319242/filtering-recyclerview-listadapter-with-searchview
    * */
    override fun getFilter(): Filter {

        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString().lowercase()
                if (charString.isEmpty()) {
                    mFilteredList = RECORDINGS_LIST.toList()
                } else {
                    val filteredList = mutableListOf<RecordingsDto>()
                    filteredList.clear()
                    RECORDINGS_LIST.toList().let {
                        for (baseDataItem in it) {
                            if (baseDataItem.filename.lowercase()
                                    .contains(charString) || baseDataItem.filename.lowercase()
                                    .contains(charString) || baseDataItem.filename.lowercase()
                                    .contains(charString)
                            ) {
                                filteredList.add(baseDataItem)
                            }

                        }
                        mFilteredList = filteredList

                    }
                }

                val filterResults = FilterResults()
                filterResults.values = mFilteredList
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence,
                results: FilterResults
            ) {

                try {
                    mFilteredList = results.values as ArrayList<RecordingsDto>
                    submitList(mFilteredList)
                } catch (e: Exception) {
                }
            }
        }
    }

}