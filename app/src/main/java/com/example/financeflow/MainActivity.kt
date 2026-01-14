package com.example.financeflow

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financeflow.adapter.LancamentoAdapter
import com.example.financeflow.database.DatabaseHandler
import com.example.financeflow.databinding.ActivityMainBinding
import com.example.financeflow.entity.Lancamento
import com.example.financeflow.utils.MoneyMask
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    companion object {
        const val FILTRO_TODOS = 0
        const val FILTRO_RECEITAS = 1
        const val FILTRO_DESPESAS = 2
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var banco: DatabaseHandler

    private lateinit var topMessage: View
    private lateinit var txtTopMessage: TextView

    private val cal: Calendar = Calendar.getInstance()
    private var dataSelecionada = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.header.txtTitulo.text = "Novo lançamento"
        binding.header.btnVoltar.visibility = View.GONE

        topMessage = findViewById(R.id.topMessage)
        txtTopMessage = findViewById(R.id.txtTopMessage)

        banco = DatabaseHandler.getInstance(this)

        binding.formLancamento.etValor.addTextChangedListener(
            MoneyMask(binding.formLancamento.etValor)
        )

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        configurarDatePicker()
        carregarUltimos()

        binding.btnVerTodos.setOnClickListener {
            startActivity(Intent(this, LancamentosActivity::class.java))
        }

        binding.fabListagem.setOnClickListener {
            startActivity(Intent(this, LancamentosActivity::class.java))
        }

        binding.btLancamento.setOnClickListener { salvar() }
    }

    private fun configurarDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            cal.set(year, month, day)
            dataSelecionada = cal.timeInMillis
            atualizarDataNoCampo()
        }

        atualizarDataNoCampo()

        binding.formLancamento.etData.setOnClickListener {
            DatePickerDialog(
                this,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun atualizarDataNoCampo() {
        binding.formLancamento.etData.setText(
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(dataSelecionada)
        )
    }

    private fun salvar() {
        val descricao = binding.formLancamento.etDescricao.text?.toString()?.trim() ?: ""
        val valorTexto = binding.formLancamento.etValor.text?.toString()
            ?.replace("[R$\\s.]".toRegex(), "")
            ?.replace(",", ".")
            ?.trim() ?: ""

        when {
            descricao.isEmpty() -> {
                binding.formLancamento.etDescricao.error = "Informe a descrição"
                binding.formLancamento.etDescricao.requestFocus()
                mostrarErro("Preencha a descrição")
                return
            }

            valorTexto.isEmpty() -> {
                binding.formLancamento.etValor.error = "Informe o valor"
                binding.formLancamento.etValor.requestFocus()
                mostrarErro("Preencha o valor")
                return
            }
        }

        val valor = valorTexto.toDoubleOrNull()
        if (valor == null || valor <= 0) {
            binding.formLancamento.etValor.error = "Valor inválido"
            binding.formLancamento.etValor.requestFocus()
            mostrarErro("Informe um valor válido")
            return
        }

        val lancamento = Lancamento(
            _id = 0,
            descricao = descricao,
            tipo = if (binding.formLancamento.rbReceita.isChecked) FILTRO_RECEITAS else FILTRO_DESPESAS,
            valor = valor,
            data = dataSelecionada
        )

        try {
            banco.inserir(lancamento)
            limparCampos()
            carregarUltimos()
            mostrarSucesso("Lançamento salvo com sucesso")
        } catch (e: Exception) {
            mostrarErro("Erro ao salvar lançamento")
            e.printStackTrace()
        }
    }

    private fun limparCampos() {
        binding.formLancamento.etDescricao.text?.clear()
        binding.formLancamento.etValor.text?.clear()
        binding.formLancamento.rbReceita.isChecked = true
        dataSelecionada = System.currentTimeMillis()
        atualizarDataNoCampo()
        binding.formLancamento.etDescricao.requestFocus()
    }

    private fun carregarUltimos() {
        val ultimos = banco.listarTodos()
            .sortedByDescending { it.data }
            .take(5)

        binding.recyclerUltimos.layoutManager = LinearLayoutManager(this)
        binding.recyclerUltimos.adapter = LancamentoAdapter(ultimos)
    }

    private fun mostrarMensagemTopo(mensagem: String, cor: Int) {
        txtTopMessage.text = mensagem
        topMessage.setBackgroundColor(getColor(cor))

        topMessage.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(250)
            .start()

        topMessage.postDelayed({
            topMessage.animate()
                .translationY(-topMessage.height.toFloat())
                .alpha(0f)
                .setDuration(250)
                .start()
        }, 2500)
    }

    private fun mostrarErro(mensagem: String) = mostrarMensagemTopo(mensagem, R.color.red)
    private fun mostrarSucesso(mensagem: String) = mostrarMensagemTopo(mensagem, R.color.green)
}
