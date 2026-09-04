package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.RpgGameViewModel
import com.example.model.GameItem
import com.example.ui.theme.CoralHp
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkOutline
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletBorder
import com.example.ui.theme.VioletContainer
import com.example.ui.theme.VioletLight
import com.example.ui.theme.VioletPrimary

@Composable
fun AlchemyScreen(viewModel: RpgGameViewModel) {
    val inventory by viewModel.inventory.collectAsState()
    val latestCrafted by viewModel.latestCraftedItem.collectAsState()

    var selectedItemA by remember { mutableStateOf<GameItem?>(null) }
    var selectedItemB by remember { mutableStateOf<GameItem?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(VioletContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Science,
                                contentDescription = "Alquimia",
                                tint = VioletPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "CRÁTERA ALQUÍMICA & FUSÃO",
                            color = VioletPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Combine dois itens coletados na dungeon para gerar um Item Único com efeitos mistos. Itens únicos se tornam permanentes e viajam com seu personagem para futuras campanhas!",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Transmutation Crucible Slots
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "RECIPIENTES DE TRANSMUTAÇÃO",
                        color = VioletPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Slot A
                        AlchemySlotBox(
                            item = selectedItemA,
                            label = "Item Base 1",
                            onClear = { selectedItemA = null }
                        )

                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Mais",
                            tint = VioletPrimary,
                            modifier = Modifier.size(24.dp)
                        )

                        // Slot B
                        AlchemySlotBox(
                            item = selectedItemB,
                            label = "Item Base 2",
                            onClear = { selectedItemB = null }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val canFuse = selectedItemA != null && selectedItemB != null && selectedItemA?.id != selectedItemB?.id
                    Button(
                        onClick = {
                            if (canFuse) {
                                viewModel.combineAlchemyItems(selectedItemA!!, selectedItemB!!)
                                selectedItemA = null
                                selectedItemB = null
                            }
                        },
                        enabled = canFuse,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VioletContainer,
                            disabledContainerColor = DarkSurfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = if (canFuse) androidx.compose.foundation.BorderStroke(1.dp, VioletBorder) else androidx.compose.foundation.BorderStroke(1.dp, DarkOutline),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("transmute_button")
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = "Transmutar",
                            tint = if (canFuse) VioletPrimary else TextMuted
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Transmutar em Item Único Permanente",
                            color = if (canFuse) VioletPrimary else TextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // New Crafted Item Celebration Banner
        item {
            AnimatedVisibility(visible = latestCrafted != null) {
                latestCrafted?.let { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, VioletPrimary, RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = "Item Lendário",
                                        tint = VioletPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "ITEM ÚNICO CRIADO!",
                                        color = VioletPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                }

                                Text(
                                    text = "PERMANENTE",
                                    color = EmeraldSuccess,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = item.name,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = item.description,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Bônus Misto: ${item.bonusStat}",
                                color = EmeraldSuccess,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            item.alchemyLineage?.let { lineage ->
                                Text(
                                    text = "Origem: $lineage",
                                    color = VioletLight,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Available Inventory Selector
        item {
            Text(
                text = "SELECIONE OS ITENS DO SEU INVENTÁRIO",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        items(inventory) { item ->
            val isSelectedA = selectedItemA?.id == item.id
            val isSelectedB = selectedItemB?.id == item.id
            val isSelected = isSelectedA || isSelectedB

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isSelectedA) {
                            selectedItemA = null
                        } else if (isSelectedB) {
                            selectedItemB = null
                        } else if (selectedItemA == null) {
                            selectedItemA = item
                        } else if (selectedItemB == null) {
                            selectedItemB = item
                        }
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) DarkSurfaceVariant else DarkSurface
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) VioletPrimary else if (item.isUniqueItem) VioletBorder else DarkBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = item.name,
                                color = if (item.isUniqueItem) VioletPrimary else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            if (item.isUniqueItem) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(VioletContainer)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "ÚNICO / PERMANENTE",
                                        color = VioletPrimary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(DarkSurfaceVariant)
                                        .border(1.dp, DarkOutline, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "COMUM (PERDE NA MORTE)",
                                        color = CoralHp,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = item.description,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(VioletPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Selecionado",
                                tint = DarkBg,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlchemySlotBox(
    item: GameItem?,
    label: String,
    onClear: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(110.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceVariant)
            .border(
                1.dp,
                if (item != null) VioletPrimary else DarkOutline,
                RoundedCornerShape(16.dp)
            )
            .clickable { onClear() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (item != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = item.name,
                    color = VioletLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "(Toque p/ remover)",
                    color = TextMuted,
                    fontSize = 9.sp
                )
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Science,
                    contentDescription = "Slot Vazio",
                    tint = TextMuted,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}
