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
import br.com.lucasfrancisco.modulopatrimonio.models.Empresa;

public class EmpresaAdapter extends FirestoreRecyclerAdapter<Empresa, EmpresaAdapter.EmpresaHolder> {
    private OnItemClickListener onItemClickListener;

    public EmpresaAdapter(@NonNull FirestoreRecyclerOptions<Empresa> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull EmpresaHolder holder, int position, @NonNull Empresa model) {
        holder.tvFantasia.setText(model.getFantasia());
        holder.tvCodigo.setText(model.getCodigo());
        holder.tvCNPJ.setText(model.getCNPJ());
    }

    @NonNull
    @Override
    public EmpresaHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_empresa, viewGroup, false);
        return new EmpresaHolder(view);
    }

    public void excluir(int posicao) {
        getSnapshots().getSnapshot(posicao).getReference().delete();
    }

    public class EmpresaHolder extends RecyclerView.ViewHolder {
        TextView tvFantasia, tvCodigo, tvCNPJ;

        public EmpresaHolder(@NonNull View itemView) {
            super(itemView);

            tvFantasia = (TextView) itemView.findViewById(R.id.tvFantasia);
            tvCodigo = (TextView) itemView.findViewById(R.id.tvCodigo);
            tvCNPJ = (TextView) itemView.findViewById(R.id.tvCNPJ);

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
