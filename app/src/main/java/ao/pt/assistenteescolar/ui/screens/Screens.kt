package ao.pt.assistenteescolar.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ao.pt.assistenteescolar.data.local.*
import ao.pt.assistenteescolar.ui.theme.*
import kotlinx.serialization.json.Json
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val jsonParser = Json { ignoreUnknownKeys = true }

private val DAY_LABELS = mapOf(
    1 to "Segunda-feira",
    2 to "Terça-feira",
    3 to "Quarta-feira",
    4 to "Quinta-feira",
    5 to "Sexta-feira",
    6 to "Sábado"
)

@Composable
fun OnboardingScreen(
    initialName: String,
    initialCountry: CountryCurriculum,
    initialGrade: String,
    initialShift: SchoolShift,
    initialDarkTheme: Boolean,
    onSave: (String, CountryCurriculum, String, SchoolShift, Boolean, Boolean) -> Unit
) {
    var studentName by remember { mutableStateOf(initialName) }
    var country by remember { mutableStateOf(initialCountry) }
    var shift by remember { mutableStateOf(initialShift) }
    var isDark by remember { mutableStateOf(initialDarkTheme) }
    val grades = remember(country) {
        if (country == CountryCurriculum.ANGOLA) {
            listOf("7.ª Classe", "8.ª Classe", "9.ª Classe", "10.ª Classe", "11.ª Classe", "12.ª Classe")
        } else {
            listOf("7.º Ano", "8.º Ano", "9.º Ano", "10.º Ano", "11.º Ano", "12.º Ano")
        }
    }
    var selectedGrade by remember(country) {
        mutableStateOf(if (initialGrade in grades) initialGrade else grades[3])
    }

    val bg = if (isDark) Color(0xFF020617) else CanvasSlate
    val cardBg = if (isDark) Color(0xFF0F172A) else CrispWhite
    val titleColor = if (isDark) CrispWhite else DeepNavy
    val textColor = if (isDark) Color(0xFFE2E8F0) else DarkSlate
    val borderColor = if (isDark) Color(0xFF1E293B) else HairlineBorder

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Assistente Escolar",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = titleColor
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Personalize na primeira utilização. Tudo fica guardado offline no telemóvel.",
            fontSize = 14.sp,
            color = MutedSlate
        )
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = studentName,
            onValueChange = { studentName = it },
            label = { Text("Nome do aluno") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(16.dp))
        Text("País e Currículo", fontWeight = FontWeight.SemiBold, color = textColor)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CountryCurriculum.entries.forEach { item ->
                val selected = country == item
                Surface(
                    modifier = Modifier.weight(1f).clickable { country = item },
                    shape = RoundedCornerShape(14.dp),
                    color = if (selected) DeepNavy else cardBg,
                    border = BorderStroke(1.dp, if (selected) BrightCyan else borderColor)
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text(
                            text = item.flagCode,
                            fontWeight = FontWeight.Bold,
                            color = if (selected) BrightCyan else SoftCyan
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = item.label,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selected) CrispWhite else textColor
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            text = if (country == CountryCurriculum.ANGOLA) "Classe" else "Ano de Escolaridade",
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(grades) { g ->
                FilterChip(
                    selected = selectedGrade == g,
                    onClick = { selectedGrade = g },
                    label = { Text(g) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Turno das aulas", fontWeight = FontWeight.SemiBold, color = textColor)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SchoolShift.entries.forEach { item ->
                val selected = shift == item
                Surface(
                    modifier = Modifier.weight(1f).clickable { shift = item },
                    shape = RoundedCornerShape(14.dp),
                    color = if (selected) DeepNavy else cardBg,
                    border = BorderStroke(1.5.dp, if (selected) BrightCyan else borderColor)
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text(
                            text = "Turno da ${item.label}",
                            fontWeight = FontWeight.SemiBold,
                            color = if (selected) CrispWhite else textColor
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = if (item == SchoolShift.MORNING) "07:30 – 13:30" else "13:00 – 19:00",
                            fontSize = 12.sp,
                            color = if (selected) BrightCyan else MutedSlate
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Tema do aplicativo", fontWeight = FontWeight.SemiBold, color = textColor)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(
                modifier = Modifier.weight(1f).clickable { isDark = false },
                shape = RoundedCornerShape(14.dp),
                color = CrispWhite,
                border = BorderStroke(2.dp, if (!isDark) SoftCyan else borderColor)
            ) {
                Box(Modifier.padding(14.dp), contentAlignment = Alignment.Center) {
                    Text("Branco (Claro)", fontWeight = FontWeight.SemiBold, color = DarkSlate)
                }
            }
            Surface(
                modifier = Modifier.weight(1f).clickable { isDark = true },
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF0F172A),
                border = BorderStroke(2.dp, if (isDark) BrightCyan else borderColor)
            ) {
                Box(Modifier.padding(14.dp), contentAlignment = Alignment.Center) {
                    Text("Preto (Escuro)", fontWeight = FontWeight.SemiBold, color = CrispWhite)
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                val finalName = studentName.trim().ifBlank { "Aluno" }
                onSave(finalName, country, selectedGrade, shift, isDark, true)
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isDark) BrightCyan else DeepNavy,
                contentColor = if (isDark) DeepNavy else CrispWhite
            )
        ) {
            Text("Entrar no Assistente Escolar", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun HojeScreen(
    settings: UserSettings,
    schedule: List<ScheduleClassEntity>,
    notes: List<StudyNoteEntity>,
    onAddNote: (String, String, String) -> Unit,
    onDeleteNote: (StudyNoteEntity) -> Unit
) {
    val today = remember { LocalDate.now() }
    val dayInt = when (today.dayOfWeek) {
        DayOfWeek.MONDAY -> 1
        DayOfWeek.TUESDAY -> 2
        DayOfWeek.WEDNESDAY -> 3
        DayOfWeek.THURSDAY -> 4
        DayOfWeek.FRIDAY -> 5
        DayOfWeek.SATURDAY -> 6
        DayOfWeek.SUNDAY -> 1
    }
    val todayClasses = remember(schedule, dayInt) {
        schedule.filter { it.dayOfWeek == dayInt }.sortedBy { it.startTime }
    }
    val nowTime = remember { LocalTime.now() }
    val fmt = remember { DateTimeFormatter.ofPattern("HH:mm") }
    var showNoteDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = DeepNavy
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text(
                        text = "${DAY_LABELS[dayInt]} · ${settings.grade} · Turno da ${settings.shift.label}",
                        color = BrightCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (todayClasses.isEmpty()) "Sem aulas agendadas hoje" else "${todayClasses.size} aulas no seu dia",
                        color = CrispWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        items(todayClasses, key = { it.id }) { item ->
            val start = runCatching { LocalTime.parse(item.startTime, fmt) }.getOrNull()
            val end = runCatching { LocalTime.parse(item.endTime, fmt) }.getOrNull()
            val isOngoing = start != null && end != null && !nowTime.isBefore(start) && nowTime.isBefore(end)
            val isDone = end != null && !nowTime.isBefore(end)

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = CrispWhite,
                border = BorderStroke(1.dp, if (isOngoing) SoftCyan else HairlineBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.width(68.dp)) {
                        Text(item.startTime, fontWeight = FontWeight.Bold, color = if (isOngoing) SoftCyan else DeepNavy)
                        Text(item.endTime, fontSize = 12.sp, color = MutedSlate)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.subject,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDone) MutedSlate else DarkSlate
                        )
                        Text(
                            text = "${item.room} · ${item.teacher}",
                            fontSize = 13.sp,
                            color = MutedSlate
                        )
                    }
                    if (isOngoing) {
                        Text("AGORA", color = SoftCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Notas Rápidas (Offline)", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = DeepNavy)
                TextButton(onClick = { showNoteDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Nova Nota")
                }
            }
        }

        items(notes, key = { it.id }) { note ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = CrispWhite,
                border = BorderStroke(1.dp, HairlineBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(note.subject, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SoftCyan)
                        Text(note.title, fontWeight = FontWeight.Bold, color = DarkSlate)
                        Spacer(Modifier.height(4.dp))
                        Text(note.content, fontSize = 13.sp, color = MutedSlate)
                    }
                    IconButton(onClick = { onDeleteNote(note) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Apagar nota", tint = MutedSlate)
                    }
                }
            }
        }
    }

    if (showNoteDialog) {
        var subject by remember { mutableStateOf("Matemática") }
        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showNoteDialog = false },
            title = { Text("Nova Nota Offline") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Disciplina") })
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") })
                    OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Resumo / Lembrete") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank() && content.isNotBlank()) {
                            onAddNote(subject.trim(), title.trim(), content.trim())
                            showNoteDialog = false
                        }
                    }
                ) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { showNoteDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
fun HorarioScreen(
    shift: SchoolShift,
    schedule: List<ScheduleClassEntity>,
    onSaveClass: (ScheduleClassEntity) -> Unit,
    onDeleteClass: (ScheduleClassEntity) -> Unit
) {
    var selectedDay by remember { mutableIntStateOf(1) }
    var showAddDialog by remember { mutableStateOf(false) }

    val dayClasses = remember(schedule, selectedDay) {
        schedule.filter { it.dayOfWeek == selectedDay }.sortedBy { it.startTime }
    }

    Scaffold(
        containerColor = CanvasSlate,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = DeepNavy,
                contentColor = CrispWhite
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar aula")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items((1..6).toList()) { day ->
                    val label = DAY_LABELS[day]?.take(3) ?: ""
                    FilterChip(
                        selected = selectedDay == day,
                        onClick = { selectedDay = day },
                        label = { Text(label) }
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = DAY_LABELS[selectedDay] ?: "",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DeepNavy
            )
            Spacer(Modifier.height(10.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(dayClasses, key = { it.id }) { item ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = CrispWhite,
                        border = BorderStroke(1.dp, HairlineBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.width(72.dp)) {
                                Text(item.startTime, fontWeight = FontWeight.Bold, color = DeepNavy)
                                Text(item.endTime, fontSize = 12.sp, color = MutedSlate)
                            }
                            Column(Modifier.weight(1f)) {
                                Text(item.subject, fontWeight = FontWeight.SemiBold, color = DarkSlate)
                                Text("${item.room} · ${item.teacher}", fontSize = 13.sp, color = MutedSlate)
                            }
                            IconButton(onClick = { onDeleteClass(item) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remover", tint = MutedSlate)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var subject by remember { mutableStateOf("") }
        var start by remember(shift) { mutableStateOf(if (shift == SchoolShift.AFTERNOON) "13:30" else "08:00") }
        var end by remember(shift) { mutableStateOf(if (shift == SchoolShift.AFTERNOON) "15:00" else "09:30") }
        var room by remember { mutableStateOf("Sala 10") }
        var teacher by remember { mutableStateOf("Professor(a)") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Nova Aula · ${DAY_LABELS[selectedDay]}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Disciplina") })
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = start, onValueChange = { start = it }, label = { Text("Início") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = end, onValueChange = { end = it }, label = { Text("Fim") }, modifier = Modifier.weight(1f))
                    }
                    OutlinedTextField(value = room, onValueChange = { room = it }, label = { Text("Sala") })
                    OutlinedTextField(value = teacher, onValueChange = { teacher = it }, label = { Text("Docente") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (subject.isNotBlank()) {
                        onSaveClass(
                            ScheduleClassEntity(
                                dayOfWeek = selectedDay,
                                subject = subject.trim(),
                                startTime = start.trim(),
                                endTime = end.trim(),
                                room = room.trim(),
                                teacher = teacher.trim()
                            )
                        )
                        showAddDialog = false
                    }
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
fun PreparatorioScreen(
    settings: UserSettings,
    isOnline: Boolean,
    schedule: List<ScheduleClassEntity>,
    prepPacks: List<PrepPackEntity>,
    isGenerating: Boolean,
    generationError: String?,
    onGenerate: (String, String, (PrepPackEntity) -> Unit) -> Unit,
    onScoreRecorded: (String, Int) -> Unit,
    onDismissError: () -> Unit
) {
    val availableSubjects = remember(schedule, settings.country) {
        val fromSchedule = schedule.map { it.subject }.distinct()
        if (fromSchedule.isNotEmpty()) fromSchedule
        else if (settings.country == CountryCurriculum.ANGOLA) listOf("Matemática", "Física", "Língua Portuguesa", "Química")
        else listOf("Matemática A", "Português", "Física e Química A", "Biologia e Geologia")
    }

    var selectedSubject by remember(availableSubjects) { mutableStateOf(availableSubjects.first()) }
    var topicInput by remember { mutableStateOf("") }
    var activePack by remember { mutableStateOf<PrepPackEntity?>(null) }

    if (activePack != null) {
        PrepPackStudyView(
            pack = activePack!!,
            onBack = { activePack = null },
            onFinishQuiz = { score -> onScoreRecorded(activePack!!.id, score) }
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = CrispWhite,
                border = BorderStroke(1.dp, HairlineBorder)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = "Preparatório de Provas · ${settings.country.label}",
                        fontWeight = FontWeight.Bold,
                        color = DeepNavy,
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(availableSubjects) { subj ->
                            FilterChip(
                                selected = selectedSubject == subj,
                                onClick = { selectedSubject = subj },
                                label = { Text(subj) }
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = topicInput,
                        onValueChange = { topicInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Tema da prova (ex: Funções, Leis de Newton)") },
                        enabled = isOnline && !isGenerating
                    )
                    if (!isOnline) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Modo Offline ativo: pode abrir e refazer qualquer resumo/simulado salvo abaixo.",
                            fontSize = 12.sp,
                            color = MutedSlate
                        )
                    }
                    if (generationError != null) {
                        Spacer(Modifier.height(6.dp))
                        Text(generationError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = {
                            onDismissError()
                            if (topicInput.isNotBlank()) {
                                onGenerate(selectedSubject, topicInput.trim()) { created ->
                                    topicInput = ""
                                    activePack = created
                                }
                            }
                        },
                        enabled = isOnline && !isGenerating && topicInput.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = DeepNavy)
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = CrispWhite, strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                            Text("A gerar resumo e simulado...")
                        } else {
                            Text("Gerar com Gemini e Salvar Offline")
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Pacotes Guardados no Dispositivo (${prepPacks.size})",
                fontWeight = FontWeight.Bold,
                color = DeepNavy
            )
        }

        items(prepPacks, key = { it.id }) { pack ->
            Surface(
                modifier = Modifier.fillMaxWidth().clickable { activePack = pack },
                shape = RoundedCornerShape(14.dp),
                color = CrispWhite,
                border = BorderStroke(1.dp, HairlineBorder)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        text = "${pack.subject} · ${pack.grade}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SoftCyan
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(pack.title, fontWeight = FontWeight.Bold, color = DarkSlate)
                    Spacer(Modifier.height(4.dp))
                    Text(pack.overview, fontSize = 13.sp, color = MutedSlate, maxLines = 2)
                    if (pack.lastScore != null) {
                        Spacer(Modifier.height(6.dp))
                        Text("Último simulado: ${pack.lastScore}%", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DeepNavy)
                    }
                }
            }
        }
    }
}

@Composable
private fun PrepPackStudyView(
    pack: PrepPackEntity,
    onBack: () -> Unit,
    onFinishQuiz: (Int) -> Unit
) {
    val keyPoints = remember(pack.id) { jsonParser.decodeFromString<List<String>>(pack.keyPointsJson) }
    val tips = remember(pack.id) { jsonParser.decodeFromString<List<String>>(pack.tipsJson) }
    val questions = remember(pack.id) { jsonParser.decodeFromString<List<QuizQuestion>>(pack.questionsJson) }

    var showQuiz by remember { mutableStateOf(false) }
    val selectedAnswers = remember(pack.id) { mutableStateMapOf<Int, Int>() }
    var submitted by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onBack) { Text("← Voltar") }
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = {
                        showQuiz = !showQuiz
                        submitted = false
                        selectedAnswers.clear()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepNavy)
                ) {
                    Text(if (showQuiz) "Ver Resumo" else "Fazer Simulado (${questions.size})")
                }
            }
        }

        if (!showQuiz) {
            item {
                Text(pack.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DeepNavy)
                Spacer(Modifier.height(6.dp))
                Text(pack.overview, fontSize = 15.sp, color = DarkSlate)
            }
            items(keyPoints) { point ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = CrispWhite,
                    border = BorderStroke(1.dp, HairlineBorder)
                ) {
                    Text(point, modifier = Modifier.padding(12.dp), fontSize = 14.sp, color = DarkSlate)
                }
            }
            items(tips) { tip ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = DeepNavy
                ) {
                    Text(tip, modifier = Modifier.padding(12.dp), fontSize = 13.sp, color = BrightCyan)
                }
            }
        } else {
            items(questions.indices.toList()) { qIdx ->
                val q = questions[qIdx]
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = CrispWhite,
                    border = BorderStroke(1.dp, HairlineBorder)
                ) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("${qIdx + 1}. ${q.question}", fontWeight = FontWeight.Bold, color = DarkSlate)
                        q.options.forEachIndexed { optIdx, opt ->
                            val picked = selectedAnswers[qIdx] == optIdx
                            val isCorrect = optIdx == q.correctIndex
                            val borderColor = when {
                                submitted && isCorrect -> Color(0xFF059669)
                                submitted && picked && !isCorrect -> Color(0xFFDC2626)
                                picked -> SoftCyan
                                else -> HairlineBorder
                            }
                            Surface(
                                modifier = Modifier.fillMaxWidth().clickable(enabled = !submitted) {
                                    selectedAnswers[qIdx] = optIdx
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (picked) Color(0xFFECFEFF) else CanvasSlate,
                                border = BorderStroke(1.dp, borderColor)
                            ) {
                                Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(opt, modifier = Modifier.weight(1f), fontSize = 14.sp, color = DarkSlate)
                                    if (submitted && isCorrect) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669))
                                    } else if (submitted && picked && !isCorrect) {
                                        Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFDC2626))
                                    }
                                }
                            }
                        }
                        if (submitted) {
                            Text("Explicação: ${q.explanation}", fontSize = 13.sp, color = MutedSlate)
                        }
                    }
                }
            }
            item {
                Button(
                    onClick = {
                        submitted = true
                        val correct = questions.indices.count { selectedAnswers[it] == questions[it].correctIndex }
                        val pct = if (questions.isEmpty()) 0 else (correct * 100) / questions.size
                        onFinishQuiz(pct)
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepNavy)
                ) {
                    Text(if (submitted) "Simulado Corrigido" else "Corrigir Simulado")
                }
            }
        }
    }
}
