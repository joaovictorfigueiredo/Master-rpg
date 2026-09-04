package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.GameItem
import com.example.model.ItemType
import com.example.model.Weapon
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

@Composable
fun AddItemDialog(
    onAddItem: (GameItem) -> Unit,
    onDismiss: () -> Unit
) {
    var isCustomTab by remember { mutableStateOf(false) }

    // Custom Form State
    var customName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(ItemType.WEAPON) }
    var customBonus by remember { mutableStateOf("+20 Dano") }
    var customDescription by remember { mutableStateOf("") }

    val presetItems = listOf(
        GameItem(
            id = "preset-tool-${System.currentTimeMillis()}-1",
            name = "Kit de Forja do Mestre",
            description = "Ferramenta essencial para forjar novas armas durante as masmorras.",
            itemType = ItemType.TOOL,
            isPermanent = true,
            bonusStat = "Habilita Forja na Dungeon"
        ),
        GameItem(
            id = "preset-mat-${System.currentTimeMillis()}-2",
            name = "Lingote de Mitril Sagrado",
            description = "Minério raro e brilhante. Forja armas de poder devastador.",
            itemType = ItemType.MATERIAL,
            isPermanent = false,
            bonusStat = "+20 Dano em Forja"
        ),
        GameItem(
            id = "preset-mat-${System.currentTimeMillis()}-3",
            name = "Cristal Elemental de Fogo",
            description = "Gema incandescente de magma que imbuí chamas às armas.",
            itemType = ItemType.MATERIAL,
            isPermanent = false,
            bonusStat = "+14 Dano de Fogo"
        ),
        GameItem(
            id = "preset-pot-${System.currentTimeMillis()}-4",
            name = "Poção de Vida Maior (+45 HP)",
            description = "Elixir restaurador que recupera 45 pontos de vida imediatamente.",
            itemType = ItemType.POTION,
            healAmount = 45,
            bonusStat = "+45 HP Cura"
        ),
        GameItem(
            id = "preset-pot-${System.currentTimeMillis()}-5",
            name = "Elixir de Mana Puro (+30 MP)",
            description = "Essência mágica concentrada que recarrega 30 pontos de mana.",
            itemType = ItemType.POTION,
            manaAmount = 30,
            bonusStat = "+30 MP Mana"
        ),
        GameItem(
            id = "preset-wep-${System.currentTimeMillis()}-6",
            name = "Espada Vorpal do Dragão",
            description = "Lâmina forjada em escamas de dragão que corta qualquer armadura.",
            itemType = ItemType.WEAPON,
            isPermanent = true,
            bonusStat = "+24 Dano de Fogo",
            weaponData = Weapon(
                id = "wep-vorpal-${System.currentTimeMillis()}",
                name = "Espada Vorpal do Dragão",
                description = "Lâmina forjada em escamas de dragão.",
                damage = 24,
                damageType = "Fogo Dragônico",
                bonusStat = "+24 Dano",
                isDungeonCrafted = true,
                isEquipped = false
            )
        ),
        GameItem(
            id = "preset-rel-${System.currentTimeMillis()}-7",
            name = "Amuleto de Proteção Arcana",
            description = "Artefato rúnico antigo que dissipa parte dos golpes recebidos.",
            itemType = ItemType.ARTIFACT,
            isPermanent = true,
            bonusStat = "+12 Proteção Mágica"
        )
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, Brush.verticalGradient(listOf(VioletPrimary, CyanHighlight)), RoundedCornerShape(24.dp))
                .testTag("add_item_dialog"),
            color = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Title Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "ADICIONAR ITEM À BAG",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            "Escolha um item pronto ou crie um customizado",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mode Tabs
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
                            .background(if (!isCustomTab) VioletPrimary else Color.Transparent)
                            .clickable { isCustomTab = false }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "ITENS PRONTOS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isCustomTab) Color.White else TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isCustomTab) VioletPrimary else Color.Transparent)
                            .clickable { isCustomTab = true }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "CRIAR CUSTOMIZADO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCustomTab) Color.White else TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!isCustomTab) {
                    // Preset items list
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        presetItems.forEach { preset ->
                            val (badgeColor, typeIcon) = when (preset.itemType) {
                                ItemType.WEAPON -> Pair(CoralRed, Icons.Default.Shield)
                                ItemType.TOOL -> Pair(AmberGold, Icons.Default.Build)
                                ItemType.MATERIAL -> Pair(CyanHighlight, Icons.Default.Diamond)
                                ItemType.POTION -> Pair(EmeraldGreen, Icons.Default.Science)
                                ItemType.ARTIFACT -> Pair(VioletPrimary, Icons.Default.FlashOn)
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onAddItem(preset)
                                        onDismiss()
                                    },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkCard),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(badgeColor.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(typeIcon, contentDescription = null, tint = badgeColor, modifier = Modifier.size(20.dp))
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            preset.name,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            preset.description,
                                            fontSize = 10.sp,
                                            color = TextSecondary,
                                            maxLines = 2
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            preset.bonusStat,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = badgeColor
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Button(
                                        onClick = {
                                            onAddItem(preset)
                                            onDismiss()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = VioletPrimary),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("+ Bag", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Custom creation form
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Nome do Item:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        OutlinedTextField(
                            value = customName,
                            onValueChange = { customName = it },
                            placeholder = { Text("Ex: Lâmina do Trovão Negro, Elixir Supremo...", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VioletPrimary,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Text("Tipo de Item:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                ItemType.WEAPON to "Arma",
                                ItemType.TOOL to "Ferramenta",
                                ItemType.MATERIAL to "Material",
                                ItemType.POTION to "Poção",
                                ItemType.ARTIFACT to "Relíquia"
                            ).forEach { (type, label) ->
                                val isSelected = selectedType == type
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) VioletPrimary else DarkCard)
                                        .border(1.dp, if (isSelected) CyanHighlight else DarkBorder, RoundedCornerShape(8.dp))
                                        .clickable { selectedType = type }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        label,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else TextSecondary
                                    )
                                }
                            }
                        }

                        Text("Bônus / Poder (Ex: +25 Dano, +50 HP):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        OutlinedTextField(
                            value = customBonus,
                            onValueChange = { customBonus = it },
                            placeholder = { Text("Ex: +25 Dano Sagrado", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VioletPrimary,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Text("Descrição do Item:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        OutlinedTextField(
                            value = customDescription,
                            onValueChange = { customDescription = it },
                            placeholder = { Text("Descreva os detalhes e lendas deste item...", fontSize = 12.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VioletPrimary,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Button(
                            onClick = {
                                val name = if (customName.isNotBlank()) customName else "Item Forjado"
                                val desc = if (customDescription.isNotBlank()) customDescription else "Item adicionado diretamente à mochila do aventureiro."
                                val wep = if (selectedType == ItemType.WEAPON) {
                                    Weapon(
                                        id = "custom-wep-${System.currentTimeMillis()}",
                                        name = name,
                                        description = desc,
                                        damage = 22,
                                        damageType = "Personalizado",
                                        bonusStat = customBonus,
                                        isDungeonCrafted = true
                                    )
                                } else null

                                val newItem = GameItem(
                                    id = "custom-item-${System.currentTimeMillis()}",
                                    name = name,
                                    description = desc,
                                    itemType = selectedType,
                                    isPermanent = selectedType == ItemType.WEAPON || selectedType == ItemType.TOOL || selectedType == ItemType.ARTIFACT,
                                    bonusStat = customBonus,
                                    weaponData = wep,
                                    healAmount = if (selectedType == ItemType.POTION && customBonus.contains("HP", ignoreCase = true)) 40 else 0,
                                    manaAmount = if (selectedType == ItemType.POTION && customBonus.contains("MP", ignoreCase = true)) 30 else 0
                                )

                                onAddItem(newItem)
                                onDismiss()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = VioletPrimary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ADICIONAR À MINHA BAG", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
