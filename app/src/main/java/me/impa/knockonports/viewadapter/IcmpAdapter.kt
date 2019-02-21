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

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.icmp_element.view.*
import me.impa.knockonports.R
import me.impa.knockonports.data.ContentEncoding
import me.impa.knockonports.ext.ItemTouchHelperAdapter
import me.impa.knockonports.ext.afterTextChanged
import me.impa.knockonports.json.IcmpData

class IcmpAdapter(val context: Context): androidx.recyclerview.widget.RecyclerView.Adapter<IcmpAdapter.ViewHolder>(), ItemTouchHelperAdapter {

    private val encodingArray = arrayOf(context.getString(R.string.encoding_raw),
            context.getString(R.string.encoding_base64), context.getString(R.string.encoding_hex))

    override var onStartDrag: ((androidx.recyclerview.widget.RecyclerView.ViewHolder) -> Unit)? = null

    var items: MutableList<IcmpData> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun addItem(item: IcmpData) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.icmp_element, parent, false))

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = items[position]

        holder.dragHandle.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                onStartDrag?.invoke(holder)
            }
            return@setOnTouchListener false
        }
        holder.imageDelete.setOnClickListener {
            val index = items.indexOf(data)
            if (index >= 0) {
                items.removeAt(index)
                notifyItemRemoved(index)
            }
        }
        holder.sizeEdit.run {
            afterTextChanged {
                data.size = it.toIntOrNull()
            }
            setText(data.size?.toString())
        }
        holder.countEdit.run {
            afterTextChanged {
                data.count = it.toIntOrNull()
            }
            setText(data.count?.toString())
        }
        holder.contentEdit.run {
            afterTextChanged {
                data.content = it
            }
            setText(data.content)
        }
        holder.encodingSpinner.run {
            val encodingAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, encodingArray)
            encodingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            adapter = encodingAdapter
            setSelection(data.encoding.ordinal)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    data.encoding = ContentEncoding.fromOrdinal(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        }
    }

    override fun onItemDismiss(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val i = items.removeAt(fromPosition)
        items.add(toPosition, i)
        notifyItemMoved(fromPosition, toPosition)
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val dragHandle = view.drag_handle!!
        val imageDelete = view.delete_icmp!!
        val sizeEdit = view.icmp_edit!!
        val countEdit = view.icmp_edit_count!!
        val contentEdit = view.icmp_content_edit!!
        val encodingSpinner = view.encoding_spinner!!
    }

}