package com.example.lab6.game

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class Bird(var x: Float, var y: Float) {
    var velocity: Float = 0f
    val radius: Float = 40f

    private val gravity = 0.8f
    private val jumpForce = -15f
    private val maxVelocity = 20f

    private val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFEB3B")
    }
    private val outlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#F57F17")
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    private val eyePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
    }
    private val beakPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FF9800")
    }

    fun update() {
        velocity += gravity
        if (velocity > maxVelocity) velocity = maxVelocity
        y += velocity
    }

    fun jump() {
        velocity = jumpForce
    }

    fun draw(canvas: Canvas) {
        canvas.drawCircle(x, y, radius, bodyPaint)
        canvas.drawCircle(x, y, radius, outlinePaint)
        canvas.drawCircle(x + 15f, y - 10f, 6f, eyePaint)
        canvas.drawRect(x + 30f, y - 5f, x + 55f, y + 10f, beakPaint)
    }

    fun reset(startX: Float, startY: Float) {
        x = startX
        y = startY
        velocity = 0f
    }
}