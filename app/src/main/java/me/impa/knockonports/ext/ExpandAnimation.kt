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

package me.impa.knockonports.ext

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout

class ExpandAnimation(val layout: View, val startWeight: Float, endWeight: Float): Animation() {

    private val deltaWeight = endWeight - startWeight

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        val lp = layout.layoutParams as LinearLayout.LayoutParams?
        lp?.weight = startWeight + deltaWeight * interpolatedTime
        layout.layoutParams = lp
    }

    override fun willChangeBounds(): Boolean = true

}

fun View.expandTo(endWeight: Float, duration: Long) {

    val lp = this.layoutParams as LinearLayout.LayoutParams? ?: return

    if (lp.weight == endWeight)
        return

    val anim = ExpandAnimation(this, lp.weight, endWeight)

    anim.duration = duration

    this.startAnimation(anim)
}