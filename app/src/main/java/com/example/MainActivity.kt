package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.RpgGameViewModel
import com.example.ui.screens.AlchemyScreen
import com.example.ui.screens.ArchitectureScreen
import com.example.ui.screens.CharacterCreationScreen
import com.example.ui.screens.CharacterScreen
import com.example.ui.screens.DungeonLobbyScreen
import com.example.ui.screens.DungeonScreen
import com.example.ui.screens.ItemsScreen
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletContainer
import com.example.ui.theme.VioletPrimary
import androidx.compose.runtime.collectAsState

class MainActivity : ComponentActivity() {
  private val viewModel: RpgGameViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val hasCreatedCharacter by viewModel.hasCreatedCharacter.collectAsState()
        val isDungeonActive by viewModel.isDungeonActive.collectAsState()
        var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

        if (!hasCreatedCharacter) {
          // STEP 1: Player must create character before entering game
          Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = DarkBg
          ) { innerPadding ->
            Box(
              modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
            ) {
              CharacterCreationScreen(viewModel = viewModel)
            }
          }
        } else {
          // STEP 2 & 3: Main Game Flow
          Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = DarkBg,
            bottomBar = {
              Column {
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(DarkBorder)
                )
                NavigationBar(
                  containerColor = DarkSurface,
                  tonalElevation = 0.dp
                ) {
                  NavigationBarItem(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    icon = { Icon(Icons.Default.Shield, contentDescription = "Masmorra") },
                    label = { Text(if (isDungeonActive) "MASMORRA" else "LOBBY", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                      selectedIconColor = VioletPrimary,
                      selectedTextColor = VioletPrimary,
                      indicatorColor = VioletContainer,
                      unselectedIconColor = TextSecondary,
                      unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_dungeon")
                  )

                  NavigationBarItem(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    icon = { Icon(Icons.Default.Inventory2, contentDescription = "Itens") },
                    label = { Text("ITEMS", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                      selectedIconColor = VioletPrimary,
                      selectedTextColor = VioletPrimary,
                      indicatorColor = VioletContainer,
                      unselectedIconColor = TextSecondary,
                      unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_items")
                  )

                  NavigationBarItem(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    icon = { Icon(Icons.Default.Science, contentDescription = "Alquimia") },
                    label = { Text("ALCHEMY", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                      selectedIconColor = VioletPrimary,
                      selectedTextColor = VioletPrimary,
                      indicatorColor = VioletContainer,
                      unselectedIconColor = TextSecondary,
                      unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_alchemy")
                  )

                  NavigationBarItem(
                    selected = selectedTabIndex == 3,
                    onClick = { selectedTabIndex = 3 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Herói") },
                    label = { Text("HERO", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                      selectedIconColor = VioletPrimary,
                      selectedTextColor = VioletPrimary,
                      indicatorColor = VioletContainer,
                      unselectedIconColor = TextSecondary,
                      unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_character")
                  )

                  NavigationBarItem(
                    selected = selectedTabIndex == 4,
                    onClick = { selectedTabIndex = 4 },
                    icon = { Icon(Icons.Default.AccountTree, contentDescription = "Sistema") },
                    label = { Text("SYSTEM", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                      selectedIconColor = VioletPrimary,
                      selectedTextColor = VioletPrimary,
                      indicatorColor = VioletContainer,
                      unselectedIconColor = TextSecondary,
                      unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_architecture")
                  )
                }
              }
            }
          ) { innerPadding ->
            Box(
              modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
            ) {
              when (selectedTabIndex) {
                0 -> {
                  if (isDungeonActive) {
                    DungeonScreen(viewModel = viewModel)
                  } else {
                    DungeonLobbyScreen(viewModel = viewModel)
                  }
                }
                1 -> ItemsScreen(viewModel = viewModel)
                2 -> AlchemyScreen(viewModel = viewModel)
                3 -> CharacterScreen(viewModel = viewModel)
                4 -> ArchitectureScreen()
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}
