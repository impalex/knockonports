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
import android.view.View
import android.view.ViewGroup
import com.github.stephenvinouze.advancedrecyclerview.core.adapters.RecyclerAdapter
import me.impa.knockonports.database.entity.Port

class PortAdapter(context: Context): RecyclerAdapter<Port>(context) {

    override fun onCreateItemView(parent: ViewGroup, viewType: Int): View = PortView(context)

    override fun onBindItemView(view: View, position: Int) {
        when(view) {
            is PortView -> view.bind(items[position], onDelete = {
                val idx = items.indexOf(it)
                if (idx>=0) {
                    removeItem(idx)
                }
            })
        }
    }
}