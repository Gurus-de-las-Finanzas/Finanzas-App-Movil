package com.example.finanzas.shared

import android.content.Context
import android.util.DisplayMetrics
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.example.finanzas.shared.enums.Coin
import com.example.finanzas.shared.enums.GracePeriod
import com.example.finanzas.shared.enums.TypeRate
import java.util.Locale
import kotlin.math.pow
import kotlin.math.roundToInt
import com.example.finanzas.shared.SharedMethods.showShortToast
import com.example.finanzas.shared.enums.LoanReason

object ExtensionMethods {

    fun Double.round(decimals: Int): Double {
        val power = 10.0.pow(decimals)
        return (this * power).roundToInt() / power
    }

    fun Double.toPercentage() = this * 100

    fun Double.calculateFee(rate: Double, maxPeriod: Int, currentPeriod: Int): Double {
        val plusRate = 1 + rate
        val pow = maxPeriod - currentPeriod + 1
        return this * ((rate * plusRate.pow(pow))/(plusRate.pow(pow) - 1))
    }

    fun Coin.toChar() = name.first()
    fun GracePeriod.toChar() = name.first()
    fun TypeRate.name() = name.lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }

    fun Int.toDP(context: Context): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        return (this / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    fun Int.isEven() = this % 2 == 0

    fun Coin.exchangeRate(): Double {
        if(this == Coin.SOLES)
            return 3.61
        if(this == Coin.DOLLAR)
            return 0.28
        return 0.0
    }

    fun Double.convertCoin(to: Coin) = this * to.exchangeRate()
    fun AppCompatActivity.showShortToast(@StringRes stringId: Int) = showShortToast(this, stringId)
    fun AppCompatActivity.showShortToast(charSequence: CharSequence) = showShortToast(this, charSequence)
    fun EditText.isNullOrEmpty() = text.isNullOrEmpty()
    fun EditText.toTextString() = text.toString()
    fun Int.convertCoin(to: Coin) = (this * to.exchangeRate()).roundToInt()

    fun LoanReason.isNone() = this == LoanReason.NONE
    fun GracePeriod.isNone() = this == GracePeriod.NONE
    fun TypeRate.isNone() = this == TypeRate.NONE
    fun Coin.isNone() = this == Coin.NONE
}