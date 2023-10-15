package api.luisangeldd.mediapicker.core

enum class StatusRequest {
    IDLE,
    EMPTY,
    NOT_EMPTY
}
enum class StateRequest {
    IDLE,
    START,
    END
}
enum class StatePicker{
    CLOSE,
    OPEN,
    DRAG,
    ADD
}