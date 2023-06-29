package com.example.finanzas.payments

import com.example.finanzas.shared.ExtensionMethods.convertCoin
import com.example.finanzas.shared.ExtensionMethods.isWhat
import com.example.finanzas.payments.enums.Coin

object LoanProperties {
    const val minInitialFee = 7.5
    const val maxInitialFee = 30.0
    const val minPrice = 65200
    const val maxPrice = 464200
    const val minYears = 5
    private const val rate = 0.1399
    const val propertyInsurance = 0.00029

    object GoodPayerBonus {
        private val firstRange = Triple(minPrice, 93100, 25700.0)
        private val secondRange = Triple(firstRange.second, 139400, 21400.0)
        private val thirdRange = Triple(secondRange.second, 232200, 19600.0)
        private val fourthRange = Triple(thirdRange.second, 343900, 10800.0)
        const val sustainableBonus = 5400.0


        private fun completeBonus(range: Triple<Int, Int, Double>) = sustainableBonus + range.third

        private fun isInRange(loan: Double, range: Triple<Int, Int, Double>, coin: Coin) =
            if(coin.isWhat(Coin.SOLES)) loan >= range.first && loan <= range.second
            else loan >= range.first.convertCoin(coin) && loan <= range.second.convertCoin(coin)

        private fun bonus(loan: Double, coin: Coin, isSustainable: Boolean): Double {
            if(isInRange(loan, firstRange, coin))
                return if(isSustainable) completeBonus(firstRange) else firstRange.third
            if(isInRange(loan, secondRange, coin))
                return if(isSustainable) completeBonus(secondRange) else secondRange.third
            if(isInRange(loan, thirdRange, coin))
                return if(isSustainable) completeBonus(thirdRange) else thirdRange.third
            if(isInRange(loan, fourthRange, coin))
                return if(isSustainable) completeBonus(fourthRange) else fourthRange.third
            return 0.0
        }

        fun getBonus(loan: Double, coin: Coin, isSustainable: Boolean = false): Double {
            val bonus = bonus(loan, coin, isSustainable)
            return if(coin.isWhat(Coin.SOLES)) bonus else bonus.convertCoin(Coin.DOLLAR)
        }
    }

    object LienTypes {
        const val individual = "Individual"
        const val shared = "Mancomunado"
        const val endorsed = "Endorsado"
        const val sharedWithReturns = "Mancomunado con retorno"
        const val individualWithReturns = "Individual con retorno"
    }

    object LoanRate {
        val dollarRate = Pair(0.1199, 0.1099)
    }

    fun getRate(loan: Double, coin: Coin): Double {
        if(coin.isWhat(Coin.DOLLAR)) {
            if(loan < 100000)
                return LoanRate.dollarRate.first
            return LoanRate.dollarRate.second
        }
        return rate
    }
}