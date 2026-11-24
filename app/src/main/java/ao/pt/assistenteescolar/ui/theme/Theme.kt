package ao.pt.assistenteescolar.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DeepNavy = Color(0xFF0B192C)
val NavySurface = Color(0xFF132847)
val SoftCyan = Color(0xFF06B6D4)
val BrightCyan = Color(0xFF22D3EE)
val CrispWhite = Color(0xFFFFFFFF)
val CanvasSlate = Color(0xFFF8FAFC)
val DarkSlate = Color(0xFF1E293B)
val MutedSlate = Color(0xFF64748B)
val HairlineBorder = Color(0xFFE2E8F0)

private val AssistenteLightColorScheme = lightColorScheme(
    primary = DeepNavy,
    onPrimary = CrispWhite,
    secondary = SoftCyan,
    onSecondary = DeepNavy,
    background = CanvasSlate,
    onBackground = DarkSlate,
    surface = CrispWhite,
    onSurface = DarkSlate,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = MutedSlate,
    outline = HairlineBorder
)

private val AssistenteDarkColorScheme = darkColorScheme(
    primary = BrightCyan,
    onPrimary = DeepNavy,
    secondary = SoftCyan,
    onSecondary = DeepNavy,
    background = Color(0xFF020617),
    onBackground = Color(0xFFF1F5F9),
    surface = Color(0xFF0F172A),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF1E293B)
)

@Composable
fun AssistenteEscolarTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) AssistenteDarkColorScheme else AssistenteLightColorScheme,
        content = content
    )
}
