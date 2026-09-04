# Arquitetura e Engenharia de Prompt: AI Game Master RPG

Este documento detalha os pilares de arquitetura de software, modelagem de banco de dados, fluxo de WebSocket em tempo real e o **System Prompt com Structured Outputs** para a IA assumir o papel de Mestre de RPG dinâmico.

---

## 1. Visão Geral da Arquitetura do Sistema

```
+-------------------------------------------------------------------------+
|                  MOBILE CLIENT (React Native / Android)                |
|  - Sala Solo / Lobby Multiplayer via Código de Sala                     |
|  - Transmissão de Ações (Texto / Áudio)                                 |
|  - Visualização de Turno, Histórico, Inventário e Alquimia              |
+------------------------------------+------------------------------------+
                                     |
                                     | WebSocket (Socket.io) / REST
                                     v
+-------------------------------------------------------------------------+
|                 BACKEND EM TEMPO REAL (NestJS + Socket.io)              |
|                                                                         |
|  [Rooms Gateway] <---> [Rooms Service] <---> [Alchemy Service]          |
|         |                     |                                         |
|         v                     v                                         |
|  [Prisma ORM]         [Gemini Master Service]                           |
|  (PostgreSQL)         - Escalonamento de Desafio (Power Level)          |
|                       - Validação de Regras e Resolução D20             |
|                       - Geração com JSON Schema Rígido                  |
+------------------------------------+------------------------------------+
                                     |
                                     | Structured Outputs (responseSchema)
                                     v
+-------------------------------------------------------------------------+
|                    GOOGLE GEMINI API (gemini-2.5-flash)                 |
|  - Narração Cinematográfica e Reativa                                   |
|  - Cálculo Estrito de Deltas de HP, Mana e Condições de Status          |
|  - Síntese Alquímica Criativa de Itens Únicos Permanentes               |
+-------------------------------------------------------------------------+
```

---

## 2. Modelagem do Banco de Dados (Prisma & PostgreSQL)

O arquivo `prisma/schema.prisma` e `prisma/schema.sql` definem a camada persistente:
- **`User` & `Character`**: Níveis, Atributos base (FOR, DES, INT, VIT, CAR) e `powerLevel`.
- **`InventoryItem`**:
  * `isCommonDungeonItem: true` -> Item comum encontrado na masmorra. É **destruído** se o grupo morrer ou abandonar a dungeon.
  * `isUniqueItem: true` & `isPermanent: true` -> Itens gerados por Alquimia ou relíquias raras. **Persistem permanentemente** e viajam com o personagem entre campanhas.
  * `alchemyLineage` (JSONB) -> Histórico da combinação que deu origem ao item.
- **`GameRoom` & `RoomParticipant`**: Controle de sessões Solo e Multiplayer por código alfanumérico de 6 dígitos.
- **`GameTurn`**: Histórico auditável de cada turno, incluindo rolagens de d20, DC aplicada e deltas de vida/mana.

---

## 3. System Prompt Oficial do Mestre de Jogo

Abaixo está o prompt mestre estruturado para ser configurado no `systemInstruction` da chamada do Gemini:

```markdown
Você é o Mestre de Jogo (Game Master - GM) supremo de um RPG sombrio e dinâmico de alta fantasia, operando em tempo real para jogadores Solo ou Grupos Multiplayer.
Sua missão é tecer narrativas vívidas, conduzir combates justos mas implacáveis, aplicar regras rígidas baseadas em D20 e balancear encontros matematicamente de acordo com o Power Level dos personagens.

=== REGRAS DE MECÂNICA E POWER SCALING ===
1. ESCALONAMENTO DE DIFICULDADE (POWER SCALING):
   - Cada personagem possui um 'powerLevel' baseado em Nível, Atributos e Itens Únicos equipados.
   - Para Grupos Multiplayer, a Força do Encontro (Challenge Rating) é proporcional à soma e média do powerLevel de todos os membros ativos.
   - Em Modo Solo, os encontros equilibram o número de alvos, dano por turno e opções táticas para que um herói solitário consiga triunfar com inteligência e uso astuto de poções/itens únicos.
   - Dificuldades base de DC (Difficulty Class):
     * Ação Fácil/Rotineira: DC 10
     * Ação Moderada: DC 14
     * Ação Difícil: DC 18
     * Ação Heroica/Boss: DC 22+

2. SISTEMA DE DADOS (D20 RESOLUTION):
   - O jogador ou o sistema envia o 'd20Roll' e o modificador de atributo ('modifier').
   - 'totalResult' = d20Roll + modifier.
   - Natural 20 = 'CRITICAL_SUCCESS': narrativa épica com bônus expressivo ou finalização triunfante.
   - Natural 1 = 'CRITICAL_FAILURE': revés dramático sem morte instantânea arbitrária (ex: arma emperra, monstro ganha contra-ataque).
   - Se totalResult >= DC: 'SUCCESS'.
   - Se totalResult < DC: 'FAILURE'.

3. PROGRESSÃO E PERDA DE ITENS:
   - 'isCommonDungeonItem' = true: Itens mundanos encontrados no chão da dungeon. Se o grupo for derrotado ('partyWiped'=true) ou abandonar a dungeon, esses itens são DESTRUÍDOS e perdidos para sempre.
   - 'isUniqueItem' = true: Itens permanentes criados por Alquimia ou relíquias raras. Eles viajam com o personagem para sempre e NUNCA são perdidos na morte do herói.

4. ALQUIMIA E COMBINAÇÃO DINÂMICA:
   - Quando um jogador combina 2 itens (ex: "Faca Comum" + "Poção de Cura"), você deve sintetizar um NOVO 'Item Único' com propriedades cruzadas (ex: "Adaga Sanguínea Restauradora" que cura aliados ou absorve vida ao atingir).
   - Esse item resultante deve ser marcado no inventário com:
     'action': 'UPGRADE_TO_UNIQUE', 'isUniqueItem': true, 'isPermanent': true.

5. FORMATO DE RESPOSTA OBRIGATÓRIO:
   - Responda EXCLUSIVAMENTE com JSON válido em conformidade com o schema GEMINI_TURN_JSON_SCHEMA.
   - Em 'narrativeText', use descrições cinemáticas, atmosféricas e sensoriais (sons de passos nas pedras úmidas, cheiro de enxofre, reluzir de lâminas).
```

---

## 4. Exemplo Prático de Turno (Input e Output JSON)

### Input enviado para a LLM:
```json
{
  "dungeonState": {
    "gameMode": "MULTIPLAYER",
    "floorNumber": 2,
    "roomTitle": "Câmara das Lágrimas Petrificadas",
    "party": [
      {
        "id": "char-warrior-01",
        "name": "Valerius",
        "class": "WARRIOR",
        "level": 3,
        "hp": 75,
        "maxHp": 90,
        "mana": 15,
        "maxMana": 20,
        "powerLevel": 68,
        "uniqueEquipment": ["Lâmina do Sangue Restaurador"]
      },
      {
        "id": "char-mage-02",
        "name": "Eldrin",
        "class": "MAGE",
        "level": 3,
        "hp": 38,
        "maxHp": 45,
        "mana": 60,
        "maxMana": 80,
        "powerLevel": 72,
        "uniqueEquipment": []
      }
    ],
    "activeMonsters": [
      {
        "id": "mon-ghoul-boss",
        "name": "Carniçal Colossal com Armadura Enferrujada",
        "hp": 85,
        "maxHp": 100,
        "attack": 16
      }
    ]
  },
  "actingPlayer": {
    "id": "char-warrior-01",
    "name": "Valerius",
    "actionText": "Investir com a Lâmina do Sangue Restaurador mirando a fresta no peitoral enferrujado do Carniçal para curar o Eldrin!",
    "actionType": "ATTACK",
    "d20Roll": 18,
    "attributeModifier": 4,
    "targetName": "Carniçal Colossal com Armadura Enferrujada"
  }
}
```

### Resposta Estruturada retornada pelo Gemini (JSON Puro):
```json
{
  "narrativeText": "Valerius projeta seu escudo e avança em velocidade estonteante contra o monstro. A Lâmina do Sangue Restaurador brilha com um pulsar carmesim e fende a chapa enferrujada do Carniçal. O impacto espalha sangue corrupto que, ao tocar as runas da espada, se transfigura instantaneamente em filamentos de luz esmeralda, envolvendo as feridas de Eldrin que sente suas forças retornarem!",
  "tacticalSummary": "Ataque bem-sucedido contra a fresta da armadura (Total 22 vs DC 16). O poder alquímico restaurou vida para Eldrin.",
  "rulesEvaluation": {
    "actionType": "ATTACK",
    "dcApplied": 16,
    "playerRoll": 18,
    "modifier": 4,
    "totalResult": 22,
    "successLevel": "SUCCESS"
  },
  "partyHpDeltas": [
    {
      "characterId": "char-mage-02",
      "characterName": "Eldrin",
      "delta": 18,
      "reason": "Cura recebida pelo efeito passivo da Lâmina do Sangue Restaurador"
    }
  ],
  "partyManaDeltas": [],
  "monsterUpdates": [
    {
      "monsterId": "mon-ghoul-boss",
      "monsterName": "Carniçal Colossal com Armadura Enferrujada",
      "hpDelta": -26,
      "remainingHp": 59,
      "statusCondition": "EXPOSED"
    }
  ],
  "inventoryChanges": [],
  "dungeonEvent": {
    "roomCleared": false,
    "bossDefeated": false,
    "partyWiped": false
  },
  "suggestedNextActions": [
    "Eldrin: Conjurar Raio de Gelo aproveitando a fresta exposta",
    "Valerius: Manter postura defensiva para bloquear o contra-ataque",
    "Tentar usar poção ou habilidade de alquimia rápida"
  ],
  "nextTurnActorCharacterId": "char-mage-02"
}
```

---

## 5. Como Executar o Backend Localmente

```bash
# 1. Navegar até a pasta backend
cd backend

# 2. Instalar dependências
npm install

# 3. Gerar o client do Prisma e rodar migrações
npx prisma generate
npx prisma migrate dev --name init

# 4. Iniciar o servidor em desenvolvimento
npm run start:dev
```
O servidor estará rodando em `http://localhost:3000` e aceitando conexões WebSocket em `ws://localhost:3000/game`.
