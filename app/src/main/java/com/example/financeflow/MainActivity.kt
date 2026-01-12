package com.example.financeflow

import android.app.DatePickerDialog
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
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
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var banco: DatabaseHandler
    val cal: Calendar = Calendar.getInstance()
    var dataSelecionada = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        banco = DatabaseHandler.getInstance(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        configCampoData()

        binding.fabListagem.setOnClickListener {
            val intent = Intent(this, LancamentosActivity::class.java)
            startActivity(intent)
        }

        binding.btLancamento.setOnClickListener { salvar() }
    }

    fun salvar(){
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

    fun configCampoData(){
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            dataSelecionada = cal.timeInMillis
            setDataSelecionada()
        }

        setDataSelecionada()

        binding.etData.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                DatePickerDialog(this@MainActivity, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
                binding.etData.clearFocus()
            }
        }
    }

    fun setDataSelecionada(){
        binding.etData.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(dataSelecionada))
    }
}