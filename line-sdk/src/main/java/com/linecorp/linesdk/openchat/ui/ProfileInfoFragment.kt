package com.linecorp.linesdk.openchat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.linecorp.linesdk.R
import com.linecorp.linesdk.databinding.ProfileInfoFragmentBinding
import com.linecorp.linesdk.openchat.FormatStringTextWatcher
import kotlinx.android.synthetic.main.activity_create_open_chat.toolbar
import kotlinx.android.synthetic.main.profile_info_fragment.displayNameEditText

class ProfileInfoFragment : Fragment() {

    private lateinit var binding: ProfileInfoFragmentBinding

    private lateinit var viewModel: OpenChatInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ProfileInfoFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(requireActivity()).get(OpenChatInfoViewModel::class.java)
        binding.viewModel = viewModel

        setupViews()
    }

    private fun setupViews() {
        setupToolbar()
        setupProfileName()
    }

    private fun setupProfileName() {
        displayNameEditText.addTextChangedListener(
            FormatStringTextWatcher(
                { name, _ -> viewModel.profileName.value = name },
                OpenChatInfoViewModel.MAX_PROFILE_NAME_LENGTH
            )
        )
    }

    private fun setupToolbar() {
        val toolbar = requireActivity().toolbar.apply {
            title = getString(R.string.openchat_create_profile_title)
            menu.clear()
            inflateMenu(R.menu.menu_profile_info)
        }

        val doneMenuItem = toolbar.menu.findItem(R.id.menu_item_create_profile_done)
        doneMenuItem.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.menu_item_create_profile_done) {
                (requireActivity() as CreateOpenChatActivity).createChatroom()
                true
            } else {
                false
            }
        }

        viewModel.isProfileValid.observe(this, Observer { isProfileValid ->
            doneMenuItem.isEnabled = isProfileValid ?: false
        })
    }

    companion object {
        fun newInstance() = ProfileInfoFragment()
    }
}
