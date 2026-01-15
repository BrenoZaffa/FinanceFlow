package com.example.financeflow

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.financeflow.databinding.ActivitySobreBinding
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.animation.AnimationUtils
import android.content.Intent
import android.net.Uri
class SobreActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySobreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySobreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val anim = AnimationUtils.loadAnimation(this, R.anim.slide_fade_in)
        binding.cardDevs.startAnimation(anim)
        binding.cardProjeto.startAnimation(anim)
        binding.whatsBreno.setOnClickListener {
            abrirWhatsApp("+554298262655")
        }

        binding.whatsCleverson.setOnClickListener {
            abrirWhatsApp("+5543996631215")
        }

        binding.gitBreno.setOnClickListener {
            abrirLink("https://github.com/BrenoZaffa")
        }

        binding.gitCleverson.setOnClickListener {
            abrirLink("https://github.com/cleverson-tiago-ramos")
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        binding.header.txtTitulo.text = "Sobre o projeto"
        binding.header.btnVoltar.visibility = View.VISIBLE
        binding.header.btnVoltar.setOnClickListener { finish() }
    }
    private fun abrirWhatsApp(numero: String) {
        val uri = Uri.parse("https://wa.me/${numero.replace("+", "")}")
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    private fun abrirLink(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

}
