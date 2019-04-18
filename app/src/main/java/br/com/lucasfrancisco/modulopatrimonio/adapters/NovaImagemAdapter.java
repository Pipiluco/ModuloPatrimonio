package br.com.lucasfrancisco.modulopatrimonio.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.lucasfrancisco.modulopatrimonio.R;

public class NovaImagemAdapter extends RecyclerView.Adapter<NovaImagemAdapter.ViewHolder> {
    private List<String> listImagensEscolhidas;
    private List<Boolean> listImagensEnviadas;
    private List<Uri> listUriImagens;
    private Context context;

    public NovaImagemAdapter(List<String> listImagensEscolhidas, List<Boolean> listImagensEnviadas, List<Uri> listUriImagens, Context context) {
        this.listImagensEscolhidas = listImagensEscolhidas;
        this.listImagensEnviadas = listImagensEnviadas;
        this.listUriImagens = listUriImagens;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_imagem, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        String imagemEscolhida = listImagensEscolhidas.get(position);
        Boolean imagemEnviada = listImagensEnviadas.get(position);
        Uri uriImagem = listUriImagens.get(position);

        viewHolder.tvNomeImagem.setText(imagemEscolhida);

        Picasso.with(context).load(uriImagem).into(viewHolder.imvImagem);

        if (!imagemEnviada) {
            viewHolder.imvProgresso.setImageResource(R.drawable.ic_loading_01);
        } else {
            viewHolder.imvProgresso.setImageResource(R.drawable.ic_checked_01);
        }
    }

    @Override
    public int getItemCount() {
        return listImagensEscolhidas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNomeImagem;
        ImageView imvProgresso;
        ImageView imvImagem;

        public ViewHolder(View itemView) {
            super(itemView);

            tvNomeImagem = (TextView) itemView.findViewById(R.id.tvNomeImagem);
            imvProgresso = (ImageView) itemView.findViewById(R.id.imvProgresso);
            imvImagem = (ImageView) itemView.findViewById(R.id.imvImagem);
        }
    }
}
