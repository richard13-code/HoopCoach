package com.example.hoopcoach.home.training

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

class TrainingViewModel (
    private val service: DrillService = DrillRepository()
): ViewModel() {


    private val _drillState = MutableStateFlow<ResponseService<List<Drill>>?>(null)
    val drillState: StateFlow<ResponseService<List<Drill>>?> = _drillState.asStateFlow()

    fun loadDrills(limit: Int = 6) {
        viewModelScope.launch {
            _drillState.value = ResponseService.Loading
            val result = service.getDrills()
            _drillState.value = result
        }
    }
}
