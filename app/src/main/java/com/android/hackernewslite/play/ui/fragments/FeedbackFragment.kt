package com.android.hackernewslite.play.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.android.hackernewslite.play.R
import com.android.hackernewslite.play.ui.SendMail
import kotlinx.android.synthetic.main.fragment_submit_feedback.*

class FeedbackFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_submit_feedback, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editTextTextMultiLine.requestFocus()
        getDialog()?.getWindow()?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
        )


        editTextTextMultiLine.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(count>0){
                    button.isEnabled = true
                    button.background.alpha= 100
                } else{
                    button.isEnabled = false
                    button.background.alpha= 50
                }
            }

        })


        button.setOnClickListener {
            val body = editTextTextMultiLine.text.toString()
            val sendMail = SendMail(context, body)
            sendMail.execute()
            dismiss()
        }
    }
}