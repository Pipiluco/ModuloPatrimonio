package br.com.lucasfrancisco.modulopatrimonio.holderes;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import br.com.lucasfrancisco.modulopatrimonio.R;

public class EnderecoHolder extends RecyclerView.ViewHolder {
    public TextView tvCEP, tvRua, tvNumero;

    public EnderecoHolder(View itemView) {
        super(itemView);

        tvCEP = (TextView) itemView.findViewById(R.id.tvCEP);
        tvRua = (TextView) itemView.findViewById(R.id.tvRua);
        tvNumero = (TextView) itemView.findViewById(R.id.tvNumero);
    }
}
