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
        val dilutionRate = editDilutionRate.text.toString().toDoubleOrNull()
        val totalAmount = editTotalAmount.text.toString().toDoubleOrNull()

        val result = PaintCalculator.calculate(mainRatio, hardenerRatio, dilutionRate, totalAmount)

        if (result == null) {
            clearResults()
            return
        }

        textMainAmount.text = formatResult(result.mainAmount)
        textHardenerAmount.text = formatResult(result.hardenerAmount)
        textDilutionAmount.text = formatResult(result.dilutionAmount)
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
