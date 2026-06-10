package com.example.hoopcoach.home.training

import androidx.lifecycle.ViewModel
import com.example.hoopcoach.core.model.Drill
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DrillShareViewModel: ViewModel() {

    private val _selectedDrill = MutableStateFlow<Drill?>(null)
    val selectedDrill: StateFlow<Drill?> = _selectedDrill.asStateFlow()

    fun selectDrill(drill: Drill) {
        _selectedDrill.value = drill
    }

}