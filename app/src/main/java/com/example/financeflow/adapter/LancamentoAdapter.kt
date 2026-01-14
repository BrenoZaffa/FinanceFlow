package com.example.financeflow.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.financeflow.databinding.ItemLancamentoBinding
import com.example.financeflow.entity.Lancamento
import java.text.SimpleDateFormat
import java.util.*

class LancamentoAdapter(private val lista: List<Lancamento>)
    : RecyclerView.Adapter<LancamentoAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemLancamentoBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLancamentoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val l = lista[position]

        holder.binding.txtDescricao.text = l.descricao
        holder.binding.txtValor.text = if (l.tipo == 1)
            "+ R$ %.2f".format(l.valor) else "- R$ %.2f".format(l.valor)

        holder.binding.txtData.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            .format(Date(l.data))
    }

    override fun getItemCount() = lista.size
}
