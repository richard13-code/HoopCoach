package com.example.hoopcoach.home.drills

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hoopcoach.core.ResponseService
import com.example.hoopcoach.core.model.Drill
import com.example.hoopcoach.core.network.DrillService
import com.example.hoopcoach.core.repostories.DrillRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DrillViewModel (
    private val service: DrillService = DrillRepository()
): ViewModel() {
    private val _drillState = MutableStateFlow<ResponseService<List<Drill>>?>(null)
    val drillState: StateFlow<ResponseService<List<Drill>>?> = _drillState.asStateFlow()

    fun loadDrills() {
        viewModelScope.launch {
            _drillState.value = ResponseService.Loading
            val result = service.getDrills()
            _drillState.value = result
        }
    }
}