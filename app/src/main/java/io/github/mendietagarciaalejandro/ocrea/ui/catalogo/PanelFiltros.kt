package io.github.mendietagarciaalejandro.ocrea.ui.catalogo

import androidx.annotation.StringRes
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import io.github.mendietagarciaalejandro.ocrea.R
import io.github.mendietagarciaalejandro.ocrea.dominio.Departamento
import io.github.mendietagarciaalejandro.ocrea.dominio.FiltrosBusqueda

@StringRes
fun Departamento.etiqueta(): Int = when (this) {
    Departamento.PinturaEsculturaEuropa -> R.string.departamento_pintura_europa
    Departamento.ArteContemporaneo -> R.string.departamento_contemporaneo
    Departamento.ArteAmericano -> R.string.departamento_americas
    Departamento.ArteAsiatico -> R.string.departamento_asia
    Departamento.ArteAfricano -> R.string.departamento_africa
    Departamento.GrabadosDibujos -> R.string.departamento_grabados
    Departamento.Fotografia -> R.string.departamento_fotografia
    Departamento.ArteTextil -> R.string.departamento_textil
    Departamento.ArteAplicado -> R.string.departamento_aplicado
    Departamento.Arquitectura -> R.string.departamento_arquitectura
}

@Composable
fun PanelFiltros(
    filtros: FiltrosBusqueda,
    onArtista: (String) -> Unit,
    onDepartamento: (Departamento?) -> Unit,
    onAnios: (Int?, Int?) -> Unit,
    onLimpiar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedTextField(
            value = filtros.artista,
            onValueChange = onArtista,
            label = { Text(stringResource(R.string.filtro_artista)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Text(
            text = stringResource(R.string.filtro_epoca),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CampoAnio(
                valor = filtros.desde,
                etiqueta = R.string.filtro_desde,
                onCambio = { onAnios(it, filtros.hasta) },
                modifier = Modifier.width(140.dp),
            )
            CampoAnio(
                valor = filtros.hasta,
                etiqueta = R.string.filtro_hasta,
                onCambio = { onAnios(filtros.desde, it) },
                modifier = Modifier.width(140.dp),
            )
        }

        // Aviso en vez de bloqueo: el usuario puede estar a medio escribir el segundo año.
        if (filtros.desde != null && filtros.hasta != null && filtros.desde > filtros.hasta) {
            Text(
                text = stringResource(R.string.anio_invalido),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }

        Text(
            text = stringResource(R.string.filtro_departamento),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Departamento.entries.forEach { departamento ->
                val elegido = filtros.departamento == departamento
                FilterChip(
                    selected = elegido,
                    onClick = { onDepartamento(if (elegido) null else departamento) },
                    label = { Text(stringResource(departamento.etiqueta())) },
                )
            }
        }

        if (filtros.hayFiltros) {
            AssistChip(
                onClick = onLimpiar,
                label = { Text(stringResource(R.string.limpiar_filtros)) },
            )
        }
    }
}

@Composable
private fun CampoAnio(
    valor: Int?,
    @StringRes etiqueta: Int,
    onCambio: (Int?) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = valor?.toString().orEmpty(),
        onValueChange = { texto ->
            // Se admite el signo menos porque el museo tiene piezas anteriores a Cristo.
            val limpio = texto.filterIndexed { i, c -> c.isDigit() || (i == 0 && c == '-') }
            onCambio(limpio.toIntOrNull())
        },
        label = { Text(stringResource(etiqueta)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier,
    )
}
