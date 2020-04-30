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

package me.impa.knockonports.tasker.bundle

import android.content.Context
import android.os.Bundle
import com.twofortyfouram.assertion.BundleAssertions
import com.twofortyfouram.spackle.AppBuildInfo
import me.impa.knockonports.util.Logging
import me.impa.knockonports.util.error
import net.jcip.annotations.ThreadSafe
import java.lang.AssertionError

@ThreadSafe
object KnockerBundleValues: Logging {
    private const val BUNDLE_EXTRA_LONG_SEQUENCE_ID = "me.impa.knockonports.setting.sequence.extra.LONG_IDENTIFIER"

    private const val BUNDLE_EXTRA_INT_VERSION_CODE = "me.impa.knockonports.setting.sequence.extra.INT_VERSION_CODE"

    fun isBundleValid(bundle: Bundle?) =
            bundle != null && try {
                BundleAssertions.assertHasLong(bundle, BUNDLE_EXTRA_LONG_SEQUENCE_ID)
                BundleAssertions.assertHasInt(bundle, BUNDLE_EXTRA_INT_VERSION_CODE)
                BundleAssertions.assertKeyCount(bundle, 2)
                true
            } catch (e: AssertionError) {
                error("Bundle failed verification", e)
                false
            }

    fun generateBundle(context: Context, sequenceId: Long) = Bundle().apply {
        putInt(BUNDLE_EXTRA_INT_VERSION_CODE, AppBuildInfo.getVersionCode(context))
        putLong(BUNDLE_EXTRA_LONG_SEQUENCE_ID, sequenceId)
    }

    fun getSequenceId(bundle: Bundle) = bundle.getLong(BUNDLE_EXTRA_LONG_SEQUENCE_ID)

}