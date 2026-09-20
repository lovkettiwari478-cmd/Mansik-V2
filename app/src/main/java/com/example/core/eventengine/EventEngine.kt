package com.example.core.eventengine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

enum class SystemEventType {
    USER_LOGIN,
    USER_LOGOUT,
    TASK_CREATED,
    TASK_COMPLETED,
    TASK_OVERDUE,
    CALENDAR_EVENT_CREATED,
    CALENDAR_EVENT_CHANGED,
    CALENDAR_EVENT_APPROACHING,
    AUTHORIZED_USER_DETECTED,
    DEVICE_STATE_CHANGED,
    AUTOMATION_TRIGGERED,
    AUTOMATION_FAILED,
    PERMISSION_CHANGED,
    SECURITY_ALERT
}

data class SystemEvent(
    val type: SystemEventType,
    val source: String,
    val payload: Map<String, Any> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis()
)

class EventEngine(
    private val scope: CoroutineScope
) {
    private val _events = MutableSharedFlow<SystemEvent>(extraBufferCapacity = 64)
    val events: SharedFlow<SystemEvent> = _events.asSharedFlow()

    fun publish(event: SystemEvent) {
        scope.launch {
            _events.emit(event)
        }
    }
}
