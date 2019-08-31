package br.net.easify.apiwebservice.Controller;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import br.net.easify.apiwebservice.Model.Contato;
import br.net.easify.apiwebservice.Model.DataFactory;
import br.net.easify.apiwebservice.Model.Empresa;
import br.net.easify.apiwebservice.R;
import br.net.easify.apiwebservice.Utils.DynamicEventsHelper;
import br.net.easify.apiwebservice.View.ContatosAdapter;
import br.net.easify.apiwebservice.interfaces.IContatosDelegate;

public class ContatosActivity extends AppCompatActivity implements IContatosDelegate {

    final private int EDIT = 100;

    private Empresa empresa;
    private RecyclerView recyclerView;
    private ContatosAdapter adapter;

    private FloatingActionButton adicionaContato;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contatos);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Contatos");
        actionBar.setDisplayHomeAsUpEnabled(true);

        empresa = (Empresa)getIntent().getSerializableExtra("empresa");

        recyclerView = findViewById(R.id.contatos);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
        adapter = new ContatosAdapter(this, this, empresa);
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

        adapter.setOnItemClickListener(new ContatosAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Contato contato = DataFactory.sharedInstance().getContato(position);
                Intent intent = new Intent(ContatosActivity.this, EditContatoActivity.class);
                intent.putExtra("empresa", empresa);
                intent.putExtra("contato", contato);
                intent.putExtra("position", position);
                startActivityForResult(intent, EDIT);
            }
        });

        adicionaContato = findViewById(R.id.adicionaContato);
        adicionaContato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContatosActivity.this, EditContatoActivity.class);
                intent.putExtra("empresa", empresa);
                startActivityForResult(intent, EDIT);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onContatos(Boolean success) {
        if ( success ) {
            adapter.notifyDataSetChanged();
        }
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
}
