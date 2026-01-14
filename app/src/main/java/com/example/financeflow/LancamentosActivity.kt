package com.example.financeflow

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

        // Ajusta o padding do topo para não ficar atrás da status bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        // Configuração do Header reutilizável
        binding.header.txtTitulo.text = "Resumo"
        binding.header.btnVoltar.visibility = View.VISIBLE
        binding.header.btnVoltar.setOnClickListener {
            finish()
        }

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
