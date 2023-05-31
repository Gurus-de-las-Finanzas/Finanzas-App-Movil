package com.example.finanzas.payments.controller.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import com.example.finanzas.R

class PaymentFormActivity : AppCompatActivity() {
    lateinit var seekBarTermYears: SeekBar
    lateinit var tvSelectedTerm: TextView
    lateinit var spinnerLienType: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_form)
        seekBarTermYears = findViewById(R.id.seekBarTermYears)
        tvSelectedTerm = findViewById(R.id.tvSelectedTerm)
        spinnerLienType = findViewById(R.id.spinnerLienType)
        enableSeekBar()
        enableLienTypeSpinner()
    }

    private fun enableLienTypeSpinner() {
        ArrayAdapter.createFromResource(
            this,
            R.array.lien_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinnerLienType.adapter = adapter
        }
        spinnerLienType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun enableSeekBar() {
        seekBarTermYears.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvSelectedTerm.text = "$progress años"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
    }
}