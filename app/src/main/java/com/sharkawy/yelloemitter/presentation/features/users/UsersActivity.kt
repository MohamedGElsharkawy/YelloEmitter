package com.sharkawy.yelloemitter.presentation.features.users

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.sharkawy.yelloemitter.R
import com.sharkawy.yelloemitter.databinding.ActivityMainBinding
import com.sharkawy.yelloemitter.entities.Status

class UsersActivity : AppCompatActivity() {

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
            UsersAdapter({
                showConfirmDialog()
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
                            binding?.usersRv?.adapter = UsersAdapter({
                                showConfirmDialog()
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

    private fun showConfirmDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_user_confirm, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .show()

        val confirmBtn = dialogView.findViewById<MaterialButton>(R.id.confirm_btn)
        val cancelBtn = dialogView.findViewById<MaterialButton>(R.id.cancel_btn)

        confirmBtn.setOnClickListener {
            sendUserToMiddeMan()
            builder.dismiss()
        }

        cancelBtn.setOnClickListener {
            builder.dismiss()
        }
    }

    private fun sendUserToMiddeMan() {
        val intent = Intent()
        intent.action = "com.sharkawy.yellomiddleman"
        intent.putExtra("KeyName", "This message sent from Yello Emitter")
        intent.component =
            ComponentName("com.sharkawy.yellomiddleman", "com.sharkawy.yellomiddleman.MiddleManReceiver")
        sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}