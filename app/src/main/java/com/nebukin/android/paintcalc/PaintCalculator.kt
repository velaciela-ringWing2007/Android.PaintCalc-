package com.nebukin.android.paintcalc

/**
 * 塗料配合計算ロジック
 */
object PaintCalculator {

    data class Result(
        val mainAmount: Double,
        val hardenerAmount: Double,
        val dilutionAmount: Double
    )

    /**
     * 塗料配合量を計算する
     *
     * @param mainRatio 主液比率
     * @param hardenerRatio 硬化剤比率
     * @param dilutionRate 希釈率 (%)
     * @param totalAmount 作成量 (kg)
     * @return 計算結果、入力が不正な場合はnull
     */
    fun calculate(
        mainRatio: Double?,
        hardenerRatio: Double?,
        dilutionRate: Double?,
        totalAmount: Double?
    ): Result? {
        if (mainRatio == null || hardenerRatio == null || totalAmount == null) {
            return null
        }

        val ratioSum = mainRatio + hardenerRatio
        if (ratioSum <= 0) {
            return null
        }

        if (totalAmount < 0) {
            return null
        }

        val effectiveDilutionRate = dilutionRate ?: 0.0

        // 希釈前の量 = 作成量 ÷ (1 + 希釈率/100)
        val preDilutionAmount = totalAmount / (1 + effectiveDilutionRate / 100)

        // 主液量 = 希釈前の量 × 主液比率 ÷ (主液比率 + 硬化剤比率)
        val mainAmount = preDilutionAmount * mainRatio / ratioSum

        // 硬化量 = 希釈前の量 × 硬化剤比率 ÷ (主液比率 + 硬化剤比率)
        val hardenerAmount = preDilutionAmount * hardenerRatio / ratioSum

        // 希釈量 = 作成量 - 主液量 - 硬化量
        val dilutionAmount = totalAmount - mainAmount - hardenerAmount

        return Result(mainAmount, hardenerAmount, dilutionAmount)
    }
}
