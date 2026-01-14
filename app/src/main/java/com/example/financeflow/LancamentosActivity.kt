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

    companion object {
        const val FILTRO_TODOS = 0
        const val FILTRO_RECEITAS = 1
        const val FILTRO_DESPESAS = 2
    }

    private lateinit var binding: ActivityLancamentosBinding
    private lateinit var banco: DatabaseHandler
    private var filtroAtual = FILTRO_TODOS

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

        // Header
        binding.header.txtTitulo.text = "Resumo"
        binding.header.btnVoltar.visibility = View.VISIBLE
        binding.header.btnVoltar.setOnClickListener { finish() }

        banco = DatabaseHandler.getInstance(this)

        // Botões de filtro (precisam existir no layout)
        binding.btnFiltroTodos.setOnClickListener { aplicarFiltro(FILTRO_TODOS) }
        binding.btnFiltroReceitas.setOnClickListener { aplicarFiltro(FILTRO_RECEITAS) }
        binding.btnFiltroDespesas.setOnClickListener { aplicarFiltro(FILTRO_DESPESAS) }

        aplicarFiltro(FILTRO_TODOS)
    }

    private fun aplicarFiltro(filtro: Int) {
        filtroAtual = filtro

        val lista = when (filtro) {
            FILTRO_RECEITAS -> banco.listarTodos().filter { it.tipo == 1 }
            FILTRO_DESPESAS -> banco.listarTodos().filter { it.tipo == 2 }
            else -> banco.listarTodos()
        }

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
