package com.linecorp.linesdk.openchat.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.linecorp.linesdk.LineApiResponse
import com.linecorp.linesdk.R
import com.linecorp.linesdk.api.OpenChatApiClient
import com.linecorp.linesdk.api.internal.OpenChatApiClientImpl
import com.linecorp.linesdk.openchat.OpenChatParameters
import com.linecorp.linesdk.openchat.OpenChatRoomInfo


class CreateOpenChatActivity : AppCompatActivity() {
    private enum class CreateOpenChatStep { ChatroomInfo, UserProfile }

    private val openChatApiClient: OpenChatApiClient by lazy {
        val apiBaseUrl = intent.getStringExtra(ARG_API_BASE_URL).orEmpty()
        val channelId = intent.getStringExtra(ARG_CHANNEL_ID).orEmpty()
        OpenChatApiClientImpl(this, Uri.parse(apiBaseUrl), channelId)
    }

    private var currentStep = CreateOpenChatStep.ChatroomInfo
    private var createOpenChatroomTask: CreateOpenChatroomTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_open_chat)

        // The first fragment doesn't need to add to back stack so that the activity can leave
        // directly without popping up the first fragment and then manually handle finish() in
        // onBackPressed() or OnBackStackChangedListener.
        addFragment(currentStep, false)
    }

    override fun onDestroy() {
        createOpenChatroomTask?.cancel(true)
        super.onDestroy()
    }

    fun goToNextScreen() = addFragment(CreateOpenChatStep.UserProfile)

    fun createChatroom() {
        val openChatInfoViewModel = ViewModelProviders.of(this).get(OpenChatInfoViewModel::class.java)
        val openChatParameters = openChatInfoViewModel.toOpenChatParameters()

        createOpenChatroomTask?.cancel(true)
        createOpenChatroomTask =
            CreateOpenChatroomTask(this, openChatApiClient, openChatParameters) { result ->
                if (result.isSuccess) {
                    val openChatRoomInfo: OpenChatRoomInfo = result.responseData
                    val intent = Intent().apply { putExtra(ARG_OPEN_CHATROOM_INFO, openChatRoomInfo)}
                    setResult(Activity.RESULT_OK, intent)
                }
                finish()
            }
        createOpenChatroomTask?.execute()
    }

    private fun addFragment(step: CreateOpenChatStep, addToBackStack: Boolean = true) =
        supportFragmentManager.beginTransaction().run {
            if (addToBackStack) {
                addToBackStack(step.name)
            }
            replace(R.id.container, createFragment(step))
            commit()
        }

    private fun createFragment(step: CreateOpenChatStep): Fragment = when (step) {
        CreateOpenChatStep.ChatroomInfo -> OpenChatInfoFragment.newInstance()
        CreateOpenChatStep.UserProfile -> ProfileInfoFragment.newInstance()
    }

    private class CreateOpenChatroomTask(
        private val activity: Activity,
        private val apiClient: OpenChatApiClient,
        private val parameters: OpenChatParameters,
        private val postAction: (LineApiResponse<OpenChatRoomInfo>) -> Unit
    ) : AsyncTask<Void, Void, LineApiResponse<OpenChatRoomInfo>>() {
        private val progressDialog: ProgressDialog by lazy {
            ProgressDialog(activity).apply {
                setCancelable(true)
            }
        }

        override fun onPreExecute() = progressDialog.show()

        override fun doInBackground(vararg params: Void): LineApiResponse<OpenChatRoomInfo> =
            apiClient.createOpenChatRoom(parameters)

        override fun onPostExecute(result: LineApiResponse<OpenChatRoomInfo>) {
            postAction(result)
            if (progressDialog.isShowing) progressDialog.dismiss()
        }
    }

    companion object {
        const val ARG_OPEN_CHATROOM_INFO: String = "arg_open_chatroom_info"
        private const val ARG_API_BASE_URL: String = "arg_api_base_url"
        private const val ARG_CHANNEL_ID: String = "arg_channel_id"
        @JvmStatic
        fun createIntent(context: Context, apiBaseUrl: String, channelId: String): Intent =
            Intent(context, CreateOpenChatActivity::class.java).apply {
                putExtra(ARG_API_BASE_URL, apiBaseUrl)
                putExtra(ARG_CHANNEL_ID, channelId)
            }
    }
}
