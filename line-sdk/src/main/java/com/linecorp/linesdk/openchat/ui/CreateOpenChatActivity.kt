package com.linecorp.linesdk.openchat.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.linecorp.linesdk.R

class CreateOpenChatActivity : AppCompatActivity() {
    enum class CreateOpenChatStep {
        ChatroomInfo, UserProfile
    }

    private var currentStep = CreateOpenChatStep.ChatroomInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_open_chat)
        addFragment(currentStep)

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) finish()
        }
    }

    fun onNextClick() {
        addFragment(CreateOpenChatStep.UserProfile)
    }

    fun createChatroom() {
        // TODO
    }

    private fun addFragment(step: CreateOpenChatStep) =
        supportFragmentManager.beginTransaction().run {
            addToBackStack(step.name)
            replace(R.id.container, createFragment(step))
            commit()
        }

    private fun createFragment(step: CreateOpenChatStep): Fragment = when(step) {
        CreateOpenChatStep.ChatroomInfo -> OpenChatInfoFragment.newInstance()
        CreateOpenChatStep.UserProfile -> ProfileInfoFragment.newInstance()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return super.onOptionsItemSelected(item)
    }

    companion object {
        @JvmStatic
        fun createIntent(context: Context) =
            Intent(context, CreateOpenChatActivity::class.java)
    }
}
