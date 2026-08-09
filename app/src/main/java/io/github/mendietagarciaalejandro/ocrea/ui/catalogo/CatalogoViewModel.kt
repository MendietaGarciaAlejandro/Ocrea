package io.github.mendietagarciaalejandro.ocrea.ui.catalogo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mendietagarciaalejandro.ocrea.datos.RepositorioObras
import io.github.mendietagarciaalejandro.ocrea.dominio.Obra
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class CatalogoViewModel @Inject constructor(
    private val repositorio: RepositorioObras,
) : ViewModel() {

    private val _consulta = MutableStateFlow("")
    val consulta: StateFlow<String> = _consulta.asStateFlow()

    /**
     * Sin consulta se muestra el catálogo (que sale de Room y funciona sin conexión);
     * con consulta, la búsqueda contra la API.
     *
     * El debounce evita disparar una petición por cada tecla: la API solo admite 60
     * peticiones por minuto y escribir "monet" son cinco. flatMapLatest cancela la
     * búsqueda anterior en cuanto cambia el texto.
     */
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val obras: Flow<PagingData<Obra>> = _consulta
        .debounce { texto -> if (texto.isEmpty()) 0L else ESPERA_TECLEO_MS }
        .map { it.trim() }
        .distinctUntilChanged()
        .flatMapLatest { texto ->
            if (texto.length < MINIMO_CARACTERES) {
                repositorio.paginarCatalogo()
            } else {
                repositorio.buscar(texto)
            }
        }
        .cachedIn(viewModelScope)

    fun alEscribir(texto: String) {
        _consulta.value = texto
    }

    fun limpiarBusqueda() {
        _consulta.value = ""
    }

    companion object {
        const val ESPERA_TECLEO_MS = 350L

        /** Con una sola letra la búsqueda devuelve medio museo y no aporta nada. */
        const val MINIMO_CARACTERES = 2
    }
}
