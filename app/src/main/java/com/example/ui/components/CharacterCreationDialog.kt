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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
fun CharacterCreationDialog(
    isAiThinking: Boolean,
    onCreateCharacter: (description: String) -> Unit,
    onDismiss: () -> Unit
) {
    var descriptionText by remember {
        mutableStateOf("Kaelen, uma feiticeira elfa sombria com olhos violetas brilhantes e cajado arcano de fogo que canaliza magias ancestrais.")
    }

    val suggestionChips = listOf(
        "Maga elfa sombria com cajado arcano",
        "Assassino ladino com adagas venenosas",
        "Guerreiro dragão colossal com machado brutal",
        "Paladino sagrado com espada e prece de cura",
        "Minotauro bárbaro feral com força esmagadora"
    )

    Dialog(onDismissRequest = { if (!isAiThinking) onDismiss() }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, Brush.verticalGradient(listOf(VioletPrimary, CyanHighlight)), RoundedCornerShape(24.dp))
                .testTag("character_creation_dialog"),
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
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = VioletPrimary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("CRIAR PERSONAGEM COM IA", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                            Text("Identificação Automática de Raça & Classe", color = CyanHighlight, fontSize = 11.sp)
                        }
                    }
                    IconButton(onClick = onDismiss, enabled = !isAiThinking, modifier = Modifier.testTag("close_char_create_btn")) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Gallery of visual art matches
                Text("RETRATOS & AVATARES COMPATÍVEIS PELA IA", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        R.drawable.avatar_elf_mage to "Elfo / Mago",
                        R.drawable.avatar_shadow_rogue to "Ladino / Sombra",
                        R.drawable.avatar_beast_warrior to "Monstro / Fera",
                        R.drawable.img_hero_portrait to "Humano / Cavaleiro"
                    ).forEach { (drawable, label) ->
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                            ) {
                                Image(
                                    painter = painterResource(id = drawable),
                                    contentDescription = label,
                                    modifier = Modifier.matchParentSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(label, color = TextSecondary, fontSize = 8.sp, maxLines = 1)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Input Box
                Text("DESCREVA SEU PERSONAGEM LIVREMENTE", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "A IA identificará Raça, Classe, Arquétipo, gerará 3 Skills personalizadas, 1 Arma inicial e atribuirá a ilustração visual correspondente.",
                    color = TextSecondary.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = descriptionText,
                    onValueChange = { descriptionText = it },
                    placeholder = { Text("Ex: Um bárbaro minotauro com chifres afiados que usa machado...", color = TextSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("char_description_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VioletPrimary,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(14.dp),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Suggestion chips
                Text("SUGESTÕES RÁPIDAS", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(suggestionChips) { chip ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkCard)
                                .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                                .clickable { descriptionText = chip }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(chip, color = CyanHighlight, fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // AI Capability Highlights
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkCard)
                        .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = VioletPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("A IA calcula atributos base (FOR, DES, INT, VIT, CAR)", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AmberGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cria 3 Skills temáticas usáveis com gasto de Mana", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Equipa arma inicial compatível e gera retrato visual", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (descriptionText.isNotBlank() && !isAiThinking) {
                            onCreateCharacter(descriptionText)
                        }
                    },
                    enabled = descriptionText.isNotBlank() && !isAiThinking,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("generate_character_ai_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VioletPrimary,
                        contentColor = TextPrimary,
                        disabledContainerColor = DarkBorder,
                        disabledContentColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    if (isAiThinking) {
                        CircularProgressIndicator(color = TextPrimary, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("ANALISANDO DESCRIÇÃO...", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("GERAR & CRIAR PERSONAGEM", fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 0.5.sp)
                    }
                }
            }
        }
    }
}
