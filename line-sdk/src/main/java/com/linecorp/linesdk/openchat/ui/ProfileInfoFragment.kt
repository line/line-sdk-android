package com.linecorp.linesdk.openchat.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.linecorp.linesdk.R
import com.linecorp.linesdk.databinding.ProfileInfoFragmentBinding
import com.linecorp.linesdk.openchat.TextUpdateWatcher
import kotlinx.android.synthetic.main.activity_create_open_chat.toolbar
import kotlinx.android.synthetic.main.profile_info_fragment.displayNameEditText

class ProfileInfoFragment : Fragment() {

    private lateinit var binding: ProfileInfoFragmentBinding

    companion object {
        fun newInstance() = ProfileInfoFragment()
    }

    private lateinit var viewModel: ProfileInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.profile_info_fragment, container, false)
        binding.setLifecycleOwner(this)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val activity = activity ?: return

        viewModel = ViewModelProviders.of(activity).get(ProfileInfoViewModel::class.java)
        binding.viewModel = viewModel

        setupViews()
    }

    private fun setupViews() {
        setupToolbar()
        setupDisplayName()
    }

    private fun setupDisplayName() {
        displayNameEditText.addTextChangedListener(
            TextUpdateWatcher(
                ::updateDisplayName,
                OpenChatInfoViewModel.MAX_CHAT_NAME_LENGTH
            )
        )

        displayNameEditText.setText(viewModel.displayName.value)
    }

    private fun updateDisplayName(displayName: String, textLengthString: String) {
        viewModel.setDisplayName(displayName)
    }

    private fun setupToolbar() {
        val toolbar = activity?.toolbar ?: return

        toolbar.title = getString(R.string.openchat_create_profile_title)
        toolbar.menu.clear()
        toolbar.inflateMenu(R.menu.menu_profile_info)
        val doneMenuItem = toolbar.menu.findItem(R.id.menu_item_create_profile_done)
        doneMenuItem.isEnabled = viewModel.isValid.value ?: true

        viewModel.isValid.observe(this, Observer { isValid ->
            doneMenuItem.isEnabled = isValid ?: true
        })

        doneMenuItem.setOnMenuItemClickListener {
            (activity as? CreateOpenChatActivity)?.run { createChatroom() }
            true
        }
    }


}
