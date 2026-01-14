package com.example.financeflow

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
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

class MainActivity : AppCompatActivity() {

    companion object {
        const val FILTRO_TODOS = 0
        const val FILTRO_RECEITAS = 1
        const val FILTRO_DESPESAS = 2
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var banco: DatabaseHandler
    private val cal: Calendar = Calendar.getInstance()
    private var dataSelecionada = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 👉 AQUI entra a configuração do header
        binding.header.txtTitulo.text = "Novo lançamento"
        binding.header.btnVoltar.visibility = View.GONE

        banco = DatabaseHandler.getInstance(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        configCampoData()

        binding.btVerLancamentos.setOnClickListener {
            val intent = Intent(this, LancamentosActivity::class.java)
            intent.putExtra("filtro", FILTRO_TODOS)
            startActivity(intent)
        }

        binding.fabListagem.setOnClickListener {
            val intent = Intent(this, LancamentosActivity::class.java)
            intent.putExtra("filtro", FILTRO_TODOS)
            startActivity(intent)
        }

        binding.btLancamento.setOnClickListener { salvar() }
    }
    private fun salvar() {
        if (binding.etDescricao.text.toString().isEmpty() ||
            binding.etValor.text.toString().isEmpty()
        ) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            return
        }

        val lancamento = Lancamento(
            _id = 0,
            descricao = binding.etDescricao.text.toString(),
            tipo = if (binding.rbReceita.isChecked) 1 else 2,
            valor = binding.etValor.text.toString().toDouble(),
            data = dataSelecionada
        )

        banco.inserir(lancamento)

        Toast.makeText(this, "Lançamento realizado com sucesso!", Toast.LENGTH_SHORT).show()
        limparCampos()
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

        binding.etData.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                DatePickerDialog(
                    this,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
                binding.etData.clearFocus()
            }
        }
    }

    private fun setDataSelecionada() {
        binding.etData.setText(
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(dataSelecionada)
        )
    }
}
