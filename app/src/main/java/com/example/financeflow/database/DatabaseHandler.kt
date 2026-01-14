package com.example.financeflow.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.financeflow.entity.Lancamento

class DatabaseHandler private constructor(context: Context)
    : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "bdfile.sqlite"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "lancamentos"

        private const val COL_ID = "_id"
        private const val COL_DESC = "descricao"
        private const val COL_TIPO = "tipo"
        private const val COL_VALOR = "valor"
        private const val COL_DATA = "data"

        @Volatile private var instance: DatabaseHandler? = null

        fun getInstance(context: Context): DatabaseHandler {
            if (instance == null) {
                instance = DatabaseHandler(context.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_DESC TEXT,
                $COL_TIPO INTEGER,
                $COL_VALOR REAL,
                $COL_DATA INTEGER
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun inserir(l: Lancamento) {
        val v = ContentValues().apply {
            put(COL_DESC, l.descricao)
            put(COL_TIPO, l.tipo)
            put(COL_VALOR, l.valor)
            put(COL_DATA, l.data)
        }
        writableDatabase.insert(TABLE_NAME, null, v)
    }

    fun listar(): Cursor =
        readableDatabase.query(TABLE_NAME, null, null, null, null, null, "$COL_DATA DESC")

    // 👉 ESTE é o método que você estava perguntando
    fun listarTodos(): List<Lancamento> {
        val lista = mutableListOf<Lancamento>()
        val cursor = listar()

        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    Lancamento(
                        _id = it.getInt(it.getColumnIndexOrThrow(COL_ID)),
                        descricao = it.getString(it.getColumnIndexOrThrow(COL_DESC)),
                        tipo = it.getInt(it.getColumnIndexOrThrow(COL_TIPO)),
                        valor = it.getDouble(it.getColumnIndexOrThrow(COL_VALOR)),
                        data = it.getLong(it.getColumnIndexOrThrow(COL_DATA)),
                    )
                )
            }
        }
        return lista
    }
}