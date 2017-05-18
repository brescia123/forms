package it.facile.form.ui.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

object Formatter {

    fun getFormatterWithCustomSeparator(groupingSeparator: Char?): DecimalFormat {
        val otherSymbols = DecimalFormatSymbols(Locale.ITALY)
        groupingSeparator?.let { otherSymbols.groupingSeparator = it }
        return DecimalFormat("###,###.###", otherSymbols)
    }

    fun getFormattedValue(originalText: String, groupingSeparator: Char?): String {
        val decimalFormat = Formatter.getFormatterWithCustomSeparator(
                groupingSeparator = groupingSeparator)
        val unformattedValue = decimalFormat.parse(originalText.replace(groupingSeparator.toString(), "")).toString()
        return decimalFormat.format(unformattedValue.toLong())
    }
}