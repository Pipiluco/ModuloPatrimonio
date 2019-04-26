package br.com.lucasfrancisco.modulopatrimonio.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.interfaces.RCYViewClickListener;
import br.com.lucasfrancisco.modulopatrimonio.models.Imagem;

public class EditImagemAdapter extends RecyclerView.Adapter<EditImagemAdapter.ViewHolder> {
    private RCYViewClickListener rcyViewClickListener;

    private List<Imagem> imagems;
    private List<Imagem> imagensRemovidas;
    private Context context;

    public EditImagemAdapter(List<Imagem> imagems, List<Imagem> imagensRemovidas, Context context) {
        this.imagems = imagems;
        this.imagensRemovidas = imagensRemovidas;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_edit_imagem, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Imagem imagem = imagems.get(position);
        viewHolder.tvNomeImagem.setText(imagem.getNome());

        Picasso.with(context).load(imagem.getUrlLocal()).placeholder(R.drawable.ic_image).fit().centerCrop().into(viewHolder.imvImagem);
    }

    @Override
    public int getItemCount() {
        return imagems.size();
    }

    public void excluir(int posicao) {
        imagensRemovidas.add(imagems.get(posicao));
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
