package com.example.hoopcoach.core

interface FragmentCommunicator {
    fun manageLoader(isVisible: Boolean)
    fun manageBottomNavigation(isVisible: Boolean)
}