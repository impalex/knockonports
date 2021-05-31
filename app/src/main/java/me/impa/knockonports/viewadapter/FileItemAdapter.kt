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

package me.impa.knockonports.viewadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import me.impa.knockonports.R
import me.impa.knockonports.databinding.FileElementBinding
import java.io.File

class FileItemAdapter(context: Context) : BaseAdapter() {
    var fileList = listOf<File>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    var onSelected: ((File) -> Unit)? = null

    override fun getCount(): Int = fileList.size

    override fun getItemId(position: Int) = position.toLong()

    override fun getItem(position: Int): File = fileList[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: FileElementBinding.inflate(inflater, parent, false)
            .apply { root.tag = ViewHolder(this, onSelected) }.root
        (view.tag as ViewHolder).run {
            image.setImageResource(if (fileList[position].isDirectory) R.drawable.ic_folder else R.drawable.ic_file)
            name.text = fileList[position].name
            file = fileList[position]
        }
        return view
    }

    class ViewHolder(val binding: FileElementBinding, private val onSelected: ((File)-> Unit)?) {
        lateinit var file: File
        init {
            binding.root.setOnClickListener { onSelected?.invoke(file) }
        }
        val image = binding.imageFileType
        val name = binding.textFileName
    }
}