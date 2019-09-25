package com.terricom.mytype.tools

import android.text.Editable
import android.text.TextWatcher

class FoodieMask : TextWatcher {

    private var updatedText: String? = null
    private var editing: Boolean = false


    override fun beforeTextChanged(
        charSequence: CharSequence,
        start: Int,
        before: Int,
        count: Int
    ) {

    }

    override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
        if (text.toString() == updatedText || editing) return

        var digits = text.toString().replace("\\D".toRegex(), "")
        val length = digits.length

        if (length <= MIN_LENGTH) {
            updatedText = digits
            return
        }

        if (length > MAX_LENGTH) {
            digits = digits.substring(0, MAX_LENGTH)
        }

        if (length == 2) {
            val month = digits.substring(0, 1)
            val day = digits.substring(1)

            updatedText = String.format("%s.%s", month, day)
        } else {
            val year = digits.substring(0 )

            updatedText = String.format("%s.0", year)
        }
    }

    override fun afterTextChanged(editable: Editable) {

        if (editing) return

        editing = true

        editable.clear()
        editable.insert(0, updatedText)

        editing = false
    }

    companion object {

        private val MAX_LENGTH = 2
        private val MIN_LENGTH = 1
    }

}