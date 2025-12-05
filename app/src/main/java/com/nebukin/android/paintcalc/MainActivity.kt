package com.nebukin.android.paintcalc

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var editMainRatio: EditText
    private lateinit var editHardenerRatio: EditText
    private lateinit var editDilutionRate: EditText
    private lateinit var editTotalAmount: EditText

    private lateinit var textMainAmount: TextView
    private lateinit var textHardenerAmount: TextView
    private lateinit var textDilutionAmount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val mainView = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.main)
        val basePadding = (20 * resources.displayMetrics.density).toInt()
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                basePadding + systemBars.left,
                basePadding + systemBars.top,
                basePadding + systemBars.right,
                basePadding + systemBars.bottom
            )
            insets
        }

        initViews()
        setupTextWatchers()
    }

    private fun initViews() {
        editMainRatio = findViewById(R.id.editMainRatio)
        editHardenerRatio = findViewById(R.id.editHardenerRatio)
        editDilutionRate = findViewById(R.id.editDilutionRate)
        editTotalAmount = findViewById(R.id.editTotalAmount)

        textMainAmount = findViewById(R.id.textMainAmount)
        textHardenerAmount = findViewById(R.id.textHardenerAmount)
        textDilutionAmount = findViewById(R.id.textDilutionAmount)

        clearResults()
    }

    private fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                calculate()
            }
        }

        editMainRatio.addTextChangedListener(textWatcher)
        editHardenerRatio.addTextChangedListener(textWatcher)
        editDilutionRate.addTextChangedListener(textWatcher)
        editTotalAmount.addTextChangedListener(textWatcher)
    }

    private fun calculate() {
        val mainRatio = editMainRatio.text.toString().toDoubleOrNull()
        val hardenerRatio = editHardenerRatio.text.toString().toDoubleOrNull()
        val dilutionRate = editDilutionRate.text.toString().toDoubleOrNull() ?: 0.0
        val totalAmount = editTotalAmount.text.toString().toDoubleOrNull()

        if (mainRatio == null || hardenerRatio == null || totalAmount == null) {
            clearResults()
            return
        }

        val ratioSum = mainRatio + hardenerRatio
        if (ratioSum <= 0) {
            clearResults()
            return
        }

        // 計算ロジック
        // 希釈前の量 = 作成量 ÷ (1 + 希釈率/100)
        val preDilutionAmount = totalAmount / (1 + dilutionRate / 100)

        // 主液量 = 希釈前の量 × 主液比率 ÷ (主液比率 + 硬化剤比率)
        val mainAmount = preDilutionAmount * mainRatio / ratioSum

        // 硬化量 = 希釈前の量 × 硬化剤比率 ÷ (主液比率 + 硬化剤比率)
        val hardenerAmount = preDilutionAmount * hardenerRatio / ratioSum

        // 希釈量 = 作成量 - 主液量 - 硬化量
        val dilutionAmount = totalAmount - mainAmount - hardenerAmount

        // 結果を表示
        textMainAmount.text = formatResult(mainAmount)
        textHardenerAmount.text = formatResult(hardenerAmount)
        textDilutionAmount.text = formatResult(dilutionAmount)
    }

    private fun formatResult(value: Double): String {
        return String.format(Locale.getDefault(), "%.2f kg", value)
    }

    private fun clearResults() {
        val emptyResult = "-- kg"
        textMainAmount.text = emptyResult
        textHardenerAmount.text = emptyResult
        textDilutionAmount.text = emptyResult
    }
}
