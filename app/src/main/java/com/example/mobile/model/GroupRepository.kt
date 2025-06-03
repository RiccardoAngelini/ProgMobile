package com.example.mobile.model

import android.util.Log
import com.example.mobile.exception.UserNotLoggedInException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import java.util.Date

class GroupRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val groupsCollection: CollectionReference = firestore.collection("groups")
    private val userCollection: CollectionReference = firestore.collection("user")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUser: FirebaseUser? = auth.currentUser


   //SEZIONE GRUPPI

    //ottiene i gruppi a cui l'utente corrente appartiene
    suspend fun getGroupsForCurrentUser(): List<Group> {
        val userId = currentUser?.uid ?: throw UserNotLoggedInException()
        try {
            // Esegui una query per ottenere i gruppi a cui l'utente corrente è associato
            val query = groupsCollection.whereArrayContains("members", userId)
            val querySnapshot = query.get().await()

            val groups = mutableListOf<Group>()

            for (document in querySnapshot.documents) {
                val group = document.toObject(Group::class.java)
                group?.let {
                    groups.add(it)
                }
            }

            return groups

        } catch (e: Exception) {
            // Gestisci eventuali eccezioni qui
            return emptyList() // Restituisci una lista vuota in caso di errore
        }
    }

//aggiunge un nuovo gruppo, l'utente che lo crea diviene admin
    suspend fun addGroup(name: String, type: String) {
        try {
            val userId = currentUser?.uid
            if (userId != null) {

                val newGroupDocument = groupsCollection.document()
                val newGroup = Group(
                    id = newGroupDocument.id,
                    name = name,
                    type = type,
                    idAmministratore = userId,
                    members = mutableListOf(userId),
                    groupExpense= emptyList() //messo dopo vedi se da problemi
                )

                // Aggiungi il nuovo gruppo al database Firestore
                newGroupDocument.set(newGroup).await()


            } else {
                throw UserNotLoggedInException()
            }
        } catch (e: Exception) {
            // Gestisci eventuali eccezioni qui
        }
    }


//verifica se l'utente corrente è amministratore del gruppo
    suspend fun isCurrentUserAdmin(groupId: String): Boolean {
        try {
            val userId = currentUser?.uid ?: throw UserNotLoggedInException()
            val groupDocument = groupsCollection.document(groupId).get().await()
            val adminId = groupDocument.getString("idAmministratore")

            Log.d("GroupRepository", "AdminId for group $groupId: $adminId")

            if (userId == adminId) {
                return true
            } else {
                return false
            }
        } catch (e: Exception) {
            Log.e("GroupRepository", "Error checking admin status", e)
            return false
        }
    }



//SEZIONE UTENTI
//restituisce il nome dall'id dello user
    suspend fun getUserNameById(userId: String): String {
        try {
            val querySnapshot = userCollection.document(userId).get().await()

            if (querySnapshot.exists()) {
                val name = querySnapshot.get("name") as? String
                return name ?: "Nome non disponibile"
            } else {
                return "Documento non trovato per l'ID $userId"
            }
        } catch (e: Exception) {
            // Gestire l'eccezione in modo appropriato, ad esempio, log o ritorno di un valore predefinito
            return "Errore durante il recupero del nome utente: ${e.message}"
        }
    }

//restituisce tutti gli utenti della collezione USER
        suspend fun allUser(): List<User> {
            return try {
                val querySnapshot = firestore.collection("user").get().await()

                val userList = mutableListOf<User>()

                for (document in querySnapshot.documents) {
                    val userId = document.id
                    val userName = document.getString("name") ?: ""
                    val userEmail = document.getString("email") ?: ""
                    val userPassword =document.getString("password") ?: ""
                    val userExpense = document.get("userExpense") as? List<String> ?: emptyList()
                    val userPayment = document.get("userPayment") as? List<String> ?: emptyList()
                    val user = User(userId, userName, userEmail,userPassword,userExpense,userPayment)
                    userList.add(user)
                }

               return userList
            } catch (e: Exception) {
                // Gestisci eventuali errori, ad esempio nel caso in cui la lettura da Firestore fallisca
                Log.e("GroupRepository", "Errore durante la lettura da Firestore di tutti gli utenti", e)
                emptyList()
            }
        }

    //restituisce i membri di un gruppo
    suspend fun getUsersGroup(groupId: String, allUsers: List<User>): List<User> {
        return try {

            val groupSnapshot = groupsCollection.document(groupId).get().await()

            val members = groupSnapshot.get("members") as List<*>?
            Log.d("GroupRepository", "Members: $members")

            // Filtra gli utenti sulla base degli ID presenti nella lista members del gruppo

            val groupUsers = allUsers.filter { user -> members?.contains(user.id) == true  }
            groupUsers
        } catch (e: FirebaseFirestoreException) {
            Log.e("GroupRepository", "Errore di Firestore durante la lettura da Firestore degli utenti del gruppo", e)
            emptyList()
        } catch (e: Exception) {
            // Gestisci eventuali altri tipi di eccezioni qui
            Log.e("GroupRepository", "Errore generico durante la lettura da Firestore degli utenti del gruppo", e)
            emptyList()
        }
    }

    //restituisce gli utenti NON membri del gruppo
    suspend fun getNonUsersGroup(groupId: String, allUsers: List<User>): List<User> {
        return try {
            val groupSnapshot = groupsCollection.document(groupId).get().await()

            val members = groupSnapshot.get("members") as List<*>?
            Log.d("GroupRepository", "Members: $members")


            // Filtra gli utenti sulla base degli ID che non sono presenti nella lista members del gruppo
            val nonGroupMembers = allUsers.filter { user -> members?.contains(user.id) != true }

            nonGroupMembers
        } catch (e: Exception) {
            // Gestisci eventuali errori, ad esempio nel caso in cui la lettura da Firestore fallisca
            Log.e("GroupRepository", "Errore durante la lettura da Firestore degli utenti non appartenenti al gruppo", e)
            emptyList()
        }
    }


    //aggiunge un utente al gruppo
    suspend fun addUserToGroup(groupId: String, user: User) {
        try {
            // Ottieni la lista corrente dei membri dal documento del gruppo
            val groupSnapshot = groupsCollection.document(groupId).get().await()
            val currentMembers = groupSnapshot["members"] as? List<String> ?: emptyList()

            // Aggiungi l'ID dell'utente alla lista dei membri (se non è già presente)
            if (user.id !in currentMembers) {
                val updatedMembers = currentMembers.toMutableList().apply {
                    add(user.id)
                }

                // Aggiorna l'intera lista dei membri nel documento del gruppo
                groupsCollection.document(groupId)
                    .update("members", updatedMembers)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("GroupRepository", "Operazione di aggiornamento completata con successo")
                        } else {
                            Log.e("GroupRepository", "Operazione di aggiornamento non riuscita", task.exception)
                        }
                    }
            } else {
                Log.e("GroupRepository", "L'utente è già presente nel gruppo")
            }
        } catch (e: Exception) {
            Log.e("GroupRepository", "Errore durante l'aggiunta dell'utente al gruppo", e)
        }
    }


    //SEZIONE SPESE

//aggiunge una spesa ed un pagamento tra gli utenti associati ad essi
    suspend fun addExpenseAmongUsers(users: List<User>, groupId: String, expenseName: String, expenseAmount: Double, paymentUser: User, date: Date) {
        try {
            // Verifica l'appartenenza dell'utente al gruppo
            val groupSnapshot = groupsCollection.document(groupId).get().await()
            val group = groupSnapshot.toObject(Group::class.java)
            val membersList = group?.members

            if (membersList != null && paymentUser.id in membersList) {
                // Calcola l'importo che ciascun utente deve pagare
                val totalUsers = users.size

                val amountPerUser = expenseAmount / totalUsers

                // Creazione della spesa
                val expenseDocument = firestore.collection("expenses").document()
                val expense = Expense(
                    id = expenseDocument.id,
                    name = expenseName,
                    amount = amountPerUser,
                    payer = paymentUser.id,
                    debtors = users.map { it.id },
                    date = date
                )
                expenseDocument.set(expense).await()

                // Itera attraverso gli utenti e aggiungi la spesa a ciascun utente
                for (user in users) {
                    if ( user.id in membersList) {
                        // Aggiungi la spesa all'utente
                        userCollection.document(user.id)
                            .update("userExpense", FieldValue.arrayUnion(expense.id))
                            .await()

                        // Aggiungi l'importo alla lista degli utenti che devono restituire i soldi (userDebts)
                        expenseDocument.update("debtors", FieldValue.arrayUnion(user.id))
                            .await()
                    }
                }

                // Creazione del pagamento
                val paymentDocument = firestore.collection("payment").document()
                val payment = Payment(
                    id = paymentDocument.id,
                    name = expenseName,
                    amount = expenseAmount,
                    expense = expenseDocument.id,
                    date=date
                )
                paymentDocument.set(payment).await()

                // Aggiorna la collezione user aggiungendo l'id del pagamento al campo userPayment
                userCollection.document(paymentUser.id)
                    .update("userPayment", FieldValue.arrayUnion(paymentDocument.id))
                    .await()

                // Aggiorna la collezione del gruppo con l'ID della spesa e del pagamento al campo groupExpense
                groupsCollection.document(groupId)
                    .update("groupExpense", FieldValue.arrayUnion(expense.id, paymentDocument.id))
                    .await()

            } else {
                Log.d("GroupRepository", "Il pagatore ${paymentUser.name} non appartiene al gruppo $groupId")
            }
        } catch (e: Exception) {
            // Gestisci eventuali errori
            Log.e("GroupRepository", "Errore durante l'aggiunta della spesa tra utenti", e)
        }
    }

    //restituisce tutte le spese della collezione EXPENSES da firestore
    suspend fun getExpense():List<Expense>{
        return try {
            val querySnapshot = firestore.collection("expenses").get().await()

            val expenseList = mutableListOf<Expense>()

            for (document in querySnapshot.documents) {
                val expenseId = document.id
                val expenseName = document.getString("name") ?: ""
                val expenseAmount = document.getDouble("amount") ?: 0.0
                val expensePayer = document.getString("payer") ?: ""
                val expenseDebtors = document.get("debtors") as? List<String> ?: emptyList()
                val expenseDate = document.getDate("date") ?: Date()

                val expense = Expense(expenseId, expenseName, expenseAmount, expensePayer, expenseDebtors,expenseDate)
                expenseList.add(expense)
            }

            return expenseList
        } catch (e: Exception) {
            // Gestisci eventuali errori, ad esempio nel caso in cui la lettura da Firestore fallisca
            Log.e("GroupRepository", "Errore durante la lettura da Firestore di tutte le spese", e)
            emptyList()
        }
    }

//restituisce le spese del gruppo groupId
    suspend fun getGroupExpenses(groupId: String, getExpense:List<Expense>): List<Expense> {
        return try {
            val groupSnapshot = groupsCollection.document(groupId).get().await()
            val groupExpense = groupSnapshot.get("groupExpense") as List<*>?
            Log.d("GroupRepository", "GroupExpense: $groupExpense")

            // Filtra le spese sulla base degli ID presenti nella lista groupExpenses del gruppo
            val expenseList = getExpense.filter { expense -> groupExpense?.contains(expense.id) == true }
            expenseList
        } catch (e: Exception) {
            // Gestisci eventuali errori
            emptyList()
        }
    }

//restituisce i pagamenti della collezione PAYMENT in firestore
    suspend fun getPayment(): List<Payment> {
        return try {
            val querySnapshot = firestore.collection("payment").get().await()

            val paymentList = mutableListOf<Payment>()

            for (document in querySnapshot.documents) {
                val paymentId = document.id
                val paymentName = document.getString("name") ?: ""
                val paymentAmount = document.getDouble("amount") ?: 0.0
                val paymentExpenseId = document.getString("expense") ?: ""
                val paymentDate = document.getDate("date") ?: Date()

                val payment = Payment(paymentId, paymentName, paymentAmount, paymentExpenseId,paymentDate)
                paymentList.add(payment)
            }

            return paymentList
        } catch (e: Exception) {
            // Gestisci eventuali errori, ad esempio nel caso in cui la lettura da Firestore fallisca
            Log.e("GroupRepository", "Errore durante la lettura da Firestore di tutti i pagamenti", e)
            emptyList()
        }
    }

    //restituisce i pagamenti del gruppo groupId
    suspend fun getGroupPayments(groupId: String, getPayment: List<Payment>): List<Payment> {
        return try {
            val groupSnapshot = groupsCollection.document(groupId).get().await()
            val groupPayment = groupSnapshot.get("groupExpense") as List<*>?
            Log.d("GroupRepository", "GroupPayment: $groupPayment")

            // Filtra i pagamenti sulla base degli ID presenti nella lista groupPayment del gruppo
            val paymentList = getPayment.filter { payment -> groupPayment?.contains(payment.id) == true }
            paymentList
        } catch (e: Exception) {
            // Gestisci eventuali errori
            emptyList()
        }
    }


//restituisce la somma totale dei pagamenti di un gruppo
    suspend fun getTotalGroupPayment(getPayment: List<Payment>): Double {
        try{
            var total=0.0
            for(payment in getPayment){
                total+=payment.amount
            }
            return total
        }catch (e: Exception){  Log.e("GroupRepository", "Errore durante il calcolo della somma totale dei pagamenti del gruppo", e)
            return 0.0
        }
    }


//recupera la spesa associata ad un pagamento
    suspend fun getExpenseByPayment(payment:Payment): Expense? {
      try {
//query di firestore che accede al documento del pagamento recuperato grazie a payment.id
          val querySnapshot = firestore.collection("payment").document(payment.id).get().await()
          //accede al campo expenses che contiene l'id della spesa associata al pagamento
          val expenseId = querySnapshot.getString("expense")
          if (expenseId != null) {
              //recupera il documento firestore della spesa
              val expenseSnapshot =
                  firestore.collection("expenses").document(expenseId).get().await()
              val expense = expenseSnapshot.toObject(Expense::class.java)
              return expense
          } else {
              return null
          }
      }catch (e: Exception) {
            Log.e("UserFragment", "Errore durante il recupero della spesa associata al pagamento", e)
        }
        return null
    }

    //restituisce le spese associate all'utente userId nel grupppo groupId
   suspend fun getUserExpenses(groupId: String, userId: String): List<Expense> {
       try {
           val allExpense = getExpense()
           val groupExpense= getGroupExpenses(groupId,allExpense)

           Log.d("GroupRepositoryGetExpense", "Group Expenses: $groupExpense")

           val userExpense = mutableListOf<Expense>()
           //dopo aver recuperato le spese del gruppo si itera attraverso queste e si cerca se l'id dell'utente è nella lista debtors di expense


           for(expense in groupExpense){
               if(userId in expense.debtors)
                   userExpense.add(expense)
           }

               Log.d("GroupRepositoryGetExpense", "User $userId - User Expenses: $userExpense")

               return userExpense

       } catch (e: Exception) {
           Log.e("GroupRepositoryGetExpense", "Errore durante il recupero delle spese del gruppo per l'utente $userId", e)
       }

       return emptyList()
   }

    //recupera i pagamenti associati all'utente userId di un gruppo groupId
    suspend fun getUserPayments(groupId: String, userId: String): List<Payment> {
        try {

            val groupPayment=getGroupPayments(groupId,getPayment())
            Log.d("GroupRepositoryGetExpense", "Group Expenses: $groupPayment")
            val userPayments = mutableListOf<Payment>()

            for(payment in groupPayment){
                val expense =  getExpenseByPayment(payment)
                Log.d("getExpenseByPayment", "Payment -> Expenses: $expense")
                if (expense != null) {
                    if(userId == expense.payer)
                        userPayments.add(payment)

                }
            }
            Log.d("GroupRepositoryGetPayment", "User $userId - Group Payments: $userPayments")
            return userPayments

        } catch (e: Exception) {
            Log.e("GroupRepositoryGetPayment", "Errore durante il recupero dei pagamenti del gruppo per l'utente $userId", e)
        }

        return emptyList()
    }


//restituisce la somma dei debiti associati all'utente corrente che ha nel gruppo groupId
    suspend fun getUserDebts(groupId:String):Double{
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: throw UserNotLoggedInException()

        val userExpenses = getUserExpenses(groupId,userId)
        return userExpenses.sumOf { it.amount }
    }
    //restituisce la somma dei crediti associati all'utente corrente che ha nel gruppo groupId
    suspend fun getUserCredits(groupId:String):Double{
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: throw UserNotLoggedInException()

        val userPayments = getUserPayments(groupId,userId)
        return userPayments.sumOf { it.amount }
    }


//calcola il bilancio tra i vari utenti appartenenti al gruppo groupId
    suspend fun calculateUserBalances(groupId: String): Map<String, Double> {

        try {
            //  mappa dei saldi contenente l'id dell'utente e l'importo
            val userBalances = mutableMapOf<String, Double>()

            // Calcola il saldo per ogni utente nel gruppo
            val groupSnapshot = groupsCollection.document(groupId).get().await()
            val group = groupSnapshot.toObject(Group::class.java)
            val groupMembers = group?.members ?: emptyList()


            for (memberId in groupMembers) {
                try {
                    // Calcola il saldo per l'utente corrente rispetto a ciascun membro del gruppo
                    val memberExpenses = getUserExpenses(groupId, memberId)
                    val memberPayments = getUserPayments(groupId, memberId)

                    val totalExpense = memberExpenses.sumOf { it.amount }
                    val totalPayment = memberPayments.sumOf { it.amount }

                    val balance = totalExpense - totalPayment
                    userBalances[memberId] = balance

                    Log.d("UserFragment", "Member $memberId - Total Expense: $totalExpense, Total Payment: $totalPayment, Balance: $balance")
                } catch (e: Exception) {
                    Log.e("UserFragment", "Errore durante il calcolo del saldo per l'utente $memberId", e)
                }
            }

            return userBalances
        } catch (e: UserNotLoggedInException) {
            // Gestisci l'eccezione in modo appropriato, ad esempio, effettua il logout dell'utente
        } catch (e: Exception) {
            // Gestisci altre eccezioni in modo appropriato
            Log.e("UserFragment", "Errore durante il calcolo dei saldi degli utenti", e)
        }

        return emptyMap()
    }



}



