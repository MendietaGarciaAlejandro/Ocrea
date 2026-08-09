package io.github.mendietagarciaalejandro.ocrea.ui.favoritos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mendietagarciaalejandro.ocrea.datos.RepositorioFavoritos
import io.github.mendietagarciaalejandro.ocrea.dominio.Obra
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FavoritosViewModel @Inject constructor(
    repositorio: RepositorioFavoritos,
) : ViewModel() {

    val favoritos: StateFlow<List<Obra>> = repositorio.observarFavoritos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
