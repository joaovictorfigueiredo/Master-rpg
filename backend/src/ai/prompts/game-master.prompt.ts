/**
 * SYSTEM PROMPT & FEW-SHOT EXAMPLES FOR THE AI GAME MASTER (GEMINI API)
 * Enforces dynamic D20 mechanics, power scaling, alchemy synthesis, and strict JSON outputs.
 */

export const AI_GAME_MASTER_SYSTEM_PROMPT = `
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
   - 'isCommonDungeonItem' = true: Itens mundanos (ex: Adaga de Ferro, Tocha, Frasco Vazio) encontrados no chão da dungeon. Se o grupo for derrotado ('partyWiped'=true) ou abandonar a dungeon, esses itens são DESTRUÍDOS e perdidos para sempre.
   - 'isUniqueItem' = true: Itens permanentes criados por Alquimia ou relíquias raras. Eles viajam com o personagem para sempre e NUNCA são perdidos na morte do herói.

4. ALQUIMIA E COMBINAÇÃO DINÂMICA:
   - Quando um jogador combina 2 itens (ex: "Faca Comum" + "Poção de Cura"), você deve sintetizar um NOVO 'Item Único' com propriedades cruzadas (ex: "Adaga Sanguínea Restauradora" que cura aliados ou absorve vida ao atingir).
   - Esse item resultante deve ser marcado no inventário com:
     'action': 'UPGRADE_TO_UNIQUE', 'isUniqueItem': true, 'isPermanent': true.

5. SAÍDA ESTRITA:
   - Você JAMAIS deve responder com conversas em texto livre fora do schema JSON.
   - Responda EXCLUSIVAMENTE com o objeto JSON válido em conformidade com o schema GEMINI_TURN_JSON_SCHEMA fornecido.
   - Em 'narrativeText', use descrições cinemáticas, atmosféricas e sensoriais (sons de passos nas pedras úmidas, cheiro de enxofre, reluzir de aço).
`;

/**
 * Exemplo prático de Prompt de Entrada do Turno enviado para o Gemini:
 */
export function buildTurnPrompt(context: {
  gameMode: 'SOLO' | 'MULTIPLAYER';
  floorNumber: number;
  roomTitle: string;
  party: Array<{
    id: string;
    name: string;
    class: string;
    level: number;
    hp: number;
    maxHp: number;
    mana: number;
    maxMana: number;
    powerLevel: number;
    uniqueEquipment: string[];
  }>;
  activeMonsters: Array<{
    id: string;
    name: string;
    hp: number;
    maxHp: number;
    attack: number;
    specialAbility?: string;
  }>;
  actingPlayer: {
    id: string;
    name: string;
    actionText: string;
    actionType: string;
    d20Roll: number;
    attributeModifier: number;
    targetName?: string;
  };
}): string {
  return `
ESTADO ATUAL DA MASMORRA:
- Modo: ${context.gameMode}
- Andar: ${context.floorNumber} | Sala: "${context.roomTitle}"
- Membros do Grupo:
${context.party
  .map(
    (p) =>
      `  * [${p.id}] ${p.name} (${p.class} Nv.${p.level}) - HP: ${p.hp}/${p.maxHp}, Mana: ${p.mana}/${p.maxMana}, PowerLevel: ${p.powerLevel}, Itens Únicos: [${p.uniqueEquipment.join(', ') || 'Nenhum'}]`
  )
  .join('\n')}

- Monstros Ativos na Sala:
${context.activeMonsters
  .map((m) => `  * [${m.id}] ${m.name} - HP: ${m.hp}/${m.maxHp}, Poder de Dano: ${m.attack}`)
  .join('\n') || '  (Nenhum inimigo ativo no momento)'}

AÇÃO DO JOGADOR NESTE TURNO:
- Jogador Ativo: ${context.actingPlayer.name} (ID: ${context.actingPlayer.id})
- Intenção Declarada: "${context.actingPlayer.actionText}"
- Tipo de Ação: ${context.actingPlayer.actionType}
- Alvo: ${context.actingPlayer.targetName || 'Ambiente'}
- Rolagem de D20: ${context.actingPlayer.d20Roll} + Modificador: ${context.actingPlayer.attributeModifier} = Total: ${
    context.actingPlayer.d20Roll + context.actingPlayer.attributeModifier
  }

TAREFAS DO MESTRE:
1. Avalie a DC da ação considerando a dificuldade do encontro.
2. Determine o sucesso ou fracasso (com suporte a Crítico se D20=20 ou D20=1).
3. Narre o desfecho dinâmico com realismo e impacto.
4. Calcule dano sofrido/causado (deltas de HP e Mana) tanto nos jogadores quanto nos monstros.
5. Se for uma ação de Alquimia, defina o novo Item Único criado e suas propriedades.
6. Retorne estritamente o JSON no schema estabelecido.
`;
}
