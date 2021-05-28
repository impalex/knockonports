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

package me.impa.knockonports.fragment

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import me.impa.knockonports.R
import me.impa.knockonports.databinding.FragmentAskReviewBinding
import me.impa.knockonports.util.AppPrefs
import me.impa.knockonports.util.toast

class RateAppFragment: DialogFragment() {

    private var _binding: FragmentAskReviewBinding? = null
    private val binding get() = _binding!!

    var onDismiss: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAskReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonRateNow.setOnClickListener {
            openPlayMarket(requireContext())
            dismiss()
        }

        binding.buttonRateDisable.setOnClickListener {
            AppPrefs.turnOffAskReviewDialog(requireContext())
            dismiss()
        }

        binding.buttonRateLater.setOnClickListener {
            AppPrefs.postponeReviewDialog(requireContext(), AppPrefs.POSTPONE_TIME)
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = super.onCreateDialog(savedInstanceState).apply { window?.setTitle(getString(R.string.title_ask_review)) }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        AppPrefs.postponeReviewDialog(this@RateAppFragment.requireContext(), AppPrefs.POSTPONE_TIME_CANCEL)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismiss?.invoke()
    }

    companion object {
        const val FRAGMENT_ASK_REVIEW = "FRAGMENT_ASK_REVIEW"
        private const val KNOCKS_REQUIRED = 20L

        fun openPlayMarket(context: Context) {
            try {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}")))
                AppPrefs.turnOffAskReviewDialog(context)
            } catch (_: Exception) {
                context.toast(R.string.error_play_store)
            }
        }

        fun isTimeToAskForReview(context: Context) = !AppPrefs.isAskReviewDialogTurnedOff(context)
                && AppPrefs.getKnockCount(context) >= KNOCKS_REQUIRED
                && System.currentTimeMillis() > AppPrefs.getAskReviewTime(context)
    }
}