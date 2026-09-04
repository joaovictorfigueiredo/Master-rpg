package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.RpgGameViewModel
import com.example.model.DungeonDefinition
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
fun DungeonLobbyScreen(viewModel: RpgGameViewModel) {
    val character by viewModel.character.collectAsState()
    val availableDungeons by viewModel.availableDungeons.collectAsState()

    var showCreateRoomCard by remember { mutableStateOf(false) }

    // Online Multiplayer Selection State
    var isOnlineModeSelected by remember { mutableStateOf(false) }
    var joinRoomCodeInput by remember { mutableStateOf("") }

    // Custom Room Form State
    var customRoomName by remember { mutableStateOf("") }
    var selectedTheme by remember { mutableStateOf("Criptas das Sombras") }
    var selectedDifficulty by remember { mutableStateOf("Normal") }
    var selectedModifier by remember { mutableStateOf("Mais Ferramentas & Minérios de Forja") }

    val themes = listOf(
        "Criptas das Sombras",
        "Vulcão de Magma",
        "Bosque Arcano",
        "Ruínas Abissais",
        "Oficina Mecânica Arcana",
        "Santuário Astral"
    )

    val difficulties = listOf("Fácil", "Normal", "Difícil", "Pesadelo")
    val modifiers = listOf(
        "Mais Ferramentas & Minérios de Forja",
        "Monstros Elites Brutais (+XP)",
        "Magia Caótica Instável",
        "Tesouros Abundantes"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("dungeon_lobby_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Player Ready Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(listOf(VioletPrimary, CyanHighlight))
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = character.avatarDrawableRes),
                        contentDescription = character.name,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.5.dp, CyanHighlight, RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                character.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(EmeraldGreen.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("HERÓI PRONTO", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                            }
                        }
                        Text(
                            "${character.race} • ${character.characterClass} • Nível ${character.level}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Text(
                            "Arma: ${character.equippedWeapon?.name ?: "Desarmado"}",
                            fontSize = 10.sp,
                            color = AmberGold,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Button(
                        onClick = { viewModel.resetCharacterForNewCreation() },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp), tint = TextSecondary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mudar", fontSize = 10.sp, color = TextSecondary)
                    }
                }
            }
        }

        // 1.5 Game Mode Selector (Solo vs Online Multiplayer)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isOnlineModeSelected) EmeraldGreen else DarkBorder
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        "MODO DE JOGO NA MASMORRA:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Solo Mode Tab
                        Button(
                            onClick = { isOnlineModeSelected = false },
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!isOnlineModeSelected) VioletPrimary else DarkCard,
                                contentColor = if (!isOnlineModeSelected) Color.White else TextSecondary
                            ),
                            border = if (!isOnlineModeSelected) null else androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("MODO SOLO", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Online Multiplayer Tab (Requested by user)
                        Button(
                            onClick = { isOnlineModeSelected = true },
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isOnlineModeSelected) EmeraldGreen else DarkCard,
                                contentColor = if (isOnlineModeSelected) DarkBg else TextSecondary
                            ),
                            border = if (isOnlineModeSelected) null else androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                        ) {
                            Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ONLINE CO-OP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // If Online Mode is selected, display room info and join-by-code field
                    if (isOnlineModeSelected) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkCard)
                                .border(1.dp, EmeraldGreen.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(EmeraldGreen)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "MULTIJOGADOR ONLINE ATIVADO",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldGreen
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Jogue cooperativamente em tempo real com amigos na mesma sala! Todas as mensagens no chat afetam a história do Mestre de IA.",
                                    fontSize = 10.sp,
                                    color = TextSecondary,
                                    lineHeight = 14.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = joinRoomCodeInput,
                                        onValueChange = { joinRoomCodeInput = it },
                                        placeholder = { Text("Código da sala (ex: #DUNGEON-7412)", fontSize = 11.sp) },
                                        modifier = Modifier.weight(1f).height(46.dp),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = EmeraldGreen,
                                            unfocusedBorderColor = DarkBorder,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            val targetCode = if (joinRoomCodeInput.isNotBlank()) joinRoomCodeInput else "#DUNGEON-COOP"
                                            viewModel.joinOnlineRoom(targetCode)
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                        modifier = Modifier.height(46.dp)
                                    ) {
                                        Text("ENTRAR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DarkBg)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Action Header: Seleção ou Criar Sala
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "ESCOLHA SUA MASMORRA",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        "Selecione uma masmorra ou crie sua própria sala",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = { showCreateRoomCard = !showCreateRoomCard },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (showCreateRoomCard) DarkCard else VioletPrimary
                    ),
                    border = if (showCreateRoomCard) androidx.compose.foundation.BorderStroke(1.dp, DarkBorder) else null,
                    modifier = Modifier.testTag("btn_toggle_create_room")
                ) {
                    Icon(
                        if (showCreateRoomCard) Icons.Default.Close else Icons.Default.MeetingRoom,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        if (showCreateRoomCard) "Fechar" else "+ Criar Sala",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 3. Expandable Create Room Form
        item {
            AnimatedVisibility(visible = showCreateRoomCard) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_room_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, CyanHighlight)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(CyanHighlight.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = CyanHighlight, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("CONFIGURAR NOVA SALA DE MASMORRA", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Ajuste os modificadores e o mestre de IA gerará o cenário!", fontSize = 10.sp, color = TextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Nome da Sala / Masmorra:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        OutlinedTextField(
                            value = customRoomName,
                            onValueChange = { customRoomName = it },
                            placeholder = { Text("Ex: Covil dos Campeões de Aço...", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanHighlight,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Tema do Cenário:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(themes) { theme ->
                                val isSelected = selectedTheme == theme
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) VioletPrimary else DarkCard)
                                        .border(1.dp, if (isSelected) CyanHighlight else DarkBorder, RoundedCornerShape(8.dp))
                                        .clickable { selectedTheme = theme }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(theme, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else TextSecondary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Dificuldade da Sala:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            difficulties.forEach { diff ->
                                val isSelected = selectedDifficulty == diff
                                val diffColor = when (diff) {
                                    "Fácil" -> EmeraldGreen
                                    "Normal" -> CyanHighlight
                                    "Difícil" -> AmberGold
                                    else -> CoralRed
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) diffColor else DarkCard)
                                        .clickable { selectedDifficulty = diff }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        diff,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) DarkBg else TextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Modificador Especial:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            modifiers.forEach { mod ->
                                val isSelected = selectedModifier == mod
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) VioletContainer else DarkCard)
                                        .border(1.dp, if (isSelected) VioletPrimary else DarkBorder, RoundedCornerShape(8.dp))
                                        .clickable { selectedModifier = mod }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(mod, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = if (isSelected) CyanHighlight else TextSecondary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                viewModel.createCustomRoom(
                                    roomName = customRoomName,
                                    theme = selectedTheme,
                                    difficulty = selectedDifficulty,
                                    modifier = selectedModifier,
                                    isOnline = isOnlineModeSelected
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_confirm_create_room"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if (isOnlineModeSelected) EmeraldGreen else CyanHighlight)
                        ) {
                            Icon(if (isOnlineModeSelected) Icons.Default.Groups else Icons.Default.PlayArrow, contentDescription = null, tint = DarkBg, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (isOnlineModeSelected) "CRIAR SALA ONLINE & INICIAR" else "CRIAR SALA & ENTRAR NA AVENTURA",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkBg
                            )
                        }
                    }
                }
            }
        }

        // 4. List of Dungeons
        items(availableDungeons) { dungeon ->
            val diffColor = when (dungeon.difficulty) {
                "Fácil" -> EmeraldGreen
                "Normal" -> CyanHighlight
                "Média" -> CyanHighlight
                "Alta" -> AmberGold
                "Difícil" -> AmberGold
                else -> CoralRed
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dungeon_card_${dungeon.id}"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Top Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(dungeon.tagEmoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(diffColor.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    dungeon.difficulty.uppercase(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = diffColor
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(VioletContainer)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    dungeon.theme,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanHighlight
                                )
                            }
                        }

                        Text(
                            "Recomendado Nv. ${dungeon.recommendedLevel}+",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Title & Subtitle
                    Text(
                        dungeon.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        dungeon.subtitle,
                        fontSize = 11.sp,
                        color = CyanHighlight,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        dungeon.description,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Boss & Loot Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkCard)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Guardião / Boss:", fontSize = 9.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(dungeon.bossName, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CoralRed)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Espólios Notáveis:", fontSize = 9.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(dungeon.lootHighlight, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AmberGold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Enter Dungeon Button
                    Button(
                        onClick = { viewModel.selectDungeon(dungeon, isOnline = isOnlineModeSelected) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_enter_dungeon_${dungeon.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isOnlineModeSelected) EmeraldGreen else VioletPrimary
                        )
                    ) {
                        Icon(
                            if (isOnlineModeSelected) Icons.Default.Groups else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = if (isOnlineModeSelected) DarkBg else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (isOnlineModeSelected) "CRIAR SALA ONLINE & ENTRAR" else "ENTRAR NESTA MASMORRA",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOnlineModeSelected) DarkBg else Color.White
                        )
                    }
                }
            }
        }
    }
}
