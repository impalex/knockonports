/*
 * Copyright (c) 2019 Alexander Yaburov
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
import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import me.impa.knockonports.R
import me.impa.knockonports.data.ContentEncoding
import me.impa.knockonports.data.SequenceStepType
import me.impa.knockonports.databinding.StepElementBinding
import me.impa.knockonports.ext.ItemTouchHelperAdapter
import me.impa.knockonports.ext.afterTextChanged
import me.impa.knockonports.ext.validate
import me.impa.knockonports.json.SequenceStep
import me.impa.knockonports.util.HintManager

class SequenceStepsAdapter(val context: Context): RecyclerView.Adapter<SequenceStepsAdapter.ViewHolder>(), ItemTouchHelperAdapter {
    override var onStartDrag: ((RecyclerView.ViewHolder) -> Unit)? = null

    var items: MutableList<SequenceStep> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun addItem(item: SequenceStep) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(StepElementBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    fun ViewHolder.selectedItem(): SequenceStep? = try {
        items[this.absoluteAdapterPosition]
    } catch (_: ArrayIndexOutOfBoundsException) {
        null
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val step = holder.selectedItem() ?: return
        holder.dragHandle.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) onStartDrag?.invoke(holder)
            return@setOnTouchListener false
        }
        holder.typeSpinner.run {
            val stepTypeAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, arrayOf(
                    context.getString(R.string.udp),
                    context.getString(R.string.tcp),
                    context.getString(R.string.icmp)
            ))
            stepTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            adapter = stepTypeAdapter
            setSelection(step.type?.ordinal ?: 0)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                    val type = SequenceStepType.fromOrdinal(pos)
                    val selectedItem = holder.selectedItem() ?: return
                    if (selectedItem.type != type) {
                        selectedItem.type = type
                        notifyItemChanged(holder.absoluteAdapterPosition)
                        if (type == SequenceStepType.ICMP)
                            HintManager.showHint(context, HintManager.Hint.CHECK_ICMP_SIZE)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }
        when(step.type) {
            SequenceStepType.ICMP -> {
                holder.portWrapper.visibility = View.INVISIBLE
                holder.icmpWrapper.visibility = View.VISIBLE
                holder.stepDataButton.visibility = if (holder.stepDataButton.isEnabled) View.VISIBLE else View.GONE
            }
            SequenceStepType.TCP -> {
                holder.portWrapper.visibility = View.VISIBLE
                holder.icmpWrapper.visibility = View.INVISIBLE
                holder.stepDataButton.visibility = View.GONE
            }
            SequenceStepType.UDP, null -> {
                holder.portWrapper.visibility = View.VISIBLE
                holder.stepDataButton.visibility = if (holder.stepDataButton.isEnabled) View.VISIBLE else View.GONE
                holder.icmpWrapper.visibility = View.INVISIBLE
            }
        }

        if (holder.stepDataButton.isEnabled) {
            holder.stepDataButton.isChecked = step.isExpanded
            holder.stepDataButton.setOnCheckedChangeListener { _, isChecked ->
                run {
                    val selectedItem = holder.selectedItem() ?: return@run
                    if (selectedItem.isExpanded != isChecked) {
                        selectedItem.isExpanded = isChecked
                        Handler(Looper.getMainLooper()).post {
                            notifyItemChanged(holder.absoluteAdapterPosition)
                        }
                    }
                }
            }
        }

        holder.dataEdit.visibility = if ((step.isExpanded || !holder.stepDataButton.isEnabled) && step.type != SequenceStepType.TCP) View.VISIBLE else View.GONE

        holder.dataEdit.setText(step.content)
        holder.dataEdit.setEncoding(step.encoding ?: ContentEncoding.RAW)
        holder.portEdit.setText(step.port?.toString())
        holder.icmpCountEdit.setText(step.icmpCount?.toString())
        holder.icmpSizeEdit.setText(step.icmpSize?.toString())

        holder.dataEdit.onEncodingSelected = {
            holder.selectedItem()?.encoding = it
        }
        holder.dataEdit.onTextChanged = {
            holder.selectedItem()?.content = it
        }
        holder.portEdit.afterTextChanged {
            holder.selectedItem()?.port = it.toIntOrNull()
        }
        if (step.type != SequenceStepType.ICMP) {
            holder.portEdit.validate {
                when (it.toIntOrNull()) {
                    null -> context.getString(R.string.error_empty_port)
                    in 1..65535 -> null
                    else -> context.getString(R.string.error_invalid_port)
                }
            }
        } else holder.portEdit.error = null
        holder.icmpSizeEdit.afterTextChanged {
            holder.selectedItem()?.icmpSize = it.toIntOrNull()
        }
        step.onIcmpSizeOffsetChanged = if (step.type == SequenceStepType.ICMP) { _, _ ->
            notifyItemChanged(holder.absoluteAdapterPosition)
        } else null

        holder.icmpCountEdit.afterTextChanged {
            holder.selectedItem()?.icmpCount = it.toIntOrNull()
        }
    }

    override fun onItemDismiss(position: Int) {
        (context as Activity).currentFocus?.clearFocus() // prevents IllegalArgumentException@android.view.ViewGroup.offsetRectBetweenParentAndChild
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val i = items.removeAt(fromPosition)
        items.add(toPosition, i)
        notifyItemMoved(fromPosition, toPosition)
    }

    class ViewHolder(binding: StepElementBinding) : RecyclerView.ViewHolder(binding.root) {
        val typeSpinner = binding.stepTypeSpinner
        val dragHandle = binding.dragHandle
        val stepDataButton = binding.stepData
        val icmpWrapper = binding.icmpConfigWrapper
        val icmpCountEdit = binding.icmpCountEdit
        val icmpSizeEdit = binding.icmpSizeEdit
        val portWrapper = binding.portWrapper
        val portEdit = binding.portEdit
        val dataEdit = binding.dataEdit
    }
}