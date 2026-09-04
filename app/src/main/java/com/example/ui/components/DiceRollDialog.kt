package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.engine.PendingAction
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CoralRed
import com.example.ui.theme.CyanHighlight
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletContainer
import com.example.ui.theme.VioletPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun DiceRollDialog(
    pendingAction: PendingAction,
    onRollConfirmed: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var isRolling by remember { mutableStateOf(false) }
    var currentDisplayNumber by remember { mutableIntStateOf(20) }
    var finalResult by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()

    val rotationAngle = remember { Animatable(0f) }
    val diceScale = remember { Animatable(1f) }

    val infinitePulse = rememberInfiniteTransition(label = "diceGlow")
    val glowAlpha by infinitePulse.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    fun startRoll() {
        if (isRolling) return
        isRolling = true
        finalResult = null

        scope.launch {
            // Animate scale up and rotation
            launch {
                diceScale.animateTo(1.25f, tween(300, easing = FastOutSlowInEasing))
                diceScale.animateTo(1.0f, tween(700, easing = FastOutSlowInEasing))
            }
            launch {
                rotationAngle.animateTo(
                    rotationAngle.value + 1080f,
                    tween(1000, easing = FastOutSlowInEasing)
                )
            }

            // Rapidly cycle numbers
            for (i in 1..18) {
                currentDisplayNumber = Random.nextInt(1, 21)
                delay(40L + (i * 4L))
            }

            val rolled = Random.nextInt(1, 21)
            currentDisplayNumber = rolled
            finalResult = rolled
            isRolling = false
        }
    }

    Dialog(onDismissRequest = { if (!isRolling) onDismiss() }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, Brush.verticalGradient(listOf(VioletPrimary, DarkBorder)), RoundedCornerShape(24.dp))
                .shadow(20.dp, spotColor = VioletPrimary)
                .testTag("manual_dice_dialog"),
            color = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(VioletContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Casino,
                                contentDescription = null,
                                tint = VioletPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ROLAGEM MANUAL D20",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Dificuldade Alvo (DC): ${pendingAction.targetDc}",
                                color = AmberGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        enabled = !isRolling,
                        modifier = Modifier.testTag("close_dice_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkCard)
                        .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "AÇÃO SELECIONADA",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = pendingAction.actionText,
                            color = CyanHighlight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (pendingAction.skill != null) {
                            Text(
                                text = "Skill: ${pendingAction.skill.name} (${pendingAction.skill.scalingStat} | ${pendingAction.skill.manaCost} MP)",
                                color = VioletPrimary,
                                fontSize = 11.sp
                            )
                        } else if (pendingAction.weapon != null) {
                            Text(
                                text = "Arma: ${pendingAction.weapon.name} (+${pendingAction.weapon.damage} Dano)",
                                color = AmberGold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Interactive 3D D20 Polyhedron Visual
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .scale(diceScale.value)
                        .rotate(rotationAngle.value)
                        .clickable(enabled = !isRolling) { startRoll() }
                        .testTag("interactive_d20_die"),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer Magic Ring
                    Canvas(modifier = Modifier.size(165.dp)) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    VioletPrimary.copy(alpha = glowAlpha * 0.35f),
                                    Color.Transparent
                                )
                            ),
                            radius = size.width / 2
                        )
                        drawCircle(
                            color = VioletPrimary.copy(alpha = glowAlpha * 0.7f),
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }

                    // D20 Icosahedron Hexagon Silhouette
                    Canvas(modifier = Modifier.size(125.dp)) {
                        val center = Offset(size.width / 2, size.height / 2)
                        val radius = size.width / 2
                        val path = Path()

                        for (i in 0 until 6) {
                            val angle = Math.toRadians((i * 60 - 30).toDouble())
                            val x = (center.x + radius * cos(angle)).toFloat()
                            val y = (center.y + radius * sin(angle)).toFloat()
                            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }
                        path.close()

                        // Fill
                        drawPath(
                            path = path,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF2A1647),
                                    Color(0xFF140D26),
                                    Color(0xFF381C61)
                                )
                            )
                        )

                        // Facet lines
                        for (i in 0 until 6) {
                            val angle = Math.toRadians((i * 60 - 30).toDouble())
                            val x = (center.x + radius * cos(angle)).toFloat()
                            val y = (center.y + radius * sin(angle)).toFloat()
                            drawLine(
                                color = VioletPrimary.copy(alpha = 0.5f),
                                start = center,
                                end = Offset(x, y),
                                strokeWidth = 1.5.dp.toPx()
                            )
                        }

                        // Border
                        drawPath(
                            path = path,
                            color = if (finalResult == 20) AmberGold else if (finalResult == 1) CoralRed else VioletPrimary,
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }

                    // Display Number
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = currentDisplayNumber.toString(),
                            color = when (finalResult) {
                                20 -> AmberGold
                                1 -> CoralRed
                                else -> TextPrimary
                            },
                            fontSize = 42.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "D20",
                            color = VioletPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Result Badge
                if (finalResult != null) {
                    val isCritSuccess = finalResult == 20
                    val isCritFail = finalResult == 1
                    val isSuccess = (finalResult ?: 0) >= pendingAction.targetDc

                    val badgeText: String
                    val badgeColor: Color
                    when {
                        isCritSuccess -> {
                            badgeText = "SUCESSO CRÍTICO MÁXIMO! (NATURAL 20)"
                            badgeColor = AmberGold
                        }
                        isCritFail -> {
                            badgeText = "FALHA CRÍTICA GRAVE! (NATURAL 1)"
                            badgeColor = CoralRed
                        }
                        isSuccess -> {
                            badgeText = "SUCESSO! Superou a DC ${pendingAction.targetDc}"
                            badgeColor = EmeraldGreen
                        }
                        else -> {
                            badgeText = "FALHA! Abaixo da DC ${pendingAction.targetDc}"
                            badgeColor = CoralRed
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(badgeColor.copy(alpha = 0.15f))
                            .border(1.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(vertical = 8.dp, horizontal = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (isCritSuccess) Icons.Default.Star else Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = badgeColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = badgeText,
                                color = badgeColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    Text(
                        text = if (isRolling) "Girando o D20 místico..." else "Toque no dado ou no botão abaixo para rolar!",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { startRoll() },
                        enabled = !isRolling,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("roll_d20_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VioletPrimary,
                            contentColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Casino, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (finalResult == null) "GIRAR D20" else "ROLAR DE NOVO",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    if (finalResult != null) {
                        Button(
                            onClick = { onRollConfirmed(finalResult!!) },
                            enabled = !isRolling,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("confirm_d20_roll_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldGreen,
                                contentColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = "CONFIRMAR",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
