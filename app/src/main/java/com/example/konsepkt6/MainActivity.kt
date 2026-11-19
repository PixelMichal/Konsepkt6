package com.example.konsepkt6

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.konsepkt6.ui.theme.Konsepkt6Theme
import kotlinx.serialization.Serializable

@Serializable
data object ZonesKey : NavKey

@Serializable
data class TimeKey(val zones: List<String>) : NavKey

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate")
        enableEdgeToEdge()
        setContent {
            Konsepkt6Theme {
                val backStack = rememberNavBackStack(ZonesKey)
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavDisplay(
                        backStack = backStack,
                        onBack = { backStack.removeLastOrNull() },
                        entryProvider = entryProvider {
                            entry<ZonesKey> {
                                Screen(
                                    onShowTime = { zones ->
                                        backStack.add(TimeKey(zones))
                                    },
                                    modifier = Modifier.padding(innerPadding),
                                )
                            }
                            entry<TimeKey> { key ->
                                TimeScreen(
                                    zones = key.zones,
                                    modifier = Modifier.padding(innerPadding),
                                )
                            }
                        },
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "onDestroy")
    }
}

private val availableZones = listOf(
    "Europe/Warsaw",
    "America/New_York",
    "Asia/Tokyo",
    "Australia/Sydney",
    "America/Sao_Paulo",
    "Africa/Cairo",
)

@Composable
fun Screen(
    onShowTime: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedZones by rememberSaveable {
        mutableStateOf(listOf("Europe/Warsaw", "America/New_York", "Asia/Tokyo"))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Header()

        Spacer(modifier = Modifier.height(12.dp))

        ZoneList(
            zones = availableZones,
            selectedZones = selectedZones,
            onToggleZone = { zone ->
                selectedZones = if (zone in selectedZones) {
                    selectedZones - zone
                } else {
                    selectedZones + zone
                }
            },
            modifier = Modifier.weight(1f),
        )

        Button(
            onClick = { onShowTime(selectedZones) },
            enabled = selectedZones.isNotEmpty(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .height(50.dp),
        ) {
            Text(
                text = stringResource(R.string.show_clocks),
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
fun ZoneList(
    zones: List<String>,
    selectedZones: List<String>,
    onToggleZone: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        items(zones) { zone ->
            val isChecked = zone in selectedZones
            val cityName = zone.substringAfter("/").replace("_", " ")

            OutlinedCard(
                onClick = { onToggleZone(zone) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = if (isChecked) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerLow
                    },
                ),
                border = BorderStroke(
                    width = if (isChecked) 1.5.dp else 1.dp,
                    color = if (isChecked) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    },
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = null,
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = cityName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isChecked) FontWeight.Medium else FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = zone,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Header() {
    Text(
        text = stringResource(R.string.time_zones),
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 4.dp),
    )
    Text(
        text = stringResource(R.string.zone_choice),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 20.dp),
    )
}
