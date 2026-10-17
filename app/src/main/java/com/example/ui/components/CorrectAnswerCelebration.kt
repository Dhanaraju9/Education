package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Premium localized correct-answer particle animation.
 * Emits 30–50 tiny colorful particles (rectangle/square/diamond)
 * in a localized burst around the selected option.
 */
@Composable
fun CorrectAnswerCelebration(
    trigger: Boolean,
    modifier: Modifier = Modifier
) {
    if (!trigger) return

    val particles = remember {
        List(40) {
            Particle(
                color = listOf(Color.Green, Color.Blue, Color.Yellow, Color.Magenta, Color(0xFFFF8C00), Color.Cyan).random(),
                angle = Random.nextDouble(0.0, 2.0 * Math.PI),
                velocity = Random.nextDouble(2.0, 5.0),
                rotation = Random.nextDouble(0.0, 360.0),
                size = Random.nextDouble(2.0, 5.0),
                type = Random.nextInt(0, 3) // 0: Square, 1: Rect, 2: Diamond
            )
        }
    }

    val animationProgress = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(trigger) {
        if (trigger) {
            animationProgress.snapTo(0f)
            animationProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
            )
        }
    }

    if (animationProgress.value > 0f && animationProgress.value < 1f) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val progress = animationProgress.value
            
            // Apply gravity
            val gravity = 9.8f * progress * progress
            
            particles.forEach { particle ->
                val distance = (particle.velocity * progress * 40.0).toFloat()
                val x = center.x + cos(particle.angle).toFloat() * distance
                val y = center.y + sin(particle.angle).toFloat() * distance + (gravity * 2f)
                
                val alpha = 1f - progress
                val pSize = particle.size.toFloat()
                
                rotate(particle.rotation.toFloat() + (progress * 360f), pivot = Offset(x, y)) {
                    when (particle.type) {
                        0 -> drawRect(
                            particle.color.copy(alpha = alpha),
                            Offset(x - pSize / 2f, y - pSize / 2f),
                            size = androidx.compose.ui.geometry.Size(pSize, pSize)
                        )
                        1 -> drawRect(
                            particle.color.copy(alpha = alpha),
                            Offset(x - pSize * 1.5f / 2f, y - pSize / 2f),
                            size = androidx.compose.ui.geometry.Size(pSize * 1.5f, pSize)
                        )
                        2 -> {
                            // Simple diamond
                            val path = androidx.compose.ui.graphics.Path().apply {
                                moveTo(x, y - pSize)
                                lineTo(x + pSize, y)
                                lineTo(x, y + pSize)
                                lineTo(x - pSize, y)
                                close()
                            }
                            drawPath(path, particle.color.copy(alpha = alpha))
                        }
                    }
                }
            }
        }
    }
}

private data class Particle(
    val color: Color,
    val angle: Double,
    val velocity: Double,
    val rotation: Double,
    val size: Double,
    val type: Int
)
