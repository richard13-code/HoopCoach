package com.example.hoopcoach.home.drillDetail

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.hoopcoach.core.FragmentCommunicator
import com.example.hoopcoach.core.model.Drill
import com.example.hoopcoach.databinding.FragmentDrillDetailBinding
import com.example.hoopcoach.home.training.DrillShareViewModel

class DrillDetailFragment : Fragment() {
    private var _binding: FragmentDrillDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<DrillDetailViewModel>()
    private val sharedViewModel by activityViewModels<DrillShareViewModel>()
    private lateinit var drill: Drill

    private var isFullScreen = false
    private var videoW = 0
    private var videoH = 0

    // Callback para manejar el botón físico de atrás
    private val backPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            if (isFullScreen) {
                toggleFullScreen()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDrillDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        drill = sharedViewModel.selectedDrill.value ?: return
        
        // Registrar el callback del botón atrás
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressedCallback)
        
        bindDrillInfo()
        setupListeners()
    }

    private fun bindDrillInfo() {
        binding.tvTitle.text = drill.title
        binding.tvDifficulty.text = drill.difficulty
        binding.tvDescription.text = drill.description
        binding.tvDuration.text = "${drill.durationMinutes} min"

        // Miniatura del propio video
        Glide.with(this)
            .asBitmap()
            .load(drill.mediaUrl)
            .frame(1000000)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.ivThumbnail)

        binding.tvVideo.setVideoURI(Uri.parse(drill.mediaUrl))
        binding.tvVideo.setOnPreparedListener { mp ->
            mp.isLooping = true
            videoW = mp.videoWidth
            videoH = mp.videoHeight
            adjustVideoSize()
        }
    }

    private fun adjustVideoSize() {
        if (videoW == 0 || videoH == 0) return

        binding.tvVideo.post {
            val containerWidth = binding.videoContainer.width.toFloat()
            val containerHeight = binding.videoContainer.height.toFloat()
            if (containerWidth == 0f || containerHeight == 0f) return@post

            val videoRatio = videoW.toFloat() / videoH.toFloat()
            val containerRatio = containerWidth / containerHeight

            val lp = binding.tvVideo.layoutParams

            if (isFullScreen) {
                // MODO PANTALLA COMPLETA: "Center Crop" para que no haya bordes
                if (videoRatio > containerRatio) {
                    lp.height = containerHeight.toInt()
                    lp.width = (containerHeight * videoRatio).toInt()
                } else {
                    lp.width = containerWidth.toInt()
                    lp.height = (containerWidth / videoRatio).toInt()
                }
            } else {
                // MODO NORMAL: "Fit Center" para ver el video completo
                if (videoRatio > containerRatio) {
                    lp.width = containerWidth.toInt()
                    lp.height = (containerWidth / videoRatio).toInt()
                } else {
                    lp.height = containerHeight.toInt()
                    lp.width = (containerHeight * videoRatio).toInt()
                }
            }
            binding.tvVideo.layoutParams = lp
        }
    }

    private fun setupListeners() {
        binding.btnClose.setOnClickListener {
            if (isFullScreen) toggleFullScreen() else findNavController().navigateUp()
        }

        val playAction = View.OnClickListener {
            if (binding.tvVideo.isPlaying) {
                binding.tvVideo.pause()
                binding.ivPlay.visibility = View.VISIBLE
            } else {
                binding.ivThumbnail.visibility = View.GONE
                binding.tvVideo.start()
                binding.ivPlay.visibility = View.GONE
            }
        }

        binding.videoContainer.setOnClickListener(playAction)
        binding.btnFullscreen.setOnClickListener { toggleFullScreen() }
    }

    private fun toggleFullScreen() {
        isFullScreen = !isFullScreen
        
        // Habilitar el callback de atrás solo cuando estemos en pantalla completa
        backPressedCallback.isEnabled = isFullScreen
        
        // Ocultar/Mostrar barra de navegación inferior de la Activity
        (activity as? FragmentCommunicator)?.manageBottomNavigation(!isFullScreen)

        val params = binding.videoContainer.layoutParams as ConstraintLayout.LayoutParams

        if (isFullScreen) {
            binding.detailScrollView.visibility = View.GONE
            binding.btnClose.visibility = View.GONE

            // Ocupar TODA la pantalla
            params.width = ConstraintLayout.LayoutParams.MATCH_PARENT
            params.height = ConstraintLayout.LayoutParams.MATCH_PARENT
            params.topMargin = 0
            params.setMargins(0, 0, 0, 0)
            params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID

            binding.btnFullscreen.setImageResource(android.R.drawable.ic_menu_revert)
        } else {
            binding.detailScrollView.visibility = View.VISIBLE
            binding.btnClose.visibility = View.VISIBLE

            // Volver al tamaño original
            params.width = 0 // match_constraint
            params.height = (250 * resources.displayMetrics.density).toInt()
            params.topMargin = (80 * resources.displayMetrics.density).toInt()
            val margin = (24 * resources.displayMetrics.density).toInt()
            params.setMargins(margin, 0, margin, 0)
            params.bottomToBottom = ConstraintLayout.LayoutParams.UNSET

            binding.btnFullscreen.setImageResource(android.R.drawable.ic_menu_zoom)
        }

        binding.videoContainer.layoutParams = params
        // Re-ajustar el video tras el cambio de contenedor
        binding.videoContainer.post { adjustVideoSize() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
