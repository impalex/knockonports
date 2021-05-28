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
import me.impa.knockonports.data.AppData
import me.impa.knockonports.databinding.AppElementBinding

class AppListAdapter(context: Context): BaseAdapter() {
    var apps = listOf<AppData>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var onSelected: ((AppData) -> Unit)? = null

    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int = apps.size

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getItem(position: Int): AppData = apps[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: AppElementBinding.inflate(inflater, parent, false).apply { root.tag = ViewHolder(this) }.root
        val holder = view.tag as ViewHolder
        holder.name.text = apps[position].name
        holder.view.setOnClickListener { onSelected?.invoke(apps[position]) }

        return view
    }

    class ViewHolder(binding: AppElementBinding) {
        val view = binding.root
        val name = binding.textApp
    }

}
