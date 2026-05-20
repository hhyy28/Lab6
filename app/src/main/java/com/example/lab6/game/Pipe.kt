package com.example.lab6.game

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class Pipe(var x: Float, val gapY: Float, val gapHeight: Float, val screenHeight: Float) {

    val width: Float = 160f
    val speed: Float = 8f
    var passed: Boolean = false

    private val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#4CAF50")
    }
    private val edgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#2E7D32")
    }

    fun update() {
        x -= speed
    }

    fun draw(canvas: Canvas) {
        canvas.drawRect(x, 0f, x + width, gapY, bodyPaint)
        canvas.drawRect(x - 10f, gapY - 30f, x + width + 10f, gapY, edgePaint)

        canvas.drawRect(x, gapY + gapHeight, x + width, screenHeight, bodyPaint)
        canvas.drawRect(x - 10f, gapY + gapHeight, x + width + 10f, gapY + gapHeight + 30f, edgePaint)
    }

    fun isOffScreen(): Boolean = x + width < 0

    fun collidesWith(bird: Bird): Boolean {
        if (bird.x + bird.radius < x || bird.x - bird.radius > x + width) return false
        return bird.y - bird.radius < gapY || bird.y + bird.radius > gapY + gapHeight
    }
}