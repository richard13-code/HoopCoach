package com.example.hoopcoach.home.training

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.hoopcoach.R
import com.example.hoopcoach.core.FragmentCommunicator
import com.example.hoopcoach.core.ResponseService
import com.example.hoopcoach.databinding.FragmentTrainingBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import kotlin.getValue

class TrainingFragment : Fragment() {

    private var _binding: FragmentTrainingBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel by activityViewModels<DrillShareViewModel>()
    private val viewModel by viewModels<TrainingViewModel>()
    private lateinit var communicator: FragmentCommunicator

    private val adapter = DrillsAdapter(isGrid = true) { drill ->

        sharedViewModel.selectDrill(drill)
        findNavController().navigate(R.id.action_homeFragment_to_drillDetailFragment)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTrainingBinding.inflate(inflater, container, false)
        communicator = requireActivity() as FragmentCommunicator
        binding.rvDrills.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvDrills.adapter = adapter
        observeState()
        viewModel.loadDrills()
        return binding.root
    }

    fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.drillState.collect { state ->
                    when (state) {
                        is ResponseService.Loading -> {
                            communicator.manageLoader(true)
                        }
                        is ResponseService.Success -> {
                            communicator.manageLoader(false)
                            // Revolvemos la lista con .shuffled() y tomamos los primeros 8
                            adapter.submitList(state.data.shuffled().take(8))
                        }
                        is ResponseService.Error -> {
                            communicator.manageLoader(false)
                            Snackbar.make(binding.root, state.error, Snackbar.LENGTH_LONG).show()
                        }
                        null -> {}
                    }
                }
            }
        }
    }
}
