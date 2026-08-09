package io.github.mendietagarciaalejandro.ocrea.ui.catalogo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mendietagarciaalejandro.ocrea.datos.RepositorioObras
import io.github.mendietagarciaalejandro.ocrea.dominio.Departamento
import io.github.mendietagarciaalejandro.ocrea.dominio.FiltrosBusqueda
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
import javax.inject.Inject

@HiltViewModel
class CatalogoViewModel @Inject constructor(
    private val repositorio: RepositorioObras,
) : ViewModel() {

    private val _filtros = MutableStateFlow(FiltrosBusqueda())
    val filtros: StateFlow<FiltrosBusqueda> = _filtros.asStateFlow()

    /**
     * Sin nada buscado se muestra el catálogo (que sale de Room y funciona sin conexión);
     * en cuanto hay texto o algún filtro, se pasa a la búsqueda contra la API.
     *
     * El debounce evita disparar una petición por cada tecla: la API solo admite 60
     * peticiones por minuto y escribir "monet" son cinco. flatMapLatest cancela la
     * búsqueda anterior en cuanto cambia algo.
     */
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val obras: Flow<PagingData<Obra>> = _filtros
        .debounce { actuales -> if (actuales.estaVacio) 0L else ESPERA_TECLEO_MS }
        .distinctUntilChanged()
        .flatMapLatest { actuales ->
            if (actuales.estaVacio) {
                repositorio.paginarCatalogo()
            } else {
                repositorio.buscar(actuales)
            }
        }
        .cachedIn(viewModelScope)

    fun alEscribir(texto: String) {
        _filtros.value = _filtros.value.copy(texto = texto)
    }

    fun limpiarTexto() {
        _filtros.value = _filtros.value.copy(texto = "")
    }

    fun alCambiarArtista(artista: String) {
        _filtros.value = _filtros.value.copy(artista = artista)
    }

    fun alCambiarDepartamento(departamento: Departamento?) {
        _filtros.value = _filtros.value.copy(departamento = departamento)
    }

    fun alCambiarAnios(desde: Int?, hasta: Int?) {
        _filtros.value = _filtros.value.copy(desde = desde, hasta = hasta)
    }

    fun limpiarFiltros() {
        _filtros.value = _filtros.value.sinFiltros()
    }

    companion object {
        const val ESPERA_TECLEO_MS = 350L
    }
}
