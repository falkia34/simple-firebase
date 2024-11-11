package dev.rulim34.simplefirebase

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class HomeFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var todoAdapter: TodoAdapter
    private val todos = mutableListOf<Todo>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Setup RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        todoAdapter = TodoAdapter(todos) { deleteTodo(it) }
        recyclerView.adapter = todoAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        view.findViewById<FloatingActionButton>(R.id.fabAddTodo).setOnClickListener {
            showAddTodoDialog()
        }

        loadTodos()
        return view
    }

    private fun loadTodos() {
        db.collection("todos").addSnapshotListener { snapshot, e ->
            if (e != null || snapshot == null) return@addSnapshotListener

            todos.clear()
            for (doc in snapshot.documents) {
                val todo = doc.toObject(Todo::class.java) ?: continue
                todos.add(todo)
            }
            todoAdapter.notifyDataSetChanged()
        }
    }

    private fun showAddTodoDialog() {
        // Create an AlertDialog with an EditText input field
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Add New To-Do")

        val input = EditText(requireContext())
        input.hint = "Enter to-do text"
        dialogBuilder.setView(input)

        dialogBuilder.setPositiveButton("Add") { _, _ ->
            val todoText = input.text.toString().trim()
            if (todoText.isNotEmpty()) {
                addNewTodo(todoText)
            } else {
                Toast.makeText(requireContext(), "Please enter some text", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        dialogBuilder.show()
    }

    private fun addNewTodo(todoText: String) {
        val newTodo = Todo(
            text = todoText,
            checked = false
        )

        db.collection("todos").document(UUID.randomUUID().toString()).set(newTodo)
    }

    private fun deleteTodo(id: String) {
        db.collection("todos").document(id).delete()
    }
}
