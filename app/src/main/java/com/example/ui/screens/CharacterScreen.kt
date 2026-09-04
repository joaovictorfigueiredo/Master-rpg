package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.RpgGameViewModel
import com.example.ui.components.CharacterCreationDialog
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CoralRed
import com.example.ui.theme.CyanHighlight
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletContainer
import com.example.ui.theme.VioletPrimary

@Composable
fun CharacterScreen(viewModel: RpgGameViewModel) {
    val character by viewModel.character.collectAsState()
    val isAiThinking by viewModel.isAiThinking.collectAsState()
    val showCreationDialog by viewModel.showCharacterCreationDialog.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "heroGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .testTag("character_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. AI Character Creation Callout Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_create_char_card"),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        Brush.horizontalGradient(listOf(VioletPrimary, CyanHighlight))
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(VioletContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = VioletPrimary, modifier = Modifier.size(22.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("NOVO HERÓI / MONSTRO", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text("A IA cria ficha completa pela sua descrição", color = CyanHighlight, fontSize = 11.sp)
                                }
                            }

                            Button(
                                onClick = { viewModel.openCharacterCreationDialog() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = VioletPrimary,
                                    contentColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("open_create_char_btn")
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("CRIAR COM IA", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Descreva seu personagem livremente (raça, classe, criatura, monstro). A IA identifica o arquétipo, distribui atributos, forja 3 skills exclusivas e equipa arma sob medida!",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // 2. Main Hero Profile Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar Portrait with Arcane Glow
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .border(
                                        2.dp,
                                        VioletPrimary.copy(alpha = glowAlpha),
                                        CircleShape
                                    )
                                    .padding(4.dp)
                                    .clip(CircleShape)
                            ) {
                                Image(
                                    painter = painterResource(id = character.avatarDrawableRes),
                                    contentDescription = character.name,
                                    modifier = Modifier.matchParentSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = character.name,
                                    color = TextPrimary,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${character.race} • ${character.characterClass}",
                                    color = CyanHighlight,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Arquétipo: ${character.archetype}",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AmberGold.copy(alpha = 0.15f))
                                        .border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = AmberGold, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Poder Total: ${character.powerLevel}", color = AmberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Description / Lore
                        Text(
                            text = "\"${character.backgroundStory}\"",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // HP, MP and Level Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DarkCard)
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text("VIDA (HP)", color = CoralRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text("${character.currentHp} / ${character.maxHp}", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DarkCard)
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text("MANA (MP)", color = CyanHighlight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text("${character.currentMana} / ${character.maxMana}", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DarkCard)
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text("NÍVEL & XP", color = AmberGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text("NV ${character.level} (${character.currentXp}/${character.maxXp})", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // 3. Attributes Matrix (FOR, DES, INT, VIT, CAR)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("ATRIBUTOS DO PERSONAGEM", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf(
                                "FOR" to character.strength,
                                "DES" to character.dexterity,
                                "INT" to character.intelligence,
                                "VIT" to character.vitality,
                                "CAR" to character.charisma
                            ).forEach { (name, value) ->
                                val modifier = (value - 10) / 2
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(DarkCard)
                                        .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                                        .padding(horizontal = 10.dp, vertical = 8.dp)
                                ) {
                                    Text(name, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text(value.toString(), color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                                    Text(if (modifier >= 0) "+$modifier" else "$modifier", color = CyanHighlight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // 4. Equipped Weapon
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AmberGold.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("ARMA EQUIPADA", color = AmberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AmberGold)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("EM USO", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (character.equippedWeapon != null) {
                            val wep = character.equippedWeapon!!
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(AmberGold.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Shield, contentDescription = null, tint = AmberGold, modifier = Modifier.size(22.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(wep.name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text("+${wep.damage} Dano (${wep.damageType}) • ${wep.bonusStat}", color = AmberGold, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(wep.description, color = TextSecondary, fontSize = 11.sp)
                        } else {
                            Text("Nenhuma arma equipada. Visite a aba ITENS para equipar ou forjar uma arma!", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }

            // 5. Active Usable Skills
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VioletPrimary.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("SKILLS & HABILIDADES ATIVAS", color = VioletPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            character.skills.forEach { skill ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(DarkCard)
                                        .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(VioletContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(skill.icon, fontSize = 18.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(skill.name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(skill.description, color = TextSecondary, fontSize = 11.sp)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("${skill.manaCost} MP", color = CyanHighlight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(if (skill.isHealing) "+${skill.power} HP" else "${skill.power} Dano", color = if (skill.isHealing) EmeraldGreen else CoralRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text("Escala: ${skill.scalingStat}", color = TextSecondary, fontSize = 9.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Character Creation Dialog
        if (showCreationDialog) {
            CharacterCreationDialog(
                isAiThinking = isAiThinking,
                onCreateCharacter = { description ->
                    viewModel.createCharacterFromDescription(description)
                },
                onDismiss = { viewModel.closeCharacterCreationDialog() }
            )
        }
    }
}
