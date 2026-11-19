package com.example.konsepkt6

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.example.konsepkt6.ui.theme.Konsepkt6Theme
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

@Composable
fun TimeScreen(
    zones: List<String>,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val dateFormatter = remember(configuration) {
        val locale = configuration.locales[0] ?: Locale.getDefault()
        DateTimeFormatter.ofPattern("EEEE, d MMMM", locale)
    }

    var now by remember { mutableStateOf(Instant.now()) }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                val current = Instant.now()
                now = current
                Log.d("TimeScreen", "tick: $current")
                val delayMs = 1000L - (current.toEpochMilli() % 1000L)
                delay(delayMs)
            }
        }
    }

    val localTime = ZonedDateTime.ofInstant(now, ZoneId.systemDefault())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 4.dp),
        ) {
            Text(
                text = stringResource(R.string.local_time),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = localTime.format(timeFormatter),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = localTime.format(dateFormatter),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant,
        )

        if (zones.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                Text(
                    text = stringResource(R.string.no_zones_selected),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp),
            ) {
                items(zones) { zone ->
                    val zoneTime = ZonedDateTime.ofInstant(now, ZoneId.of(zone))
                    ZoneClockItem(
                        zone = zone,
                        zoneTime = zoneTime,
                        localTime = localTime,
                    )
                }
            }
        }

        Text(
            text = stringResource(R.string.refreshed_every_second),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        )
    }
}

@Composable
fun ZoneClockItem(
    zone: String,
    zoneTime: ZonedDateTime,
    localTime: ZonedDateTime,
) {
    val cityName = zone.substringAfter("/").replace("_", " ")
    val diffHours = (zoneTime.offset.totalSeconds - localTime.offset.totalSeconds) / 3600
    val diffText = when {
        diffHours == 0 -> stringResource(R.string.same_time)
        diffHours > 0 -> "+$diffHours h"
        else -> "$diffHours h"
    }

    OutlinedCard(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cityName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = zone,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = zoneTime.format(timeFormatter),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = diffText,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimeScreenPreview() {
    Konsepkt6Theme {
        TimeScreen(zones = listOf("Europe/Warsaw", "America/New_York", "Asia/Tokyo"))
    }
}
