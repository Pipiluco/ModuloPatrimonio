package br.com.lucasfrancisco.modulopatrimonio.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.interfaces.RCYViewClickListener;
import br.com.lucasfrancisco.modulopatrimonio.models.Imagem;

public class EditImagemAdapter extends RecyclerView.Adapter<EditImagemAdapter.ViewHolder> {
    private RCYViewClickListener rcyViewClickListener;

    private List<Imagem> imagens;
    private Context context;

    public EditImagemAdapter(List<Imagem> imagens, Context context) {
        this.imagens = imagens;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_edit_imagem, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Imagem imagem = imagens.get(position);
        viewHolder.tvNomeImagem.setText(imagem.getNome());

        Picasso.with(context).load(imagem.getUrlRemota()).placeholder(R.drawable.ic_image).fit().centerCrop().into(viewHolder.imvImagem);
    }

    @Override
    public int getItemCount() {
        return imagens.size();
    }

    public void excluir(int posicao) {
        // Remove a imagem na lista local
        imagens.remove(posicao);
        notifyItemRemoved(posicao);
        notifyItemRangeChanged(posicao, imagens.size());
    }

    public void setRcyViewClickListener(RCYViewClickListener rcyViewClickListener) {
        this.rcyViewClickListener = rcyViewClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView tvNomeImagem;
        ImageView imvImagem;

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
