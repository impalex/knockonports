/*
 * Copyright (c) 2020 Alexander Yaburov
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
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.log_element.view.*
import me.impa.knockonports.R
import me.impa.knockonports.data.EventType
import me.impa.knockonports.database.entity.LogEntry
import java.lang.IndexOutOfBoundsException
import java.text.DateFormat
import java.util.*

class LogEntryAdapter(val context: Context): PagedListAdapter<LogEntry, LogEntryAdapter.ViewHolder>(DiffCallback) {

    // ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.step_element, parent, false))
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.log_element, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = try {
            getItem(position)
        } catch (_: IndexOutOfBoundsException) {
            null
        } ?: return
        val date = Date(item.date ?: 0)
        holder.logDate.text = DateFormat.getDateInstance().format(date)
        holder.logTime.text = DateFormat.getTimeInstance().format(date)
        holder.logEntry.text = context.resources.getString(
                (item.event ?: EventType.UNKNOWN).resourceId,
                *((item.data ?: listOf<String?>()).toTypedArray()))
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val logDate = view.log_date!!
        val logTime = view.log_time!!
        val logEntry = view.log_entry!!
    }

    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<LogEntry>() {
            override fun areItemsTheSame(oldItem: LogEntry, newItem: LogEntry): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: LogEntry, newItem: LogEntry): Boolean = oldItem == newItem
        }
    }

}