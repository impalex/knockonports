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
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.PopupMenu
import kotlinx.android.synthetic.main.sequence_element.view.*
import me.impa.knockonports.R
import me.impa.knockonports.database.entity.Sequence

class SequenceView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    init {
        LayoutInflater.from(context).inflate(R.layout.sequence_element, this, true)
    }

    fun bind(sequence: Sequence,
             onAction: ((actionId: Int, model: Sequence) -> Unit)? = null,
             onClick: ((model: Sequence) -> Unit)? = null) {
        sequence_element_root.setOnClickListener {
            onClick?.invoke(sequence)
        }
        sequence_name.text = if (sequence.name.isNullOrBlank()) { context.getString(R.string.untitled_sequence) } else { sequence.name }

        sequence_element_root.isSelected = sequence.selected

        sequence_more_image.setOnClickListener{ _ ->
            val popupMenu = PopupMenu(context, sequence_more_image)
            popupMenu.menuInflater.inflate(R.menu.menu_seq_element, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {
                onAction?.invoke(it.itemId, sequence)
                true
            }
            popupMenu.show()
        }
    }
}