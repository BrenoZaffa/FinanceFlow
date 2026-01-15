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

        private const val PREFS = "financeflow_prefs"
        private const val KEY_FILTRO = "filtro_lancamentos"
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

        // Botões de filtro
        binding.btnFiltroTodos.setOnClickListener { aplicarFiltro(FILTRO_TODOS) }
        binding.btnFiltroReceitas.setOnClickListener { aplicarFiltro(FILTRO_RECEITAS) }
        binding.btnFiltroDespesas.setOnClickListener { aplicarFiltro(FILTRO_DESPESAS) }

        // Recupera o último filtro usado
        aplicarFiltro(recuperarFiltro())
    }

    private fun aplicarFiltro(filtro: Int) {
        filtroAtual = filtro

        animarTrocaLista()

        val lista = when (filtro) {
            FILTRO_RECEITAS -> banco.listarTodos().filter { it.tipo == 1 }
            FILTRO_DESPESAS -> banco.listarTodos().filter { it.tipo == 2 }
            else -> banco.listarTodos()
        }

        binding.recyclerLancamentos.layoutManager = LinearLayoutManager(this)
        binding.recyclerLancamentos.adapter = LancamentoAdapter(lista)

        atualizarSaldo(lista)
        atualizarEstiloFiltro()
        salvarFiltro(filtro)
    }

    private fun atualizarSaldo(lista: List<com.example.financeflow.entity.Lancamento>) {
        var saldo = 0.0
        lista.forEach {
            if (it.tipo == 1) saldo += it.valor else saldo -= it.valor
        }

        binding.txtSaldo.text = "Saldo: R$ %.2f".format(saldo)
    }

    private fun atualizarEstiloFiltro() {
        binding.btnFiltroTodos.alpha = if (filtroAtual == FILTRO_TODOS) 1f else 0.4f
        binding.btnFiltroReceitas.alpha = if (filtroAtual == FILTRO_RECEITAS) 1f else 0.4f
        binding.btnFiltroDespesas.alpha = if (filtroAtual == FILTRO_DESPESAS) 1f else 0.4f
    }

    private fun animarTrocaLista() {
        binding.recyclerLancamentos.animate()
            .alpha(0f)
            .setDuration(100)
            .withEndAction {
                binding.recyclerLancamentos.animate()
                    .alpha(1f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }

    private fun salvarFiltro(filtro: Int) {
        getSharedPreferences(PREFS, MODE_PRIVATE)
            .edit()
            .putInt(KEY_FILTRO, filtro)
            .apply()
    }

    private fun recuperarFiltro(): Int {
        return getSharedPreferences(PREFS, MODE_PRIVATE)
            .getInt(KEY_FILTRO, FILTRO_TODOS)
    }

}