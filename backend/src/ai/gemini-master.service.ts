import { Injectable, Logger } from '@nestjs/common';
import { GoogleGenAI } from '@google/genai';
import {
  GeminiTurnOutput,
  GEMINI_TURN_JSON_SCHEMA,
} from './schemas/gemini-turn-output.schema';
import {
  AI_GAME_MASTER_SYSTEM_PROMPT,
  buildTurnPrompt,
} from './prompts/game-master.prompt';

@Injectable()
export class GeminiMasterService {
  private readonly logger = new Logger(GeminiMasterService.name);
  private aiClient: GoogleGenAI | null = null;

  constructor() {
    const apiKey = process.env.GEMINI_API_KEY;
    if (apiKey) {
      this.aiClient = new GoogleGenAI({ apiKey });
      this.logger.log('Gemini AI Client initialized successfully for Game Master.');
    } else {
      this.logger.warn(
        'GEMINI_API_KEY not found in environment. Falling back to deterministic RPG rules engine.'
      );
    }
  }

  /**
   * Process a player turn via Gemini Structured Outputs or deterministic fallback.
   */
  async processTurn(turnContext: Parameters<typeof buildTurnPrompt>[0]): Promise<GeminiTurnOutput> {
    const userPrompt = buildTurnPrompt(turnContext);

    if (this.aiClient) {
      try {
        const response = await this.aiClient.models.generateContent({
          model: 'gemini-2.5-flash',
          contents: userPrompt,
          config: {
            systemInstruction: AI_GAME_MASTER_SYSTEM_PROMPT,
            temperature: 0.7,
            responseMimeType: 'application/json',
            responseSchema: GEMINI_TURN_JSON_SCHEMA as any,
          },
        });

        if (response.text) {
          const parsed: GeminiTurnOutput = JSON.parse(response.text);
          this.logger.log(
            `Gemini GM Turn processed: ${parsed.tacticalSummary} [${parsed.rulesEvaluation.successLevel}]`
          );
          return parsed;
        }
      } catch (error) {
        this.logger.error('Failed to query Gemini API, falling back to local GM rules engine', error);
      }
    }

    // Deterministic fallback rules engine (guarantees offline/solo mode stability)
    return this.resolveFallbackTurn(turnContext);
  }

  /**
   * High-fidelity local D20 RPG resolution engine ensuring zero game downtime.
   */
  private resolveFallbackTurn(
    ctx: Parameters<typeof buildTurnPrompt>[0]
  ): GeminiTurnOutput {
    const { actingPlayer, activeMonsters, party } = ctx;
    const isNat20 = actingPlayer.d20Roll === 20;
    const isNat1 = actingPlayer.d20Roll === 1;
    const dc = 12 + Math.min(6, Math.floor(ctx.floorNumber * 1.5));
    const total = actingPlayer.d20Roll + actingPlayer.attributeModifier;

    let successLevel: GeminiTurnOutput['rulesEvaluation']['successLevel'] = 'SUCCESS';
    if (isNat20) successLevel = 'CRITICAL_SUCCESS';
    else if (isNat1) successLevel = 'CRITICAL_FAILURE';
    else if (total < dc) successLevel = 'FAILURE';

    const targetMonster = activeMonsters[0];
    const partyHpDeltas: GeminiTurnOutput['partyHpDeltas'] = [];
    const partyManaDeltas: GeminiTurnOutput['partyManaDeltas'] = [];
    const monsterUpdates: GeminiTurnOutput['monsterUpdates'] = [];
    const inventoryChanges: GeminiTurnOutput['inventoryChanges'] = [];

    let narrative = '';
    let tactical = '';

    if (actingPlayer.actionType === 'ATTACK') {
      if (successLevel === 'CRITICAL_SUCCESS') {
        const dmg = 24 + actingPlayer.attributeModifier * 2;
        narrative = `${actingPlayer.name} avança em perfeita sincronia com as sombras da catacumba. A lâmina encontra a fresta na carcaça do ${targetMonster ? targetMonster.name : 'inimigo'} com um estalo ensurdecedor, espalhando faíscas etéreas pela masmorra!`;
        tactical = `Acerto Crítico! ${actingPlayer.name} desferiu ${dmg} de dano fatal.`;
        if (targetMonster) {
          monsterUpdates.push({
            monsterId: targetMonster.id,
            monsterName: targetMonster.name,
            hpDelta: -dmg,
            remainingHp: Math.max(0, targetMonster.hp - dmg),
            statusCondition: targetMonster.hp - dmg <= 0 ? 'DEAD' : 'BLEEDING',
          });
        }
      } else if (successLevel === 'SUCCESS') {
        const dmg = 12 + actingPlayer.attributeModifier;
        narrative = `${actingPlayer.name} executa um golpe firme e calculado. O impacto reverbera contra o ${targetMonster ? targetMonster.name : 'inimigo'}, que recua rugindo de fúria!`;
        tactical = `Ataque bem-sucedido causando ${dmg} de dano.`;
        if (targetMonster) {
          monsterUpdates.push({
            monsterId: targetMonster.id,
            monsterName: targetMonster.name,
            hpDelta: -dmg,
            remainingHp: Math.max(0, targetMonster.hp - dmg),
          });
        }
      } else if (successLevel === 'CRITICAL_FAILURE') {
        narrative = `O chão úmido da masmorra trai o passo de ${actingPlayer.name}! O golpe passa no vazio, desequilibrando o aventureiro e abrindo flanco para o contra-ataque feroz do monstro!`;
        tactical = `Falha Crítica! Desvantagem de postura e contra-ataque sofrido (-14 HP).`;
        partyHpDeltas.push({
          characterId: actingPlayer.id,
          characterName: actingPlayer.name,
          delta: -14,
          reason: 'Contra-ataque por desequilíbrio crítico',
        });
      } else {
        narrative = `A lâmina de ${actingPlayer.name} resvala na couraça espessa do monstro sem penetrar. O inimigo apenas sibila, mantendo sua postura defensiva.`;
        tactical = `Ataque não superou a DC (${total} vs DC ${dc}). Nenhum dano causado.`;
      }
    } else if (actingPlayer.actionType === 'CAST_SPELL') {
      partyManaDeltas.push({
        characterId: actingPlayer.id,
        delta: -15,
      });
      const dmg = 18 + actingPlayer.attributeModifier * 2;
      narrative = `Runas arcanas acendem no ar ao redor de ${actingPlayer.name}. Uma rajada de energia ancestral crepita pelas paredes de pedra da masmorra atingindo o alvo em cheio!`;
      tactical = `Magia conjurada com sucesso: ${dmg} de dano mágico!`;
      if (targetMonster) {
        monsterUpdates.push({
          monsterId: targetMonster.id,
          monsterName: targetMonster.name,
          hpDelta: -dmg,
          remainingHp: Math.max(0, targetMonster.hp - dmg),
          statusCondition: 'ARCANE_BURN',
        });
      }
    } else if (actingPlayer.actionType === 'ALCHEMY_COMBINE') {
      narrative = `Usando seu estojo de alquimia de campo, ${actingPlayer.name} destila e funde a essência dos dois artefatos. Fumaça violeta e ouro líquido fluem, transmutando a matéria em um Item Único permanente!`;
      tactical = `Alquimia Realizada com Sucesso! Item Único gerado com propriedades vitais cruzadas.`;
      inventoryChanges.push({
        action: 'UPGRADE_TO_UNIQUE',
        characterId: actingPlayer.id,
        itemName: 'Adaga do Sangue Curativo',
        isUniqueItem: true,
        isPermanent: true,
        customDescription: 'Lâmina forjada que restaura a vida dos aliados ao perfurar inimigos.',
      });
    } else {
      narrative = `${actingPlayer.name} observa as passagens sombrias e investiga as ranhuras nas paredes de pedra, identificando os padrões da masmorra.`;
      tactical = `Investigação concluída. O grupo antecipa os movimentos da masmorra.`;
    }

    const nextActor =
      party.length > 1
        ? party[(party.findIndex((p) => p.id === actingPlayer.id) + 1) % party.length].id
        : actingPlayer.id;

    return {
      narrativeText: narrative,
      tacticalSummary: tactical,
      rulesEvaluation: {
        actionType: actingPlayer.actionType,
        dcApplied: dc,
        playerRoll: actingPlayer.d20Roll,
        modifier: actingPlayer.attributeModifier,
        totalResult: total,
        successLevel,
      },
      partyHpDeltas,
      partyManaDeltas,
      monsterUpdates,
      inventoryChanges,
      dungeonEvent: {
        roomCleared: monsterUpdates.some((m) => m.remainingHp <= 0),
        bossDefeated: false,
        partyWiped: false,
      },
      suggestedNextActions: [
        'Atacar com arma equipada',
        'Conjurar magia elemental',
        'Usar Poção ou Item Único',
        'Examinar os arredores da masmorra',
      ],
      nextTurnActorCharacterId: nextActor,
    };
  }
}
