package dev.rulim34.simplefirebase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TodoAdapter(
    private val todos: MutableList<Todo>,
    private val onDelete: (String) -> Unit
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    inner class TodoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val todoText: TextView = view.findViewById(R.id.textTodo)
        private val todoCheck: CheckBox = view.findViewById(R.id.todoCheck)
        private val deleteButton: Button = view.findViewById(R.id.btnDelete)

        fun bind(todo: Todo) {
            todoText.text = todo.text
            todoCheck.isChecked = todo.checked

            deleteButton.setOnClickListener { onDelete(todo.id) }

            todoCheck.setOnCheckedChangeListener { _, isChecked ->
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(todos[position])
    }

    override fun getItemCount(): Int = todos.size
}