package com.example.mobile.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.exception.GroupAdditionException
import com.example.mobile.model.Expense
import com.example.mobile.model.Group
import com.example.mobile.model.GroupRepository
import com.example.mobile.model.Payment
import com.example.mobile.model.User
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Date


class GroupViewModel(private val groupRepository: GroupRepository): ViewModel() {

    private val _groupLive = MutableLiveData<List<Group>>()
    val groupLive: LiveData<List<Group>>
        get() = _groupLive

    private val _isCurrentUserAdmin = MutableLiveData<Boolean>()
    val isCurrentUserAdmin: LiveData<Boolean>
        get() = _isCurrentUserAdmin

    private val _userLiveData = MutableLiveData<List<User>>()
    val userLiveData: LiveData<List<User>>
        get() = _userLiveData

    private val _expenseLiveData=MutableLiveData<List<Expense>>()
    val expenseLive: LiveData<List<Expense>>
        get()=_expenseLiveData

    private val _paymentLiveData=MutableLiveData<List<Payment>>()
    val paymentLive :LiveData<List<Payment>>
        get()=_paymentLiveData

    private val _userNameLiveData = MutableLiveData<String>()
    val userNameLiveData: LiveData<String> get() = _userNameLiveData

    private val _debtorNamesLiveData = MutableLiveData<List<String>>()
    val debtorNamesLiveData: LiveData<List<String>> get() = _debtorNamesLiveData

    private val _expenseLive=MutableLiveData<Expense?>()
    val expense: MutableLiveData<Expense?>
        get()=_expenseLive

    private val _creditsLiveData = MutableLiveData<Double>()
    val creditsLiveData: LiveData<Double> get() = _creditsLiveData

    private val _debtsLiveData = MutableLiveData<Double>()
    val debtsLiveData: LiveData<Double> get() = _debtsLiveData

    private val _totalGroupPaymentLiveData = MutableLiveData<Double>()
    val totalGroupPayment: LiveData<Double>
        get() = _totalGroupPaymentLiveData


    private val _userBalancesLiveData = MutableLiveData<Map<String, Double>>()
    val userBalancesLiveData: LiveData<Map<String, Double>> get() = _userBalancesLiveData


    //SEZIONE USER

    //ottiene i gruppi a cui l'utente corrente appartiene
    fun loadGroupUser(groupId:String){
        viewModelScope.launch {
            try {
                val allUsers=groupRepository.allUser()
                val groupUser=groupRepository.getUsersGroup(groupId,allUsers)
                _userLiveData.postValue(groupUser)
            } catch (e:Exception){
                Log.e("GroupViewModel", "Errore durante il cricamento dei partecipanti", e)
            }
        }
    }

    //aggiunge un utente al gruppo
    fun addUser(groupId: String,user: User){
        viewModelScope.launch {
            val addUserJob = launch {
                try {
                    groupRepository.addUserToGroup(groupId, user)
                } catch (e: Exception) {Log.e("GroupViewModel", "Errore nell'aggiunta dei partecipanti", e)
                }
            }

            addUserJob.join() // Attendi il completamento di addUserToGroup

            try {
                loadGroupUser(groupId)
                loadNoGroupUser(groupId)
            } catch (e: Exception) {
                Log.e("GroupViewModel", "Errore durante il cricamento dei partecipanti",e)
            }
        }


    }

    //ottiene gli utenti non appartenenti al gruppo
    fun loadNoGroupUser(groupId: String){
        viewModelScope.launch {
            try {

                val allUsers=groupRepository.allUser()
                val groupUser=groupRepository.getNonUsersGroup(groupId,allUsers)
                _userLiveData.postValue(groupUser)

            }catch (e:Exception){
                Log.e("GroupViewModel", "Errore durante il cricamento dei non partecipanti", e)

            }
        }

    }

    //ottiene il nome dell'utente userID
    fun getUserName(userId: String) {
        viewModelScope.launch {
            try {
                val name = groupRepository.getUserNameById(userId)
                _userNameLiveData.postValue(name)
            } catch (e: Exception) {
                Log.e("GroupViewModel", "Errore durante il recupero del nome utente", e)
                _userNameLiveData.postValue("Errore durante il recupero del nome utente")
            }
        }
    }

    //ottiene il nome dei debitori debtorIds
    fun getDebtorNames(debtorIds: List<String>) {
        viewModelScope.launch {
            try {
                val debtorNames = debtorIds.map { userId ->
                    groupRepository.getUserNameById(userId)
                }
                _debtorNamesLiveData.postValue(debtorNames)
            } catch (e: Exception) {
                Log.e("GroupViewModel", "Errore durante il recupero dei nomi dei debitori", e)
                _debtorNamesLiveData.postValue(emptyList())
            }
        }
    }



    //SEZIONE GROUP



    //restituisce i gruppi a cui appartiene l'utente corrente
    fun loadGroup() {
        viewModelScope.launch {
            try {
                val groups = groupRepository.getGroupsForCurrentUser()
                _groupLive.postValue(groups)
            } catch (e: Exception) {
                Log.e("GroupViewModel", "Errore durante il caricamento dei gruppi", e)
            }
        }
    }

    fun addGroup(name: String, type: String) {
            viewModelScope.launch {
                try {
                    groupRepository.addGroup(name, type)
                   loadGroup()

                } catch (e: Exception) {
                    throw GroupAdditionException("Errore durante l'aggiunta del gruppo.")
                }
            }

        }


    // Funzione per verificare se l'utente corrente è amministratore del gruppo
    fun checkIfCurrentUserIsAdmin(groupId: String) {
        viewModelScope.launch {
            try {
                // Logica per verificare se l'utente corrente è amministratore del gruppo
                val isCurrentUserAdmin = groupRepository.isCurrentUserAdmin(groupId)
                _isCurrentUserAdmin.postValue(isCurrentUserAdmin)
            } catch (e: Exception) {
                // Gestisci l'errore in base alle tue esigenze
                Log.e("GroupViewModel", "Errore durante la verifica dell'amministratore", e)
                _isCurrentUserAdmin.postValue(false)
            }
        }
    }

    //SEZIONE EXPENSE

    //restituisce la somma dei crediti associati all'utente corrente che ha nel gruppo groupId
    fun loadCredits(groupId: String) {
        viewModelScope.launch {
            try {
                val credits = groupRepository.getUserCredits(groupId)
                _creditsLiveData.postValue(credits)
            } catch (e: Exception) {
                // Gestisci l'eccezione se necessario
            }
        }
    }
    //restituisce la somma dei debiti associati all'utente corrente che ha nel gruppo groupId
    fun loadDebts(groupId: String) {
        viewModelScope.launch {
            try {
                val debts = groupRepository.getUserDebts(groupId)
                _debtsLiveData.postValue(debts)
            } catch (e: Exception) {
                // Gestisci l'eccezione se necessario
            }
        }
    }
    //restituisce la somma totale dei pagamenti di un gruppo
    fun loadTotalExpense(groupId: String) {
        viewModelScope.launch {
            try {

                val allPayment= groupRepository.getPayment()
                val groupPayments = groupRepository.getGroupPayments(groupId, allPayment)
                val debts = groupRepository.getTotalGroupPayment(groupPayments)
                _totalGroupPaymentLiveData.postValue(debts)
            } catch (e: Exception) {
                // Gestisci l'eccezione se necessario
            }
        }
    }

//restituisce le spese associate al grupo groupID
    fun loadExpenseGroup(groupId : String){
        viewModelScope.launch {
            try {
                val allExpense = groupRepository.getExpense()
                val groupExpense=groupRepository.getGroupExpenses(groupId,allExpense)
                _expenseLiveData.postValue(groupExpense)
                Log.d("GroupViewModel", "Expense loaded successfully: $groupExpense")
            }catch (e:Exception){
                Log.d("GroupViewModel","Errore nel caricamento delle spese")
            }
        }
    }
    //restituisce i pagamenti associate al grupo groupID
    fun loadGroupPayments(groupId: String) {
        viewModelScope.launch {
            try {
                // Ottieni tutti i pagamenti
                val allPayments = groupRepository.getPayment()

                // Ottieni i pagamenti relativi al gruppo specificato
                val groupPayments = groupRepository.getGroupPayments(groupId, allPayments)

                // Aggiorna il LiveData con i pagamenti relativi al gruppo
                _paymentLiveData.postValue(groupPayments)
                Log.d("GroupViewModel", "Payment loaded successfully: $groupPayments")
            } catch (e: Exception) {
                // Gestisci eventuali errori
                Log.e("GroupViewModel", "Errore durante il caricamento dei pagamenti del gruppo", e)

            }
        }
    }

    fun addExpense(userExpense:List<User>,groupId:String,expenseName:String,expenseAmount:Double ,userPayment:User, date:Date){
        viewModelScope.launch{
            val addUserJob = launch {
                try{
                    groupRepository.addExpenseAmongUsers(userExpense,groupId,expenseName,expenseAmount,userPayment,date)
                } catch (e:Exception){
                    Log.d("GroupViewModel","Errore nell'aggiunta delle spese")
                }
            }

            addUserJob.join()
            try{
                loadExpenseGroup(groupId)
                loadGroupPayments(groupId)
            }catch (e:Exception){
                Log.d("GroupViewModel","Errore nel caricamento nell'AddExpense delle spese")
            }
        }
    }
//carica la spesa associata al pagamento payment
    suspend fun fetchExpenseByPayment(payment: Payment): Expense? {
        return try {
            // Chiamata alla funzione del repository per ottenere l'Expense
            val expense = groupRepository.getExpenseByPayment(payment)

            // Aggiorna il LiveData con il risultato
            _expenseLive.postValue(expense)

            expense // Restituisce l'Expense ottenuto dal repository
        } catch (e: Exception) {
            // Gestisci eventuali errori qui, ad esempio log o visualizzazione di un messaggio di errore
            Log.e("GroupViewModel", "Errore durante il recupero dell'Expense", e)
            null // In caso di errore, restituisci null o un valore di default
        }
    }

//calcola il bilancio tra gli utenti appartenenti al gruppo grooupId
     fun calculateUserBalances(groupId: String) {
        viewModelScope.launch {
            try {
                val userBalances = groupRepository.calculateUserBalances(groupId)
                _userBalancesLiveData.postValue(userBalances)
            } catch (e: Exception) {
                // Gestisci l'errore in base alle tue esigenze
                Log.e("UserViewModel", "Errore durante il calcolo dei saldi degli utenti", e)
            }
        }
    }

//L'override di onCleared per cancellare tutte le coroutine in corso
    override fun onCleared() {
        super.onCleared()
        // Cancella tutte le coroutine in corso legate a questa ViewModel
        viewModelScope.cancel()
    }



    }

