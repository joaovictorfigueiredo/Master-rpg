package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.RpgGameViewModel
import com.example.model.GameItem
import com.example.model.ItemType
import com.example.ui.components.WeaponForgeDialog
import com.example.ui.theme.AmberGold
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
fun ItemsScreen(viewModel: RpgGameViewModel) {
    val inventory by viewModel.inventory.collectAsState()
    val character by viewModel.character.collectAsState()
    val showForgeDialog by viewModel.showForgeDialog.collectAsState()

    var selectedFilter by remember { mutableStateOf("TODOS") }
    val filters = listOf("TODOS", "ARMAS", "FERRAMENTAS", "MATERIAIS", "POÇÕES", "RELÍQUIAS")

    val filteredItems = remember(inventory, selectedFilter) {
        when (selectedFilter) {
            "ARMAS" -> inventory.filter { it.itemType == ItemType.WEAPON }
            "FERRAMENTAS" -> inventory.filter { it.itemType == ItemType.TOOL }
            "MATERIAIS" -> inventory.filter { it.itemType == ItemType.MATERIAL }
            "POÇÕES" -> inventory.filter { it.itemType == ItemType.POTION }
            "RELÍQUIAS" -> inventory.filter { it.itemType == ItemType.ARTIFACT || it.isUniqueItem }
            else -> inventory
        }
    }

    val hasForgeTool = inventory.any { it.itemType == ItemType.TOOL }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .testTag("items_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Screen Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(VioletContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Inventory2, contentDescription = null, tint = VioletPrimary, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "INVENTÁRIO & ITENS",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${inventory.size} Itens na Mochila | Ouro: ${character.gold} GP",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    if (hasForgeTool) {
                        Button(
                            onClick = { viewModel.openForgeDialog() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AmberGold,
                                contentColor = androidx.compose.ui.graphics.Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("open_forge_btn")
                        ) {
                            Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("FORJAR ARMA", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Dungeon Weapon Crafting Callout Banner
            if (hasForgeTool) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberGold.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(AmberGold.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Build, contentDescription = null, tint = AmberGold, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "FERRAMENTAS DE FORJA ENCONTRADAS",
                                    color = AmberGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    "Crie armas personalizadas na masmorra que ficam com seu herói entre dungeons até a morte!",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filters) { filter ->
                        val isSelected = selectedFilter == filter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) VioletPrimary else DarkCard)
                                .border(1.dp, if (isSelected) VioletPrimary else DarkBorder, RoundedCornerShape(10.dp))
                                .clickable { selectedFilter = filter }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = filter,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Items List
            if (filteredItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Nenhum item encontrado nesta categoria.", color = TextSecondary, fontSize = 13.sp)
                    }
                }
            } else {
                items(filteredItems, key = { it.id }) { item ->
                    val isEquippedWeapon = item.itemType == ItemType.WEAPON &&
                            (item.weaponData?.isEquipped == true || character.equippedWeapon?.id == item.id)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("item_card_${item.id}"),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isEquippedWeapon) AmberGold else if (item.isUniqueItem) VioletPrimary else DarkBorder
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                when (item.itemType) {
                                                    ItemType.WEAPON -> AmberGold.copy(alpha = 0.15f)
                                                    ItemType.TOOL -> CyanHighlight.copy(alpha = 0.15f)
                                                    ItemType.POTION -> EmeraldGreen.copy(alpha = 0.15f)
                                                    ItemType.ARTIFACT -> VioletContainer
                                                    ItemType.MATERIAL -> DarkCard
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            when (item.itemType) {
                                                ItemType.WEAPON -> Icons.Default.Shield
                                                ItemType.TOOL -> Icons.Default.Build
                                                ItemType.POTION -> Icons.Default.LocalDrink
                                                ItemType.ARTIFACT -> Icons.Default.AutoAwesome
                                                ItemType.MATERIAL -> Icons.Default.FilterList
                                            },
                                            contentDescription = null,
                                            tint = when (item.itemType) {
                                                ItemType.WEAPON -> AmberGold
                                                ItemType.TOOL -> CyanHighlight
                                                ItemType.POTION -> EmeraldGreen
                                                ItemType.ARTIFACT -> VioletPrimary
                                                ItemType.MATERIAL -> TextSecondary
                                            },
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = item.name,
                                            color = TextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = item.bonusStat,
                                            color = if (item.itemType == ItemType.WEAPON) AmberGold else CyanHighlight,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                // Badges
                                Column(horizontalAlignment = Alignment.End) {
                                    if (isEquippedWeapon) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(AmberGold)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("EQUIPADA", color = androidx.compose.ui.graphics.Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else if (item.isDungeonCrafted) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(EmeraldGreen.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("FORJADA", color = EmeraldGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else if (item.isPermanent) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(VioletContainer)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("PERMANENTE", color = VioletPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = item.description,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )

                            if (item.alchemyLineage != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Origem: ${item.alchemyLineage}",
                                    color = VioletPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Actions
                            if (item.itemType == ItemType.WEAPON && !isEquippedWeapon) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { viewModel.equipWeapon(item) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = VioletPrimary,
                                        contentColor = TextPrimary
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .testTag("equip_weapon_btn_${item.id}")
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("EQUIPAR ESTA ARMA", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            } else if (item.itemType == ItemType.POTION) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { viewModel.usePotion(item) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = EmeraldGreen,
                                        contentColor = TextPrimary
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .testTag("use_potion_btn_${item.id}")
                                ) {
                                    Icon(Icons.Default.LocalDrink, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("USAR POÇÃO", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Weapon Forge Modal
        if (showForgeDialog) {
            WeaponForgeDialog(
                inventory = inventory,
                onCraftWeapon = { tool, material, name, type ->
                    viewModel.craftDungeonWeapon(tool, material, name, type)
                },
                onDismiss = { viewModel.closeForgeDialog() }
            )
        }
    }
}
