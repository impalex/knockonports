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
import kotlinx.android.synthetic.main.file_element.view.*
import me.impa.knockonports.R
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
        val view = convertView ?: inflater.inflate(R.layout.file_element, parent, false).apply { tag = ViewHolder(this) }
        val holder = view.tag as ViewHolder
        holder.image.setImageResource(if (fileList[position].isDirectory) R.drawable.ic_folder else R.drawable.ic_file)
        holder.name.text = fileList[position].name
        holder.view.setOnClickListener { onSelected?.invoke(fileList[position]) }
        return view
    }

    class ViewHolder(val view: View) {
        val image = view.image_file_type!!
        val name = view.text_file_name!!
    }
}