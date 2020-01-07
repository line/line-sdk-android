package com.linecorp.linesdk.openchat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.linecorp.linesdk.R
import com.linecorp.linesdk.databinding.OpenChatInfoFragmentBinding
import com.linecorp.linesdk.openchat.FormatStringTextWatcher
import com.linecorp.linesdk.openchat.ui.OpenChatInfoViewModel.Companion.MAX_CHAT_DESCRIPTION_LENGTH
import com.linecorp.linesdk.openchat.ui.OpenChatInfoViewModel.Companion.MAX_CHAT_NAME_LENGTH
import kotlinx.android.synthetic.main.activity_create_open_chat.toolbar
import kotlinx.android.synthetic.main.open_chat_info_fragment.categoryLabelTextView
import kotlinx.android.synthetic.main.open_chat_info_fragment.descriptionEditText
import kotlinx.android.synthetic.main.open_chat_info_fragment.nameEditText
import kotlinx.android.synthetic.main.open_chat_info_fragment.searchIncludedCheckBox
import kotlinx.android.synthetic.main.open_chat_info_fragment.searchIncludedContainer

class OpenChatInfoFragment : Fragment() {

    private lateinit var binding: OpenChatInfoFragmentBinding

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
        binding = OpenChatInfoFragmentBinding.inflate(inflater, container, false)
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
        setupName()
        setupDescription()
        setupCategoryLabel()
        setupSearchOption()
    }

    private fun setupToolbar() {
        val toolbar = requireActivity().toolbar.apply {
            title = getString(R.string.openchat_create_room_title)
            menu.clear()
            inflateMenu(R.menu.menu_openchat_info)
        }

        val nextMenuItem = toolbar.menu.findItem(R.id.menu_item_openchat_next)
        nextMenuItem.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.menu_item_openchat_next) {
                (requireActivity() as CreateOpenChatActivity).goToNextScreen()
                true
            } else {
                false
            }
        }

        viewModel.isValid.observe(this, Observer { isValid ->
            nextMenuItem.isEnabled = isValid ?: false
        })

    }

    private fun setupSearchOption() {
        searchIncludedCheckBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isSearchIncluded.value = isChecked
        }

        searchIncludedContainer.setOnClickListener { searchIncludedCheckBox.toggle() }
    }

    private fun setupCategoryLabel() {
        categoryLabelTextView.setOnClickListener { showCategorySelectionDialog() }
        viewModel.category.observe(this, Observer { category ->
            categoryLabelTextView.text = category?.defaultString?.orEmpty()
        })
    }

    private fun showCategorySelectionDialog() =
        AlertDialog.Builder(requireContext())
            .setItems(viewModel.getCategoryStringArray()) { _, which ->
                val selectedCategory = viewModel.getSelectedCategory(which)
                viewModel.category.value = selectedCategory
            }
            .create()
            .show()

    private fun setupDescription() {
        descriptionEditText.addTextChangedListener(
            FormatStringTextWatcher(::updateDescription, MAX_CHAT_DESCRIPTION_LENGTH)
        )

        descriptionEditText.setText(viewModel.description.value)
    }

    private fun setupName() {
        nameEditText.addTextChangedListener(
            FormatStringTextWatcher(::updateName, MAX_CHAT_NAME_LENGTH)
        )

        nameEditText.setText(viewModel.chatroomName.value)
    }

    private fun updateName(updatedName: String, lengthString: String) {
        viewModel.chatroomName.value = updatedName
        binding.nameMaxTextView.text = lengthString
    }

    private fun updateDescription(updatedDescription: String, lengthString: String) {
        viewModel.description.value = updatedDescription
        binding.descriptionMaxTextView.text = lengthString
    }

    companion object {
        fun newInstance() = OpenChatInfoFragment()
    }
}
