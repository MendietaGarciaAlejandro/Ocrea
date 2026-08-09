package io.github.mendietagarciaalejandro.ocrea

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import io.github.mendietagarciaalejandro.ocrea.ui.navegacion.NavegacionOcrea
import io.github.mendietagarciaalejandro.ocrea.ui.tema.TemaOcrea

@AndroidEntryPoint
class ActividadPrincipal : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Tiene que ir antes de super.onCreate para que el sistema sepa que esta pantalla
        // reemplaza al arranque en blanco.
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TemaOcrea {
                NavegacionOcrea(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
