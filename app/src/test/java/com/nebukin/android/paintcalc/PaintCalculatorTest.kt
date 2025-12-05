package com.nebukin.android.paintcalc

import org.junit.Assert.*
import org.junit.Test

/**
 * 塗料計算ロジックのユニットテスト
 * 実際の塗料配合に使用するため、計算精度を厳密に検証
 */
class PaintCalculatorTest {

    companion object {
        // 許容誤差 (小数点以下3桁まで)
        private const val DELTA = 0.001
    }

    // ========================================
    // 基本的な計算パターン
    // ========================================

    @Test
    fun `基本パターン - 2液型塗料 主液2対硬化剤1 希釈なし 1kg`() {
        // 2:1の配合比で1kgを作る場合
        val result = PaintCalculator.calculate(
            mainRatio = 2.0,
            hardenerRatio = 1.0,
            dilutionRate = 0.0,
            totalAmount = 1.0
        )

        assertNotNull(result)
        assertEquals(0.667, result!!.mainAmount, DELTA)    // 2/3 kg
        assertEquals(0.333, result.hardenerAmount, DELTA)  // 1/3 kg
        assertEquals(0.0, result.dilutionAmount, DELTA)    // 希釈なし

        // 合計が作成量と一致することを確認
        val total = result.mainAmount + result.hardenerAmount + result.dilutionAmount
        assertEquals(1.0, total, DELTA)
    }

    @Test
    fun `基本パターン - 主液3対硬化剤1 希釈なし 2kg`() {
        val result = PaintCalculator.calculate(
            mainRatio = 3.0,
            hardenerRatio = 1.0,
            dilutionRate = 0.0,
            totalAmount = 2.0
        )

        assertNotNull(result)
        assertEquals(1.5, result!!.mainAmount, DELTA)      // 3/4 * 2 = 1.5 kg
        assertEquals(0.5, result.hardenerAmount, DELTA)    // 1/4 * 2 = 0.5 kg
        assertEquals(0.0, result.dilutionAmount, DELTA)

        val total = result.mainAmount + result.hardenerAmount + result.dilutionAmount
        assertEquals(2.0, total, DELTA)
    }

    @Test
    fun `基本パターン - 主液1対硬化剤1 (等量) 希釈なし 1kg`() {
        val result = PaintCalculator.calculate(
            mainRatio = 1.0,
            hardenerRatio = 1.0,
            dilutionRate = 0.0,
            totalAmount = 1.0
        )

        assertNotNull(result)
        assertEquals(0.5, result!!.mainAmount, DELTA)
        assertEquals(0.5, result.hardenerAmount, DELTA)
        assertEquals(0.0, result.dilutionAmount, DELTA)

        val total = result.mainAmount + result.hardenerAmount + result.dilutionAmount
        assertEquals(1.0, total, DELTA)
    }

    // ========================================
    // 希釈ありパターン
    // ========================================

    @Test
    fun `希釈あり - 主液2対硬化剤1 希釈10% 1kg`() {
        // 1kgの塗料を作る、うち10%が希釈剤
        val result = PaintCalculator.calculate(
            mainRatio = 2.0,
            hardenerRatio = 1.0,
            dilutionRate = 10.0,
            totalAmount = 1.0
        )

        assertNotNull(result)
        // 希釈前の量 = 1.0 / 1.1 ≒ 0.909 kg
        // 主液 = 0.909 * 2/3 ≒ 0.606 kg
        // 硬化剤 = 0.909 * 1/3 ≒ 0.303 kg
        // 希釈剤 = 1.0 - 0.606 - 0.303 ≒ 0.091 kg
        assertEquals(0.606, result!!.mainAmount, DELTA)
        assertEquals(0.303, result.hardenerAmount, DELTA)
        assertEquals(0.091, result.dilutionAmount, DELTA)

        val total = result.mainAmount + result.hardenerAmount + result.dilutionAmount
        assertEquals(1.0, total, DELTA)
    }

    @Test
    fun `希釈あり - 主液3対硬化剤1 希釈20% 5kg`() {
        val result = PaintCalculator.calculate(
            mainRatio = 3.0,
            hardenerRatio = 1.0,
            dilutionRate = 20.0,
            totalAmount = 5.0
        )

        assertNotNull(result)
        // 希釈前の量 = 5.0 / 1.2 ≒ 4.167 kg
        // 主液 = 4.167 * 3/4 ≒ 3.125 kg
        // 硬化剤 = 4.167 * 1/4 ≒ 1.042 kg
        // 希釈剤 = 5.0 - 3.125 - 1.042 ≒ 0.833 kg
        assertEquals(3.125, result!!.mainAmount, DELTA)
        assertEquals(1.042, result.hardenerAmount, DELTA)
        assertEquals(0.833, result.dilutionAmount, DELTA)

        val total = result.mainAmount + result.hardenerAmount + result.dilutionAmount
        assertEquals(5.0, total, DELTA)
    }

    @Test
    fun `希釈あり - 主液4対硬化剤1 希釈30% 10kg`() {
        val result = PaintCalculator.calculate(
            mainRatio = 4.0,
            hardenerRatio = 1.0,
            dilutionRate = 30.0,
            totalAmount = 10.0
        )

        assertNotNull(result)
        // 希釈前の量 = 10.0 / 1.3 ≒ 7.692 kg
        // 主液 = 7.692 * 4/5 ≒ 6.154 kg
        // 硬化剤 = 7.692 * 1/5 ≒ 1.538 kg
        // 希釈剤 = 10.0 - 6.154 - 1.538 ≒ 2.308 kg
        assertEquals(6.154, result!!.mainAmount, DELTA)
        assertEquals(1.538, result.hardenerAmount, DELTA)
        assertEquals(2.308, result.dilutionAmount, DELTA)

        val total = result.mainAmount + result.hardenerAmount + result.dilutionAmount
        assertEquals(10.0, total, DELTA)
    }

    @Test
    fun `希釈あり - 希釈50% (大量希釈) 2kg`() {
        val result = PaintCalculator.calculate(
            mainRatio = 2.0,
            hardenerRatio = 1.0,
            dilutionRate = 50.0,
            totalAmount = 2.0
        )

        assertNotNull(result)
        // 希釈前の量 = 2.0 / 1.5 ≒ 1.333 kg
        // 主液 = 1.333 * 2/3 ≒ 0.889 kg
        // 硬化剤 = 1.333 * 1/3 ≒ 0.444 kg
        // 希釈剤 = 2.0 - 0.889 - 0.444 ≒ 0.667 kg
        assertEquals(0.889, result!!.mainAmount, DELTA)
        assertEquals(0.444, result.hardenerAmount, DELTA)
        assertEquals(0.667, result.dilutionAmount, DELTA)

        val total = result.mainAmount + result.hardenerAmount + result.dilutionAmount
        assertEquals(2.0, total, DELTA)
    }

    // ========================================
    // 実務で使いそうな具体的な配合パターン
    // ========================================

    @Test
    fun `実務パターン - エポキシ塗料 主液2対硬化剤1 希釈5% 3kg`() {
        val result = PaintCalculator.calculate(
            mainRatio = 2.0,
            hardenerRatio = 1.0,
            dilutionRate = 5.0,
            totalAmount = 3.0
        )

        assertNotNull(result)
        // 希釈前の量 = 3.0 / 1.05 ≒ 2.857 kg
        // 主液 = 2.857 * 2/3 ≒ 1.905 kg
        // 硬化剤 = 2.857 * 1/3 ≒ 0.952 kg
        // 希釈剤 = 3.0 - 1.905 - 0.952 ≒ 0.143 kg
        assertEquals(1.905, result!!.mainAmount, DELTA)
        assertEquals(0.952, result.hardenerAmount, DELTA)
        assertEquals(0.143, result.dilutionAmount, DELTA)

        val total = result.mainAmount + result.hardenerAmount + result.dilutionAmount
        assertEquals(3.0, total, DELTA)
    }

    @Test
    fun `実務パターン - ウレタン塗料 主液5対硬化剤1 希釈15% 4kg`() {
        val result = PaintCalculator.calculate(
            mainRatio = 5.0,
            hardenerRatio = 1.0,
            dilutionRate = 15.0,
            totalAmount = 4.0
        )

        assertNotNull(result)
        // 希釈前の量 = 4.0 / 1.15 ≒ 3.478 kg
        // 主液 = 3.478 * 5/6 ≒ 2.899 kg
        // 硬化剤 = 3.478 * 1/6 ≒ 0.580 kg
        // 希釈剤 = 4.0 - 2.899 - 0.580 ≒ 0.521 kg
        assertEquals(2.899, result!!.mainAmount, DELTA)
        assertEquals(0.580, result.hardenerAmount, DELTA)
        assertEquals(0.521, result.dilutionAmount, DELTA)

        val total = result.mainAmount + result.hardenerAmount + result.dilutionAmount
        assertEquals(4.0, total, DELTA)
    }

    @Test
    fun `実務パターン - フッ素塗料 主液10対硬化剤1 希釈10% 2kg`() {
        val result = PaintCalculator.calculate(
            mainRatio = 10.0,
            hardenerRatio = 1.0,
            dilutionRate = 10.0,
            totalAmount = 2.0
        )

        assertNotNull(result)
        // 希釈前の量 = 2.0 / 1.1 ≒ 1.818 kg
        // 主液 = 1.818 * 10/11 ≒ 1.653 kg
        // 硬化剤 = 1.818 * 1/11 ≒ 0.165 kg
        // 希釈剤 = 2.0 - 1.653 - 0.165 ≒ 0.182 kg
        assertEquals(1.653, result!!.mainAmount, DELTA)
        assertEquals(0.165, result.hardenerAmount, DELTA)
        assertEquals(0.182, result.dilutionAmount, DELTA)

        val total = result.mainAmount + result.hardenerAmount + result.dilutionAmount
        assertEquals(2.0, total, DELTA)
    }

    // ========================================
    // 小数点を含む比率パターン
    // ========================================

    @Test
    fun `小数比率 - 主液2点5対硬化剤1 希釈なし 1kg`() {
        val result = PaintCalculator.calculate(
            mainRatio = 2.5,
            hardenerRatio = 1.0,
            dilutionRate = 0.0,
            totalAmount = 1.0
        )

        assertNotNull(result)
        // 主液 = 1.0 * 2.5/3.5 ≒ 0.714 kg
        // 硬化剤 = 1.0 * 1/3.5 ≒ 0.286 kg
        assertEquals(0.714, result!!.mainAmount, DELTA)
        assertEquals(0.286, result.hardenerAmount, DELTA)
        assertEquals(0.0, result.dilutionAmount, DELTA)

        val total = result.mainAmount + result.hardenerAmount + result.dilutionAmount
        assertEquals(1.0, total, DELTA)
    }

    @Test
    fun `小数比率 - 主液3点5対硬化剤1点5 希釈12% 2kg`() {
        val result = PaintCalculator.calculate(
            mainRatio = 3.5,
            hardenerRatio = 1.5,
            dilutionRate = 12.0,
            totalAmount = 2.0
        )

        assertNotNull(result)
        // 希釈前の量 = 2.0 / 1.12 ≒ 1.786 kg
        // 主液 = 1.786 * 3.5/5 ≒ 1.250 kg
        // 硬化剤 = 1.786 * 1.5/5 ≒ 0.536 kg
        // 希釈剤 = 2.0 - 1.250 - 0.536 ≒ 0.214 kg
        assertEquals(1.250, result!!.mainAmount, DELTA)
        assertEquals(0.536, result.hardenerAmount, DELTA)
        assertEquals(0.214, result.dilutionAmount, DELTA)

        val total = result.mainAmount + result.hardenerAmount + result.dilutionAmount
        assertEquals(2.0, total, DELTA)
    }

    // ========================================
    // 境界値・エッジケース
    // ========================================

    @Test
    fun `境界値 - 非常に少量 0点1kg`() {
        val result = PaintCalculator.calculate(
            mainRatio = 2.0,
            hardenerRatio = 1.0,
            dilutionRate = 10.0,
            totalAmount = 0.1
        )

        assertNotNull(result)
        val total = result!!.mainAmount + result.hardenerAmount + result.dilutionAmount
        assertEquals(0.1, total, DELTA)
    }

    @Test
    fun `境界値 - 大量 100kg`() {
        val result = PaintCalculator.calculate(
            mainRatio = 2.0,
            hardenerRatio = 1.0,
            dilutionRate = 10.0,
            totalAmount = 100.0
        )

        assertNotNull(result)
        // 希釈前の量 = 100.0 / 1.1 ≒ 90.909 kg
        assertEquals(60.606, result!!.mainAmount, DELTA)
        assertEquals(30.303, result.hardenerAmount, DELTA)
        assertEquals(9.091, result.dilutionAmount, DELTA)

        val total = result.mainAmount + result.hardenerAmount + result.dilutionAmount
        assertEquals(100.0, total, DELTA)
    }

    @Test
    fun `境界値 - 希釈率0%`() {
        val result = PaintCalculator.calculate(
            mainRatio = 2.0,
            hardenerRatio = 1.0,
            dilutionRate = 0.0,
            totalAmount = 1.0
        )

        assertNotNull(result)
        assertEquals(0.0, result!!.dilutionAmount, DELTA)
    }

    @Test
    fun `境界値 - 希釈率null (0%として扱う)`() {
        val result = PaintCalculator.calculate(
            mainRatio = 2.0,
            hardenerRatio = 1.0,
            dilutionRate = null,
            totalAmount = 1.0
        )

        assertNotNull(result)
        assertEquals(0.667, result!!.mainAmount, DELTA)
        assertEquals(0.333, result.hardenerAmount, DELTA)
        assertEquals(0.0, result.dilutionAmount, DELTA)
    }

    @Test
    fun `境界値 - 作成量0kg`() {
        val result = PaintCalculator.calculate(
            mainRatio = 2.0,
            hardenerRatio = 1.0,
            dilutionRate = 10.0,
            totalAmount = 0.0
        )

        assertNotNull(result)
        assertEquals(0.0, result!!.mainAmount, DELTA)
        assertEquals(0.0, result.hardenerAmount, DELTA)
        assertEquals(0.0, result.dilutionAmount, DELTA)
    }

    // ========================================
    // 無効な入力パターン
    // ========================================

    @Test
    fun `無効入力 - 主液比率がnull`() {
        val result = PaintCalculator.calculate(
            mainRatio = null,
            hardenerRatio = 1.0,
            dilutionRate = 10.0,
            totalAmount = 1.0
        )

        assertNull(result)
    }

    @Test
    fun `無効入力 - 硬化剤比率がnull`() {
        val result = PaintCalculator.calculate(
            mainRatio = 2.0,
            hardenerRatio = null,
            dilutionRate = 10.0,
            totalAmount = 1.0
        )

        assertNull(result)
    }

    @Test
    fun `無効入力 - 作成量がnull`() {
        val result = PaintCalculator.calculate(
            mainRatio = 2.0,
            hardenerRatio = 1.0,
            dilutionRate = 10.0,
            totalAmount = null
        )

        assertNull(result)
    }

    @Test
    fun `無効入力 - 比率の合計が0`() {
        val result = PaintCalculator.calculate(
            mainRatio = 0.0,
            hardenerRatio = 0.0,
            dilutionRate = 10.0,
            totalAmount = 1.0
        )

        assertNull(result)
    }

    @Test
    fun `無効入力 - 作成量がマイナス`() {
        val result = PaintCalculator.calculate(
            mainRatio = 2.0,
            hardenerRatio = 1.0,
            dilutionRate = 10.0,
            totalAmount = -1.0
        )

        assertNull(result)
    }

    // ========================================
    // 手計算で検算しやすいパターン
    // ========================================

    @Test
    fun `検算用 - 主液4対硬化剤1 希釈なし 5kg`() {
        // 手計算: 主液 = 5 * 4/5 = 4kg, 硬化剤 = 5 * 1/5 = 1kg
        val result = PaintCalculator.calculate(
            mainRatio = 4.0,
            hardenerRatio = 1.0,
            dilutionRate = 0.0,
            totalAmount = 5.0
        )

        assertNotNull(result)
        assertEquals(4.0, result!!.mainAmount, DELTA)
        assertEquals(1.0, result.hardenerAmount, DELTA)
        assertEquals(0.0, result.dilutionAmount, DELTA)
    }

    @Test
    fun `検算用 - 主液9対硬化剤1 希釈なし 10kg`() {
        // 手計算: 主液 = 10 * 9/10 = 9kg, 硬化剤 = 10 * 1/10 = 1kg
        val result = PaintCalculator.calculate(
            mainRatio = 9.0,
            hardenerRatio = 1.0,
            dilutionRate = 0.0,
            totalAmount = 10.0
        )

        assertNotNull(result)
        assertEquals(9.0, result!!.mainAmount, DELTA)
        assertEquals(1.0, result.hardenerAmount, DELTA)
        assertEquals(0.0, result.dilutionAmount, DELTA)
    }

    @Test
    fun `検算用 - 主液2対硬化剤1 希釈100% 2kg`() {
        // 希釈前の量 = 2.0 / 2.0 = 1.0 kg
        // 主液 = 1.0 * 2/3 ≒ 0.667 kg
        // 硬化剤 = 1.0 * 1/3 ≒ 0.333 kg
        // 希釈剤 = 2.0 - 0.667 - 0.333 = 1.0 kg (全体の半分が希釈剤)
        val result = PaintCalculator.calculate(
            mainRatio = 2.0,
            hardenerRatio = 1.0,
            dilutionRate = 100.0,
            totalAmount = 2.0
        )

        assertNotNull(result)
        assertEquals(0.667, result!!.mainAmount, DELTA)
        assertEquals(0.333, result.hardenerAmount, DELTA)
        assertEquals(1.0, result.dilutionAmount, DELTA)

        val total = result.mainAmount + result.hardenerAmount + result.dilutionAmount
        assertEquals(2.0, total, DELTA)
    }

    // ========================================
    // 合計値の整合性チェック
    // ========================================

    // ========================================
    // 少量・小数点作成量パターン (現場で実際に使う量)
    // ========================================

    @Test
    fun `少量パターン - 0点15kg 主液2対硬化剤1 希釈なし`() {
        val result = PaintCalculator.calculate(
            mainRatio = 2.0,
            hardenerRatio = 1.0,
            dilutionRate = 0.0,
            totalAmount = 0.15
        )

        assertNotNull(result)
        // 主液 = 0.15 * 2/3 = 0.1 kg
        // 硬化剤 = 0.15 * 1/3 = 0.05 kg
        assertEquals(0.1, result!!.mainAmount, DELTA)
        assertEquals(0.05, result.hardenerAmount, DELTA)
        assertEquals(0.0, result.dilutionAmount, DELTA)

        val total = result.mainAmount + result.hardenerAmount + result.dilutionAmount
        assertEquals(0.15, total, DELTA)
    }

    @Test
    fun `少量パターン - 0点15kg 主液2対硬化剤1 希釈10パーセント`() {
        val result = PaintCalculator.calculate(
            mainRatio = 2.0,
            hardenerRatio = 1.0,
            dilutionRate = 10.0,
            totalAmount = 0.15
        )

        assertNotNull(result)
        // 希釈前の量 = 0.15 / 1.1 ≒ 0.1364 kg
        // 主液 = 0.1364 * 2/3 ≒ 0.0909 kg
        // 硬化剤 = 0.1364 * 1/3 ≒ 0.0455 kg
        // 希釈剤 = 0.15 - 0.0909 - 0.0455 ≒ 0.0136 kg
        assertEquals(0.0909, result!!.mainAmount, DELTA)
        assertEquals(0.0455, result.hardenerAmount, DELTA)
        assertEquals(0.0136, result.dilutionAmount, DELTA)

        val total = result.mainAmount + result.hardenerAmount + result.dilutionAmount
        assertEquals(0.15, total, DELTA)
    }

    @Test
    fun `少量パターン - 0点25kg 主液3対硬化剤1 希釈5パーセント`() {
        val result = PaintCalculator.calculate(
            mainRatio = 3.0,
            hardenerRatio = 1.0,
            dilutionRate = 5.0,
            totalAmount = 0.25
        )

        assertNotNull(result)
        // 希釈前の量 = 0.25 / 1.05 ≒ 0.2381 kg
        // 主液 = 0.2381 * 3/4 ≒ 0.1786 kg
        // 硬化剤 = 0.2381 * 1/4 ≒ 0.0595 kg
        // 希釈剤 = 0.25 - 0.1786 - 0.0595 ≒ 0.0119 kg
        assertEquals(0.1786, result!!.mainAmount, DELTA)
        assertEquals(0.0595, result.hardenerAmount, DELTA)
        assertEquals(0.0119, result.dilutionAmount, DELTA)

        val total = result.mainAmount + result.hardenerAmount + result.dilutionAmount
        assertEquals(0.25, total, DELTA)
    }

    @Test
    fun `少量パターン - 0点08kg 極少量`() {
        val result = PaintCalculator.calculate(
            mainRatio = 2.0,
            hardenerRatio = 1.0,
            dilutionRate = 0.0,
            totalAmount = 0.08
        )

        assertNotNull(result)
        // 主液 = 0.08 * 2/3 ≒ 0.0533 kg
        // 硬化剤 = 0.08 * 1/3 ≒ 0.0267 kg
        assertEquals(0.0533, result!!.mainAmount, DELTA)
        assertEquals(0.0267, result.hardenerAmount, DELTA)
        assertEquals(0.0, result.dilutionAmount, DELTA)

        val total = result.mainAmount + result.hardenerAmount + result.dilutionAmount
        assertEquals(0.08, total, DELTA)
    }

    @Test
    fun `少量パターン - 0点5kg 主液4対硬化剤1 希釈15パーセント`() {
        val result = PaintCalculator.calculate(
            mainRatio = 4.0,
            hardenerRatio = 1.0,
            dilutionRate = 15.0,
            totalAmount = 0.5
        )

        assertNotNull(result)
        // 希釈前の量 = 0.5 / 1.15 ≒ 0.4348 kg
        // 主液 = 0.4348 * 4/5 ≒ 0.3478 kg
        // 硬化剤 = 0.4348 * 1/5 ≒ 0.0870 kg
        // 希釈剤 = 0.5 - 0.3478 - 0.0870 ≒ 0.0652 kg
        assertEquals(0.3478, result!!.mainAmount, DELTA)
        assertEquals(0.0870, result.hardenerAmount, DELTA)
        assertEquals(0.0652, result.dilutionAmount, DELTA)

        val total = result.mainAmount + result.hardenerAmount + result.dilutionAmount
        assertEquals(0.5, total, DELTA)
    }

    @Test
    fun `少量パターン - 0点12kg 中途半端な量`() {
        val result = PaintCalculator.calculate(
            mainRatio = 2.0,
            hardenerRatio = 1.0,
            dilutionRate = 8.0,
            totalAmount = 0.12
        )

        assertNotNull(result)
        // 希釈前の量 = 0.12 / 1.08 ≒ 0.1111 kg
        // 主液 = 0.1111 * 2/3 ≒ 0.0741 kg
        // 硬化剤 = 0.1111 * 1/3 ≒ 0.0370 kg
        // 希釈剤 = 0.12 - 0.0741 - 0.0370 ≒ 0.0089 kg
        assertEquals(0.0741, result!!.mainAmount, DELTA)
        assertEquals(0.0370, result.hardenerAmount, DELTA)
        assertEquals(0.0089, result.dilutionAmount, DELTA)

        val total = result.mainAmount + result.hardenerAmount + result.dilutionAmount
        assertEquals(0.12, total, DELTA)
    }

    @Test
    fun `少量パターン - 0点33kg 主液5対硬化剤2 希釈12パーセント`() {
        val result = PaintCalculator.calculate(
            mainRatio = 5.0,
            hardenerRatio = 2.0,
            dilutionRate = 12.0,
            totalAmount = 0.33
        )

        assertNotNull(result)
        // 希釈前の量 = 0.33 / 1.12 ≒ 0.2946 kg
        // 主液 = 0.2946 * 5/7 ≒ 0.2104 kg
        // 硬化剤 = 0.2946 * 2/7 ≒ 0.0842 kg
        // 希釈剤 = 0.33 - 0.2104 - 0.0842 ≒ 0.0354 kg
        assertEquals(0.2104, result!!.mainAmount, DELTA)
        assertEquals(0.0842, result.hardenerAmount, DELTA)
        assertEquals(0.0354, result.dilutionAmount, DELTA)

        val total = result.mainAmount + result.hardenerAmount + result.dilutionAmount
        assertEquals(0.33, total, DELTA)
    }

    @Test
    fun `少量整合性 - 様々な少量パターンで合計が一致`() {
        val testCases = listOf(
            arrayOf(2.0, 1.0, 0.0, 0.15),
            arrayOf(2.0, 1.0, 10.0, 0.15),
            arrayOf(3.0, 1.0, 5.0, 0.25),
            arrayOf(4.0, 1.0, 15.0, 0.5),
            arrayOf(2.0, 1.0, 0.0, 0.08),
            arrayOf(2.0, 1.0, 8.0, 0.12),
            arrayOf(5.0, 2.0, 12.0, 0.33),
            arrayOf(3.0, 1.0, 20.0, 0.18),
            arrayOf(2.0, 1.0, 0.0, 0.05),
            arrayOf(4.0, 1.0, 10.0, 0.22)
        )

        for (testCase in testCases) {
            val result = PaintCalculator.calculate(
                mainRatio = testCase[0],
                hardenerRatio = testCase[1],
                dilutionRate = testCase[2],
                totalAmount = testCase[3]
            )

            assertNotNull("Failed for: ${testCase.contentToString()}", result)
            val total = result!!.mainAmount + result.hardenerAmount + result.dilutionAmount
            assertEquals(
                "Total mismatch for: ${testCase.contentToString()}",
                testCase[3],
                total,
                DELTA
            )

            // 各成分が0以上であることを確認 (浮動小数点の丸め誤差を許容)
            assertTrue("mainAmount negative for: ${testCase.contentToString()}", result.mainAmount >= -DELTA)
            assertTrue("hardenerAmount negative for: ${testCase.contentToString()}", result.hardenerAmount >= -DELTA)
            assertTrue("dilutionAmount negative for: ${testCase.contentToString()}", result.dilutionAmount >= -DELTA)
        }
    }

    @Test
    fun `整合性 - 様々なパターンで合計が作成量と一致する`() {
        val testCases = listOf(
            arrayOf(2.0, 1.0, 0.0, 1.0),
            arrayOf(3.0, 1.0, 10.0, 2.0),
            arrayOf(4.0, 1.0, 20.0, 5.0),
            arrayOf(5.0, 1.0, 15.0, 3.0),
            arrayOf(1.0, 1.0, 5.0, 4.0),
            arrayOf(10.0, 1.0, 25.0, 8.0),
            arrayOf(2.5, 1.5, 12.0, 6.0),
            arrayOf(3.0, 2.0, 8.0, 10.0)
        )

        for (testCase in testCases) {
            val result = PaintCalculator.calculate(
                mainRatio = testCase[0],
                hardenerRatio = testCase[1],
                dilutionRate = testCase[2],
                totalAmount = testCase[3]
            )

            assertNotNull("Failed for: ${testCase.contentToString()}", result)
            val total = result!!.mainAmount + result.hardenerAmount + result.dilutionAmount
            assertEquals(
                "Total mismatch for: ${testCase.contentToString()}",
                testCase[3],
                total,
                DELTA
            )
        }
    }
}
