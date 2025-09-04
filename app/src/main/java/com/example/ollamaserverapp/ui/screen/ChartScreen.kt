package com.example.ollamaserverapp.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ollamaserverapp.model.Emotion
import com.example.ollamaserverapp.presentation.ChatViewModel

@Composable
fun ChartScreen(vm: ChatViewModel = viewModel()) {
    val ui by vm.uiState.collectAsState()
    val entries = ui.entries

    if (entries.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Chart", style = MaterialTheme.typography.titleMedium)
            Text("No data yet. Analyze a journal first.")
        }
        return
    }

    val counts: Map<Emotion, Int> = remember(entries) {
        Emotion.entries.associateWith { emo ->
            entries.count { (it.emotion ?: Emotion.NEUTRAL) == emo }
        }
    }
    val total = counts.values.sum().coerceAtLeast(1)

    val palette = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Chart", style = MaterialTheme.typography.titleMedium)

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            var start = -90f
            val diameter = size.minDimension
            val topLeft = Offset(
                (size.width - diameter) / 2f,
                (size.height - diameter) / 2f
            )
            Emotion.entries.forEachIndexed { idx, emo ->
                val v = counts[emo] ?: 0
                if (v == 0) return@forEachIndexed
                val sweep = 360f * (v.toFloat() / total.toFloat())
                drawArc(
                    color = palette[idx % palette.size],
                    startAngle = start,
                    sweepAngle = sweep,
                    useCenter = true,
                    topLeft = topLeft,
                    size = Size(diameter, diameter)
                )
                start += sweep
            }
        }

        Emotion.entries.forEachIndexed { idx, emo ->
            val v = counts[emo] ?: 0
            if (v == 0) return@forEachIndexed
            val pct = (v * 100f / total).toInt()
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    Modifier
                        .size(14.dp)
                        .background(palette[idx % palette.size])
                )
                Text("${emo.name}  $v ($pct%)", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
