/*
 * Copyright (c) 2018 Alexander Yaburov
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package me.impa.knockonports.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import hendrawd.storageutil.library.StorageUtil
import me.impa.knockonports.R
import me.impa.knockonports.databinding.FragmentFileChooserBinding
import me.impa.knockonports.ext.afterTextChanged
import me.impa.knockonports.ext.validate
import me.impa.knockonports.viewadapter.FileItemAdapter
import java.io.File

class FileChooserFragment: DialogFragment() {

    private var _binding: FragmentFileChooserBinding? = null
    private val binding get() = _binding!!

    private val fileItemAdapter by lazy { FileItemAdapter(requireContext()) }
    private val storageList by lazy { StorageUtil.getStorageDirectories(context) }
    var onSelected: ((String) -> Unit)? = null
    var onDismiss: (() -> Unit)? = null
    var currentDir: String? = null
    var dirsOnly: Boolean = false
    var showSaveButton: Boolean = true
    var showFileNameEdit: Boolean = true
    var title: String? = null
    var fileName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFileChooserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.listFiles.adapter = fileItemAdapter
        fileItemAdapter.onSelected = {
            when {
                it.name == ".." -> navigateTo(File(currentDir!!).parent!!)
                it.isDirectory -> navigateTo(it.canonicalPath)
                else -> {
                    onSelected?.invoke(it.canonicalPath)
                    dismiss()
                }
            }
        }


        binding.layoutCurDir.setOnClickListener {
            if (storageList.isEmpty())
                return@setOnClickListener
            val menu = PopupMenu(activity, binding.imageStorageDown)
            storageList.forEachIndexed { index, storage ->
                menu.menu.add(0, STORAGE_ITEM_FIRST + index, index, storage)
            }
            menu.setOnMenuItemClickListener { m ->
                val i = m.itemId - STORAGE_ITEM_FIRST
                if (i>=0 && i<storageList.size)
                    navigateTo(storageList[i])
                true
            }
            menu.show()
        }

        val saveButton = binding.buttonSave
        val editFileName = binding.editFileName
        if (!showFileNameEdit)
            binding.editFileNameWrapper.visibility = View.GONE
        editFileName.setText(fileName)
        editFileName.afterTextChanged { fileName = it }
        editFileName.validate {
            if (it.isBlank()) {
                saveButton.isEnabled = false
                getString(R.string.error_empty_file_name)
            } else {
                saveButton.isEnabled = true
                null
            }
        }

        if (!showSaveButton)
            saveButton.visibility = View.GONE
        saveButton.setOnClickListener {
            onSelected?.invoke("$currentDir/$fileName")
            dismiss()
        }

        binding.buttonCancel.setOnClickListener { dismiss() }

        navigateTo(currentDir ?: storageList.firstOrNull() ?: requireContext().filesDir.canonicalPath)

    }

    override fun onCreateDialog(savedInstanceState: Bundle?) = super.onCreateDialog(savedInstanceState).apply { window?.setTitle(title) }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismiss?.invoke()
    }

    private fun navigateTo(dir: String) {
        val directory = (if (dir == "..") File(currentDir!!).parentFile else File(dir))
        if (!directory.canRead())
            return
        currentDir = directory.canonicalPath
        binding.textStorage.text = currentDir

        val list = mutableListOf<File>()
        if (directory.parentFile != null) {
            list.add(File(".."))
        }

        list.addAll(directory.listFiles { file -> !dirsOnly || file.isDirectory }!!.sortedWith(compareBy({ !it.isDirectory }, { it.name })))

        fileItemAdapter.fileList = list

    }

    companion object {
        const val FRAGMENT_FILE_CHOOSER = "FILE_CHOOSER"
        const val STORAGE_ITEM_FIRST = 10000
    }

}

fun FragmentActivity.fileChooser(init: FileChooserFragment.() -> Unit): FileChooserFragment =
        FileChooserFragment().apply(init).apply {
            val ft = this@fileChooser.supportFragmentManager.beginTransaction()
            val prev = this@fileChooser.supportFragmentManager.findFragmentByTag(FileChooserFragment.FRAGMENT_FILE_CHOOSER)
            if (prev != null) {
                ft.remove(prev)
            }
            ft.commitAllowingStateLoss()
            show(this@fileChooser.supportFragmentManager, FileChooserFragment.FRAGMENT_FILE_CHOOSER)
        }

