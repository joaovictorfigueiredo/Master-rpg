package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkOutline
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletBorder
import com.example.ui.theme.VioletContainer
import com.example.ui.theme.VioletLight
import com.example.ui.theme.VioletPrimary

@Composable
fun ArchitectureScreen() {
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
                                Icons.Default.AccountTree,
                                contentDescription = "Arquitetura",
                                tint = VioletPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "ARQUITETURA DO SISTEMA",
                            color = VioletPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Visão geral da solução Full Stack de alta performance: Mobile Multiplataforma, Backend NestJS em tempo real com Socket.io, Banco PostgreSQL via Prisma e IA Gemini com Saídas Estruturadas (Structured Outputs).",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Pillar 1: Mobile & Real-time WebSockets
        item {
            ArchPillCard(
                title = "1. TEMPO REAL: NESTJS + SOCKET.IO",
                icon = Icons.Default.Wifi,
                accentColor = EmeraldSuccess,
                details = listOf(
                    "Eventos WebSocket: 'create_room', 'join_room', 'submit_action', 'alchemy_combine'",
                    "Broadcast imediato: Ao submeter uma ação de combate, o backend invoca o Gemini e emite 'turn_resolved' para todos os celulares na sala",
                    "Modo Solo & Multiplayer: Ambos usam o mesmo fluxo de rooms e regras de D20, permitindo transição sem atrito"
                )
            )
        }

        // Pillar 2: Database & Progression Rules
        item {
            ArchPillCard(
                title = "2. BANCO DE DADOS: POSTGRESQL & PRISMA",
                icon = Icons.Default.Storage,
                accentColor = VioletPrimary,
                details = listOf(
                    "Modelos: User, Character, Spell, ItemTemplate, InventoryItem, GameRoom, GameTurn, AlchemyRecipeLog",
                    "Regra de Ouro da Morte/Wipe: Itens comuns (isCommonDungeonItem=true) são destruídos ao morrer; Itens Únicos (isUniqueItem=true e isPermanent=true) são gravados para sempre na ficha",
                    "Rastreamento de Linhagem: O campo alchemyLineage em JSONB armazena quais itens geraram o artefato"
                )
            )
        }

        // Pillar 3: Adaptive Gemini AI Master
        item {
            ArchPillCard(
                title = "3. IA ADAPTATIVA: GEMINI + JSON SCHEMA",
                icon = Icons.Default.Psychology,
                accentColor = VioletLight,
                details = listOf(
                    "Modelo: gemini-2.5-flash com systemInstruction e responseSchema",
                    "Power Scaling Dinâmico: A IA lê o powerLevel do grupo e calibra a DC das ações, HP dos monstros e dano por turno",
                    "Garantia de Tipagem: O retorno é 100% JSON estrito com deltas numéricos exatos de vida/mana, evitando alucinações nas regras"
                )
            )
        }

        // Quick Code Snippet Preview
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Code,
                            contentDescription = "Código",
                            tint = VioletPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ARQUIVOS GERADOS NO PROJETO",
                            color = VioletPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val files = listOf(
                        "prisma/schema.prisma" to "Modelagem ORM para PostgreSQL",
                        "prisma/schema.sql" to "DDL SQL nativo com ENUMs e Índices",
                        "backend/src/rooms/rooms.gateway.ts" to "WebSocket Gateway com Socket.io",
                        "backend/src/ai/gemini-master.service.ts" to "Integração Gemini Structured Outputs",
                        "backend/src/alchemy/alchemy.service.ts" to "Lógica de Transmutação de Itens Únicos",
                        "docs/ai_master_system_prompt.md" to "Engenharia de Prompt e Exemplos Reais"
                    )

                    files.forEach { (path, desc) ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text(
                                text = path,
                                color = VioletPrimary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = desc,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArchPillCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: androidx.compose.ui.graphics.Color,
    details: List<String>
) {
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
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(VioletContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = VioletPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    color = VioletPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            details.forEach { detail ->
                Row(modifier = Modifier.padding(vertical = 3.dp)) {
                    Text(text = "•", color = VioletPrimary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = detail, color = TextPrimary, fontSize = 11.sp, lineHeight = 15.sp)
                }
            }
        }
    }
}
