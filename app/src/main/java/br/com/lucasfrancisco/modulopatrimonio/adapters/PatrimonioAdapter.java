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
import br.com.lucasfrancisco.modulopatrimonio.models.Patrimonio;

public class PatrimonioAdapter extends FirestoreRecyclerAdapter<Patrimonio, PatrimonioAdapter.PatrimonioHolder> {
    private RCYDocumentSnapshotClickListener RCYDocumentSnapshotClickListener;

    public PatrimonioAdapter(@NonNull FirestoreRecyclerOptions<Patrimonio> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PatrimonioHolder holder, int position, @NonNull Patrimonio model) {
        String empresa = model.getSetor().getEmpresa().getFantasia() + " " + model.getSetor().getEmpresa().getEndereco().getCidade();
        String setor = model.getSetor().getBloco() + " - " + model.getSetor().getSala();
        holder.tvPlaqueta.setText(model.getPlaqueta());
        holder.tvEmpresa.setText(empresa);
        holder.tvTipo.setText(model.getTipo());
        holder.tvSetor.setText(setor);
    }

    @NonNull
    @Override
    public PatrimonioHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_patrimonio, viewGroup, false);
        return new PatrimonioHolder(view);
    }

    public void excluir(int posicao) {
        getSnapshots().getSnapshot(posicao).getReference().delete();
    }

    public void setRCYDocumentSnapshotClickListener(RCYDocumentSnapshotClickListener RCYDocumentSnapshotClickListener) {
        this.RCYDocumentSnapshotClickListener = RCYDocumentSnapshotClickListener;
    }

    class PatrimonioHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView tvPlaqueta, tvEmpresa, tvTipo, tvSetor;

        public PatrimonioHolder(View itemView) {
            super(itemView);

            tvPlaqueta = (TextView) itemView.findViewById(R.id.tvPlaqueta);
            tvEmpresa = (TextView) itemView.findViewById(R.id.tvEmpresa);
            tvTipo = (TextView) itemView.findViewById(R.id.tvTipo);
            tvSetor = (TextView) itemView.findViewById(R.id.tvSetor);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int posicao = getAdapterPosition();

            if (posicao != RecyclerView.NO_POSITION && RCYDocumentSnapshotClickListener != null) {
                RCYDocumentSnapshotClickListener.onItemClick(getSnapshots().getSnapshot(posicao), posicao);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int posicao = getAdapterPosition();

            if (posicao != RecyclerView.NO_POSITION && RCYDocumentSnapshotClickListener != null) {
                RCYDocumentSnapshotClickListener.onItemLongClick(getSnapshots().getSnapshot(posicao), posicao);
            }
            return true;
        }
    }
}
