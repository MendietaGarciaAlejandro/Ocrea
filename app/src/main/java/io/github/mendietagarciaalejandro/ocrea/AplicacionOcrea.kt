package io.github.mendietagarciaalejandro.ocrea

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import javax.inject.Inject

/** Punto de entrada de Hilt: desde aquí cuelga el grafo de dependencias de toda la app. */
@HiltAndroidApp
class AplicacionOcrea : Application(), SingletonImageLoader.Factory {

    @Inject
    lateinit var clienteHttp: OkHttpClient

    /**
     * Coil reutiliza el mismo OkHttpClient que la API en lugar de crear el suyo: comparten
     * el pool de conexiones y las imágenes del museo salen del mismo servidor.
     */
    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader.Builder(context)
            .components { add(OkHttpNetworkFetcherFactory(callFactory = { clienteHttp })) }
            .crossfade(true)
            .build()
}
