package com.example.loginsignup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class DashBoard : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var  btnLogout : Button
    private lateinit var recyclerView : RecyclerView
    private lateinit var btnAdd : Button
    private lateinit var etTodo: EditText

    private lateinit var todoList : ArrayList<Todo>
    private lateinit var todoAdapter: TodoAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)

//        recycler View
        recyclerView = findViewById(R.id.rvTodoList)
       recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
//        recycler view


        auth = FirebaseAuth.getInstance()
        btnLogout = findViewById(R.id.btnLogOut)
        btnAdd = findViewById(R.id.btnAdd)
        etTodo = findViewById(R.id.etTodo)

        todoList = ArrayList()
        todoAdapter = TodoAdapter(todoList)

        todoList.add(Todo("1st Sample task", false))
        todoList.add(Todo("2st Sample task", false))
        todoList.add(Todo("3st Sample task", false))

        recyclerView.adapter = todoAdapter

        //        Logout Btn
        btnLogout.setOnClickListener{
            auth.signOut()

            startActivity(Intent(this, MainActivity::class.java))
        }
        //        Logout Btn

        // Add Btn
        btnAdd.setOnClickListener{
            var task = etTodo.text
            var addNewTask = Todo("$task",false)

            todoList.add(addNewTask)
            etTodo.setText("")
            recyclerView.adapter = todoAdapter
        }

        // Add Btn


    }
}

