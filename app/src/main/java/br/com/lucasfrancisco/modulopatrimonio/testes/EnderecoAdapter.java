package br.com.lucasfrancisco.modulopatrimonio.testes;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.models.Endereco;

public class EnderecoAdapter extends RecyclerView.Adapter<EnderecoHolder> {
    private List<Endereco> enderecos;

    public EnderecoAdapter(List<Endereco> enderecos) {
        this.enderecos = enderecos;
    }

    @Override
    public EnderecoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_endereco, parent, false);
        return new EnderecoHolder(view);
    }

    @Override
    public void onBindViewHolder(EnderecoHolder holder, int position) {
        holder.tvCEP.setText(enderecos.get(position).getCEP());
        holder.tvRua.setText(enderecos.get(position).getRua());
        holder.tvNumero.setText(String.valueOf(enderecos.get(position).getNumero()));
    }

    @Override
    public int getItemCount() {
        return enderecos.size();
    }
}
