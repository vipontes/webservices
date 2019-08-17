package br.net.easify.apiwebservice.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.net.easify.apiwebservice.Model.DataFactory;
import br.net.easify.apiwebservice.Model.Orgao;
import br.net.easify.apiwebservice.R;
import br.net.easify.apiwebservice.interfaces.IOrgaoDelegate;

public class OrgaoAdapter extends RecyclerView.Adapter<OrgaoAdapter.DataHolder> implements IOrgaoDelegate {
    private Context context;
    public List<Orgao> orgaos = new ArrayList<>();

    public OrgaoAdapter(Context context) {
        this.context = context;
        DataFactory.sharedInstance().getOrgaos(this);
    }

    @NonNull
    @Override
    public DataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.orgao_row, parent, false);
        return new OrgaoAdapter.DataHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DataHolder holder, int i) {
        Orgao orgao = this.orgaos.get(i);

        holder.sigla.setText(orgao.getSigla());
        holder.descricao.setText(orgao.getDescricao());
    }

    @Override
    public int getItemCount() {
        return orgaos.size();
    }

    @Override
    public void onOrgao(Boolean success) {
        if ( success ) {
            this.orgaos = DataFactory.sharedInstance().getOrgaos();
            notifyDataSetChanged();
        }
    }

    public class DataHolder extends RecyclerView.ViewHolder {

        TextView sigla;
        TextView descricao;

        public DataHolder(@NonNull View itemView) {
            super(itemView);

            sigla = itemView.findViewById(R.id.sigla);
            descricao = itemView.findViewById(R.id.descricao);
        }
    }

}
