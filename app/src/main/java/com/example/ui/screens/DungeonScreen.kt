package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.RpgGameViewModel
import com.example.model.GameMode
import com.example.model.RollSuccessLevel
import com.example.ui.theme.CoralHp
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkOutline
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GreenOnline
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletBorder
import com.example.ui.theme.VioletContainer
import com.example.ui.theme.VioletLight
import com.example.ui.theme.VioletPrimary

@Composable
fun DungeonScreen(viewModel: RpgGameViewModel) {
    val roomState by viewModel.roomState.collectAsState()
    val character by viewModel.character.collectAsState()
    val isThinking by viewModel.isAiThinking.collectAsState()

    var customActionInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Sophisticated Dark Header with GM Avatar & Party Strip
            RoomHeaderSection(
                roomState = roomState,
                character = character,
                onSoloClick = { viewModel.startSoloDungeon() },
                onMultiplayerClick = { viewModel.createMultiplayerRoom() }
            )
        }

        // Active Monster Encounter
        item {
            roomState.currentMonster?.let { monster ->
                MonsterCard(monster = monster)
            }
        }

        // Hero Vitals Card
        item {
            HeroStatusCard(
                character = character,
                onRevive = { viewModel.healHero() }
            )
        }

        // Latest Turn Story Box from Gemini AI Master (Sophisticated Narrator Card)
        item {
            val latestTurn = roomState.turnHistory.lastOrNull()
            if (latestTurn != null) {
                LatestStoryNarrativeCard(
                    turn = latestTurn,
                    isThinking = isThinking
                )
            }
        }

        // Combat & Action Control Center
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("action_control_card"),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AÇÃO DO JOGADOR",
                            color = VioletPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Casino,
                                contentDescription = "D20",
                                tint = VioletPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "D20 + Modificador",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Action Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = false,
                            onClick = { viewModel.executePlayerAction("Golpear com arma corporal na fresta da armadura", "ATTACK") },
                            label = { Text("Atacar", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                            colors = FilterChipDefaults.filterChipColors(containerColor = DarkSurfaceVariant),
                            border = FilterChipDefaults.filterChipBorder(enabled = true, selected = false, borderColor = DarkOutline),
                            modifier = Modifier.testTag("chip_attack")
                        )
                        FilterChip(
                            selected = false,
                            onClick = { viewModel.executePlayerAction("Conjurar Vórtice de Chamas Arcanas", "MAGIC") },
                            label = { Text("Magia Arcana", color = VioletPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                            colors = FilterChipDefaults.filterChipColors(containerColor = DarkSurfaceVariant),
                            border = FilterChipDefaults.filterChipBorder(enabled = true, selected = false, borderColor = VioletBorder),
                            modifier = Modifier.testTag("chip_magic")
                        )
                        FilterChip(
                            selected = false,
                            onClick = { viewModel.executePlayerAction("Observar fraquezas e preparar guarda", "DEFEND") },
                            label = { Text("Investigar", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                            colors = FilterChipDefaults.filterChipColors(containerColor = DarkSurfaceVariant),
                            border = FilterChipDefaults.filterChipBorder(enabled = true, selected = false, borderColor = DarkOutline),
                            modifier = Modifier.testTag("chip_defend")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Custom Action Input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customActionInput,
                            onValueChange = { customActionInput = it },
                            placeholder = { Text("Descreva sua ação para o Mestre IA...", color = TextMuted, fontSize = 12.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("action_text_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkBg,
                                unfocusedContainerColor = DarkBg,
                                focusedBorderColor = VioletPrimary,
                                unfocusedBorderColor = DarkOutline,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(50),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (customActionInput.isNotBlank()) {
                                    viewModel.executePlayerAction(customActionInput, "ATTACK")
                                    customActionInput = ""
                                }
                            },
                            enabled = !isThinking && character.currentHp > 0,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VioletPrimary,
                                disabledContainerColor = DarkSurfaceVariant
                            ),
                            shape = CircleShape,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("send_action_button")
                        ) {
                            if (isThinking) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = DarkBg,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    Icons.Default.Send,
                                    contentDescription = "Enviar",
                                    tint = DarkBg
                                )
                            }
                        }
                    }
                }
            }
        }

        // Historic Turn Log Header
        item {
            Text(
                text = "HISTÓRICO DA CRÔNICA",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        // List of past turns
        items(roomState.turnHistory.reversed()) { turn ->
            TurnLogItem(turn = turn)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun RoomHeaderSection(
    roomState: com.example.model.GameRoomState,
    character: com.example.model.CharacterModel,
    onSoloClick: () -> Unit,
    onMultiplayerClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: GM Avatar + Title + Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Signature Gradient Avatar
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(VioletPrimary, VioletContainer)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "GM",
                            color = DarkBg,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "AI GAME MASTER",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 0.5.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(GreenOnline)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Sessão: ${roomState.roomTitle} • #${roomState.roomCode}",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                letterSpacing = 0.2.sp
                            )
                        }
                    }
                }

                // Players pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(DarkSurfaceVariant)
                        .border(1.dp, DarkOutline, RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (roomState.mode == GameMode.SOLO) "1/1 Solo" else "4/4 Players",
                        color = VioletPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Party horizontal strip
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Active hero card
                PartyMemberCard(
                    icon = "⚔️",
                    name = character.name,
                    level = character.level,
                    hpPercent = character.currentHp.toFloat() / character.maxHp
                )

                // Companion cards in multiplayer
                if (roomState.mode == GameMode.MULTIPLAYER) {
                    PartyMemberCard(icon = "🧙", name = "Eldrin", level = 11, hpPercent = 0.9f)
                    PartyMemberCard(icon = "🏹", name = "Lyra", level = 10, hpPercent = 0.65f)
                    PartyMemberCard(icon = "🛡️", name = "Thorgar", level = 12, hpPercent = 0.85f)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Solo vs Multiplayer toggle buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSoloClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (roomState.mode == GameMode.SOLO) VioletContainer else DarkSurfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = if (roomState.mode == GameMode.SOLO) androidx.compose.foundation.BorderStroke(1.dp, VioletBorder) else androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Solo",
                        tint = if (roomState.mode == GameMode.SOLO) VioletPrimary else TextSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Modo Solo",
                        color = if (roomState.mode == GameMode.SOLO) VioletPrimary else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onMultiplayerClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (roomState.mode == GameMode.MULTIPLAYER) VioletContainer else DarkSurfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = if (roomState.mode == GameMode.MULTIPLAYER) androidx.compose.foundation.BorderStroke(1.dp, VioletBorder) else androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
                ) {
                    Icon(
                        Icons.Default.Groups,
                        contentDescription = "Multiplayer",
                        tint = if (roomState.mode == GameMode.MULTIPLAYER) VioletPrimary else TextSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Criar Sala",
                        color = if (roomState.mode == GameMode.MULTIPLAYER) VioletPrimary else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun PartyMemberCard(
    icon: String,
    name: String,
    level: Int,
    hpPercent: Float
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurfaceVariant)
            .border(1.dp, VioletPrimary.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
            .padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(VioletContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = "$name (Lv.$level)",
                    color = VioletPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(DarkOutline)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(hpPercent.coerceIn(0f, 1f))
                            .height(4.dp)
                            .background(if (hpPercent > 0.3f) CoralHp else CoralHp)
                    )
                }
            }
        }
    }
}

@Composable
fun MonsterCard(monster: com.example.model.Monster) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CoralHp.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = monster.name.uppercase(),
                    color = CoralHp,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "${monster.currentHp}/${monster.maxHp} HP",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            val healthFraction = monster.currentHp.toFloat() / monster.maxHp.coerceAtLeast(1)
            LinearProgressIndicator(
                progress = { healthFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = CoralHp,
                trackColor = DarkSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = monster.description,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun HeroStatusCard(
    character: com.example.model.CharacterModel,
    onRevive: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${character.name} • ${character.characterClass}",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Nível ${character.level} • Power Level: ${character.powerLevel}",
                        color = VioletPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (character.currentHp <= 0) {
                    Button(
                        onClick = onRevive,
                        colors = ButtonDefaults.buttonColors(containerColor = CoralHp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Reviver", color = DarkBg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // HP Bar
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("HP", color = CoralHp, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(28.dp))
                LinearProgressIndicator(
                    progress = { character.currentHp.toFloat() / character.maxHp },
                    modifier = Modifier
                        .weight(1f)
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = CoralHp,
                    trackColor = DarkSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("${character.currentHp}/${character.maxHp}", color = TextSecondary, fontSize = 10.sp)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Mana Bar
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("MP", color = VioletPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(28.dp))
                LinearProgressIndicator(
                    progress = { character.currentMana.toFloat() / character.maxMana },
                    modifier = Modifier
                        .weight(1f)
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = VioletPrimary,
                    trackColor = DarkSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("${character.currentMana}/${character.maxMana}", color = TextSecondary, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun LatestStoryNarrativeCard(
    turn: com.example.model.GameTurnEvent,
    isThinking: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header with NARRATOR and Log version tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NARRADOR",
                    color = VioletPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                if (isThinking) {
                    Text(
                        text = "Calculando regras...",
                        color = VioletPrimary,
                        fontSize = 10.sp
                    )
                } else {
                    Text(
                        text = "LOG_${turn.turnNumber}.v3",
                        color = VioletPrimary.copy(alpha = 0.5f),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Serif Italic Narrative (matching Sophisticated Dark design)
            Text(
                text = turn.gmNarrative,
                color = TextPrimary,
                fontSize = 14.sp,
                lineHeight = 21.sp,
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tactical Reaction Box with left border indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurfaceVariant)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Left 4.dp Accent Line
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(60.dp)
                            .background(VioletPrimary)
                    )

                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "REAÇÃO TÁTICA DA IA",
                                color = VioletPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Dificuldade: DC ${turn.difficultyClass}",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = turn.tacticalSummary,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TurnLogItem(turn: com.example.model.GameTurnEvent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${turn.actorName} - \"${turn.actionText}\"",
                    color = VioletLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "D20: ${turn.d20Roll} + ${turn.modifier} = ${turn.totalResult} (DC ${turn.difficultyClass})",
                    color = when (turn.successLevel) {
                        RollSuccessLevel.CRITICAL_SUCCESS -> EmeraldSuccess
                        RollSuccessLevel.SUCCESS -> VioletPrimary
                        RollSuccessLevel.CRITICAL_FAILURE -> CoralHp
                        RollSuccessLevel.FAILURE -> TextMuted
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = turn.tacticalSummary,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}
