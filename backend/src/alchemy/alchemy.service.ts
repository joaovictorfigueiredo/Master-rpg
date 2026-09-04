import { Injectable, Logger } from '@nestjs/common';

export interface AlchemyCombineInput {
  characterId: string;
  characterAlchemySkill: number;
  item1: {
    id: string;
    name: string;
    type: string;
    isPermanent: boolean;
    isCommonDungeonItem: boolean;
  };
  item2: {
    id: string;
    name: string;
    type: string;
    isPermanent: boolean;
    isCommonDungeonItem: boolean;
  };
  catalystName?: string;
}

export interface UniqueCraftedItem {
  name: string;
  description: string;
  rarity: 'RARE' | 'EPIC' | 'LEGENDARY';
  isUniqueItem: true;
  isPermanent: true; // Will NEVER be lost upon dungeon death or exit!
  isCommonDungeonItem: false;
  hybridEffects: {
    primaryStatBonus: string;
    passiveEffect: string;
    flavourLore: string;
  };
  alchemyLineage: {
    baseItemA: string;
    baseItemB: string;
    craftedTimestamp: string;
  };
}

@Injectable()
export class AlchemyService {
  private readonly logger = new Logger(AlchemyService.name);

  /**
   * Synthesize two dungeon items into a permanent Unique Item.
   */
  async synthesizeUniqueItem(input: AlchemyCombineInput): Promise<UniqueCraftedItem> {
    this.logger.log(
      `Synthesizing item for character ${input.characterId}: [${input.item1.name}] + [${input.item2.name}]`
    );

    const name1 = input.item1.name.toLowerCase();
    const name2 = input.item2.name.toLowerCase();

    let uniqueName = `Relíquia de ${input.item1.name} e ${input.item2.name}`;
    let uniqueDesc = 'Artefato único forjado nas câmaras ocultas da masmorra.';
    let primaryStat = '+15 Ataque / Efeito Híbrido';
    let passive = 'Efeito simbiótico ativado ao golpear.';

    // Intelligent hybrid pairing based on business rules
    if (
      (name1.includes('faca') || name1.includes('adaga') || name1.includes('espada')) &&
      (name2.includes('cura') || name2.includes('poção') || name2.includes('vitalidade'))
    ) {
      uniqueName = 'Lâmina do Sangue Restaurador';
      uniqueDesc =
        'Uma arma alquímica gravada com veios de rubi líquido. Ao invés de meramente rasgar carne, a lâmina transmuta o sangue derramado em névoa rejuvenescedora que cura o portador e seus aliados próximos.';
      primaryStat = '+12 Dano Físico & +18 Cura ao Golpear';
      passive = 'A cada golpe desferido, 30% do dano é convertido em cura instantânea para o grupo.';
    } else if (name1.includes('escudo') && (name2.includes('fogo') || name2.includes('brasa'))) {
      uniqueName = 'Baluarte das Chamas Vingadoras';
      uniqueDesc =
        'Forjado com essência vulcânica e aço rúnico. Absorve o impacto de ataques inimigos e revida em uma explosão cônica de labaredas.';
      primaryStat = '+20 Defesa & +15 Dano de Retaliação por Fogo';
      passive = 'Bloqueios perfeitos causam atordoamento e queimadura contínua no atacante.';
    } else if (name1.includes('arco') && (name2.includes('veneno') || name2.includes('sombra'))) {
      uniqueName = 'Arco do Vento Pútrido';
      uniqueDesc = 'Flechas disparadas dissolvem o éter ao redor, envenenando os pulmões da criatura alvejada.';
      primaryStat = '+18 Dano Perfurante & +25 Dano Periódico de Veneno';
      passive = 'Inimigos envenenados têm sua velocidade de ataque e iniciativa reduzidas em 40%.';
    }

    return {
      name: uniqueName,
      description: uniqueDesc,
      rarity: 'EPIC',
      isUniqueItem: true,
      isPermanent: true,
      isCommonDungeonItem: false,
      hybridEffects: {
        primaryStatBonus: primaryStat,
        passiveEffect: passive,
        flavourLore: `Transmutado através da habilidade alquímica do herói com auxílio do Mestre de Jogo.`,
      },
      alchemyLineage: {
        baseItemA: input.item1.name,
        baseItemB: input.item2.name,
        craftedTimestamp: new Date().toISOString(),
      },
    };
  }
}
