package it.facile.form.ui.utils

import it.facile.form.storage.FieldValue
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

fun FieldValue.Text.formatNumberGrouping(separator: Char) = text.formatNumberGrouping(separator)

fun String.getUnformattedNumberWithGrouping(separator: Char): Number? {
    val symbols = DecimalFormatSymbols(Locale.ITALY).apply { groupingSeparator = separator }
    val decimalFormat = DecimalFormat("###,###.###", symbols)

    val stringWithoutSeparator = this.replace(separator.toString(), "")

    return try {
        decimalFormat.parse(stringWithoutSeparator)
    } catch (pe: ParseException) {
        return null
    }
}

fun FieldValue.Text.getUnformattedNumberWithGrouping(separator: Char) = text.getUnformattedNumberWithGrouping(separator)
