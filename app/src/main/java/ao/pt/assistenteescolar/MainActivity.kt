package ao.pt.assistenteescolar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ao.pt.assistenteescolar.ui.MainViewModel
import ao.pt.assistenteescolar.ui.screens.HojeScreen
import ao.pt.assistenteescolar.ui.screens.HorarioScreen
import ao.pt.assistenteescolar.ui.screens.OnboardingScreen
import ao.pt.assistenteescolar.ui.screens.PreparatorioScreen
import ao.pt.assistenteescolar.ui.theme.*

private enum class AppTab(val label: String) {
    HOJE("Hoje"),
    HORARIO("Horário"),
    PREPARATORIO("Preparatório")
}

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settings by viewModel.settings.collectAsState()
            val isOnline by viewModel.isOnline.collectAsState()
            val schedule by viewModel.schedule.collectAsState()
            val notes by viewModel.notes.collectAsState()
            val prepPacks by viewModel.prepPacks.collectAsState()
            val isGenerating by viewModel.isGenerating.collectAsState()
            val generationError by viewModel.generationError.collectAsState()

            AssistenteEscolarTheme(darkTheme = settings.isDarkTheme) {
                var showSetup by remember { mutableStateOf(false) }
                var currentTab by remember { mutableStateOf(AppTab.HOJE) }

                if (!settings.onboardingDone || showSetup) {
                    OnboardingScreen(
                        initialName = settings.studentName,
                        initialCountry = settings.country,
                        initialGrade = settings.grade,
                        initialShift = settings.shift,
                        initialDarkTheme = settings.isDarkTheme,
                        onSave = { name, country, grade, shift, darkTheme, replaceSchedule ->
                            viewModel.saveCurriculumSetup(name, country, grade, shift, darkTheme, replaceSchedule)
                            showSetup = false
                        }
                    )
                    return@AssistenteEscolarTheme
                }

                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = DeepNavy,
                                titleContentColor = CrispWhite
                            ),
                            title = {
                                Column {
                                    Text("Assistente Escolar", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Text(
                                        text = "${settings.country.flagCode} · ${settings.grade} · Turno da ${settings.shift.label}",
                                        fontSize = 12.sp,
                                        color = BrightCyan
                                    )
                                }
                            },
                            actions = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(end = 12.dp)
                                ) {
                                    Text(
                                        text = if (isOnline) "Online" else "Offline",
                                        color = if (isOnline) Color(0xFF6EE7B7) else BrightCyan,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    TextButton(onClick = { showSetup = true }) {
                                        Text(
                                            text = settings.studentName.ifBlank { "Aluno" },
                                            color = CrispWhite,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        )
                    },
                    bottomBar = {
                        NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                            NavigationBarItem(
                                selected = currentTab == AppTab.HOJE,
                                onClick = { currentTab = AppTab.HOJE },
                                icon = { Icon(Icons.Default.Today, contentDescription = "Hoje") },
                                label = { Text("Hoje") }
                            )
                            NavigationBarItem(
                                selected = currentTab == AppTab.HORARIO,
                                onClick = { currentTab = AppTab.HORARIO },
                                icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Horário") },
                                label = { Text("Horário") }
                            )
                            NavigationBarItem(
                                selected = currentTab == AppTab.PREPARATORIO,
                                onClick = { currentTab = AppTab.PREPARATORIO },
                                icon = { Icon(Icons.Default.MenuBook, contentDescription = "Preparatório") },
                                label = { Text("Preparatório") }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        when (currentTab) {
                            AppTab.HOJE -> HojeScreen(
                                settings = settings,
                                schedule = schedule,
                                notes = notes,
                                onAddNote = viewModel::addNote,
                                onDeleteNote = viewModel::deleteNote
                            )
                            AppTab.HORARIO -> HorarioScreen(
                                shift = settings.shift,
                                schedule = schedule,
                                onSaveClass = viewModel::saveClass,
                                onDeleteClass = viewModel::deleteClass
                            )
                            AppTab.PREPARATORIO -> PreparatorioScreen(
                                settings = settings,
                                isOnline = isOnline,
                                schedule = schedule,
                                prepPacks = prepPacks,
                                isGenerating = isGenerating,
                                generationError = generationError,
                                onGenerate = viewModel::generatePrep,
                                onScoreRecorded = viewModel::recordQuizScore,
                                onDismissError = viewModel::clearError
                            )
                        }
                    }
                }
            }
        }
    }
}
