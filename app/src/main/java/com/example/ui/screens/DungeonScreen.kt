package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.engine.RpgGameViewModel
import com.example.model.DungeonChatMessage
import com.example.model.ItemType
import com.example.model.MessageSenderType
import com.example.model.OnlinePartyMember
import com.example.model.RollSuccessLevel
import com.example.ui.components.AddItemDialog
import com.example.ui.components.CharacterAndBagDialog
import com.example.ui.components.DiceRollDialog
import com.example.ui.components.WeaponForgeDialog
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
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun DungeonScreen(viewModel: RpgGameViewModel) {
    val character by viewModel.character.collectAsState()
    val roomState by viewModel.roomState.collectAsState()
    val inventory by viewModel.inventory.collectAsState()
    val isAiThinking by viewModel.isAiThinking.collectAsState()
    val pendingAction by viewModel.pendingAction.collectAsState()
    val showDiceModal by viewModel.showDiceModal.collectAsState()
    val showForgeDialog by viewModel.showForgeDialog.collectAsState()
    val showCharacterAndBagSheet by viewModel.showCharacterAndBagSheet.collectAsState()
    val showAddItemDialog by viewModel.showAddItemDialog.collectAsState()

    val chatMessages by viewModel.chatMessages.collectAsState()
    val onlineParty by viewModel.onlineParty.collectAsState()
    val isOnlineMode by viewModel.isOnlineMode.collectAsState()
    val currentRoomCode by viewModel.currentRoomCode.collectAsState()
    val currentSituationPrompt by viewModel.currentSituationPrompt.collectAsState()

    val damageTrigger by viewModel.damageAnimTrigger.collectAsState()
    val healTrigger by viewModel.healAnimTrigger.collectAsState()
    val lastDamageAmount by viewModel.lastDamageAmount.collectAsState()
    val lastHealAmount by viewModel.lastHealAmount.collectAsState()

    var playerActionInput by remember { mutableStateOf("") }
    val chatListState = rememberLazyListState()

    // Automatically scroll to the bottom when a new chat message arrives
    LaunchedEffect(chatMessages.size, isAiThinking) {
        if (chatMessages.isNotEmpty()) {
            chatListState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    // Screen Shake Animation for Damage
    val shakeOffset = remember { Animatable(0f) }
    var showDamageOverlay by remember { mutableStateOf(false) }
    var showHealOverlay by remember { mutableStateOf(false) }

    LaunchedEffect(damageTrigger) {
        if (damageTrigger > 0L) {
            showDamageOverlay = true
            for (i in 0..5) {
                val offset = if (i % 2 == 0) 14f else -14f
                shakeOffset.animateTo(offset, tween(40, easing = LinearEasing))
            }
            shakeOffset.animateTo(0f, tween(50))
            delay(500)
            showDamageOverlay = false
        }
    }

    LaunchedEffect(healTrigger) {
        if (healTrigger > 0L) {
            showHealOverlay = true
            delay(800)
            showHealOverlay = false
        }
    }

    // Animated HP & Mana
    val animatedHp by animateFloatAsState(
        targetValue = character.currentHp.toFloat() / character.maxHp.toFloat(),
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "hpAnim"
    )
    val animatedMana by animateFloatAsState(
        targetValue = character.currentMana.toFloat() / character.maxMana.toFloat(),
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "manaAnim"
    )

    val hasForgeTool = inventory.any { it.itemType == ItemType.TOOL }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
            .background(DarkBg)
            .testTag("dungeon_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // ==========================================
            // 1. TOP HEADER & NAVIGATION BAR
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.exitDungeonSession() },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DarkCard)
                            .testTag("btn_change_dungeon")
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Voltar ao Lobby",
                            tint = CyanHighlight,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = roomState.roomTitle.ifBlank { "Masmorra Ativa" },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // Online / Solo Room Tag
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isOnlineMode) EmeraldGreen.copy(alpha = 0.2f) else VioletContainer)
                                    .border(0.5.dp, if (isOnlineMode) EmeraldGreen else VioletPrimary, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isOnlineMode) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(EmeraldGreen)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("ONLINE $currentRoomCode", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                                    } else {
                                        Text("SOLO", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CyanHighlight)
                                    }
                                }
                            }
                        }
                        Text(
                            text = "Andar ${roomState.floorNumber} • Ação via Chat com Mestre IA",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Top Right Action Buttons (Character & Bag Window)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (hasForgeTool) {
                        IconButton(
                            onClick = { viewModel.openForgeDialog() },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AmberGold.copy(alpha = 0.2f))
                                .border(1.dp, AmberGold, RoundedCornerShape(8.dp))
                        ) {
                            Icon(Icons.Default.Build, contentDescription = "Forja", tint = AmberGold, modifier = Modifier.size(18.dp))
                        }
                    }

                    // FICHA E BAG WINDOW BUTTON (Requested by user)
                    Button(
                        onClick = { viewModel.setShowCharacterAndBagSheet(true) },
                        colors = ButtonDefaults.buttonColors(containerColor = VioletPrimary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("btn_open_char_bag")
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("FICHA & BAG", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(DarkBg)
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text("${inventory.size}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CyanHighlight)
                        }
                    }
                }
            }

            // ==========================================
            // 2. HERO STATUS BAR (Compact HUD)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCard)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Hero Mini Avatar & Name
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = character.avatarDrawableRes),
                        contentDescription = character.name,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, CyanHighlight, RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(character.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("${character.race} ${character.characterClass} • Nv. ${character.level}", fontSize = 9.sp, color = TextSecondary)
                    }
                }

                // Health and Mana Progress Bars
                Column(modifier = Modifier.width(150.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("HP: ${character.currentHp}/${character.maxHp}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CoralRed)
                        Text("MANA: ${character.currentMana}/${character.maxMana}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CyanHighlight)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    LinearProgressIndicator(
                        progress = { animatedHp },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = CoralRed,
                        trackColor = DarkSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    LinearProgressIndicator(
                        progress = { animatedMana },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = CyanHighlight,
                        trackColor = DarkSurface
                    )
                }
            }

            // ==========================================
            // 3. ONLINE MULTIPLAYER PARTY BAR (Online Room)
            // ==========================================
            if (isOnlineMode && onlineParty.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurface.copy(alpha = 0.8f))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Groups, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("JOGADORES ONLINE NA SALA:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(onlineParty) { member ->
                            OnlinePartyChip(member = member)
                        }
                    }
                }
            }

            // ==========================================
            // 4. CURRENT PERIL / DANGER SITUATION BANNER
            // ==========================================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .testTag("dungeon_situation_banner"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CoralRed.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(CoralRed.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = CoralRed, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("SITUAÇÃO ATUAL DA MASMORRA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CoralRed)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("• Fale no chat para escapar!", fontSize = 10.sp, color = TextSecondary)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = currentSituationPrompt.ifBlank { "A câmara da masmorra impõe um perigo iminente. O que você vai fazer?" },
                            fontSize = 11.sp,
                            color = TextPrimary,
                            lineHeight = 15.sp,
                            maxLines = 3
                        )
                    }
                }
            }

            // ==========================================
            // 5. MAIN CHAT LOG (THE HEART OF THE GAMEPLAY)
            // ==========================================
            LazyColumn(
                state = chatListState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .testTag("dungeon_chat_list"),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(chatMessages, key = { it.id }) { message ->
                    DungeonChatMessageBubble(message = message)
                }

                // AI Master Thinking Indicator
                if (isAiThinking) {
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = VioletContainer),
                            border = androidx.compose.foundation.BorderStroke(1.dp, VioletPrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ai_thinking_indicator")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = CyanHighlight,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        "Mestre da IA está narrando a história em tempo real...",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyanHighlight
                                    )
                                    Text(
                                        "Avaliando seu plano, rolando D20 e gerando o novo perigo...",
                                        fontSize = 10.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // 6. BOTTOM CHAT INPUT BAR (Action Description)
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                // Quick Tactical Suggestion Chips
                Text(
                    "SUGESTÕES DE AÇÃO RÁPIDA:",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val weaponName = character.equippedWeapon?.name ?: "Arma"
                    val suggestions = listOf(
                        "Atacar com $weaponName com força total",
                        "Esquivar para as sombras e flanquear",
                        "Conjurar feitiço elemental de proteção",
                        "Empurrar o monstro contra a armadilha",
                        "Usar item da bag para me proteger"
                    )
                    suggestions.forEach { text ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkCard)
                                .border(0.5.dp, VioletPrimary, RoundedCornerShape(8.dp))
                                .clickable {
                                    playerActionInput = text
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text, fontSize = 9.sp, color = CyanHighlight)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Input Field and Send Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = playerActionInput,
                        onValueChange = { playerActionInput = it },
                        placeholder = {
                            Text(
                                "Descreva o que vai fazer para se livrar da situação...",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("dungeon_action_input"),
                        singleLine = false,
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanHighlight,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = DarkCard,
                            unfocusedContainerColor = DarkCard
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (playerActionInput.isNotBlank() && !isAiThinking) {
                                viewModel.sendPlayerAction(playerActionInput)
                                playerActionInput = ""
                            }
                        },
                        enabled = playerActionInput.isNotBlank() && !isAiThinking,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VioletPrimary,
                            disabledContainerColor = DarkCard
                        ),
                        contentPadding = PaddingValues(12.dp),
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("btn_send_chat_action")
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Enviar Ação",
                            tint = if (playerActionInput.isNotBlank() && !isAiThinking) Color.White else TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Damage Animation Overlay
        AnimatedVisibility(
            visible = showDamageOverlay,
            enter = fadeIn(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CoralRed.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.FlashOn, contentDescription = null, tint = CoralRed, modifier = Modifier.size(54.dp))
                    Text(
                        "-$lastDamageAmount HP!",
                        color = CoralRed,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text("DANO SOFRIDO EM COMBATE", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Healing Animation Overlay
        AnimatedVisibility(
            visible = showHealOverlay,
            enter = fadeIn(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(EmeraldGreen.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(54.dp))
                    Text(
                        "+$lastHealAmount HP",
                        color = EmeraldGreen,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text("ENERGIA VITAL RESTAURADA", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Manual D20 Dice Rolling Dialog (fallback support)
        if (showDiceModal && pendingAction != null) {
            DiceRollDialog(
                pendingAction = pendingAction!!,
                onRollConfirmed = { rolledD20 ->
                    viewModel.resolveManualDiceRoll(rolledD20)
                },
                onDismiss = { viewModel.dismissDiceModal() }
            )
        }

        // Weapon Forge Dialog
        if (showForgeDialog) {
            WeaponForgeDialog(
                inventory = inventory,
                onCraftWeapon = { tool, material, name, type ->
                    viewModel.craftDungeonWeapon(tool, material, name, type)
                },
                onDismiss = { viewModel.closeForgeDialog() }
            )
        }

        // Character Information & Item Bag Dialog (Window)
        if (showCharacterAndBagSheet) {
            CharacterAndBagDialog(
                character = character,
                inventory = inventory,
                onEquipWeapon = { item ->
                    item.weaponData?.let { wep ->
                        viewModel.equipWeapon(wep)
                    }
                },
                onUsePotion = { item ->
                    viewModel.usePotion(item)
                },
                onOpenForge = {
                    viewModel.setShowCharacterAndBagSheet(false)
                    viewModel.openForgeDialog()
                },
                onOpenAddItem = {
                    viewModel.setShowAddItemDialog(true)
                },
                onDismiss = {
                    viewModel.setShowCharacterAndBagSheet(false)
                }
            )
        }

        // Add Item Dialog (for adding weapons, tools, materials, potions to bag)
        if (showAddItemDialog) {
            AddItemDialog(
                onAddItem = { item ->
                    viewModel.addItemToInventory(item)
                },
                onDismiss = {
                    viewModel.setShowAddItemDialog(false)
                }
            )
        }
    }
}

/**
 * Message bubble in the Dungeon Chat
 */
@Composable
fun DungeonChatMessageBubble(message: DungeonChatMessage) {
    val isLocal = message.senderType == MessageSenderType.LOCAL_PLAYER
    val isAi = message.senderType == MessageSenderType.DUNGEON_MASTER_AI
    val isOnlineFriend = message.senderType == MessageSenderType.ONLINE_PLAYER

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isLocal) Alignment.End else Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(if (isAi) 1f else 0.92f),
            horizontalArrangement = if (isLocal) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            if (!isLocal) {
                Image(
                    painter = painterResource(id = message.avatarRes),
                    contentDescription = message.senderName,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, if (isAi) AmberGold else EmeraldGreen, RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(
                modifier = Modifier.weight(1f, fill = false),
                horizontalAlignment = if (isLocal) Alignment.End else Alignment.Start
            ) {
                // Header (Sender Name & Role)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = if (isLocal) Arrangement.End else Arrangement.Start
                ) {
                    Text(
                        message.senderName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isAi -> AmberGold
                            isLocal -> CyanHighlight
                            else -> EmeraldGreen
                        }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(DarkCard)
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(message.senderRole, fontSize = 8.sp, color = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                // Bubble Container
                Card(
                    shape = RoundedCornerShape(
                        topStart = 14.dp,
                        topEnd = 14.dp,
                        bottomStart = if (isLocal) 14.dp else 2.dp,
                        bottomEnd = if (isLocal) 2.dp else 14.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            isAi -> DarkSurface
                            isLocal -> VioletContainer
                            else -> DarkCard
                        }
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        when {
                            isAi -> AmberGold.copy(alpha = 0.5f)
                            isLocal -> VioletPrimary
                            else -> EmeraldGreen.copy(alpha = 0.5f)
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        // If AI rolled D20, display tactical resolution badge
                        if (message.d20Roll != null) {
                            val successColor = when (message.successLevel) {
                                RollSuccessLevel.CRITICAL_SUCCESS -> AmberGold
                                RollSuccessLevel.SUCCESS -> EmeraldGreen
                                RollSuccessLevel.FAILURE -> CoralRed
                                RollSuccessLevel.CRITICAL_FAILURE -> CoralRed
                                else -> CyanHighlight
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DarkBg)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "🎲 TESTE D20: ${message.d20Roll} (Tot: ${message.totalCheckResult})",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = successColor
                                )
                                Text(
                                    when (message.successLevel) {
                                        RollSuccessLevel.CRITICAL_SUCCESS -> "CRÍTICO!"
                                        RollSuccessLevel.SUCCESS -> "SUCESSO!"
                                        RollSuccessLevel.FAILURE -> "FALHA!"
                                        RollSuccessLevel.CRITICAL_FAILURE -> "DESASTRE!"
                                        else -> ""
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = successColor
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        // Message Text Body
                        Text(
                            text = message.messageText,
                            fontSize = 12.sp,
                            color = TextPrimary,
                            lineHeight = 17.sp
                        )

                        // Health or Mana Delta Pill
                        if (message.hpDelta != null && message.hpDelta != 0) {
                            Spacer(modifier = Modifier.height(6.dp))
                            val isDamage = message.hpDelta < 0
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isDamage) CoralRed.copy(alpha = 0.2f) else EmeraldGreen.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    if (isDamage) "❤️ DANO: ${message.hpDelta} HP" else "✨ CURA: +${message.hpDelta} HP",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDamage) CoralRed else EmeraldGreen
                                )
                            }
                        }
                    }
                }
            }

            if (isLocal) {
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = message.avatarRes),
                    contentDescription = message.senderName,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, CyanHighlight, RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

/**
 * Chip showing an online party member in the room
 */
@Composable
fun OnlinePartyChip(member: OnlinePartyMember) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DarkCard)
            .border(1.dp, if (member.isLocalPlayer) CyanHighlight else EmeraldGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = member.avatarRes),
            contentDescription = member.name,
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    member.name.split(" ").first(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (member.isLocalPlayer) CyanHighlight else TextPrimary
                )
                Spacer(modifier = Modifier.width(3.dp))
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(EmeraldGreen)
                )
            }
            Text(
                "HP ${member.currentHp}/${member.maxHp}",
                fontSize = 8.sp,
                color = CoralRed
            )
        }
    }
}
