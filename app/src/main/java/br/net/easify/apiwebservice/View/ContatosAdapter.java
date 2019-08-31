package br.net.easify.apiwebservice.View;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import br.net.easify.apiwebservice.Model.Contato;
import br.net.easify.apiwebservice.Model.DataFactory;
import br.net.easify.apiwebservice.Model.Empresa;
import br.net.easify.apiwebservice.R;
import br.net.easify.apiwebservice.interfaces.IContatosDelegate;
import br.net.easify.apiwebservice.interfaces.IRemoveContatoDelegate;

public class ContatosAdapter extends RecyclerView.Adapter<ContatosAdapter.DataHolder> implements IContatosDelegate, IRemoveContatoDelegate {

    private IContatosDelegate delegate;
    private Activity parent;
    private Empresa empresa;
    public List<Contato> contatos = new ArrayList<>();
    private ContatosAdapter.OnItemClickListener mListener;

    public ContatosAdapter(IContatosDelegate delegate, Activity parent, Empresa empresa) {
        this.delegate = delegate;
        this.parent = parent;
        this.empresa = empresa;

        DataFactory.sharedInstance().getContatos(this, empresa);
    }

    @NonNull
    @Override
    public DataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contato_row, parent, false);
        return new ContatosAdapter.DataHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DataHolder holder, int i) {
        Contato contato = this.contatos.get(i);
        holder.nome.setText(contato.getNome());
        holder.telefone.setText(contato.getTelefone());
        holder.email.setText(contato.getEmail());
    }

    @Override
    public int getItemCount() {
        return this.contatos.size();
    }

    @Override
    public void onContatos(Boolean success) {
        if (success) {
            this.contatos = DataFactory.sharedInstance().getContatos();
        }

        this.delegate.onContatos(success);
    }

    @Override
    public void onRemoveContato(Boolean success, int position) {
        if (success) {
            notifyItemRemoved(position);
        } else {
            notifyItemChanged(position);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(ContatosAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    
    public class DataHolder extends RecyclerView.ViewHolder {

        public TextView nome;
        public TextView telefone;
        public TextView email;
        public ImageView imagem;

        public DataHolder(@NonNull View itemView, final ContatosAdapter.OnItemClickListener listener) {
            super(itemView);

            nome = itemView.findViewById(R.id.nome);
            telefone = itemView.findViewById(R.id.telefone);
            email = itemView.findViewById(R.id.email);
            imagem = itemView.findViewById(R.id.imagem);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ( listener != null ) {
                        int position = getAdapterPosition();
                        if ( position != RecyclerView.NO_POSITION ) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public void removeItem(final int position) {

        Snackbar.make(parent.findViewById(R.id.firestore_layout), "Por favor confirme a exclusão", 2500)
                .setAction("Excluir", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DataFactory.sharedInstance().removeContato(empresa, position, ContatosAdapter.this);
                    }
                }).addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                notifyItemChanged(position);
            }
        }).show();
    }
}
