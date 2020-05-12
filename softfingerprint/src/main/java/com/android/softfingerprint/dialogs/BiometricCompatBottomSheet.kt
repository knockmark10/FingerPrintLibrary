package com.android.softfingerprint.dialogs

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.softfingerprint.R
import com.android.softfingerprint.entities.DialogOptions
import kotlinx.android.synthetic.main.bottom_sheet_biometric.*

class BiometricCompatBottomSheet : BottomSheetDialogFragment() {

    private var dialogOptions: DialogOptions = DialogOptions()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        LayoutInflater.from(context)
            .inflate(R.layout.bottom_sheet_biometric, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        this.setupView()

    }

    fun setData(dialogOptions: DialogOptions) {
        this.dialogOptions = dialogOptions
    }

    private fun setupView() {
        this.setClickListener()
        this.biometric_title.text = this.dialogOptions.title
        this.biometric_subtitle.text = this.dialogOptions.subtitle
        this.biometric_description.text = this.dialogOptions.description
        this.biometric_cancel_btn.text = this.dialogOptions.cancel
    }

    private fun setClickListener() {
        this.biometric_cancel_btn.setOnClickListener { this.dismiss() }
    }

}