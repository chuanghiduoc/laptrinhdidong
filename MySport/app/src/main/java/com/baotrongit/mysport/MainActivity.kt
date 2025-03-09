package com.baotrongit.mysport

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.baotrongit.mysport.ui.theme.MySportTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MySportTheme {
                MySportApp()
            }
        }
    }
}

data class BottomNavItem(
    val title: String,
    val icon: ImageVector
)

@Composable
fun MySportApp() {
    val navItems = listOf(
        BottomNavItem("Run", Icons.Filled.DirectionsWalk),    // Using DirectionsWalk instead
        BottomNavItem("Cycling", Icons.Filled.Directions),    // Using Directions instead
        BottomNavItem("History", Icons.Filled.Timer),         // Using Timer instead
        BottomNavItem("Profile", Icons.Filled.Person)
    )

    var selectedItemIndex by remember { mutableStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = { selectedItemIndex = index },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = { Text(text = item.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        // Content based on selected tab
        when (selectedItemIndex) {
            0 -> RunScreen(modifier = Modifier.padding(innerPadding))
            1 -> CyclingScreen(modifier = Modifier.padding(innerPadding))
            2 -> HistoryScreen(modifier = Modifier.padding(innerPadding))
            3 -> ProfileScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun RunScreen(modifier: Modifier = Modifier) {
    Text(
        text = "Run Screen",
        modifier = modifier
    )
}

@Composable
fun CyclingScreen(modifier: Modifier = Modifier) {
    Text(
        text = "Cycling Screen",
        modifier = modifier
    )
}

@Composable
fun HistoryScreen(modifier: Modifier = Modifier) {
    Text(
        text = "History Screen",
        modifier = modifier
    )
}

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    Text(
        text = "Profile Screen",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun MySportAppPreview() {
    MySportTheme {
        MySportApp()
    }
}
