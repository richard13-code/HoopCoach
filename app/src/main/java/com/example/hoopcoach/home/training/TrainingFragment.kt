package com.example.hoopcoach.home.training

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hoopcoach.R
import com.example.hoopcoach.core.FragmentCommunicator
import com.example.hoopcoach.core.ResponseService
import com.example.hoopcoach.databinding.FragmentTrainingBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class TrainingFragment : Fragment() {

    private var _binding: FragmentTrainingBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<TrainingViewModel>()
    private lateinit var communicator: FragmentCommunicator

    private val adapter = DrillsAdapter{ drill ->

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTrainingBinding.inflate(inflater, container, false)
        communicator = requireActivity() as FragmentCommunicator
        binding.rvDrills.layoutManager = LinearLayoutManager(requireContext())
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
                            adapter.submitList(state.data)
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


