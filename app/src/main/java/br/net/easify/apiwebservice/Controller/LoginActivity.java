package br.net.easify.apiwebservice.Controller;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import br.net.easify.apiwebservice.Model.DataFactory;
import br.net.easify.apiwebservice.R;
import br.net.easify.apiwebservice.interfaces.ILoginDelegate;

public class LoginActivity extends AppCompatActivity implements ILoginDelegate {

    private Button btnLogin;
    private TextView txtCadastre;
    private EditText txtEmail;
    private EditText txtSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataFactory.sharedInstance().setContext(this);

        if (DataFactory.sharedInstance().isUserLoggedIn()) {
            Intent main = new Intent(this, MainActivity.class);
            startActivity(main);
            finish();
        } else {
            setContentView(R.layout.activity_login);

            btnLogin = findViewById(R.id.btnLogin);
            txtCadastre = findViewById(R.id.txtCadastre);
            txtEmail = findViewById(R.id.txtEmail);
            txtSenha = findViewById(R.id.txtSenha);

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String email = txtEmail.getText().toString();
                    String senha = txtSenha.getText().toString();

                    if ( email.length() == 0 ) {
                        Toast.makeText(LoginActivity.this, "Informe o endereço de e-mail", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if ( senha.length() == 0 ) {
                        Toast.makeText(LoginActivity.this, "Informe a senha", Toast.LENGTH_LONG).show();
                        return;
                    }

                    DataFactory.sharedInstance().login(LoginActivity.this, email, senha);
                }
            });

            txtCadastre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(LoginActivity.this, NewAccountActivity.class);
                    startActivity(i);
                }
            });
        }
    }

    @Override
    public void onLogin(Boolean success) {
        if ( success ) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
            return;
        } else {
            Toast.makeText(LoginActivity.this, "e-mail e/ou senha inválido(s)", Toast.LENGTH_LONG).show();
        }
    }
}
