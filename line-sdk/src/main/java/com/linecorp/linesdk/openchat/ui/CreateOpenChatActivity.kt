package com.linecorp.linesdk.openchat.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.linecorp.linesdk.ActionResult
import com.linecorp.linesdk.Constants
import com.linecorp.linesdk.LineApiError
import com.linecorp.linesdk.R
import com.linecorp.linesdk.api.LineApiClient
import com.linecorp.linesdk.api.LineApiClientBuilder
import com.linecorp.linesdk.auth.internal.LineAppVersion
import com.linecorp.linesdk.openchat.OpenChatRoomInfo
import kotlinx.android.synthetic.main.activity_create_open_chat.progressBar

class CreateOpenChatActivity : AppCompatActivity() {
    private enum class CreateOpenChatStep { ChatroomInfo, UserProfile }

    private val lineApiClient: LineApiClient by lazy {
        val apiBaseUrl = intent.getStringExtra(ARG_API_BASE_URL).orEmpty()
        val channelId = intent.getStringExtra(ARG_CHANNEL_ID).orEmpty()
        LineApiClientBuilder(this, channelId)
            .apiBaseUri(Uri.parse(apiBaseUrl))
            .build()
    }

    private lateinit var viewModel: OpenChatInfoViewModel

    private var currentStep = CreateOpenChatStep.ChatroomInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_open_chat)

        initViewModel()

        // The first fragment doesn't need to add to back stack so that the activity can leave
        // directly without popping up the first fragment and then manually handle finish() in
        // onBackPressed() or OnBackStackChangedListener.
        addFragment(currentStep, false)
    }

    fun goToNextScreen() = addFragment(CreateOpenChatStep.UserProfile)

    private fun initViewModel() {
        val sharedPreferences = getSharedPreferences("openchat", Context.MODE_PRIVATE)
        viewModel = ViewModelProviders.of(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(OpenChatInfoViewModel::class.java)) {
                        return OpenChatInfoViewModel(sharedPreferences, lineApiClient) as T
                    }
                    error("Unknown ViewModel class")
                }
            }
        )[OpenChatInfoViewModel::class.java]

        viewModel.openChatRoomInfo.observe(this, Observer { openChatRoomInfo ->
            setResult(
                Activity.RESULT_OK,
                Intent().putExtra(ARG_OPEN_CHATROOM_INFO, openChatRoomInfo)
            )
            finish()
        })

        viewModel.createChatRoomError.observe(this, Observer { lineApiResponse ->
            setResult(
                RESULT_OK,
                Intent().putExtra(ARG_ERROR_RESULT, lineApiResponse.errorData)
            )
            finish()
        })

        viewModel.isCreatingChatRoom.observe(this, Observer { isCreatingChatRoom ->
            progressBar.visibility = if (isCreatingChatRoom) VISIBLE else GONE
        })

        viewModel.shouldShowAgreementWarning.observe(this, Observer { shouldShowWarning ->
            if (shouldShowWarning) {
                showAgreementWarningDialog()
            }
        })
    }

    private fun showAgreementWarningDialog() {
        val isLineAppInstalled: Boolean = LineAppVersion.getLineAppVersion(this) != null

        AlertDialog.Builder(this)
            .setMessage(R.string.login_openchat_not_agree_with_terms)
            .setOnDismissListener { finish() }
            .apply {
                if (isLineAppInstalled) {
                    setPositiveButton(R.string.openchat_open_line) { _, _ -> launchLineApp() }
                    setNegativeButton(android.R.string.cancel) { _, _ -> finish() }
                } else {
                    setPositiveButton(android.R.string.ok) { _, _ -> finish() }
                }
            }
            .show()
    }

    private fun launchLineApp() =
        startActivity(packageManager.getLaunchIntentForPackage(Constants.LINE_APP_PACKAGE_NAME))

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

    companion object {
        const val ARG_OPEN_CHATROOM_INFO: String = "arg_open_chatroom_info"
        const val ARG_ERROR_RESULT: String = "arg_error_result"
        private const val ARG_API_BASE_URL: String = "arg_api_base_url"
        private const val ARG_CHANNEL_ID: String = "arg_channel_id"
        @JvmStatic
        @JvmOverloads
        fun createIntent(
            context: Context,
            channelId: String,
            apiBaseUrl: String = Constants.API_SERVER_BASE_URI
        ): Intent =
            Intent(context, CreateOpenChatActivity::class.java)
                .putExtra(ARG_API_BASE_URL, apiBaseUrl)
                .putExtra(ARG_CHANNEL_ID, channelId)

        @JvmStatic
        fun getChatRoomCreationResult(intent: Intent): ActionResult<OpenChatRoomInfo, LineApiError> {
            val openChatRoomInfo =
                intent.getParcelableExtra(ARG_OPEN_CHATROOM_INFO) as? OpenChatRoomInfo
            openChatRoomInfo?.let { openChatRoomInfo -> return ActionResult.Success(openChatRoomInfo) }

            val lineApiError = intent.getParcelableExtra(ARG_ERROR_RESULT) as? LineApiError
            lineApiError?.let { lineApiError -> return ActionResult.Error(lineApiError) }

            return ActionResult.Error(LineApiError.DEFAULT)
        }
    }
}
