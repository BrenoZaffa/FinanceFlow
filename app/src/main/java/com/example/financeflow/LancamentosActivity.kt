package com.example.financeflow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financeflow.adapter.LancamentoAdapter
import com.example.financeflow.database.DatabaseHandler
import com.example.financeflow.databinding.ActivityLancamentosBinding

class LancamentosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLancamentosBinding
    private lateinit var banco: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLancamentosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        banco = DatabaseHandler.getInstance(this)

        carregarLancamentos()
    }

    private fun carregarLancamentos() {
        val lista = banco.listarTodos()

        binding.recyclerLancamentos.layoutManager = LinearLayoutManager(this)
        binding.recyclerLancamentos.adapter = LancamentoAdapter(lista)

        atualizarSaldo(lista)
    }

    private fun atualizarSaldo(lista: List<com.example.financeflow.entity.Lancamento>) {
        var saldo = 0.0
        lista.forEach {
            if (it.tipo == 1) saldo += it.valor else saldo -= it.valor
        }

        binding.txtSaldo.text = "Saldo: R$ %.2f".format(saldo)
    }
}
