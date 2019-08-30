package br.net.easify.apiwebservice.View;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.net.easify.apiwebservice.Model.DataFactory;
import br.net.easify.apiwebservice.Model.Empresa;
import br.net.easify.apiwebservice.R;
import br.net.easify.apiwebservice.interfaces.IEmpresasDelegate;
import br.net.easify.apiwebservice.interfaces.IRemoveEmpresaDelegate;

public class EmpresasAdapter extends RecyclerView.Adapter<EmpresasAdapter.DataHolder> implements IEmpresasDelegate, IRemoveEmpresaDelegate {

    private IEmpresasDelegate delegate;
    private View fragment;
    public List<Empresa> empresas = new ArrayList<>();
    private EmpresasAdapter.OnItemClickListener mListener;

    private FirebaseStorage storage;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(EmpresasAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public EmpresasAdapter(IEmpresasDelegate delegate, View fragment) {
        this.delegate = delegate;
        this.fragment = fragment;
        this.storage = FirebaseStorage.getInstance();
        DataFactory.sharedInstance().getEmpresas(this);
    }

    @NonNull
    @Override
    public DataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.empresa_row, parent, false);
        return new EmpresasAdapter.DataHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final DataHolder holder, int i) {
        Empresa empresa = this.empresas.get(i);
        holder.nome.setText(empresa.getNome());

        StorageReference pathReference = storage.getReference().child("empresas");
        pathReference.child(empresa.getLogo())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).centerCrop().fit().into(holder.imagem);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    @Override
    public int getItemCount() {
        return empresas.size();
    }

    @Override
    public void onEmpresas(Boolean success) {
        if (success) {
            this.empresas = DataFactory.sharedInstance().getEmpresas();
        }

        this.delegate.onEmpresas(success);
    }

    public void removeItem(final int position) {

        Snackbar.make(fragment.findViewById(R.id.firestore_layout), "Por favor confirme a exclusão", 2500)
                .setAction("Excluir", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DataFactory.sharedInstance().removeEmpresa(position, EmpresasAdapter.this);
                    }
                }).addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                notifyItemChanged(position);
            }
        }).show();
    }

    @Override
    public void onRemoveEmpresa(Boolean success, int position) {
        if (success) {
            notifyItemRemoved(position);
        } else {
            notifyItemChanged(position);
        }
    }

    public class DataHolder extends RecyclerView.ViewHolder {

        public TextView nome;
        public ImageView imagem;

        public DataHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            nome = itemView.findViewById(R.id.nome);
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
}
