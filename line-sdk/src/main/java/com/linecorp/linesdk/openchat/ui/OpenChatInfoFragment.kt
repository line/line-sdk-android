package com.linecorp.linesdk.openchat.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.linecorp.linesdk.R
import com.linecorp.linesdk.databinding.OpenChatInfoFragmentBinding
import com.linecorp.linesdk.openchat.TextUpdateWatcher
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.open_chat_info_fragment, container, false)
        binding.setLifecycleOwner(this)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val activity = activity ?: return

        viewModel = ViewModelProviders.of(activity).get(OpenChatInfoViewModel::class.java)
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
        val toolbar = activity?.toolbar ?: return

        toolbar.title = getString(R.string.openchat_create_room_title)
        toolbar.menu.clear()
        toolbar.inflateMenu(R.menu.menu_openchat_info)
        val nextMenuItem = toolbar.menu.findItem(R.id.menu_item_openchat_next)
        nextMenuItem.isEnabled = viewModel.isValid.value ?: true

        viewModel.isValid.observe(this, Observer { isValid ->
            nextMenuItem.isEnabled = isValid ?: true
        })

        nextMenuItem.setOnMenuItemClickListener {
            (activity as? CreateOpenChatActivity)?.run { onNextClick() }
            true
        }
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

    private fun showCategorySelectionDialog() {
        val context = context ?: return

        val builder = AlertDialog.Builder(context)
        val categoryStrings = viewModel.getCategoryStringArray()
        builder.setItems(categoryStrings) { _, which ->
            val selectedCategory = viewModel.getSelectedCategory(which)
            viewModel.category.value = selectedCategory
        }

        builder.create().show()
    }

    private fun setupDescription() {
        descriptionEditText.addTextChangedListener(
            TextUpdateWatcher(
                ::updateDescription,
                MAX_CHAT_DESCRIPTION_LENGTH
            )
        )

        descriptionEditText.setText(viewModel.description.value)
    }

    private fun setupName() {
        nameEditText.addTextChangedListener(
            TextUpdateWatcher(
                ::updateName,
                MAX_CHAT_NAME_LENGTH
            )
        )

        nameEditText.setText(viewModel.chatroomName.value)
    }

    private fun updateName(updatedName: String, lengthString: String) {
        viewModel.setChatroomName(updatedName)
        binding.nameMaxTextView.text = lengthString
    }

    private fun updateDescription(updatedDescription: String, lengthString: String) {
        viewModel.setDescription(updatedDescription)
        binding.descriptionMaxTextView.text = lengthString
    }

    companion object {
        fun newInstance() = OpenChatInfoFragment()
    }
}
