package br.net.easify.apiwebservice.Controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import br.net.easify.apiwebservice.Model.DataFactory;
import br.net.easify.apiwebservice.R;
import br.net.easify.apiwebservice.interfaces.INewAccountDelegate;

public class NewAccountActivity extends AppCompatActivity implements INewAccountDelegate {

    private TextView txtLogin;
    private EditText txtNome;
    private EditText txtEmail;
    private EditText txtSenha;
    private Button btnNewAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        txtNome = findViewById(R.id.txtNome);
        txtEmail = findViewById(R.id.txtEmail);
        txtSenha = findViewById(R.id.txtSenha);

        txtLogin = findViewById(R.id.txtLogin);
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnNewAccount = findViewById(R.id.btnNewAccount);
        btnNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = txtNome.getText().toString();
                String email = txtEmail.getText().toString();
                String senha = txtSenha.getText().toString();

                if ( nome.length() == 0 ) {
                    Toast.makeText(NewAccountActivity.this, "Informe o nome do usuário", Toast.LENGTH_LONG).show();
                    return;
                }

                if ( email.length() == 0 ) {
                    Toast.makeText(NewAccountActivity.this, "Informe o endereço de e-mail", Toast.LENGTH_LONG).show();
                    return;
                }

                if ( senha.length() == 0 ) {
                    Toast.makeText(NewAccountActivity.this, "Informe a senha", Toast.LENGTH_LONG).show();
                    return;
                }

                DataFactory.sharedInstance().createAccount(NewAccountActivity.this, nome, email, senha);
            }
        });
    }

    @Override
    public void onNewAccount(Boolean success, String msg) {
        if ( success ) {
            finish();
        } else {
            Toast.makeText(NewAccountActivity.this, msg, Toast.LENGTH_LONG).show();
        }
    }
}
