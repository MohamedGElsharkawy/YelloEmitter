package com.sharkawy.yelloemitter.presentation.features.users

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.sharkawy.yelloemitter.R
import com.sharkawy.yelloemitter.databinding.ActivityMainBinding
import com.sharkawy.yelloemitter.domain.usecases.users.usersUseCase
import com.sharkawy.yelloemitter.entities.Status
import com.sharkawy.yelloemitter.entities.users.User
import com.sharkawy.yelloemitter.entities.users.UsersResponseItem
import com.sharkawy.yelloemitter.presentation.core.EmitterReceiver


class UsersActivity : AppCompatActivity() {
    private val INTENT_ACTION_MiddleMan = "com.sharkawy.yellomiddleman"
    private val INTENT_ACTION_Emitter = "com.sharkawy.yelloemitter"
    private val INTENT_KEY = "EmitterUser"
    private val INTENT_COMPONENT_PACKAGE = "com.sharkawy.yellomiddleman"
    private val INTENT_COMPONENT_PACKAGE_CLASS =
        "com.sharkawy.yellomiddleman.presentation.MiddleManReceiver"


    private val viewModel: UsersViewModel by viewModels {
        UsersViewModelFactory((usersUseCase
                ))
    }

    private var binding: ActivityMainBinding? = null
    private var receiver: EmitterReceiver? = null

    private lateinit var progress: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    override fun onStart() {
        super.onStart()
        setupAdapter()
        setupObservers()
        fireUsersRefreshing()
    }

    override fun onResume() {
        super.onResume()
        showLoading()
    }
    private fun showLoading() {
        progress = ProgressDialog(this@UsersActivity)
        progress.setTitle("Loading")
        progress.setMessage("Waiting...")
        progress.setCancelable(false)
        progress.show()

        Handler(Looper.getMainLooper()).postDelayed({
            progress.dismiss()
            registerBroadCastReceiver()
            listenForBroadCastReceivedData()
        }, 2000)
    }

    private fun registerBroadCastReceiver() {
        receiver = EmitterReceiver()
        val intentFilter = IntentFilter(INTENT_ACTION_Emitter)
        registerReceiver(receiver, intentFilter)
    }

    private fun listenForBroadCastReceivedData() {
        val prefs = this.getSharedPreferences(
            "YelloPref",
            Context.MODE_PRIVATE
        )
        val status = prefs.getString("Status", null)


        checkIfBroadCastReceivedData(status)
    }

    private fun checkIfBroadCastReceivedData(status: String?) {
        if (status != null && status == "OK") {
            showReceivedStatus()
        }
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
        intent.action = INTENT_ACTION_MiddleMan
        intent.putExtra(INTENT_KEY, Gson().toJson(User(user.username, user.phone)))
        intent.component =
            ComponentName(
                INTENT_COMPONENT_PACKAGE,
                INTENT_COMPONENT_PACKAGE_CLASS
            )
        sendBroadcast(intent)
    }

    private fun showReceivedStatus() {
        val dialogView =
            LayoutInflater.from(this@UsersActivity).inflate(R.layout.dialog_receive_status, null)
        val builder = AlertDialog.Builder(this@UsersActivity)
            .setView(dialogView)
            .show()

        val confirmBtn = dialogView.findViewById<MaterialButton>(R.id.confirm_btn)

        confirmBtn.setOnClickListener {
            builder.dismiss()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}