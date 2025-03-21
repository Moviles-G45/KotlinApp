import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.budgetbuddy.ui.theme.DarkGreen
import com.example.budgetbuddy.ui.theme.LightGreenishWhite
import com.example.budgetbuddy.ui.theme.PrimaryBlue


@Composable
fun IncomeExpenseProgressBar(
    totalIncome: Double,
    totalExpense: Double,
    modifier: Modifier = Modifier
) {
    val fraction = if (totalIncome != 0.0) (totalExpense / totalIncome).toFloat() else 0f
    val progress = fraction.coerceIn(0f, 1f)
    val percentage = (progress * 100).toInt()

    // Caja externa con borde "azul" (simulando el estilo de la imagen)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(30.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(PrimaryBlue) // Color de borde
    ) {
        // Fondo "track" de la barra (color claro)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(15.dp))
                .background(LightGreenishWhite) // Color de la barra vacía
        )

        // Barra de progreso (porción "rellena")
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .clip(RoundedCornerShape(15.dp))
                .background(DarkGreen) // Color de la barra llena (ej. DarkGreen)
        )

        // Textos: porcentaje a la izquierda, total income a la derecha
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${percentage}%",
                style = MaterialTheme.typography.bodySmall.copy(color = LightGreenishWhite)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$${"%.2f".format(totalIncome)}",
                style = MaterialTheme.typography.bodyMedium.copy(color = DarkGreen)
            )
        }
    }
}
