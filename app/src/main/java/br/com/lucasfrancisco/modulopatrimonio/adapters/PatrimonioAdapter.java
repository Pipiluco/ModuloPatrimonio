package br.com.lucasfrancisco.modulopatrimonio.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.models.Patrimonio;

public class PatrimonioAdapter extends FirestoreRecyclerAdapter<Patrimonio, PatrimonioAdapter.PatrimonioHolder> {
    private OnItemClickListener onItemClickListener;

    public PatrimonioAdapter(@NonNull FirestoreRecyclerOptions<Patrimonio> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PatrimonioHolder holder, int position, @NonNull Patrimonio model) {
        holder.tvPlaqueta.setText(model.getPlaqueta());
        holder.tvEmpresa.setText(model.getEmpresa());
        holder.tvTipo.setText(model.getTipo());
        holder.tvSetor.setText(model.getSetor());
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

    class PatrimonioHolder extends RecyclerView.ViewHolder {
        TextView tvPlaqueta, tvEmpresa, tvTipo, tvSetor;

        public PatrimonioHolder(View itemView) {
            super(itemView);

            tvPlaqueta = (TextView) itemView.findViewById(R.id.tvPlaqueta);
            tvEmpresa = (TextView) itemView.findViewById(R.id.tvEmpresa);
            tvTipo = (TextView) itemView.findViewById(R.id.tvTipo);
            tvSetor = (TextView) itemView.findViewById(R.id.tvSetor);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int posicao = getAdapterPosition();

                    if (posicao != RecyclerView.NO_POSITION && onItemClickListener != null) {
                        onItemClickListener.onItemClick(getSnapshots().getSnapshot(posicao), posicao);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int posicao);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}
