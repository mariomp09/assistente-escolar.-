package ao.pt.assistenteescolar.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import ao.pt.assistenteescolar.MainActivity
import ao.pt.assistenteescolar.data.local.AppDatabase
import ao.pt.assistenteescolar.data.local.ScheduleClassEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

private enum class WidgetClassState { ONGOING, NEXT, DONE, UPCOMING }

private data class WidgetClassRow(
    val entity: ScheduleClassEntity,
    val state: WidgetClassState
)

class ScheduleGlanceWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val today = LocalDate.now()
        val nowTime = LocalTime.now()
        val dayInt = when (today.dayOfWeek) {
            DayOfWeek.MONDAY -> 1
            DayOfWeek.TUESDAY -> 2
            DayOfWeek.WEDNESDAY -> 3
            DayOfWeek.THURSDAY -> 4
            DayOfWeek.FRIDAY -> 5
            DayOfWeek.SATURDAY -> 6
            DayOfWeek.SUNDAY -> 1 // Domingo pré-visualiza Segunda-feira
        }

        val dayLabel = when (today.dayOfWeek) {
            DayOfWeek.MONDAY -> "Segunda-feira"
            DayOfWeek.TUESDAY -> "Terça-feira"
            DayOfWeek.WEDNESDAY -> "Quarta-feira"
            DayOfWeek.THURSDAY -> "Quinta-feira"
            DayOfWeek.FRIDAY -> "Sexta-feira"
            DayOfWeek.SATURDAY -> "Sábado"
            DayOfWeek.SUNDAY -> "Segunda-feira (Prévia)"
        }

        val classes = AppDatabase.getInstance(context).scheduleDao().getForDaySync(dayInt)
        val rows = classifyClasses(classes, nowTime, isSundayPreview = today.dayOfWeek == DayOfWeek.SUNDAY)

        provideContent {
            WidgetContent(dayLabel = dayLabel, rows = rows)
        }
    }

    private fun classifyClasses(
        classes: List<ScheduleClassEntity>,
        now: LocalTime,
        isSundayPreview: Boolean
    ): List<WidgetClassRow> {
        val fmt = DateTimeFormatter.ofPattern("HH:mm")
        var assignedNext = false
        return classes.map { item ->
            if (isSundayPreview) {
                val state = if (!assignedNext) {
                    assignedNext = true
                    WidgetClassState.NEXT
                } else WidgetClassState.UPCOMING
                return@map WidgetClassRow(item, state)
            }
            val start = runCatching { LocalTime.parse(item.startTime, fmt) }.getOrNull()
            val end = runCatching { LocalTime.parse(item.endTime, fmt) }.getOrNull()
            val state = when {
                start != null && end != null && !now.isBefore(start) && now.isBefore(end) -> {
                    assignedNext = true
                    WidgetClassState.ONGOING
                }
                end != null && !now.isBefore(end) -> WidgetClassState.DONE
                !assignedNext && start != null && now.isBefore(start) -> {
                    assignedNext = true
                    WidgetClassState.NEXT
                }
                else -> WidgetClassState.UPCOMING
            }
            WidgetClassRow(item, state)
        }
    }

    @Composable
    private fun WidgetContent(dayLabel: String, rows: List<WidgetClassRow>) {
        val deepNavy = ColorProvider(Color(0xFF0B192C))
        val surfaceCard = ColorProvider(Color(0xFF132847))
        val highlightCard = ColorProvider(Color(0xFF0E3A5D))
        val softCyan = ColorProvider(Color(0xFF22D3EE))
        val whiteText = ColorProvider(Color(0xFFFFFFFF))
        val mutedText = ColorProvider(Color(0xFF94A3B8))

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(deepNavy)
                .cornerRadius(20.dp)
                .padding(14.dp)
                .clickable(actionStartActivity<MainActivity>())
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = GlanceModifier.defaultWeight()) {
                    Text(
                        text = dayLabel,
                        style = TextStyle(color = whiteText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = if (rows.isEmpty()) "Sem aulas hoje" else "${rows.size} disciplinas programadas",
                        style = TextStyle(color = mutedText, fontSize = 11.sp)
                    )
                }
                Text(
                    text = "Offline",
                    style = TextStyle(color = softCyan, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                )
            }

            Spacer(modifier = GlanceModifier.height(10.dp))

            if (rows.isEmpty()) {
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(surfaceCard)
                        .cornerRadius(12.dp)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Toque para adicionar aulas no Horário",
                        style = TextStyle(color = mutedText, fontSize = 12.sp)
                    )
                }
            } else {
                LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
                    items(rows) { row ->
                        val isHighlighted = row.state == WidgetClassState.ONGOING || row.state == WidgetClassState.NEXT
                        val bg = if (isHighlighted) highlightCard else surfaceCard
                        val titleColor = if (row.state == WidgetClassState.DONE) mutedText else whiteText
                        val badgeLabel = when (row.state) {
                            WidgetClassState.ONGOING -> "AGORA"
                            WidgetClassState.NEXT -> "PRÓXIMA"
                            WidgetClassState.DONE -> "CONCLUÍDA"
                            WidgetClassState.UPCOMING -> row.entity.room
                        }

                        Column(modifier = GlanceModifier.fillMaxWidth().padding(bottom = 6.dp)) {
                            Row(
                                modifier = GlanceModifier
                                    .fillMaxWidth()
                                    .background(bg)
                                    .cornerRadius(12.dp)
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${row.entity.startTime}–${row.entity.endTime}",
                                    style = TextStyle(
                                        color = if (isHighlighted) softCyan else mutedText,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                                Spacer(modifier = GlanceModifier.width(10.dp))
                                Text(
                                    text = row.entity.subject,
                                    modifier = GlanceModifier.defaultWeight(),
                                    style = TextStyle(
                                        color = titleColor,
                                        fontSize = 13.sp,
                                        fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    maxLines = 1
                                )
                                Spacer(modifier = GlanceModifier.width(8.dp))
                                Text(
                                    text = badgeLabel,
                                    style = TextStyle(
                                        color = if (isHighlighted) softCyan else mutedText,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

class ScheduleGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ScheduleGlanceWidget()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val refreshActions = setOf(
            Intent.ACTION_DATE_CHANGED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_BOOT_COMPLETED
        )
        if (intent.action in refreshActions) {
            CoroutineScope(Dispatchers.IO).launch {
                ScheduleGlanceWidget().updateAll(context)
            }
        }
    }
}

object ScheduleWidgetUpdater {
    suspend fun updateAll(context: Context) {
        val manager = GlanceAppWidgetManager(context)
        val ids = manager.getGlanceIds(ScheduleGlanceWidget::class.java)
        ids.forEach { id ->
            ScheduleGlanceWidget().update(context, id)
        }
    }
}

class ScheduleWidgetWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        ScheduleWidgetUpdater.updateAll(applicationContext)
        return Result.success()
    }

    companion object {
        fun enqueuePeriodic(context: Context) {
            val request = PeriodicWorkRequestBuilder<ScheduleWidgetWorker>(15, TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "schedule_widget_refresh",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
