package com.example.dooit.ui.doitviewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PermissionsViewModel: ViewModel() {
    private val _deniedPermissions = MutableStateFlow<MutableList<String>>(mutableListOf())
    var deniedPermissions = _deniedPermissions.asStateFlow()
    fun addToDeniedPermissions(permission: String, isGranted: Boolean) {
        if(!isGranted && !_deniedPermissions.value.contains(permission)){
            _deniedPermissions.update {
                it.add(permission)
                it
            }
        }
    }
    fun removePermission() {
        if(_deniedPermissions.value.size >0){
            _deniedPermissions.update {
                it.removeFirst()
                it
            }
        }
    }
}