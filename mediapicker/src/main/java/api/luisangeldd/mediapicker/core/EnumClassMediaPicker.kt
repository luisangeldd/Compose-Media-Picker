package api.luisangeldd.mediapicker.core

internal enum class StatusOfRequest {
    IDLE,
    EMPTY,
    NOT_EMPTY
}
internal enum class StateOfRequest {
    IDLE,
    START,
    END
}
internal enum class AnswerOfRequest {
    IDLE,
    EMPTY,
    NOT_EMPTY
}
internal enum class StatePicker{
    CLOSE,
    OPEN,
    DRAG,
    ADD
}