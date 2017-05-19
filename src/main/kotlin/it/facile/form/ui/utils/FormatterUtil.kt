package it.facile.form.ui.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.util.*


fun String.formatNumberGrouping(separator: Char): String {

    val symbols = DecimalFormatSymbols(Locale.ITALY).apply { groupingSeparator = separator }
    val decimalFormat = DecimalFormat("###,###.###", symbols)

    val stringWithoutSeparator = this.replace(separator.toString(), "")

    val number = try {
        decimalFormat.parse(stringWithoutSeparator)
    } catch (pe: ParseException) {
        return this
    }

    return decimalFormat.format(number)
}