package com.example.financeflow.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.NumberFormat
import java.util.Locale

class MoneyMask(private val editText: EditText) : TextWatcher {

    private val locale = Locale("pt", "BR")
    private val formatter = NumberFormat.getCurrencyInstance(locale)
    private var isUpdating = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (isUpdating) return

        isUpdating = true

        val digits = s.toString().replace("[^0-9]".toRegex(), "")

        val value = if (digits.isEmpty()) 0.0 else digits.toDouble() / 100

        val formatted = formatter.format(value)

        editText.setText(formatted)
        editText.setSelection(formatted.length)

        isUpdating = false
    }
}
