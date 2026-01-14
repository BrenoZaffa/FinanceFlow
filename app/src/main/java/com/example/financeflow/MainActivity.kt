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
import com.example.financeflow.database.DatabaseHandler
import com.example.financeflow.databinding.ActivityMainBinding
import com.example.financeflow.entity.Lancamento
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.example.financeflow.utils.MoneyMask
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

        // Header
        binding.header.txtTitulo.text = "Novo lançamento"
        binding.header.btnVoltar.visibility = View.GONE

        // Mensagem topo
        topMessage = findViewById(R.id.topMessage)
        txtTopMessage = findViewById(R.id.txtTopMessage)

        banco = DatabaseHandler.getInstance(this)
        binding.etValor.addTextChangedListener(MoneyMask(binding.etValor))
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        configCampoData()

        binding.btVerLancamentos.setOnClickListener {
            startActivity(Intent(this, LancamentosActivity::class.java))
        }

        binding.fabListagem.setOnClickListener {
            startActivity(Intent(this, LancamentosActivity::class.java))
        }

        binding.btLancamento.setOnClickListener { salvar() }
    }

    private fun salvar() {
        val descricao = binding.etDescricao.text?.toString()?.trim() ?: ""
        val valorTexto = binding.etValor.text?.toString()
            ?.replace("[R$\\s.]".toRegex(), "")
            ?.replace(",", ".")
            ?.trim() ?: ""

        when {
            descricao.isEmpty() -> {
                binding.etDescricao.error = "Informe a descrição"
                binding.etDescricao.requestFocus()
                mostrarErro("Preencha a descrição")
                return
            }

            valorTexto.isEmpty() -> {
                binding.etValor.error = "Informe o valor"
                binding.etValor.requestFocus()
                mostrarErro("Preencha o valor")
                return
            }
        }

        val valor = valorTexto.toDoubleOrNull()
        if (valor == null || valor <= 0) {
            binding.etValor.error = "Valor inválido"
            binding.etValor.requestFocus()
            mostrarErro("Informe um valor válido")
            return
        }

        val lancamento = Lancamento(
            _id = 0,
            descricao = descricao,
            tipo = if (binding.rbReceita.isChecked) FILTRO_RECEITAS else FILTRO_DESPESAS,
            valor = valor,
            data = dataSelecionada
        )

        try {
            banco.inserir(lancamento)
            limparCampos()
            mostrarSucesso("Lançamento salvo com sucesso")

        } catch (e: Exception) {
            mostrarErro("Erro ao salvar lançamento")
            e.printStackTrace()
        }
    }

    private fun limparCampos() {
        binding.etDescricao.text?.clear()
        binding.etValor.text?.clear()
        binding.rbReceita.isChecked = true
        dataSelecionada = System.currentTimeMillis()
        setDataSelecionada()
        binding.etDescricao.requestFocus()
    }

    private fun configCampoData() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            cal.set(year, month, day)
            dataSelecionada = cal.timeInMillis
            setDataSelecionada()
        }

        setDataSelecionada()

        binding.etData.setOnClickListener {
            DatePickerDialog(
                this,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setDataSelecionada() {
        binding.etData.setText(
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(dataSelecionada)
        )
    }

    /* =======================
     * Mensagens topo
     * ======================= */

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

    private fun mostrarErro(mensagem: String) {
        mostrarMensagemTopo(mensagem, R.color.red)
    }

    private fun mostrarSucesso(mensagem: String) {
        mostrarMensagemTopo(mensagem, R.color.green)

        binding.btLancamento.animate()
            .scaleX(1.05f)
            .scaleY(1.05f)
            .setDuration(120)
            .withEndAction {
                binding.btLancamento.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(120)
                    .start()
            }
            .start()
    }
}
