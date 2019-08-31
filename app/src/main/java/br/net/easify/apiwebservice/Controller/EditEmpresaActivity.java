package br.net.easify.apiwebservice.Controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import br.net.easify.apiwebservice.Model.DataFactory;
import br.net.easify.apiwebservice.Model.Empresa;
import br.net.easify.apiwebservice.R;
import br.net.easify.apiwebservice.interfaces.IEditEmpresaDelegate;

public class EditEmpresaActivity extends AppCompatActivity implements IEditEmpresaDelegate {
    private ImageView imageView;
    private Button add_image;
    private TextInputEditText txtNome;
    private static int SELECT_PICTURE = 1;

    private Empresa empresa;
    private int editPosition = -1;

    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_empresa);

        this.storage = FirebaseStorage.getInstance();

        empresa = (Empresa)getIntent().getSerializableExtra("empresa");
        editPosition = getIntent().getIntExtra("position", -1);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Cancelar");
        actionBar.setDisplayHomeAsUpEnabled(true);

        txtNome = findViewById(R.id.txtNome);
        imageView  = findViewById(R.id.image_display);
        add_image = findViewById(R.id.add_image);
        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Selecione uma foto"), SELECT_PICTURE);
            }
        });

        if ( editPosition != -1 ) {
            txtNome.setText(empresa.getNome());

            StorageReference pathReference = storage.getReference().child("empresas");
            pathReference.child(empresa.getLogo())
                    .getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).centerCrop().fit().into(imageView);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_empresa, menu);
        return true;
    }

    private Bitmap uriToBitmap(Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri uri = data.getData();
                Bitmap bmp = uriToBitmap(uri);

                ContextWrapper cw = new ContextWrapper(this);
                File tempDir = cw.getDir("temp", Context.MODE_PRIVATE);
                if (!tempDir.exists()) {
                    tempDir.mkdir();
                }

                String tempFilename = "temp.jpg";
                File f = new File(tempDir.getAbsolutePath(), tempFilename);

                String fileName = f.getAbsolutePath();
                FileOutputStream fos = null;

                try {
                    fos = new FileOutputStream(f);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {

                    e.printStackTrace();
                } catch (Exception e) {

                    e.printStackTrace();
                }

                imageView.setImageBitmap(bmp);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.save:
                boolean edit = true;
                if ( empresa == null ) {
                    edit = false;
                    empresa = new Empresa("", "", "");
                }

                String nome = txtNome.getText().toString();
                if ( nome.length() == 0 ) {
                    Toast.makeText(EditEmpresaActivity.this, "Informe o nome da empresa", Toast.LENGTH_LONG).show();
                    return true;
                }

                empresa.setNome(nome);

                if ( edit ) {
                    DataFactory.sharedInstance().updateEmpresa(EditEmpresaActivity.this, empresa, editPosition);
                } else {
                    DataFactory.sharedInstance().addEmpresa(EditEmpresaActivity.this, empresa);
                }
                return true;

            case R.id.contacts:
                if ( empresa != null ) {
                    Intent intent = new Intent(this, ContatosActivity.class);
                    intent.putExtra("empresa", empresa);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Grave a empresa antes de adicionar contatos", Toast.LENGTH_LONG).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onEditEmpresa(Boolean success) {

        if ( success ) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(this, "Não foi possível gravar a empresa", Toast.LENGTH_LONG).show();
        }
    }
}
