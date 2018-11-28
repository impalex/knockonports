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
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.port_element.view.*
import me.impa.knockonports.R
import me.impa.knockonports.data.PortType
import me.impa.knockonports.database.entity.Sequence
import me.impa.knockonports.ext.ItemTouchHelperAdapter
import me.impa.knockonports.ext.afterTextChanged
import me.impa.knockonports.json.PortData

class PortAdapter: RecyclerView.Adapter<PortAdapter.ViewHolder>(), ItemTouchHelperAdapter {

    override var onStartDrag: ((RecyclerView.ViewHolder) -> Unit)? = null

    var items: MutableList<PortData> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun addItem(item: PortData) {
        items.add(item)
        notifyItemInserted(items.size-1)
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.port_element, parent, false))

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val port = items[position]
        holder?.editPort?.run {
            afterTextChanged {
                port.value = it.toIntOrNull()
            }
            setText(port.value?.toString())
        }

        holder?.imageDelete?.setOnClickListener {
            val index = items.indexOf(port)
            if (index >= 0) {
                items.removeAt(index)
                notifyItemRemoved(index)
            }
        }
        holder?.groupProtocolType?.run {
            setToggled(if (port.type == PortType.TCP) {
                R.id.type_tcp
            } else {
                R.id.type_udp
            }, true)
            onToggledListener = { toggle, _ ->
                port.type = if (toggle.id == R.id.type_tcp) {
                    PortType.TCP
                } else {
                    PortType.UDP
                }
            }
        }

        holder?.dragHandle?.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                onStartDrag?.invoke(holder)
            }
            return@setOnTouchListener false
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val i = items.removeAt(fromPosition)
        items.add(toPosition, i)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dragHandle = view.drag_handle!!
        val editPort = view.port_edit!!
        val groupProtocolType = view.protocol_toggle_group!!
        val imageDelete = view.delete_port!!
    }
}