package sp.android.hackernewslite.play.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_submit_feedback.*
import sp.android.hackernewslite.play.R
import sp.android.hackernewslite.play.email.SendMail

/*
* Fragment responsible for prompting the user for feedback and send an email
* with the user feedback after user submits.
* */
class FeedbackFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_submit_feedback, container)
        view?.setBackgroundColor(resources.getColor(R.color.backgroundColor, null))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editTextTextMultiLine.requestFocus()
        getDialog()?.getWindow()
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        editTextTextMultiLine.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 0) {
                    button.isEnabled = true
                    button.alpha = 1f
                } else {
                    button.isEnabled = false
                    button.alpha = 0.5f
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