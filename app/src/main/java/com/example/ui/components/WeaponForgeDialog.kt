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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.model.GameItem
import com.example.model.ItemType
import com.example.ui.theme.AmberGold
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
fun WeaponForgeDialog(
    inventory: List<GameItem>,
    onCraftWeapon: (tool: GameItem, material: GameItem, name: String, type: String) -> Unit,
    onDismiss: () -> Unit
) {
    val tools = inventory.filter { it.itemType == ItemType.TOOL }
    val materials = inventory.filter { it.itemType == ItemType.MATERIAL }

    var selectedTool by remember { mutableStateOf(tools.firstOrNull()) }
    var selectedMaterial by remember { mutableStateOf(materials.firstOrNull()) }

    val weaponTypes = listOf("Espada Rúnica", "Adagas Duplas", "Machado de Guerra", "Cajado Arcano", "Arco Místico")
    var selectedWeaponType by remember { mutableStateOf(weaponTypes[0]) }
    var weaponNameInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, Brush.verticalGradient(listOf(AmberGold, DarkBorder)), RoundedCornerShape(24.dp))
                .testTag("weapon_forge_dialog"),
            color = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
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
                            Icon(Icons.Default.Build, contentDescription = null, tint = AmberGold, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("FORJA DE ARMAS", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Text("Armas Forjadas são Permanentes", color = EmeraldGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_forge_btn")) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Anvil & Forge Illustration
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.forge_weapons_anvil),
                        contentDescription = "Forja de Armas",
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(androidx.compose.ui.graphics.Color.Transparent, DarkSurface.copy(alpha = 0.85f))
                                )
                            )
                    )
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = AmberGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Fica com você até perder na masmorra!",
                            color = AmberGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Tool Selection
                Text("1. FERRAMENTA DE FORJA", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                Spacer(modifier = Modifier.height(6.dp))
                if (tools.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkCard)
                            .padding(12.dp)
                    ) {
                        Text("Nenhuma ferramenta encontrada na masmorra ainda! Explore caminhos de oficina.", color = TextSecondary, fontSize = 12.sp)
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        tools.forEach { tool ->
                            val isSelected = selectedTool?.id == tool.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) VioletContainer else DarkCard)
                                    .border(1.dp, if (isSelected) VioletPrimary else DarkBorder, RoundedCornerShape(10.dp))
                                    .clickable { selectedTool = tool }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(tool.name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(tool.bonusStat, color = AmberGold, fontSize = 11.sp)
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = VioletPrimary)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Material Selection
                Text("2. MINÉRIO OU MATERIAL", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                Spacer(modifier = Modifier.height(6.dp))
                if (materials.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkCard)
                            .padding(12.dp)
                    ) {
                        Text("Sem materiais na mochila! Derrote monstros ou explore baús para coletar lingotes.", color = TextSecondary, fontSize = 12.sp)
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        materials.forEach { mat ->
                            val isSelected = selectedMaterial?.id == mat.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) DarkCard.copy(alpha = 0.9f) else DarkCard)
                                    .border(1.dp, if (isSelected) CyanHighlight else DarkBorder, RoundedCornerShape(10.dp))
                                    .clickable { selectedMaterial = mat }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(mat.name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(mat.bonusStat, color = CyanHighlight, fontSize = 11.sp)
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = CyanHighlight)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Weapon Type Selection
                Text("3. TIPO DA ARMA", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(weaponTypes) { type ->
                        val isSelected = selectedWeaponType == type
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) VioletPrimary else DarkCard)
                                .border(1.dp, if (isSelected) VioletPrimary else DarkBorder, RoundedCornerShape(10.dp))
                                .clickable { selectedWeaponType = type }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(type, color = if (isSelected) TextPrimary else TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 4. Custom Weapon Name
                Text("4. NOME PERSONALIZADO (OPCIONAL)", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = weaponNameInput,
                    onValueChange = { weaponNameInput = it },
                    placeholder = { Text("Ex: Lâmina do Trovão Negro", color = TextSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("weapon_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGold,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Confirm Craft Button
                val canCraft = selectedTool != null && selectedMaterial != null
                Button(
                    onClick = {
                        if (canCraft) {
                            val defaultName = if (weaponNameInput.isNotBlank()) weaponNameInput else "$selectedWeaponType de ${selectedMaterial!!.name.replace("Lingote de ", "").replace("Minério de ", "")}"
                            onCraftWeapon(selectedTool!!, selectedMaterial!!, defaultName, selectedWeaponType)
                        }
                    },
                    enabled = canCraft,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_craft_weapon_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberGold,
                        contentColor = androidx.compose.ui.graphics.Color.Black,
                        disabledContainerColor = DarkBorder,
                        disabledContentColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (canCraft) "FORJAR ARMA PERMANENTE" else "ESCOLHA FERRAMENTA E MATERIAL",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
