package dev.rulim34.simplefirebase

data class Todo(
    val id: String = "",
    val text: String = "",
    var checked: Boolean = false,
    val userId: String = ""
)