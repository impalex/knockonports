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

package me.impa.knockonports.component

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import me.impa.knockonports.R
import me.impa.knockonports.data.ContentEncoding
import me.impa.knockonports.ext.afterTextChanged

class DataEditor @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    var onTextChanged: ((String) -> Unit)? = null
    var onEncodingSelected: ((ContentEncoding) -> Unit)? = null

    private val typeSpinner by lazy { findViewById<Spinner>(R.id.encoding_spinner) }
    private val dataEdit by lazy { findViewById<TextInputEditText>(R.id.data_content_edit) }
    private val encodingArray = arrayOf(context.getString(R.string.encoding_raw),
            context.getString(R.string.encoding_base64), context.getString(R.string.encoding_hex))

    init {
        LayoutInflater.from(context).inflate(R.layout.component_data, this, true)
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.DataEditor, 0, 0)
            findViewById<TextInputLayout>(R.id.data_content_wrapper).hint =
                    resources.getText(typedArray.getResourceId(R.styleable.DataEditor_text_hint, R.string.data_editor_default_hint))
            typedArray.recycle()
        }

    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        dataEdit.afterTextChanged {
            onTextChanged?.invoke(it)
        }
        val encodingAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, encodingArray)
        encodingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = encodingAdapter
        typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                onEncodingSelected?.invoke(ContentEncoding.fromOrdinal(position))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    fun setText(text: String?) {
        dataEdit.setText(text)
    }

    fun setEncoding(encoding: ContentEncoding) {
        typeSpinner.setSelection(encoding.ordinal)
    }

}