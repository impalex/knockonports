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
import kotlinx.android.synthetic.main.port_element.view.*
import me.impa.knockonports.R
import me.impa.knockonports.database.entity.Port
import me.impa.knockonports.ext.afterTextChanged

class PortView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): FrameLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.port_element, this, true)
    }

    fun bind(port: Port, onDelete: ((model: Port) -> Unit)? = null) {

        port_edit.afterTextChanged {
            port.number = it.toIntOrNull()
        }
        port_edit.setText(port.number?.toString())

        delete_port.setOnClickListener { onDelete?.invoke(port) }
        if (port.type != Port.PORT_TYPE_TCP && port.type != Port.PORT_TYPE_UDP) {
            port.type = Port.PORT_TYPE_UDP
        }

        protocol_toggle_group.setToggled(
                if (port.type == Port.PORT_TYPE_UDP) {
                    R.id.type_udp
                } else {
                    R.id.type_tcp
                }, true)
        protocol_toggle_group.onToggledListener = { toggle, _ ->
            port.type = if (toggle.id == R.id.type_tcp) {
                Port.PORT_TYPE_TCP
            } else {
                Port.PORT_TYPE_UDP
            }
        }
    }
}