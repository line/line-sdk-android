package com.linecorp.linesdk.openchat.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.linecorp.linesdk.R
import com.linecorp.linesdk.databinding.ProfileInfoFragmentBinding
import com.linecorp.linesdk.openchat.addAfterTextChangedAction
import kotlinx.android.synthetic.main.activity_create_open_chat.toolbar
import kotlinx.android.synthetic.main.profile_info_fragment.displayNameEditText
import kotlinx.android.synthetic.main.profile_info_fragment.displayNameGuide

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
        setupProfileNameGuide()
    }

    private fun setupProfileNameGuide() {
        displayNameGuide.text = resources.getString(R.string.openchat_create_profile_input_guide, viewModel.chatroomName.value)
    }

    private fun setupProfileName() =
        displayNameEditText.addAfterTextChangedAction { name ->
            viewModel.profileName.value = name
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
                dismissKeyboard()
                viewModel.createChatroom()
                true
            } else {
                false
            }
        }

        viewModel.isProfileValid.observe(this, Observer { isProfileValid ->
            doneMenuItem.isEnabled = isProfileValid ?: false
        })
    }

    private fun dismissKeyboard() {
        val focusedView = requireActivity().currentFocus ?: return

        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(
                focusedView.windowToken,
                InputMethodManager.RESULT_UNCHANGED_SHOWN
            )
    }

    companion object {
        fun newInstance() = ProfileInfoFragment()
    }
}
