package com.allmoviedatabase.pandastore.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

fun Double.toCurrency(): String {
    val symbols = DecimalFormatSymbols(Locale.getDefault())
    symbols.decimalSeparator = '.' // Küsürat ayracı nokta olsun

    // Eğer sayı tam sayıysa (örn: 100.0) küsürat gösterme (#)
    // Eğer küsüratlıysa en fazla 2 basamak göster (#.##)
    val pattern = if (this % 1.0 == 0.0) "#,###" else "#,###.##"

    val decimalFormat = DecimalFormat(pattern, symbols)
    return "${decimalFormat.format(this)} ₺"
}
