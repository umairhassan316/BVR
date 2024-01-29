package com.dt.auto.background.video.recorder.app_ui.activity.quality_selection

import android.content.Intent
import android.os.Bundle
import androidx.annotation.Keep
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.concurrent.futures.await
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import androidx.recyclerview.widget.LinearLayoutManager
import com.dt.auto.background.video.recorder.R
import com.dt.auto.background.video.recorder.base.BaseActivity
import com.dt.auto.background.video.recorder.databinding.ActivityVideoQualitySelectioBinding
import com.dt.auto.background.video.recorder.di.settings
import com.dt.auto.background.video.recorder.app_ui.in_apps.BillingPurchases.isPurchase
import com.dt.auto.background.video.recorder.app_ui.in_apps.InAppScreen
import com.dt.auto.background.video.recorder.app_ui.adapter.VideoQualitySelectionListAdapter
import com.dt.auto.background.video.recorder.helpers.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async


@Keep
data class CameraCapabilityDataClass(
    val quality: String,
    val selectedQuality: Int,
    val isProAvailable: Boolean = false
)

var DEFAULT_QUALITY_IDX = 0
var CAMERA_CHECK_FRONT_OR_BACK = 2

@AndroidEntryPoint
class VideoQualitySelectionActivity : BaseActivity(),
    VideoQualitySelectionListAdapter.OnItemClickListener {

    override fun setOnItemPlayClickListener(position: Int, item: CameraCapabilityDataClass) {
        when {
            item.quality == getString(R.string.fourk_res) && !item.isProAvailable -> {
                startActivity(Intent(this@VideoQualitySelectionActivity, InAppScreen::class.java))
            }
            item.quality == getString(R.string.twok_res) && !item.isProAvailable -> {
                startActivity(Intent(this@VideoQualitySelectionActivity, InAppScreen::class.java))
            }
            item.quality == getString(R.string.full_hd_res) && !item.isProAvailable -> {
                startActivity(Intent(this@VideoQualitySelectionActivity, InAppScreen::class.java))
            }
            item.quality == getString(R.string.hd_res) && !item.isProAvailable -> {
                startActivity(Intent(this@VideoQualitySelectionActivity, InAppScreen::class.java))
            }
            else -> {
                settings.videoQualitySelected = position
                DEFAULT_QUALITY_IDX = settings.videoQualitySelected
                super.onBackPressed()
            }
        }


    }

    companion object {
        var cameraIndex = 0
        private var enumerationDeferred: Deferred<Unit>? = null
    }

    private val binding by viewBinding(ActivityVideoQualitySelectioBinding::inflate)

    private val videoQualitySelectionListAdapter by lazy { VideoQualitySelectionListAdapter(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        logEvent("video_quality_selection_activity")


    }

    override fun onResume() {
        super.onResume()

        //showNativeAds()

        cameraIndex = settings.isBackCameraInt
        CAMERA_CHECK_FRONT_OR_BACK = cameraIndex

        setupRecyclerViewAdapter()
        loadVideoQualities()

        /*if(AppOpenManager.isShowingAd)
            hideNativeAd()
        else
            visibleNativeAd()*/
    }

    private fun loadVideoQualities() {
        val listOfQualitiesToEnumerate = mutableListOf<CameraCapabilityDataClass>()


        enumerationDeferred = lifecycleScope.async {
            whenCreated {
                val provider =
                    ProcessCameraProvider.getInstance(this@VideoQualitySelectionActivity).await()

                provider.unbindAll()
                for (camSelector in arrayOf(
                    CameraSelector.DEFAULT_BACK_CAMERA
                )) {
                    try {
                        // just get the camera.cameraInfo to query capabilities
                        // we are not binding anything here.
                        if (provider.hasCamera(camSelector)) {
                            val camera = provider.bindToLifecycle(
                                this@VideoQualitySelectionActivity,
                                camSelector
                            )
                            val supportedQualities =
                                QualitySelector.getSupportedQualities(camera.cameraInfo)

                            listOfQualitiesToEnumerate.clear()
                            for (quality in supportedQualities) {
                                when (quality) {
                                    Quality.HIGHEST -> {
                                        listOfQualitiesToEnumerate.add(
                                            CameraCapabilityDataClass(
                                                getString(R.string.fourk_res),
                                                0,
                                                isPurchase
                                            )
                                        )
                                    }
                                    Quality.UHD -> {
                                        //Add "Ultra High Definition (UHD) - 2160p" to the list
                                        listOfQualitiesToEnumerate.add(
                                            CameraCapabilityDataClass(
                                                getString(R.string.twok_res),
                                                1,
                                                isPurchase
                                            )
                                        )
                                    }
                                    Quality.FHD -> {
                                        //Add "Full High Definition (FHD) - 1080p" to the list
                                        listOfQualitiesToEnumerate.add(
                                            CameraCapabilityDataClass(
                                                getString(R.string.full_hd_res),
                                                2,
                                                isPurchase
                                            )
                                        )
                                    }
                                    Quality.HD -> {
                                        //Add "High Definition (HD) - 720p" to the list
                                        listOfQualitiesToEnumerate.add(
                                            CameraCapabilityDataClass(
                                                getString(R.string.hd_res),
                                                3,
                                                isPurchase
                                            )
                                        )
                                    }
                                    Quality.SD -> {
                                        //Add "Standard Definition (SD) - 480p" to the list

                                        listOfQualitiesToEnumerate.add(
                                            CameraCapabilityDataClass(
                                                getString(R.string.sd_res),
                                                4,
                                                isPurchase
                                            )
                                        )
                                    }
                                    Quality.LOWEST -> {
                                        //Add "Standard Definition (SD) - 360p" to the list
                                        listOfQualitiesToEnumerate.add(
                                            CameraCapabilityDataClass(
                                                getString(R.string.ld_res),
                                                5,
                                                isPurchase
                                            )
                                        )
                                    }
                                }

                            }
                        }
                        videoQualitySelectionListAdapter.submitList(listOfQualitiesToEnumerate)
                    } catch (exc: java.lang.Exception) {
                    }
                }
            }
        }


    }


    private fun setupRecyclerViewAdapter() {
        binding.qualitySelection.apply {
            adapter = videoQualitySelectionListAdapter
            layoutManager?.isItemPrefetchEnabled = true
            layoutManager = LinearLayoutManager(
                this@VideoQualitySelectionActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            setHasFixedSize(true)
            itemAnimator?.changeDuration = 0
        }

    }


}