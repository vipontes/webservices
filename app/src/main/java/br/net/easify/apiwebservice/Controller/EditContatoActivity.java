package br.net.easify.apiwebservice.Controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import br.net.easify.apiwebservice.Model.Contato;
import br.net.easify.apiwebservice.Model.DataFactory;
import br.net.easify.apiwebservice.Model.Empresa;
import br.net.easify.apiwebservice.R;
import br.net.easify.apiwebservice.interfaces.IEditContatoDelegate;

public class EditContatoActivity extends AppCompatActivity implements IEditContatoDelegate {

    private EditText nome;
    private EditText telefone;
    private EditText email;

    private Empresa empresa;
    private Contato contato;
    private int editPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contato);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Cancelar");
        actionBar.setDisplayHomeAsUpEnabled(true);

        nome = findViewById(R.id.nome);
        telefone = findViewById(R.id.telefone);
        email = findViewById(R.id.email);

        empresa = (Empresa)getIntent().getSerializableExtra("empresa");
        contato = (Contato)getIntent().getSerializableExtra("contato");
        editPosition = getIntent().getIntExtra("position", -1);

        if ( editPosition != -1 ) {
            nome.setText(contato.getNome());
            telefone.setText(contato.getTelefone());
            email.setText(contato.getEmail());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contato, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.save:
                boolean edit = true;
                if ( contato == null ) {
                    edit = false;
                    contato = new Contato();
                }

                String _nome = nome.getText().toString();
                if ( _nome.length() == 0 ) {
                    Toast.makeText(EditContatoActivity.this, "Informe o nome do contato", Toast.LENGTH_LONG).show();
                    return true;
                }

                String _telefone = telefone.getText().toString();
                if ( _telefone.length() == 0 ) {
                    Toast.makeText(EditContatoActivity.this, "Informe o telefone do contato", Toast.LENGTH_LONG).show();
                    return true;
                }

                String _email = email.getText().toString();
                if ( _email.length() == 0 ) {
                    Toast.makeText(EditContatoActivity.this, "Informe o e-mail do contato", Toast.LENGTH_LONG).show();
                    return true;
                }

                contato.setNome(_nome);
                contato.setTelefone(_telefone);
                contato.setEmail(_email);

                if ( edit ) {
                    DataFactory.sharedInstance().updateContato(EditContatoActivity.this, empresa, contato, editPosition);
                } else {
                    DataFactory.sharedInstance().addContato(EditContatoActivity.this, empresa, contato);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onEditContatoDelegate(Boolean success) {
        if ( success ) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(this, "Não foi possível gravar o contato", Toast.LENGTH_LONG).show();
        }
    }
}
