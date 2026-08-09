package io.github.mendietagarciaalejandro.ocrea.ui.detalle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mendietagarciaalejandro.ocrea.datos.RepositorioFavoritos
import io.github.mendietagarciaalejandro.ocrea.datos.RepositorioObras
import io.github.mendietagarciaalejandro.ocrea.dominio.Obra
import io.github.mendietagarciaalejandro.ocrea.ui.navegacion.Detalle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

sealed interface EstadoDetalle {
    data object Cargando : EstadoDetalle
    data class Contenido(val obra: Obra) : EstadoDetalle
    data class Error(val sinConexion: Boolean) : EstadoDetalle
}

@HiltViewModel
class DetalleViewModel @Inject constructor(
    private val repositorio: RepositorioObras,
    private val favoritos: RepositorioFavoritos,
    estadoGuardado: SavedStateHandle,
) : ViewModel() {

    private val obraId = estadoGuardado.toRoute<Detalle>().obraId

    private val _estado = MutableStateFlow<EstadoDetalle>(EstadoDetalle.Cargando)
    val estado: StateFlow<EstadoDetalle> = _estado.asStateFlow()

    /** Sale de Room, así que el corazón se actualiza solo al marcar o desmarcar. */
    val esFavorita: StateFlow<Boolean> = favoritos.observarSiEsFavorita(obraId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _estado.value = EstadoDetalle.Cargando
            _estado.value = try {
                EstadoDetalle.Contenido(repositorio.obtenerDetalle(obraId))
            } catch (e: IOException) {
                EstadoDetalle.Error(sinConexion = true)
            } catch (e: Exception) {
                EstadoDetalle.Error(sinConexion = false)
            }
        }
    }

    fun alternarFavorito() {
        val actual = _estado.value
        if (actual !is EstadoDetalle.Contenido) return

        viewModelScope.launch { favoritos.alternar(actual.obra) }
    }
}
