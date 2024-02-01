package api.luisangeldd.mediapicker.core

internal enum class StatusRequest {
    IDLE,
    EMPTY,
    NOT_EMPTY
}
internal enum class StateRequest {
    IDLE,
    START,
    END
}
internal enum class StatePicker{
    CLOSE,
    OPEN,
    DRAG,
    ADD
}