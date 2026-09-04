package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import com.example.R
import com.example.engine.RpgGameViewModel
import com.example.model.Skill
import com.example.model.Weapon
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
fun CharacterCreationScreen(viewModel: RpgGameViewModel) {
    val isAiThinking by viewModel.isAiThinking.collectAsState()

    var isAiMode by remember { mutableStateOf(true) }
    var aiDescriptionText by remember {
        mutableStateOf("Kaelen, uma poderosa feiticeira elfa sombria com olhos violetas arcanos e cajado elemental de fogo.")
    }

    // Manual creation fields
    var charName by remember { mutableStateOf("Kaelen") }
    var selectedRace by remember { mutableStateOf("Elfo") }
    var selectedClass by remember { mutableStateOf("Mago Arcano") }
    var selectedAvatarRes by remember { mutableStateOf(R.drawable.avatar_elf_mage) }

    val presetAvatars = listOf(
        R.drawable.avatar_elf_mage to "Elfo / Mago",
        R.drawable.avatar_shadow_rogue to "Ladino / Sombra",
        R.drawable.avatar_beast_warrior to "Draconato / Berserker",
        R.drawable.img_hero_portrait to "Guerreiro / Paladino"
    )

    val aiSuggestions = listOf(
        "Maga elfa sombria com cajado arcano de fogo",
        "Assassino ladino com adagas venenosas e manto das sombras",
        "Guerreiro draconato colossal com machado brutal de duas mãos",
        "Paladino sagrado com espada rúnica e prece de cura",
        "Minotauro bárbaro feral com força esmagadora",
        "Necromante das catacumbas com crânios de almas penadas"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("character_creation_screen")
    ) {
        // Top Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                Brush.horizontalGradient(listOf(VioletPrimary, CyanHighlight))
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(VioletContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = VioletPrimary, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "CRIAÇÃO DO PERSONAGEM",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AmberGold.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("PASSO 1", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AmberGold)
                            }
                        }
                        Text(
                            "O jogador não começa com personagem pré-definido. Crie o seu antes de adentrar as masmorras!",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Mode Switcher (IA vs Manual)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurface)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isAiMode) VioletPrimary else Color.Transparent)
                    .clickable { isAiMode = true }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = if (isAiMode) Color.White else TextSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "CRIAR COM IA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAiMode) Color.White else TextSecondary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (!isAiMode) VioletPrimary else Color.Transparent)
                    .clickable { isAiMode = false }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Psychology, contentDescription = null, tint = if (!isAiMode) Color.White else TextSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "ESCOLHA MANUAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!isAiMode) Color.White else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (isAiMode) {
            // IA Description Mode
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "DESCREVA SEU PERSONAGEM LIVREMENTE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanHighlight
                    )
                    Text(
                        "Escreva como desejar: raça, aparência, estilo de luta, monstros ou poderes. A IA identificará seus atributos, forjará skills e armas correspondentes!",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = aiDescriptionText,
                        onValueChange = { aiDescriptionText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VioletPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        placeholder = { Text("Ex: Paladino sagrado da ordem da luz com armadura dourada e escudo reluzente...", fontSize = 11.sp) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Sugestões Rápidas de Criação:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AmberGold)
                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        aiSuggestions.forEach { suggestion ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkCard)
                                    .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                                    .clickable { aiDescriptionText = suggestion }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = VioletPrimary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(suggestion, fontSize = 10.sp, color = TextPrimary)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.createCharacterFromDescription(aiDescriptionText)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_create_char_ai"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VioletPrimary),
                        enabled = !isAiThinking && aiDescriptionText.isNotBlank()
                    ) {
                        if (isAiThinking) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("FORJANDO PERSONAGEM COM IA...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("CRIAR COM IA & IR PARA MASMORRAS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // Manual Customization Mode
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Nome do Herói:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    OutlinedTextField(
                        value = charName,
                        onValueChange = { charName = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VioletPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Retrato Visual do Herói:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        presetAvatars.forEach { (drawableRes, label) ->
                            val isSelected = selectedAvatarRes == drawableRes
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) VioletContainer else DarkCard)
                                    .border(1.5.dp, if (isSelected) CyanHighlight else DarkBorder, RoundedCornerShape(10.dp))
                                    .clickable { selectedAvatarRes = drawableRes }
                                    .padding(6.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = drawableRes),
                                    contentDescription = label,
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(label, fontSize = 8.sp, maxLines = 1, color = if (isSelected) Color.White else TextSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Raça:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(listOf("Elfo", "Humano", "Draconato", "Ladino Feral", "Orc", "Anão", "Morto-Vivo")) { race ->
                            val isSelected = selectedRace == race
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) VioletPrimary else DarkCard)
                                    .border(1.dp, if (isSelected) CyanHighlight else DarkBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedRace = race }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(race, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else TextSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Classe & Arquétipo:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(listOf("Mago Arcano", "Ladino das Sombras", "Guerreiro de Batalha", "Paladino Sagrado", "Berserker Feral")) { cls ->
                            val isSelected = selectedClass == cls
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) VioletPrimary else DarkCard)
                                    .border(1.dp, if (isSelected) CyanHighlight else DarkBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedClass = cls }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(cls, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else TextSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val (str, dex, intell, vit, car) = when {
                                selectedClass.contains("Mago") -> listOf(10, 13, 18, 12, 14)
                                selectedClass.contains("Ladino") -> listOf(12, 18, 12, 13, 14)
                                selectedClass.contains("Berserker") -> listOf(19, 12, 8, 17, 9)
                                selectedClass.contains("Paladino") -> listOf(16, 10, 12, 16, 15)
                                else -> listOf(16, 13, 11, 15, 12)
                            }

                            val starterWeapon = when {
                                selectedClass.contains("Mago") -> Weapon("wep-mago-01", "Cajado Rúnico Elemental", "Cajado imbuído com orbes de plasma arcano.", 16, "Mágico", "+16 Dano Arcano")
                                selectedClass.contains("Ladino") -> Weapon("wep-lad-01", "Par de Adagas das Sombras", "Adagas afiadas temperadas em veneno paralítico.", 15, "Perfurante", "+15 Dano Crítico")
                                selectedClass.contains("Berserker") -> Weapon("wep-ber-01", "Machado Brutal de Duas Mãos", "Machado colossal com peso devastador.", 19, "Corte Pesado", "+19 Dano Brutal")
                                selectedClass.contains("Paladino") -> Weapon("wep-pal-01", "Espada Justiceira Sagrada", "Lâmina forjada sob juramento divino.", 17, "Sagrado", "+17 Dano Sagrado")
                                else -> Weapon("wep-war-01", "Espada de Batalha de Ferro Negro", "Lâmina forjada para combates árduos.", 16, "Corte", "+16 Dano de Corte")
                            }

                            val skills = when {
                                selectedClass.contains("Mago") -> listOf(
                                    Skill("sk-m-1", "Orbe de Plasma Arcano", "Dispara projétil arcano de alto impacto.", 12, 30, false, "INT", "🔮"),
                                    Skill("sk-m-2", "Labareda Incandescente", "Chamas que queimam o monstro.", 16, 36, false, "INT", "🔥"),
                                    Skill("sk-m-3", "Escudo Restaurador", "Transmuta mana em 35 pontos de cura.", 20, 35, true, "INT", "✨")
                                )
                                selectedClass.contains("Ladino") -> listOf(
                                    Skill("sk-l-1", "Golpe Furtivo nas Sombras", "Ataque rápido na fresta da armadura.", 10, 28, false, "DES", "🗡️"),
                                    Skill("sk-l-2", "Lâmina Envenenada", "Corta infligindo dano prolongado.", 14, 34, false, "DES", "☠️"),
                                    Skill("sk-l-3", "Preparo Restaurador", "Estanca sangramentos e cura 28 HP.", 18, 28, true, "DES", "🩹")
                                )
                                else -> listOf(
                                    Skill("sk-w-1", "Golpe Sísmico", "Abalroamento que racha o solo.", 10, 25, false, "FOR", "⚔️"),
                                    Skill("sk-w-2", "Fúria do Conquistador", "Golpe potente com bônus de força.", 15, 32, false, "FOR", "💥"),
                                    Skill("sk-w-3", "Determinação Inabalável", "Vontade de ferro que recupera 30 HP.", 20, 30, true, "VIT", "🛡️")
                                )
                            }

                            viewModel.createCharacterManual(
                                name = charName,
                                race = selectedRace,
                                characterClass = selectedClass,
                                archetype = "$selectedRace $selectedClass",
                                avatarRes = selectedAvatarRes,
                                str = str,
                                dex = dex,
                                intell = intell,
                                vit = vit,
                                car = car,
                                starterWeapon = starterWeapon,
                                skills = skills
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_confirm_char_manual"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VioletPrimary)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("CONFIRMAR PERSONAGEM & IR PARA MASMORRAS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
