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
import br.com.lucasfrancisco.modulopatrimonio.models.Objeto;

public class ObjetoAdapter extends FirestoreRecyclerAdapter<Objeto, ObjetoAdapter.ObjetoHolder> {
    private OnItemClickListener onItemClickListener;

    public ObjetoAdapter(@NonNull FirestoreRecyclerOptions<Objeto> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ObjetoHolder holder, int position, @NonNull Objeto model) {
        holder.tvTipo.setText(model.getTipo());
        holder.tvMarca.setText(model.getMarca());
        holder.tvModelo.setText(model.getModelo());
    }

    @NonNull
    @Override
    public ObjetoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_objeto, viewGroup, false);
        return new ObjetoHolder(view);
    }

    public void excluir(int posicao) {
        getSnapshots().getSnapshot(posicao).getReference().delete();
    }

    class ObjetoHolder extends RecyclerView.ViewHolder {
        TextView tvTipo, tvCor, tvMarca, tvModelo;

        public ObjetoHolder(@NonNull View itemView) {
            super(itemView);

            tvTipo = (TextView) itemView.findViewById(R.id.tvTipo);
            tvCor = (TextView) itemView.findViewById(R.id.tvCor);
            tvMarca = (TextView) itemView.findViewById(R.id.tvMarca);
            tvModelo = (TextView) itemView.findViewById(R.id.tvModelo);

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
