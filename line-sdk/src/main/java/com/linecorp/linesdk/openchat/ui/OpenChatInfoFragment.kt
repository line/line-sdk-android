package com.linecorp.linesdk.openchat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntegerRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.linecorp.linesdk.R
import com.linecorp.linesdk.databinding.OpenChatInfoFragmentBinding
import com.linecorp.linesdk.openchat.addAfterTextChangedAction
import kotlinx.android.synthetic.main.activity_create_open_chat.toolbar
import kotlinx.android.synthetic.main.open_chat_info_fragment.categoryLabelTextView
import kotlinx.android.synthetic.main.open_chat_info_fragment.descriptionEditText
import kotlinx.android.synthetic.main.open_chat_info_fragment.descriptionMaxTextView
import kotlinx.android.synthetic.main.open_chat_info_fragment.nameEditText
import kotlinx.android.synthetic.main.open_chat_info_fragment.nameMaxTextView
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

        setupViewModel()
        setupViews()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(requireActivity()).get(OpenChatInfoViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.chatroomName.observe(this, Observer { name ->
            nameMaxTextView.text =
                generateTextLengthLimitString(name, R.integer.max_chatroom_name_length)
        })

        viewModel.description.observe(this, Observer { name ->
            descriptionMaxTextView.text =
                generateTextLengthLimitString(name, R.integer.max_chatroom_description_length)
        })

        viewModel.category.observe(this, Observer { category ->
            category?.resourceId?.let { resourceId ->
                categoryLabelTextView.text = resources.getString(resourceId)
            }
        })
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

    private fun generateTextLengthLimitString(text: String, limitResId: Int): String {
        val maxCount = getResourceInt(limitResId)
        return "${text.length}/$maxCount"
    }

    private fun setupSearchOption() {
        searchIncludedCheckBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isSearchIncluded.value = isChecked
        }

        searchIncludedContainer.setOnClickListener { searchIncludedCheckBox.toggle() }
    }

    private fun setupCategoryLabel() {
        categoryLabelTextView.setOnClickListener { showCategorySelectionDialog() }
    }

    private fun showCategorySelectionDialog() =
        AlertDialog.Builder(requireContext())
            .setItems(viewModel.getCategoryStringArray(requireContext())) { _, which ->
                val selectedCategory = viewModel.getSelectedCategory(which)
                viewModel.category.value = selectedCategory
            }
            .show()

    private fun setupDescription() =
        descriptionEditText.addAfterTextChangedAction(viewModel.description::setValue)

    private fun setupName() =
        nameEditText.addAfterTextChangedAction(viewModel.chatroomName::setValue)

    private fun getResourceInt(@IntegerRes resId: Int): Int =
        requireActivity().resources.getInteger(resId)

    companion object {
        fun newInstance() = OpenChatInfoFragment()
    }
}
