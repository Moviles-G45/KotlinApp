package com.example.budgetbuddy.utils

import android.content.Context
import android.util.Log
import com.example.budgetbuddy.model.TransactionRequest
import java.io.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionDiskCache @Inject constructor(private val context: Context) {

    private companion object {
        const val CACHE_DIR = "transactions_cache"
        const val MAX_CACHE_SIZE = 10 * 1024 * 1024 // 10MB máximo
    }

    // Directorio de caché
    private val cacheDir = File(context.cacheDir, CACHE_DIR).apply { mkdirs() }
    private val cacheFile = File(cacheDir, "transactions_cache")
    private val lock = Any()

    // Recuperamos el mapa de transacciones almacenado
    private fun getTransactionsMap(): MutableMap<String, List<TransactionRequest>> {
        if (!cacheFile.exists()) return mutableMapOf()

        return try {
            ObjectInputStream(FileInputStream(cacheFile)).use { inputStream ->
                val obj = inputStream.readObject()
                if (obj is MutableMap<*, *>) {
                    @Suppress("UNCHECKED_CAST")
                    obj as MutableMap<String, List<TransactionRequest>>
                } else {
                    mutableMapOf()
                }
            }
        } catch (e: IOException) {
            mutableMapOf()
        } catch (e: ClassNotFoundException) {
            mutableMapOf()
        }
    }

    // Guardamos el mapa de transacciones en el caché
    private fun saveTransactionsMap(transactionsMap: MutableMap<String, List<TransactionRequest>>) {
        synchronized(lock) {
            ObjectOutputStream(FileOutputStream(cacheFile)).use { outputStream ->
                outputStream.writeObject(transactionsMap)
            }
        }
    }

    // Guardar las transacciones de un usuario específico (usando el token como llave)
    fun saveTransactionsForToken(token: String, transactions: List<TransactionRequest>) {
        synchronized(lock) {
            val transactionsMap = getTransactionsMap()
            transactionsMap[token] = transactions
            if (transactionsMap.size > MAX_CACHE_SIZE) {
                // Si el tamaño del caché excede el máximo, eliminar el más antiguo (LRU)
                val oldestToken = transactionsMap.keys.first()
                transactionsMap.remove(oldestToken)
            }

            saveTransactionsMap(transactionsMap)

        }
    }

    // Limpiar todas las transacciones del caché
    fun clearAll() {
        synchronized(lock) {
            if (cacheFile.exists()) {
                cacheFile.delete()
            }
        }
    }
    // Recuperar las transacciones de un usuario específico (usando el token como llave)
    fun getTransactionsForToken(token: String): List<TransactionRequest> {
        val transactionsMap = getTransactionsMap()
        return transactionsMap[token] ?: emptyList()
        }


}
