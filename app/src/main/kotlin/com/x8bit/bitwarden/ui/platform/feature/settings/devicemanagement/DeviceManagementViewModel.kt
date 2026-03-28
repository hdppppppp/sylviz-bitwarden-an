package com.x8bit.bitwarden.ui.platform.feature.settings.devicemanagement

import androidx.lifecycle.viewModelScope
import com.bitwarden.network.model.DeviceResponseJson
import com.bitwarden.network.service.DevicesService
import com.bitwarden.ui.platform.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceManagementViewModel @Inject constructor(
    private val devicesService: DevicesService,
) : BaseViewModel<DeviceManagementState, DeviceManagementEvent, DeviceManagementAction>(
    initialState = DeviceManagementState(isLoading = true, devices = emptyList(), error = null, confirmDeleteId = null),
) {
    init {
        loadDevices()
    }

    override fun handleAction(action: DeviceManagementAction) {
        when (action) {
            DeviceManagementAction.Refresh -> loadDevices()
            is DeviceManagementAction.ConfirmDelete -> handleConfirmDelete(action.id)
            is DeviceManagementAction.RequestDelete -> mutableStateFlow.value =
                mutableStateFlow.value.copy(confirmDeleteId = action.id)
            DeviceManagementAction.DismissDeleteConfirm -> mutableStateFlow.value =
                mutableStateFlow.value.copy(confirmDeleteId = null)
        }
    }

    private fun loadDevices() {
        mutableStateFlow.value = mutableStateFlow.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            devicesService.getDevices()
                .onSuccess { devices ->
                    mutableStateFlow.value = mutableStateFlow.value.copy(
                        isLoading = false,
                        devices = devices.sortedByDescending { it.creationDate },
                    )
                }
                .onFailure {
                    mutableStateFlow.value = mutableStateFlow.value.copy(
                        isLoading = false,
                        error = "加载设备列表失败，请重试。",
                    )
                }
        }
    }

    private fun handleConfirmDelete(id: String) {
        mutableStateFlow.value = mutableStateFlow.value.copy(confirmDeleteId = null, isLoading = true)
        viewModelScope.launch {
            devicesService.deleteDevice(id)
                .onSuccess { loadDevices() }
                .onFailure {
                    mutableStateFlow.value = mutableStateFlow.value.copy(
                        isLoading = false,
                        error = "移除设备失败，请重试。",
                    )
                }
        }
    }
}

data class DeviceManagementState(
    val isLoading: Boolean,
    val devices: List<DeviceResponseJson>,
    val error: String?,
    val confirmDeleteId: String?,
)

sealed class DeviceManagementEvent

sealed class DeviceManagementAction {
    data object Refresh : DeviceManagementAction()
    data class RequestDelete(val id: String) : DeviceManagementAction()
    data class ConfirmDelete(val id: String) : DeviceManagementAction()
    data object DismissDeleteConfirm : DeviceManagementAction()
}
