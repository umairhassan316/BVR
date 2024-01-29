package com.dt.auto.background.video.recorder.app_ui.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dt.auto.background.video.recorder.R
import com.dt.auto.background.video.recorder.databinding.VideoQualityItemBinding
import com.dt.auto.background.video.recorder.di.settings
import com.dt.auto.background.video.recorder.helpers.VersionCheckHelper.isMarshmallowPlus
import com.dt.auto.background.video.recorder.app_ui.activity.quality_selection.CameraCapabilityDataClass
import com.dt.auto.background.video.recorder.app_ui.activity.quality_selection.DEFAULT_QUALITY_IDX
import com.dt.auto.background.video.recorder.app_ui.activity.quality_selection.VideoQualitySelectionActivity
import com.dt.auto.background.video.recorder.helpers.utils.invisible
import com.dt.auto.background.video.recorder.helpers.utils.log
import com.dt.auto.background.video.recorder.helpers.utils.setOnClickListenerCoolDown
import com.dt.auto.background.video.recorder.helpers.utils.visible


class VideoQualitySelectionListAdapter(private val categoriesAfterChipsAdapterOnClickInterface: OnItemClickListener? = null) :
    ListAdapter<CameraCapabilityDataClass, VideoQualitySelectionListAdapter.HorizontalViewHolder>(
        ListCheckDiffCallback()
    ) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalViewHolder {
        return HorizontalViewHolder.from(parent, categoriesAfterChipsAdapterOnClickInterface)
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
        private val binding: VideoQualityItemBinding,
        private var OnItemClickListener: OnItemClickListener?
    ) : RecyclerView.ViewHolder(view) {

        fun bind(item: CameraCapabilityDataClass?, view: View) {
            item?.let { recordingItems ->

                view.apply {

                    when {
                        recordingItems.quality == context.getString(R.string.fourk_res) && !recordingItems.isProAvailable -> {
                            binding.proIv.visible()
                        }
                        recordingItems.quality == context.getString(R.string.twok_res) && !recordingItems.isProAvailable -> {
                            binding.proIv.visible()
                        }
                        recordingItems.quality == context.getString(R.string.full_hd_res) && !recordingItems.isProAvailable -> {
                            binding.proIv.visible()
                        }
                        recordingItems.quality == context.getString(R.string.hd_res) && !recordingItems.isProAvailable -> {
                            binding.proIv.visible()
                        }
                        recordingItems.quality == context.getString(R.string.sd_res) -> {
                            binding.proIv.invisible()


                            VideoQualitySelectionActivity().settings.videoQualitySelected =
                                absoluteAdapterPosition
                            DEFAULT_QUALITY_IDX =
                                VideoQualitySelectionActivity().settings.videoQualitySelected
                            binding.qualityTextView.setTextColor(context.resources.getColor(R.color.white))
                            log("selected quality: $DEFAULT_QUALITY_IDX")
                            if (isMarshmallowPlus()) {
                                binding.container.setBackgroundColor(
                                    context.resources.getColor(
                                        R.color.light_blue_100,
                                        null
                                    )
                                )
                                binding.qualityTextView.setTextColor(
                                    context.resources.getColor(
                                        R.color.white,
                                        null
                                    )
                                )

                            } else {
                                binding.container.setBackgroundColor(context.resources.getColor(R.color.light_blue_100))
                                binding.qualityTextView.setTextColor(context.resources.getColor(R.color.white))

                            }

                        }
                        else -> {
                            binding.proIv.invisible()
                            VideoQualitySelectionActivity().settings.videoQualitySelected =
                                absoluteAdapterPosition
                            DEFAULT_QUALITY_IDX =
                                VideoQualitySelectionActivity().settings.videoQualitySelected
                            log("selected quality: $DEFAULT_QUALITY_IDX")
                            if (isMarshmallowPlus()) {
                                binding.container.setBackgroundColor(
                                    context.resources.getColor(
                                        R.color.teal_200,
                                        null
                                    )
                                )
                                binding.qualityTextView.setTextColor(
                                    context.resources.getColor(
                                        R.color.white,
                                        null
                                    )
                                )

                            } else {
                                binding.container.setBackgroundColor(
                                    context.resources.getColor(
                                        R.color.teal_200
                                    )
                                )
                                binding.qualityTextView.setTextColor(
                                    context.resources.getColor(
                                        R.color.white
                                    )
                                )

                            }
                        }
                    }
                }
                binding.qualityTextView.text = recordingItems.quality
                binding.container.setOnClickListenerCoolDown {
                    if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                        OnItemClickListener?.setOnItemPlayClickListener(
                            absoluteAdapterPosition,
                            recordingItems
                        )
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
                val binding = VideoQualityItemBinding.inflate(layoutInflater, parent, false)
                return HorizontalViewHolder(binding.root, binding, OnItemClickListener)
            }
        }
    }


    class ListCheckDiffCallback : DiffUtil.ItemCallback<CameraCapabilityDataClass>() {
        override fun areItemsTheSame(
            oldItem: CameraCapabilityDataClass,
            newItem: CameraCapabilityDataClass
        ): Boolean {
            return oldItem.quality == newItem.quality
        }

        override fun areContentsTheSame(
            oldItem: CameraCapabilityDataClass,
            newItem: CameraCapabilityDataClass
        ): Boolean {
            return oldItem == newItem
        }
    }

    interface OnItemClickListener {
        fun setOnItemPlayClickListener(position: Int, item: CameraCapabilityDataClass)

    }


}