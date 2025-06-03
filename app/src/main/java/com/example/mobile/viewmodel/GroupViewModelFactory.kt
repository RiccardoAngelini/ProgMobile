

package com.example.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mobile.model.GroupRepository


//creazione di istanze delle classi ViewModel.

@Suppress("UNCHECKED_CAST")
class GroupViewModelFactory(

    private val groupRepository: GroupRepository,

) : ViewModelProvider.Factory {

    //Il metodo è responsabile della creazione di istanze di GroupViewModel .
    override fun <T : ViewModel> create(modelClass: Class<T>): T { //modelClass corrisponde alla GroupViewModel
        require(modelClass.isAssignableFrom(GroupViewModel::class.java)) {
            "Classe ViewModel non supportata: ${modelClass.simpleName}"
        }
        return GroupViewModel(groupRepository) as T
        // Se modelClasscorrisponde a GroupViewModel, crea un'istanza GroupViewModel utilizzando i parametri del costruttore groupRepository.
        // Converte il ViewModel creato nel tipo generico Te lo restituisce.

    }
}
//Supponendo che tu fornisca correttamente la Factory e le dipendenze necessarie, il tuo ViewModel
// verrà istanziato correttamente con le dipendenze richieste e funzionerà bene con la Factory