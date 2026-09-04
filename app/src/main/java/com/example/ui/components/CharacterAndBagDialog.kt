package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.window.Dialog
import com.example.model.CharacterModel
import com.example.model.GameItem
import com.example.model.ItemType
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
fun CharacterAndBagDialog(
    character: CharacterModel,
    inventory: List<GameItem>,
    onEquipWeapon: (GameItem) -> Unit,
    onUsePotion: (GameItem) -> Unit,
    onOpenForge: () -> Unit,
    onOpenAddItem: () -> Unit,
    onDismiss: () -> Unit
) {
    var activeTab by remember { mutableIntStateOf(0) } // 0: Ficha, 1: Bag de Itens
    var filterType by remember { mutableStateOf<ItemType?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, Brush.verticalGradient(listOf(VioletPrimary, CyanHighlight)), RoundedCornerShape(24.dp))
                .testTag("character_and_bag_dialog"),
            color = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(VioletContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (activeTab == 0) Icons.Default.Person else Icons.Default.Shield,
                                contentDescription = null,
                                tint = VioletPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                if (activeTab == 0) "FICHA DO PERSONAGEM" else "BAG DE ITENS (MOCHILA)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                "${character.name} • ${character.race} • ${character.characterClass} • Nv. ${character.level}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Navigation Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkCard)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (activeTab == 0) VioletPrimary else Color.Transparent)
                            .clickable { activeTab = 0 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "STATUS & SKILLS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeTab == 0) Color.White else TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (activeTab == 1) VioletPrimary else Color.Transparent)
                            .clickable { activeTab = 1 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "BAG DE ITENS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activeTab == 1) Color.White else TextSecondary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CyanHighlight.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    "${inventory.size}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanHighlight
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (activeTab == 0) {
                    // TAB 1: Character Info
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Portrait & Vitals Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkCard),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(id = character.avatarDrawableRes),
                                        contentDescription = character.name,
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .border(1.5.dp, CyanHighlight, RoundedCornerShape(14.dp)),
                                        contentScale = ContentScale.Crop
                                    )

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            character.name,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            "${character.archetype} • Nível ${character.level}",
                                            fontSize = 11.sp,
                                            color = CyanHighlight,
                                            fontWeight = FontWeight.SemiBold
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        // HP Bar
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("HP", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CoralRed)
                                            Text("${character.currentHp}/${character.maxHp}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CoralRed)
                                        }
                                        LinearProgressIndicator(
                                            progress = { character.currentHp.toFloat() / character.maxHp.toFloat() },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp)),
                                            color = CoralRed,
                                            trackColor = DarkBorder
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // Mana Bar
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("MP", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyanHighlight)
                                            Text("${character.currentMana}/${character.maxMana}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyanHighlight)
                                        }
                                        LinearProgressIndicator(
                                            progress = { character.currentMana.toFloat() / character.maxMana.toFloat() },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp)),
                                            color = CyanHighlight,
                                            trackColor = DarkBorder
                                        )
                                    }
                                }
                            }
                        }

                        // Attributes Grid
                        item {
                            Text("ATRIBUTOS & MODIFICADORES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AmberGold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    "FOR" to character.strength,
                                    "DES" to character.dexterity,
                                    "INT" to character.intelligence,
                                    "VIT" to character.vitality,
                                    "CAR" to character.charisma
                                ).forEach { (statName, statVal) ->
                                    val mod = (statVal - 10) / 2
                                    val modStr = if (mod >= 0) "+$mod" else "$mod"
                                    Card(
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(statName, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                            Text("$statVal", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                            Text(modStr, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CyanHighlight)
                                        }
                                    }
                                }
                            }
                        }

                        // Equipped Weapon
                        item {
                            Text("ARMA EQUIPADA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AmberGold)
                            Spacer(modifier = Modifier.height(4.dp))
                            val wep = character.equippedWeapon
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkCard),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CoralRed.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(CoralRed.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Shield, contentDescription = null, tint = CoralRed, modifier = Modifier.size(22.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            wep?.name ?: "Mãos Desarmadas",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            wep?.description ?: "Sem arma equipada.",
                                            fontSize = 10.sp,
                                            color = TextSecondary,
                                            maxLines = 2
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            "${wep?.bonusStat ?: "+2 Dano"} • ${wep?.damageType ?: "Impacto"}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CoralRed
                                        )
                                    }
                                }
                            }
                        }

                        // Usable Skills
                        item {
                            Text("HABILIDADES DISPONÍVEIS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AmberGold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                character.skills.forEach { skill ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(skill.icon, fontSize = 22.sp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(skill.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("Custo: ${skill.manaCost} MP", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyanHighlight)
                                                }
                                                Text(skill.description, fontSize = 10.sp, color = TextSecondary)
                                            }
                                            Text(
                                                if (skill.isHealing) "+${skill.power} Cura" else "${skill.power} Dano",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (skill.isHealing) EmeraldGreen else AmberGold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // TAB 2: Bag de Itens (Mochila)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        // Action Bar: "+ ADICIONAR ITEM À BAG"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "${inventory.size} Itens na Mochila",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )

                            Button(
                                onClick = onOpenAddItem,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = VioletPrimary),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("btn_open_add_item")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ADICIONAR ITEM", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Filter Chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item {
                                FilterChip(
                                    label = "Todos",
                                    isSelected = filterType == null,
                                    onClick = { filterType = null }
                                )
                            }
                            item {
                                FilterChip(
                                    label = "Armas",
                                    isSelected = filterType == ItemType.WEAPON,
                                    onClick = { filterType = ItemType.WEAPON }
                                )
                            }
                            item {
                                FilterChip(
                                    label = "Ferramentas",
                                    isSelected = filterType == ItemType.TOOL,
                                    onClick = { filterType = ItemType.TOOL }
                                )
                            }
                            item {
                                FilterChip(
                                    label = "Materiais",
                                    isSelected = filterType == ItemType.MATERIAL,
                                    onClick = { filterType = ItemType.MATERIAL }
                                )
                            }
                            item {
                                FilterChip(
                                    label = "Poções",
                                    isSelected = filterType == ItemType.POTION,
                                    onClick = { filterType = ItemType.POTION }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val filteredList = if (filterType != null) {
                            inventory.filter { it.itemType == filterType }
                        } else inventory

                        if (filteredList.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Nenhum item nesta categoria.", color = TextSecondary, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(onClick = onOpenAddItem, colors = ButtonDefaults.buttonColors(containerColor = VioletPrimary)) {
                                        Text("Adicionar Item à Mochila", fontSize = 11.sp)
                                    }
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(filteredList) { item ->
                                    val isEquipped = item.weaponData?.id == character.equippedWeapon?.id
                                    val (badgeColor, typeIcon) = when (item.itemType) {
                                        ItemType.WEAPON -> Pair(CoralRed, Icons.Default.Shield)
                                        ItemType.TOOL -> Pair(AmberGold, Icons.Default.Build)
                                        ItemType.MATERIAL -> Pair(CyanHighlight, Icons.Default.Diamond)
                                        ItemType.POTION -> Pair(EmeraldGreen, Icons.Default.Science)
                                        ItemType.ARTIFACT -> Pair(VioletPrimary, Icons.Default.FlashOn)
                                    }

                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isEquipped) EmeraldGreen else DarkBorder
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(38.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(badgeColor.copy(alpha = 0.2f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(typeIcon, contentDescription = null, tint = badgeColor, modifier = Modifier.size(20.dp))
                                            }

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        item.name,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = TextPrimary
                                                    )
                                                    if (isEquipped) {
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            "[EQUIPADA]",
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = EmeraldGreen
                                                        )
                                                    }
                                                    if (item.isDungeonCrafted) {
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            "[FORJADA]",
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = AmberGold
                                                        )
                                                    }
                                                }
                                                Text(
                                                    item.description,
                                                    fontSize = 10.sp,
                                                    color = TextSecondary,
                                                    maxLines = 2
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    item.bonusStat,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = badgeColor
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(8.dp))

                                            // Quick Actions
                                            when {
                                                item.itemType == ItemType.WEAPON && !isEquipped -> {
                                                    Button(
                                                        onClick = { onEquipWeapon(item) },
                                                        shape = RoundedCornerShape(8.dp),
                                                        colors = ButtonDefaults.buttonColors(containerColor = VioletPrimary),
                                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                                    ) {
                                                        Text("Equipar", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                                item.itemType == ItemType.POTION -> {
                                                    Button(
                                                        onClick = { onUsePotion(item) },
                                                        shape = RoundedCornerShape(8.dp),
                                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                                    ) {
                                                        Text("Usar", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                                item.itemType == ItemType.TOOL -> {
                                                    Button(
                                                        onClick = onOpenForge,
                                                        shape = RoundedCornerShape(8.dp),
                                                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                                    ) {
                                                        Text("Forjar", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DarkBg)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Text("VOLTAR AO CHAT DA MASMORRA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            }
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) VioletPrimary else DarkCard)
            .border(1.dp, if (isSelected) CyanHighlight else DarkBorder, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else TextSecondary
        )
    }
}
