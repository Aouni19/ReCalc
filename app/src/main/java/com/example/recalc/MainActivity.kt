package com.example.recalc

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.ArrayDeque
import java.util.regex.Pattern

public class MainActivity : AppCompatActivity() {

    private lateinit var mainDisplay: TextView
    private lateinit var previousDisplay: TextView

    private var currentExpression: String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainDisplay=findViewById(R.id.main_display)
        previousDisplay=findViewById(R.id.previous_calculation)

        val numIds= listOf(
            R.id.zero, R.id.one, R.id.two, R.id.three,
            R.id.four, R.id.five, R.id.six, R.id.seven,
            R.id.eight, R.id.nine
        )

        val numText= listOf(
            "0","1","2","3","4","5","6","7","8","9"
        )

        numIds.forEachIndexed { idx, id ->
            val btn =findViewById<Button>(id)
            btn.setOnClickListener {
                flash(it)
                appendNumber(numText[idx])
            }
        }

        findViewById<Button?>(R.id.dot)?.setOnClickListener{
            flash(it)
            appendDot()
        }

        findViewById<Button?>(R.id.plus)?.setOnClickListener{
            flash(it); appendOperator("+")
        }
        findViewById<Button?>(R.id.minus)?.setOnClickListener{
            flash(it); appendOperator("-")
        }
        findViewById<Button?>(R.id.product)?.setOnClickListener{
            flash(it); appendOperator("x")
        }
        findViewById<Button?>(R.id.divide)?.setOnClickListener{
            flash(it); appendOperator("/")
        }

        findViewById<Button?>(R.id.equal)?.setOnClickListener{
            flash(it)
            calculateResult()
        }

        findViewById<Button?>(R.id.allclear)?.setOnClickListener{
            flash(it)
            clearAll()
        }

        findViewById<Button?>(R.id.backspace)?.setOnClickListener{
            flash(it)
            backspace()
        }

        findViewById<Button?>(R.id.sin)?.setOnClickListener {
            flash(it)
            applyUnaryToLastNumber("sin") { Math.sin(it) }
        }

        findViewById<Button?>(R.id.cos)?.setOnClickListener {
            flash(it)
            applyUnaryToLastNumber("cos") { Math.cos(it) }
        }

        findViewById<Button?>(R.id.tan)?.setOnClickListener {
            flash(it)
            applyUnaryToLastNumber("tan") { Math.tan(it) }
        }

        findViewById<Button?>(R.id.log)?.setOnClickListener {
            flash(it)
            applyUnaryToLastNumber("log") { Math.log10(it) } // base-10 log
        }

        findViewById<Button?>(R.id.modulo)?.setOnClickListener {
            flash(it)
            appendOperator("%")
        }

        findViewById<Button?>(R.id.pi)?.setOnClickListener {
            flash(it)
            insertPi()
        }

        updateMainDisplay()
        previousDisplay.text=""

    }
    //A visual flare to simulate the touch
    private fun flash(view: View){
        view.alpha=0.0f
        view.postDelayed({view.alpha=1f}, 100)
    }

    private fun appendNumber(digit: String){
        if(currentExpression=="0"){
            currentExpression=digit
        }
        else{
            currentExpression+=digit
        }
        updateMainDisplay()
    }
    private fun appendOperator(op: String){
        if(currentExpression.isEmpty()){
            if(op=="-"){
                //An exception case for negative numbers

                currentExpression="-"
                updateMainDisplay(); return
            }
            return
        }
        if (currentExpression.last().toString().matches(Regex("[+\\-x%]"))) {
            currentExpression = currentExpression.dropLast(1) + op
        } else {
            currentExpression += op
        }
        updateMainDisplay()
    }

    private fun appendDot(){
        val lastnum = currentExpression.split(Regex("[+\\-x/%]")).lastOrNull() ?: ""
        if(!lastnum.contains(".")){
            if(lastnum.isEmpty()){
                currentExpression+="0."
            }else{
                currentExpression+="."
            }
            updateMainDisplay()
        }
    }

    private fun backspace(){
        if(currentExpression.isNotEmpty()){
            currentExpression=currentExpression.dropLast(1)
            updateMainDisplay()
        }
    }

    private fun applyUnaryToLastNumber(name: String, useDegrees: Boolean = false, fn: (Double) -> Double) {
        if (currentExpression.isEmpty()) return

        // find index of last operator: + - x / %
        val lastOpIndex = currentExpression.indexOfLast { ch -> "+-x/%".contains(ch) }
        val numStart = if (lastOpIndex == -1) 0 else lastOpIndex + 1

        // get the last number substring
        val lastNumberStr = currentExpression.substring(numStart)
        if (lastNumberStr.isEmpty()) return

        val value = lastNumberStr.toDoubleOrNull() ?: return

        // convert degrees->radians if requested (by default we work in radians)
        val input = if (useDegrees) Math.toRadians(value) else value

        // compute, but guard against NaN/Inf (e.g. tan(90deg) in degrees -> huge; user can handle)
        val rawResult = try {
            fn(input)
        } catch (e: Exception) {
            Double.NaN
        }

        if (rawResult.isNaN() || rawResult.isInfinite()) {
            mainDisplay.text = "Error"
            return
        }

        // format result to a nice string using your existing formatter
        val resultStr = formatResult(rawResult)

        // replace the last number with the computed result
        currentExpression = currentExpression.substring(0, numStart) + resultStr
        updateMainDisplay()

        // show previous calculation like sin(45)
        previousDisplay.text = "$name($lastNumberStr)"
    }

    private fun insertPi() {
        val piStr = formatResult(Math.PI)
        if (currentExpression.isNotEmpty() && currentExpression.last().toString().matches(Regex("[0-9\\.]"))) {
            // implicit multiply
            currentExpression += "x$piStr"
        } else {
            currentExpression += piStr
        }
        updateMainDisplay()
    }


    private fun clearAll() {
        currentExpression = ""
        mainDisplay.text = ""
        previousDisplay.text = ""
    }

    private fun calculateResult() {
        if (currentExpression.isEmpty()) return
        if (currentExpression.last().toString().matches(Regex("[+\\-x/%]"))) {

            currentExpression = currentExpression.dropLast(1)
        }
        if (currentExpression.isEmpty()) return

        val expressionToEvaluate = currentExpression

        try {
            val result = evaluateExpression(expressionToEvaluate)
            // format result neatly
            val formatted = formatResult(result)

            previousDisplay.text = expressionToEvaluate

            currentExpression = formatted
            mainDisplay.text = formatted
        } catch (e: Exception) {
            mainDisplay.text = "Error"
        }
    }

    private fun updateMainDisplay() {
        mainDisplay.text = if (currentExpression.isEmpty()) "0" else currentExpression
    }

    @Throws(Exception::class)
    private fun evaluateExpression(expr: String): Double {
        var s = expr.replace("x", "*").replace("X", "*")

        if (s.startsWith("-")) s = "0$s"

        val tokenPattern = Pattern.compile("\\d+(?:\\.\\d+)?|[+\\-*/%]")
        val matcher = tokenPattern.matcher(s)
        val tokens = ArrayList<String>()
        while (matcher.find()) tokens.add(matcher.group())

        if (tokens.isEmpty()) throw IllegalArgumentException("Empty expression")
        val output = ArrayList<String>()
        val ops = ArrayDeque<String>()
        val precedence = mapOf("+" to 1, "-" to 1, "*" to 2, "/" to 2, "%" to 2)

        for (t in tokens) {
            if (t.matches(Regex("\\d+(?:\\.\\d+)?"))) {
                output.add(t)
            } else { // operator
                while (ops.isNotEmpty() && precedence[ops.peek()] != null &&
                    precedence[ops.peek()]!! >= precedence[t]!!
                ) {
                    output.add(ops.pop())
                }
                ops.push(t)
            }
        }
        while (ops.isNotEmpty()) output.add(ops.pop())

        // evaluate RPN
        val stack = ArrayDeque<Double>()
        for (tok in output) {
            if (tok.matches(Regex("\\d+(?:\\.\\d+)?"))) {
                stack.push(tok.toDouble())
            } else {
                if (stack.size < 2) throw IllegalArgumentException("Malformed expression")
                val b = stack.pop()
                val a = stack.pop()
                val res = when (tok) {
                    "+" -> a + b
                    "-" -> a - b
                    "*" -> a * b
                    "/" -> {
                        if (b == 0.0) throw ArithmeticException("Division by zero")
                        a / b
                    }
                    "%" -> {
                        if (b == 0.0) throw ArithmeticException("Modulo by zero")
                        a % b
                    }
                    else -> throw IllegalArgumentException("Unknown operator $tok")
                }
                stack.push(res)
            }
        }

        if (stack.size != 1) throw IllegalArgumentException("Malformed expression")

        return stack.pop()
    }

    private fun formatResult(value: Double): String {
        if (value.isNaN() || value.isInfinite()) return "Error"
        return if (value % 1.0 == 0.0) {
            // integer result
            value.toLong().toString()
        } else {
            val bd = BigDecimal(value.toString()).setScale(8, RoundingMode.HALF_UP).stripTrailingZeros()
            bd.toPlainString()
        }
    }



}

