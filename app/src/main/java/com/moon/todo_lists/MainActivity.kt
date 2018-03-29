package com.moon.todo_lists

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.*

import kotlinx.android.synthetic.main.activity_main.*
import android.view.LayoutInflater
import android.widget.*

import java.util.ArrayList


class MainActivity : AppCompatActivity() {
    var mHelper = TaskDBHelper(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        fab.setOnClickListener {
            showPopWindow()
            }
        updateUI()
        }

    private fun updateUI() {

        val list_view = findViewById<ListView>(R.id.list_todo)
        val emptyview = findViewById<LinearLayout>(R.id.toDoEmptyView)
        var mAdapter: ArrayAdapter<String>? = null
        var taskList = ArrayList<String>()
        var db:SQLiteDatabase =  mHelper.readableDatabase
        var cursor:Cursor = db.query(TaskDBHelper.TABLENAME, arrayOf<String>(TaskDBHelper.TASK_ID, TaskDBHelper.TASK_TITLE),
                null,null,null,null,null)
        if (cursor.count <= 0) {
           emptyview.visibility = View.VISIBLE
        }
        else {
            emptyview.visibility = View.GONE
        }
        while (cursor.moveToNext()){
            var idx = cursor.getColumnIndex(TaskDBHelper.TASK_TITLE)
            taskList.add(cursor.getString(idx))
        }

        if (mAdapter == null) {
            mAdapter = ArrayAdapter(this,
                    R.layout.item_list,
                    R.id.task_title,
                    taskList)
            list_view.adapter = mAdapter
        } else {
            mAdapter.clear()
            mAdapter.addAll()
            mAdapter.notifyDataSetChanged()
        }
        cursor.close()
        db.close()
    }

    private fun getRandomColor(): Int {
        return Color.parseColor("#BFEFFF")
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showPopWindow(){
        val contentView = LayoutInflater.from(this@MainActivity).inflate(R.layout.add_new_item, null)
        val mPopWindow = PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        mPopWindow.animationStyle = R.style.popmenu_animation
        mPopWindow.contentView = contentView
        mPopWindow.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
        val button = contentView.findViewById<FloatingActionButton>(R.id.sendbutton)
        val text = contentView.findViewById<EditText>(R.id.userToDoEditText)

        button.setOnClickListener {
            if (text.length() <= 0) {
                Toast.makeText(this,getString(R.string.todo_error),Toast.LENGTH_SHORT).show()
            }
            else {
                val task = text.text.toString()
                val db:SQLiteDatabase = mHelper.writableDatabase
                val values = ContentValues()
                var cursor:Cursor = db.query(
                        TaskDBHelper.TABLENAME,
                        arrayOf(TaskDBHelper.TASK_TITLE),
                        TaskDBHelper.TASK_TITLE + "=?",
                        arrayOf(text.text.toString()),
                        null,
                        null,
                        null
                )
                if (cursor.count > 0) {
                    Toast.makeText(this,"提醒已经存在",Toast.LENGTH_SHORT).show()
                }else {
                    values.put(TaskDBHelper.TASK_TITLE, task)
                    db.insertWithOnConflict(
                            TaskDBHelper.TABLENAME,
                            null,
                            values,
                            SQLiteDatabase.CONFLICT_REPLACE
                    )
                    cursor.close()
                    db.close()
                    mPopWindow.dismiss()
                    updateUI()
                }
            }
        }
        val rootview = LayoutInflater.from(this@MainActivity).inflate(R.layout.activity_main, null)
        mPopWindow.showAtLocation(rootview, Gravity.TOP, 0, 0)
    }

    fun deleteTask(view: View) {
        val parent:View = view.parent as View
        val taskTextView:TextView = parent.findViewById(R.id.task_title)
        val deletbutton:Button = parent.findViewById(R.id.task_delete)
        val task = taskTextView.text.toString()
        val db = mHelper.writableDatabase
        db.delete(TaskDBHelper.TABLENAME,
                TaskDBHelper.TASK_TITLE + " = ?",
                arrayOf(task))
        db.close()
        updateUI()
    }

}

