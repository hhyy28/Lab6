package com.example.lab6.game

import android.graphics.Canvas
import android.view.SurfaceHolder

class GameThread(
    private val surfaceHolder: SurfaceHolder,
    private val gameView: GameView
) : Thread() {

    private var running: Boolean = false
    private val targetFPS = 60
    private val targetTime = (1000 / targetFPS).toLong()

    fun setRunning(isRunning: Boolean) {
        running = isRunning
    }

    override fun run() {
        var startTime: Long
        var timeMillis: Long
        var waitTime: Long

        while (running) {
            startTime = System.nanoTime()
            var canvas: Canvas? = null
            try {
                canvas = surfaceHolder.lockCanvas()
                synchronized(surfaceHolder) {
                    gameView.update()
                    canvas?.let { gameView.draw(it) }
                }
            } finally {
                canvas?.let {
                    try { surfaceHolder.unlockCanvasAndPost(it) } catch (_: Exception) {}
                }
            }

            timeMillis = (System.nanoTime() - startTime) / 1_000_000
            waitTime = targetTime - timeMillis
            if (waitTime > 0) {
                try { sleep(waitTime) } catch (_: InterruptedException) {}
            }
        }
    }
}