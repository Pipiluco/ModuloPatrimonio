package br.com.lucasfrancisco.modulopatrimonio.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.interfaces.RCYViewClickListener;
import br.com.lucasfrancisco.modulopatrimonio.models.Imagem;

public class NovaImagemAdapter extends RecyclerView.Adapter<NovaImagemAdapter.ViewHolder> {
    private RCYViewClickListener rcyViewClickListener;

    private ArrayList<Imagem> imagems;
    private Context context;

    public NovaImagemAdapter(ArrayList<Imagem> imagems, Context context) {
        this.imagems = imagems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_imagem, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Imagem imagem = imagems.get(position);
        viewHolder.tvNomeImagem.setText(imagem.getNome());
        Picasso.with(context).load(imagem.getUrlLocal()).into(viewHolder.imvImagem);

        if (!imagem.isEnviada()) {
            viewHolder.imvProgresso.setImageResource(R.drawable.ic_loading_01);
        } else {
            viewHolder.imvProgresso.setImageResource(R.drawable.ic_checked_01);
        }
    }

    @Override
    public int getItemCount() {
        return imagems.size();
    }

    public void excluir(int posicao) {
        imagems.remove(posicao);
        notifyItemRemoved(posicao);
        notifyItemRangeChanged(posicao, imagems.size());
    }

    public void setRcyViewClickListener(RCYViewClickListener rcyViewClickListener) {
        this.rcyViewClickListener = rcyViewClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView tvNomeImagem;
        ImageView imvProgresso, imvImagem;

        public ViewHolder(View itemView) {
            super(itemView);

            tvNomeImagem = (TextView) itemView.findViewById(R.id.tvNomeImagem);
            imvProgresso = (ImageView) itemView.findViewById(R.id.imvProgresso);
            imvImagem = (ImageView) itemView.findViewById(R.id.imvImagem);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int posicao = getAdapterPosition();
            if (posicao != RecyclerView.NO_POSITION && rcyViewClickListener != null) {
                rcyViewClickListener.onItemClick(v, posicao);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int posicao = getAdapterPosition();
            if (posicao != RecyclerView.NO_POSITION && rcyViewClickListener != null) {
                rcyViewClickListener.onItemLongClick(v, posicao);
            }
            return true;
        }
    }
}


/*
public class NovaImagemAdapter extends RecyclerView.Adapter<NovaImagemAdapter.ViewHolder> {
    private List<Imagem> imagems;
    private List<Boolean> listImagensEnviadas;
    private List<Uri> listUriImagens;
    private Context context;

    public NovaImagemAdapter(List<Imagem> imagems, List<Boolean> listImagensEnviadas, List<Uri> listUriImagens, Context context) {
        this.imagems = imagems;
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
        Imagem imagemEscolhida = imagems.get(position);
        Boolean imagemEnviada = listImagensEnviadas.get(position);
        Uri uriImagem = listUriImagens.get(position);

        viewHolder.tvNomeImagem.setText(imagemEscolhida.getNome());

        Picasso.with(context).load(uriImagem).into(viewHolder.imvImagem);

        if (!imagemEnviada) {
            viewHolder.imvProgresso.setImageResource(R.drawable.ic_loading_01);
        } else {
            viewHolder.imvProgresso.setImageResource(R.drawable.ic_checked_01);
        }
    }

    @Override
    public int getItemCount() {
        return imagems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNomeImagem;
        ImageView imvProgresso, imvImagem;

        public ViewHolder(View itemView) {
            super(itemView);

            tvNomeImagem = (TextView) itemView.findViewById(R.id.tvNomeImagem);
            imvProgresso = (ImageView) itemView.findViewById(R.id.imvProgresso);
            imvImagem = (ImageView) itemView.findViewById(R.id.imvImagem);
        }
    }
 */