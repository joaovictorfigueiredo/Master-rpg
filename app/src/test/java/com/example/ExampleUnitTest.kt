package com.example

import com.example.engine.RpgGameViewModel
import com.example.model.GameItem
import com.example.model.ItemType
import com.example.model.Weapon
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testCharacterCreationFromDescription() {
    val viewModel = RpgGameViewModel()
    viewModel.createCharacterFromDescription("Kaelen, uma feiticeira elfa com olhos arcanos e magias de fogo")

    val char = viewModel.character.value
    assertEquals("Elfo", char.race)
    assertTrue(char.characterClass.contains("Mago"))
    assertEquals(3, char.skills.size)
    assertNotNull(char.equippedWeapon)
    assertTrue(char.equippedWeapon!!.name.contains("Cajado"))
  }

  @Test
  fun testWeaponCraftingKeepsWeaponPermanent() {
    val viewModel = RpgGameViewModel()
    val tool = GameItem(
      id = "tool-test",
      name = "Kit de Forja do Mestre",
      description = "Ferramenta de forja",
      itemType = ItemType.TOOL
    )
    val material = GameItem(
      id = "mat-test",
      name = "Lingote de Aço Negro",
      description = "Minério raro",
      itemType = ItemType.MATERIAL
    )

    viewModel.craftDungeonWeapon(tool, material, "Lâmina Eterna", "Espada Rúnica")

    val char = viewModel.character.value
    assertEquals("Lâmina Eterna", char.equippedWeapon?.name)
    assertTrue(char.equippedWeapon?.isDungeonCrafted == true)

    // Check inventory has the crafted weapon as permanent
    val craftedInInventory = viewModel.inventory.value.find { it.name == "Lâmina Eterna" }
    assertNotNull(craftedInInventory)
    assertTrue(craftedInInventory!!.isPermanent)
    assertTrue(craftedInInventory.isDungeonCrafted)
  }

  @Test
  fun testManualDiceRollCombat() {
    val viewModel = RpgGameViewModel()
    viewModel.requestActionWithManualDice("Golpe com Espada", "ATTACK")
    assertNotNull(viewModel.pendingAction.value)

    // Roll natural 20
    viewModel.resolveManualDiceRoll(20)

    val roomState = viewModel.roomState.value
    val lastEvent = roomState.turnHistory.last()
    assertEquals(20, lastEvent.d20Roll)
    assertTrue(lastEvent.gmNarrative.contains("CRÍTICO"))
  }
}
