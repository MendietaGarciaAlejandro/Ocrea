package io.github.mendietagarciaalejandro.ocrea

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/** Punto de entrada de Hilt: desde aquí cuelga el grafo de dependencias de toda la app. */
@HiltAndroidApp
class AplicacionOcrea : Application()
