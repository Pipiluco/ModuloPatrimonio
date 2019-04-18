package br.com.lucasfrancisco.modulopatrimonio.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.interfaces.RCYDocumentSnapshotClickListener;
import br.com.lucasfrancisco.modulopatrimonio.models.Endereco;

public class EnderecoAdapter extends FirestoreRecyclerAdapter<Endereco, EnderecoAdapter.EnderecoHolder> {
    private RCYDocumentSnapshotClickListener rcyDocumentSnapshotClickListener;

    public EnderecoAdapter(@NonNull FirestoreRecyclerOptions<Endereco> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull EnderecoHolder holder, int position, @NonNull Endereco model) {
        holder.tvCEP.setText(model.getCEP());
        holder.tvRua.setText(model.getRua());
        holder.tvNumero.setText(String.valueOf(model.getNumero()));
    }

    @Override
    public EnderecoHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_endereco, viewGroup, false);
        return new EnderecoHolder(view);
    }

    public void excluir(int posicao) {
        getSnapshots().getSnapshot(posicao).getReference().delete();
    }

    public void setRCYDocumentSnapshotClickListener(RCYDocumentSnapshotClickListener RCYDocumentSnapshotClickListener) {
        this.rcyDocumentSnapshotClickListener = RCYDocumentSnapshotClickListener;
    }

    // ViewHolder
    public class EnderecoHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView tvCEP, tvRua, tvNumero;

        public EnderecoHolder(View itemView) {
            super(itemView);

            tvCEP = (TextView) itemView.findViewById(R.id.tvCEP);
            tvRua = (TextView) itemView.findViewById(R.id.tvRua);
            tvNumero = (TextView) itemView.findViewById(R.id.tvNumero);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int posicao = getAdapterPosition();

            if (posicao != RecyclerView.NO_POSITION && rcyDocumentSnapshotClickListener != null) {
                rcyDocumentSnapshotClickListener.onItemClick(getSnapshots().getSnapshot(posicao), posicao);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int posicao = getAdapterPosition();

            if (posicao != RecyclerView.NO_POSITION && rcyDocumentSnapshotClickListener != null) {
                rcyDocumentSnapshotClickListener.onItemLongClick(getSnapshots().getSnapshot(posicao), posicao);
            }
            return true;
        }
    }
}
