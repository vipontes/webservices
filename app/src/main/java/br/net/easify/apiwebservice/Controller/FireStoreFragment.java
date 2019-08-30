package br.net.easify.apiwebservice.Controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import br.net.easify.apiwebservice.Model.DataFactory;
import br.net.easify.apiwebservice.Model.Empresa;
import br.net.easify.apiwebservice.R;
import br.net.easify.apiwebservice.Utils.DynamicEventsHelper;
import br.net.easify.apiwebservice.View.EmpresasAdapter;
import br.net.easify.apiwebservice.interfaces.IEmpresasDelegate;

public class FireStoreFragment extends Fragment implements IEmpresasDelegate {

    final private int EDIT = 100;

    private RecyclerView recyclerView;
    private EmpresasAdapter adapter;

    private FloatingActionButton adicionaEmpresa;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_fire_store, container,false);

        recyclerView = v.findViewById(R.id.empresas);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
        adapter = new EmpresasAdapter(this, v);
        recyclerView.setAdapter(adapter);

        DynamicEventsHelper.DynamicEventsCallback callback = new DynamicEventsHelper.DynamicEventsCallback() {
            @Override
            public void onItemMove(int initialPosition, int finalPosition) {

            }

            @Override
            public void onRemove(int position) {
                adapter.removeItem(position);
            }
        };
        ItemTouchHelper androidItemTouchHelper = new ItemTouchHelper(new DynamicEventsHelper(callback));
        androidItemTouchHelper.attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new EmpresasAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Empresa empresa = DataFactory.sharedInstance().getEmpresa(position);
                Intent intent = new Intent(getContext(), EditEmpresaActivity.class);
                intent.putExtra("empresa", empresa);
                intent.putExtra("position", position);
                startActivityForResult(intent, EDIT);
            }
        });

        adicionaEmpresa = v.findViewById(R.id.adicionaEmpresa);
        adicionaEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditEmpresaActivity.class);
                startActivityForResult(intent, EDIT);
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode == Activity.RESULT_OK ) {
            if ( requestCode == EDIT) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onEmpresas(Boolean success) {
        adapter.notifyDataSetChanged();
    }
}
