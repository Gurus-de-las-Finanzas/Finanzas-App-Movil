package com.example.finanzas.shared

import android.content.Context
import android.content.Intent
import android.util.DisplayMetrics
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.example.finanzas.clients.models.Client
import com.example.finanzas.payments.LoanProperties
import com.example.finanzas.payments.models.PaymentPlan
import com.example.finanzas.payments.models.Period
import com.example.finanzas.payments.models.SavePaymentPlanResource
import com.example.finanzas.payments.models.SavePeriodResource
import com.example.finanzas.payments.enums.Coin
import com.example.finanzas.payments.enums.GracePeriod
import com.example.finanzas.payments.enums.LienTypes
import com.example.finanzas.payments.enums.TypeRate
import java.util.Locale
import kotlin.math.pow
import kotlin.math.roundToInt
import com.example.finanzas.shared.SharedMethods.showShortToast
import com.example.finanzas.payments.enums.LoanReason

object ExtensionMethods {

    fun Double.round(decimals: Int): Double {
        val power = 10.0.pow(decimals)
        return (this * power).roundToInt() / power
    }

    fun Double.toPercentage() = this * 100

    fun Double.calculateFee(rate: Double, maxPeriod: Int, currentPeriod: Int): Double {
        val plusRate = 1 + rate
        val pow = maxPeriod - currentPeriod + 1
        return this * (((rate) * plusRate.pow(pow))/(plusRate.pow(pow) - 1))
    }

    fun Coin.toChar() = name.first()
    fun GracePeriod.toChar() = name.first()
    fun TypeRate.capitalize() = name.lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
    fun TypeRate.toChar() = name.first()
    fun Int.toDP(context: Context): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        return (this / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    fun Int.isEven() = this % 2 == 0

    fun Coin.exchangeRate(): Double {
        if(this == Coin.SOLES)
            return 3.61
        if(this == Coin.DOLLAR)
            return 0.27701
        return 0.0
    }

    fun Double.convertCoin(to: Coin) = this * to.exchangeRate()
    fun Double.getGoodPayerBonus(coin: Coin, isSustainable: Boolean = false) = LoanProperties.GoodPayerBonus.getBonus(this, coin, isSustainable)
    fun Int.getGoodPayerBonus(coin: Coin, isSustainable: Boolean = false) = toDouble().getGoodPayerBonus(coin, isSustainable)
    fun AppCompatActivity.showShortToast(@StringRes stringId: Int) = showShortToast(this, stringId)
    fun AppCompatActivity.showShortToast(charSequence: CharSequence) = showShortToast(this, charSequence)
    fun EditText.isNullOrEmpty() = text.isNullOrEmpty()
    fun EditText.toTextString() = text.toString()
    fun Int.convertCoin(to: Coin) = (this * to.exchangeRate()).roundToInt()

    fun LoanReason.isNone() = isWhat(LoanReason.NONE)
    fun LoanReason.isWhat(reason: LoanReason) = this == reason
    fun GracePeriod.isNone() = isWhat(GracePeriod.NONE)
    fun GracePeriod.isWhat(period: GracePeriod) = this == period
    fun TypeRate.isNone() = isWhat(TypeRate.NONE)
    fun TypeRate.isWhat(rate: TypeRate) = this == rate
    fun Coin.isNone() = isWhat(Coin.NONE)
    fun Coin.isWhat(coin: Coin) = this == coin

    fun LienTypes.isWhat(lienType: LienTypes) = this == lienType
    fun LienTypes.isNone() = isWhat(LienTypes.NONE)

    fun LienTypes.getRate() =
        if (isWhat(LienTypes.INDIVIDUAL)) 0.00044
        else if(isWhat(LienTypes.SHARED)) 0.00079
        else if(isWhat(LienTypes.INDIVIDUAL_WITH_RETURN)) 0.00053
        else if(isWhat(LienTypes.SHARED_WITH_RETURN)) 0.00096
        else 0.0
    fun LienTypes.capitalize() = name.lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }

    private fun String.isLienType(lienType: String) = lienType == this

    fun String.getLienType(): LienTypes =
        if(isLienType(LoanProperties.LienTypes.individual))
            LienTypes.INDIVIDUAL
        else if(isLienType(LoanProperties.LienTypes.shared))
            LienTypes.SHARED
        else if(isLienType(LoanProperties.LienTypes.endorsed))
            LienTypes.ENDORSED
        else if(isLienType(LoanProperties.LienTypes.individualWithReturns))
            LienTypes.INDIVIDUAL_WITH_RETURN
        else if(isLienType(LoanProperties.LienTypes.sharedWithReturns))
            LienTypes.SHARED_WITH_RETURN
        else LienTypes.NONE
    fun Double.convertEffectiveRate(days: Int, maxDays: Int = 360): Double = (1 + this).pow(days / maxDays.toDouble()) - 1

    fun Double.getLoanRate(coin: Coin) = LoanProperties.getRate(this, coin)

    fun String.putPercent() = "$this%"

    fun Client.firstName() = name.split(" ").first()
    fun AppCompatActivity.startActivityAndClean(cls: Class<*>) = startActivity(
        Intent(this, cls).addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))

    fun AppCompatActivity.startActivityAndFinish(cls: Class<*>) = startActivity(Intent(this, cls)).also { finish() }
    fun Period.toSavePeriod() = SavePeriodResource(
        numberPeriod =  numberPeriod,
        initialBalance =  initialBalance,
        interest = interest, amortization =  amortization,
        fee =  fee,
        finalBalance = finalBalance,
        lienInsurance = lienInsurance,
        propertyInsurance = propertyInsurance,
        scheduleId = schedule.id
    )
    fun PaymentPlan.toSavePaymentPlan() = SavePaymentPlanResource(
        coin = coin,
        periods = periods,
        typeRate = typeRate,
        interestRate = interestRate, propertyCost = propertyCost,
        graceMonths = graceMonths,
        initialFeePercent = initialFeePercent, gracePeriod = gracePeriod,
        loan = loan,
        goodPayerBonus = goodPayerBonus,
        miViviendaBonus = miViviendaBonus,
        modality = modality,
        name = name,
        date = date,
        typePeriod = typePeriod,
        clientId = client.id
    )

    fun Char.getCoin() = if(this == Coin.DOLLAR.toChar()) "Dolares" else "Soles"
    fun Char.toCoin() = if(this == Coin.DOLLAR.toChar()) Coin.DOLLAR else if(this == Coin.SOLES.toChar()) Coin.SOLES else Coin.NONE
    fun Char.getRate() = if (this == TypeRate.EFFECTIVE.toChar()) "Efectiva" else "Nominal"

    fun Char.getGracePeriod() = if(this == GracePeriod.PARTIAL.toChar()) "Parcial" else "Total"
    fun Char?.getGracePeriod() = this?.getGracePeriod() ?: "Ninguna"

}