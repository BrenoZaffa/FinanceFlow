package com.example.financeflow.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.financeflow.entity.Lancamento

class DatabaseHandler private constructor (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "bdfile.sqlite"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "lancamentos"

        @Volatile
        private var instance: DatabaseHandler? = null

        fun getInstance(context: Context): DatabaseHandler {
            if (instance == null) {
                instance = DatabaseHandler(context.applicationContext)
            }
            return instance as DatabaseHandler
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME (_id INTEGER PRIMARY KEY AUTOINCREMENT, descricao TEXT, tipo INTEGER, valor REAL, data INTEGER)")
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun inserir(lancamento: Lancamento) {
        val values = ContentValues().apply {
            put("descricao", lancamento.descricao)
            put("tipo", lancamento.tipo)
            put("valor", lancamento.valor)
            put("data", lancamento.data)
        }
        writableDatabase.insert(TABLE_NAME, null, values)
    }

    fun listar(): Cursor {
        return readableDatabase.query(TABLE_NAME, null, null, null, null, null, null)
    }

}