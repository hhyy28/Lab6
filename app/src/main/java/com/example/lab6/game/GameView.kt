package com.example.lab6.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.random.Random

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private var thread: GameThread? = null
    private lateinit var bird: Bird
    private val pipes = mutableListOf<Pipe>()

    private var screenWidth: Float = 0f
    private var screenHeight: Float = 0f

    private var score: Int = 0
    private var highScore: Int = 0
    private var gameOver: Boolean = false
    private var gameStarted: Boolean = false

    private var pipeSpawnCounter: Int = 0
    private val pipeSpawnInterval: Int = 90

    private val skyPaint = Paint().apply { color = Color.parseColor("#87CEEB") }
    private val groundPaint = Paint().apply { color = Color.parseColor("#8D6E63") }
    private val grassPaint = Paint().apply { color = Color.parseColor("#7CB342") }
    private val scorePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 90f
        textAlign = Paint.Align.CENTER
        setShadowLayer(8f, 2f, 2f, Color.BLACK)
    }
    private val gameOverPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 110f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        setShadowLayer(10f, 2f, 2f, Color.BLACK)
    }
    private val infoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 50f
        textAlign = Paint.Align.CENTER
        setShadowLayer(6f, 2f, 2f, Color.BLACK)
    }

    init {
        holder.addCallback(this)
        isFocusable = true
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        screenWidth = width.toFloat()
        screenHeight = height.toFloat()
        resetGame()
        thread = GameThread(holder, this)
        thread?.setRunning(true)
        thread?.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        screenWidth = width.toFloat()
        screenHeight = height.toFloat()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        thread?.setRunning(false)
        while (retry) {
            try {
                thread?.join()
                retry = false
            } catch (_: InterruptedException) {}
        }
    }

    fun pause() {
        thread?.setRunning(false)
        try { thread?.join() } catch (_: InterruptedException) {}
    }

    fun resume() {
        if (thread?.isAlive != true) {
            thread = GameThread(holder, this)
            thread?.setRunning(true)
            thread?.start()
        }
    }

    private fun resetGame() {
        bird = Bird(screenWidth / 4f, screenHeight / 2f)
        pipes.clear()
        score = 0
        pipeSpawnCounter = 0
        gameOver = false
        gameStarted = false
    }

    private fun spawnPipe() {
        val minGapY = 200f
        val maxGapY = screenHeight - 200f - 400f - 200f
        val gapY = Random.nextFloat() * (maxGapY - minGapY) + minGapY
        pipes.add(Pipe(screenWidth, gapY, 400f, screenHeight - 150f))
    }

    fun update() {
        if (!gameStarted || gameOver) return

        bird.update()

        if (bird.y - bird.radius < 0) {
            bird.y = bird.radius
            bird.velocity = 0f
        }
        if (bird.y + bird.radius > screenHeight - 150f) {
            gameOver = true
            if (score > highScore) highScore = score
        }

        pipeSpawnCounter++
        if (pipeSpawnCounter >= pipeSpawnInterval) {
            spawnPipe()
            pipeSpawnCounter = 0
        }

        val iterator = pipes.iterator()
        while (iterator.hasNext()) {
            val pipe = iterator.next()
            pipe.update()

            if (pipe.collidesWith(bird)) {
                gameOver = true
                if (score > highScore) highScore = score
            }

            if (!pipe.passed && pipe.x + pipe.width < bird.x) {
                pipe.passed = true
                score++
            }

            if (pipe.isOffScreen()) iterator.remove()
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        canvas.drawRect(0f, 0f, screenWidth, screenHeight, skyPaint)

        for (pipe in pipes) pipe.draw(canvas)

        canvas.drawRect(0f, screenHeight - 150f, screenWidth, screenHeight - 130f, grassPaint)
        canvas.drawRect(0f, screenHeight - 130f, screenWidth, screenHeight, groundPaint)

        bird.draw(canvas)

        canvas.drawText("$score", screenWidth / 2f, 150f, scorePaint)

        when {
            !gameStarted -> {
                canvas.drawText("Flappy Lab", screenWidth / 2f, screenHeight / 2f - 100f, gameOverPaint)
                canvas.drawText("Тапніть, щоб почати", screenWidth / 2f, screenHeight / 2f + 50f, infoPaint)
                canvas.drawText("Рекорд: $highScore", screenWidth / 2f, screenHeight / 2f + 120f, infoPaint)
            }
            gameOver -> {
                canvas.drawText("Гра завершена", screenWidth / 2f, screenHeight / 2f - 100f, gameOverPaint)
                canvas.drawText("Рахунок: $score", screenWidth / 2f, screenHeight / 2f + 20f, infoPaint)
                canvas.drawText("Рекорд: $highScore", screenWidth / 2f, screenHeight / 2f + 90f, infoPaint)
                canvas.drawText("Тапніть, щоб почати знову", screenWidth / 2f, screenHeight / 2f + 180f, infoPaint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            when {
                gameOver -> resetGame()
                !gameStarted -> {
                    gameStarted = true
                    bird.jump()
                }
                else -> bird.jump()
            }
            return true
        }
        return super.onTouchEvent(event)
    }
}