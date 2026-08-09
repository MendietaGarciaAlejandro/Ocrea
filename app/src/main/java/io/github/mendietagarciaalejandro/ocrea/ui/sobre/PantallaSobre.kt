package io.github.mendietagarciaalejandro.ocrea.ui.sobre

import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import io.github.mendietagarciaalejandro.ocrea.BuildConfig
import io.github.mendietagarciaalejandro.ocrea.R

private const val URL_API = "https://api.artic.edu"
private const val URL_CODIGO = "https://github.com/MendietaGarciaAlejandro/Ocrea"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaSobre(
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contexto = LocalContext.current

    fun abrir(url: String) {
        contexto.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.sobre_titulo)) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.volver),
                        )
                    }
                },
            )
        },
    ) { relleno ->
        Column(
            modifier = Modifier
                .padding(relleno)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Apartado(R.string.sobre_que_es_titulo, R.string.sobre_que_es)

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Apartado(R.string.sobre_datos_titulo, R.string.sobre_datos)
            TextButton(onClick = { abrir(URL_API) }) {
                Text(stringResource(R.string.sobre_datos_enlace))
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Apartado(R.string.sobre_autoria_titulo, R.string.sobre_autoria)
            TextButton(onClick = { abrir(URL_CODIGO) }) {
                Text(stringResource(R.string.sobre_codigo))
            }

            Text(
                text = stringResource(R.string.sobre_version, BuildConfig.VERSION_NAME),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 20.dp),
            )
        }
    }
}

@Composable
private fun Apartado(@StringRes titulo: Int, @StringRes cuerpo: Int) {
    Text(
        text = stringResource(titulo),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 6.dp),
    )
    Text(
        text = stringResource(cuerpo),
        style = MaterialTheme.typography.bodyMedium,
    )
}
