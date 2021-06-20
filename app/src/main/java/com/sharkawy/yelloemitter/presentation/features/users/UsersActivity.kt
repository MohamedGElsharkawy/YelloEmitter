package com.sharkawy.yelloemitter.presentation.features.users

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.sharkawy.yelloemitter.R
import com.sharkawy.yelloemitter.databinding.ActivityMainBinding
import com.sharkawy.yelloemitter.entities.Status
import com.sharkawy.yelloemitter.entities.users.User
import com.sharkawy.yelloemitter.entities.users.UsersResponseItem


class UsersActivity : AppCompatActivity() {
    private val INTENT_ACTION = "com.sharkawy.yellomiddleman"
    private val INTENT_KEY = "EmitterUser"
    private val INTENT_COMPONENT_PACKAGE = "com.sharkawy.yellomiddleman"
    private val INTENT_COMPONENT_PACKAGE_CLASS =
        "com.sharkawy.yellomiddleman.presentation.MiddleManReceiver"


    private var binding: ActivityMainBinding? = null
    private lateinit var viewModel: UsersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    override fun onStart() {
        super.onStart()
        setupAdapter()
        setupViewModel()
        setupObservers()
        fireUsersRefreshing()
    }

    private fun fireUsersRefreshing() {
        binding?.swipeRefresh?.setOnRefreshListener {
            setupObservers()
        }
    }

    private fun setupAdapter() {
        binding?.usersRv?.adapter =
            UsersAdapter({ user ->
                showConfirmDialog(user)
            }, mutableListOf())
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(UsersViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.getUsers().observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.LOADING -> {
                        binding?.swipeRefresh?.isRefreshing = true
                        Log.w("STATUS", "LOADING")
                    }
                    Status.SUCCESS -> {
                        binding?.swipeRefresh?.isRefreshing = false
                        resource.data?.let { users ->
                            binding?.usersRv?.adapter = UsersAdapter({ user ->
                                showConfirmDialog(user)
                            }, users.toMutableList())
                        }
                    }
                    Status.ERROR -> {
                        binding?.swipeRefresh?.isRefreshing = false
                        Log.w("STATUS", "ERROR")
                        Log.w("STATUS", it.message.toString())
                    }

                }
            }
        })
    }

    private fun showConfirmDialog(user: UsersResponseItem) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_user_confirm, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .show()

        val confirmBtn = dialogView.findViewById<MaterialButton>(R.id.confirm_btn)
        val cancelBtn = dialogView.findViewById<MaterialButton>(R.id.cancel_btn)

        confirmBtn.setOnClickListener {
            sendUserToMiddleMan(user)
            builder.dismiss()
            Toast.makeText(applicationContext, "User sent to Middle Man!", Toast.LENGTH_SHORT)
                .show()
        }

        cancelBtn.setOnClickListener {
            builder.dismiss()
        }
    }

    private fun sendUserToMiddleMan(user: UsersResponseItem) {
        val intent = Intent()
        intent.action = INTENT_ACTION
        intent.putExtra(INTENT_KEY, Gson().toJson(User(user.username, user.phone)))
        intent.component =
            ComponentName(
                INTENT_COMPONENT_PACKAGE,
                INTENT_COMPONENT_PACKAGE_CLASS
            )
        sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}