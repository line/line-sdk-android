package com.linecorp.linesdk.openchat.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.linecorp.linesdk.R

class CreateOpenChatActivity : AppCompatActivity() {
    enum class CreateOpenChatStep {
        ChatroomInfo, UserProfile
    }

    private var currentStep = CreateOpenChatStep.ChatroomInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_open_chat)
        //setSupportActionBar(toolbar)

        addFragment(currentStep)

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) finish()
        }
    }

    private fun addFragment(step: CreateOpenChatStep) =
        supportFragmentManager.beginTransaction().run {
            addToBackStack(step.name)
            replace(R.id.container, createFragment(step))
            commit()
        }

    private fun createFragment(step: CreateOpenChatStep): Fragment = when(step) {
        CreateOpenChatStep.ChatroomInfo -> OpenChatInfoFragment.newInstance()
        else -> OpenChatInfoFragment.newInstance()
    }

    companion object {
        @JvmStatic
        fun createIntent(context: Context) =
            Intent(context, CreateOpenChatActivity::class.java)
    }
}
