package com.example.recordatorios.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthRepository(private val auth: FirebaseAuth) {

    // Flow que emite el usuario actual cada vez que cambia: inicia sesión, cierra sesión, etc.
    // "callbackFlow" conecta el listener de Firebase (basado en callbacks) con el mundo de Flow.
    val currentUser: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        // Cuando nadie más escucha este Flow, se desconecta el listener para no dejarlo colgado
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    val userId: String?
        get() = auth.currentUser?.uid

    suspend fun signUp(email: String, password: String): Result<Unit> = runCatching {
        auth.createUserWithEmailAndPassword(email, password).await()
        Unit
    }

    suspend fun signIn(email: String, password: String): Result<Unit> = runCatching {
        auth.signInWithEmailAndPassword(email, password).await()
        Unit
    }

    fun signOut() {
        auth.signOut()
    }
}

// Pequeño ayudante para poder usar "await()" sobre una Task de Firebase
// dentro de una función suspend, sin agregar otra librería
suspend fun <T> com.google.android.gms.tasks.Task<T>.await(): T =
    kotlinx.coroutines.suspendCancellableCoroutine { cont ->
        addOnSuccessListener { cont.resume(it) }
        addOnFailureListener { cont.resumeWithException(it) }
    }