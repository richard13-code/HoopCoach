package com.example.hoopcoach.home.drills

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
import com.example.hoopcoach.R
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hoopcoach.core.FragmentCommunicator
import com.example.hoopcoach.core.ResponseService
import com.example.hoopcoach.core.model.Drill
import com.example.hoopcoach.databinding.FragmentDrillsBinding
import com.example.hoopcoach.home.training.DrillShareViewModel
import com.example.hoopcoach.home.training.DrillsAdapter
import com.example.hoopcoach.home.training.TrainingViewModel
import com.google.android.material.search.SearchView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class DrillsFragment : Fragment() {

    private var _binding: FragmentDrillsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TrainingViewModel>()
    private val sharedViewModel by activityViewModels<DrillShareViewModel>()
    private lateinit var communicator: FragmentCommunicator

    private var allDrills: List<Drill> = emptyList() // Guardamos la lista completa aquí

    private val mainAdapter = DrillsAdapter(isGrid = false) { drill ->
        sharedViewModel.selectDrill(drill)
        findNavController().navigate(R.id.action_drillsFragment_to_drillDetailFragment)
    }

    private val searchAdapter = DrillsAdapter(isGrid = true) { drill ->
        sharedViewModel.selectDrill(drill)
        findNavController().navigate(R.id.action_drillsFragment_to_drillDetailFragment)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDrillsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        communicator = requireActivity() as FragmentCommunicator

        setupTabs()
        setupRecyclerViews()
        setupSearchView()
        observeState()

        viewModel.loadDrills()
    }

    private fun setupTabs() {
        val tabs = binding.tabCategorias
        tabs.addTab(tabs.newTab().setText("All"))
        tabs.addTab(tabs.newTab().setText("Shooting"))
        tabs.addTab(tabs.newTab().setText("Dribbling"))
        tabs.addTab(tabs.newTab().setText("Workout"))

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                applyFilters()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupRecyclerViews() {
        binding.rvDrills.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDrills.adapter = mainAdapter

        binding.rvSearchResults.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvSearchResults.adapter = searchAdapter
    }

    private fun setupSearchView() {
        binding.searchView.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.SHOWING) communicator.manageBottomNavigation(false)
            else if (newState == SearchView.TransitionState.HIDING) communicator.manageBottomNavigation(true)
        }

        binding.searchView.editText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                applyFilters(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun applyFilters(query: String = "") {
        val selectedTab = binding.tabCategorias.selectedTabPosition
        val categoryFilter = when (selectedTab) {
            1 -> "Tiro"
            2 -> "Dribbling"
            3 -> "General"
            else -> null // "Todos"
        }

        // Filtrar por categoría
        var filteredList = if (categoryFilter == null) {
            allDrills
        } else {
            allDrills.filter { it.category.equals(categoryFilter, ignoreCase = true) }
        }

        // Filtrar por búsqueda si hay texto
        if (query.isNotEmpty()) {
            filteredList = filteredList.filter { it.title.contains(query, ignoreCase = true) }
            searchAdapter.submitList(filteredList.shuffled())
        } else {
            // Si no estamos buscando, actualizamos la lista principal revuelta
            mainAdapter.submitList(filteredList.shuffled())
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.drillState.collect { state ->
                    when (state) {
                        is ResponseService.Loading -> communicator.manageLoader(true)
                        is ResponseService.Success -> {
                            communicator.manageLoader(false)
                            allDrills = state.data
                            applyFilters() // Aplicar filtro inicial (Todos + Shuffled)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}