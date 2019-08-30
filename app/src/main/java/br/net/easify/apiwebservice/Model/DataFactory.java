package br.net.easify.apiwebservice.Model;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.net.easify.apiwebservice.interfaces.IChuckNorrisJokeDelegate;
import br.net.easify.apiwebservice.interfaces.IEditEmpresaDelegate;
import br.net.easify.apiwebservice.interfaces.IEmpresasDelegate;
import br.net.easify.apiwebservice.interfaces.ILoginDelegate;
import br.net.easify.apiwebservice.interfaces.INewAccountDelegate;
import br.net.easify.apiwebservice.interfaces.IOrgaoDelegate;
import br.net.easify.apiwebservice.interfaces.IRemoveEmpresaDelegate;

public class DataFactory {

    private final String USER_COLLECTION = "usuarios";
    private final String EMPRESAS_COLLECTION = "empresas";

    private static DataFactory instance = null;
    private Context context;
    private List<Orgao> orgaos = new ArrayList<>();
    private List<Empresa> empresas = new ArrayList<>();


    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore fireStore;
    private FirebaseStorage storage;

    private DataFactory() {
    }

    public static DataFactory sharedInstance() {

        if (instance == null) {
            instance = new DataFactory();
        }

        return instance;
    }

    public void setContext(Context context) {

        this.context = context;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.fireStore = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    public void getChuckNorrisJoke(IChuckNorrisJokeDelegate delegate) {
        new ChuckNorrisJokeAsync(delegate).execute();
    }

    public class ChuckNorrisJokeAsync extends AsyncTask<Void, Void, String> {

        private IChuckNorrisJokeDelegate delegate;

        public ChuckNorrisJokeAsync(IChuckNorrisJokeDelegate delegate) {
            this.delegate = delegate;
        }

        @Override
        protected String doInBackground(Void... voids) {
            if (!isNetworkAvailable()) {
                return "";
            }

            String jsonStr = "";

            try {
                URL url = new URL("https://matchilling-chuck-norris-jokes-v1.p.rapidapi.com/jokes/random");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(5000);
                connection.setDoInput(true);
                connection.setDoOutput(false);
                connection.setRequestMethod("GET");

                connection.setRequestProperty("X-RapidAPI-Host", "matchilling-chuck-norris-jokes-v1.p.rapidapi.com");
                connection.setRequestProperty("X-RapidAPI-Key", "d19e2048c2mshde8a2bb09253b57p1181b0jsnb74d1f19ca6c");
                connection.setRequestProperty("accept", "application/json");
                connection.setRequestProperty("Accept-Charset", "UTF-8");

                connection.setUseCaches(false);

                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    jsonStr += line;
                    line = reader.readLine();
                }
                reader.close();
                inputStreamReader.close();
                inputStream.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return jsonStr;
        }

        @Override
        protected void onPostExecute(String jsonStr) {
            super.onPostExecute(jsonStr);

            if (jsonStr.length() > 0) {
                try {
                    String data = jsonStr;
                    data = data.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
                    data = data.replaceAll("\\+", "%2B");

                    JSONObject obj = new JSONObject(URLDecoder.decode(data, "UTF-8"));

                    ChuckNorrisJoke joke = new ChuckNorrisJoke(
                            obj.getString("created_at"),
                            obj.getString("updated_at"),
                            obj.getString("icon_url"),
                            obj.getString("id"),
                            obj.getString("url"),
                            obj.getString("value")
                    );

                    delegate.onChuckNorrisJoke(joke);

                } catch (JSONException e) {
                    e.printStackTrace();
                    delegate.onChuckNorrisJoke(null);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    delegate.onChuckNorrisJoke(null);
                }
            } else {
                delegate.onChuckNorrisJoke(null);
            }
        }
    }

    public List<Orgao> getOrgaos() {
        return this.orgaos;
    }

    public void getOrgaos(IOrgaoDelegate delegate) {
        new OrgaoAsync(delegate).execute();
    }

    class OrgaoAsync extends AsyncTask<Void, Void, SoapObject> {

        private IOrgaoDelegate delegate;

        public OrgaoAsync(IOrgaoDelegate delegate) {
            this.delegate = delegate;
        }

        @Override
        protected SoapObject doInBackground(Void... voids) {

            if (!isNetworkAvailable()) return null;

            final String url = "https://www.camara.leg.br/SitCamaraWS/Orgaos.asmx";
            final String name_space = "https://www.camara.gov.br/SitCamaraWS/Orgaos";
            final String method = "ObterOrgaos";
            final String action = name_space + "/" + method;

            SoapObject request = new SoapObject(name_space, method);
            SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
            HttpTransportSE ht = new HttpTransportSE(Proxy.NO_PROXY, url, 10000);
            try {
                ht.call(action, envelope);
                SoapObject soap = (SoapObject) envelope.bodyIn;
                return soap;
            } catch (SocketTimeoutException t) {
                t.printStackTrace();
                return null;
            } catch (IOException i) {
                i.printStackTrace();
                return null;
            } catch (Exception q) {
                q.printStackTrace();
                return null;
            }
        }

        private final SoapSerializationEnvelope getSoapSerializationEnvelope(SoapObject request) {
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = request;
            envelope.dotNet = true;
            envelope.implicitTypes = true;
            envelope.setAddAdornments(false);
            envelope.setOutputSoapObject(request);
            return envelope;
        }

        @Override
        protected void onPostExecute(SoapObject result) {
            super.onPostExecute(result);

            if (result == null) {
                delegate.onOrgao(false);
                return;
            }

            orgaos.clear();

            SoapObject ObterOrgaosResult = (SoapObject) result.getProperty(0);
            SoapObject orgaosObject = (SoapObject) ObterOrgaosResult.getProperty(0);
            for (int i = 0; i < orgaosObject.getPropertyCount(); i++) {

                SoapObject property = (SoapObject) orgaosObject.getProperty(i);

                String id = property.getAttribute(0).toString();
                String idTipodeOrgao = property.getAttribute(1).toString();
                String sigla = property.getAttribute(2).toString();
                String descricao = property.getAttribute(3).toString();

                Orgao orgao = new Orgao(id, idTipodeOrgao, sigla, descricao);
                orgaos.add(orgao);
            }

            delegate.onOrgao(true);
        }
    }

    public void login(final ILoginDelegate delegate, String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = firebaseAuth.getCurrentUser();
                            delegate.onLogin(true);
                        } else {
                            currentUser = null;
                            delegate.onLogin(false);
                        }
                    }
                });
    }

    public boolean isUserLoggedIn() {
        this.currentUser = this.firebaseAuth.getCurrentUser();
        return (this.currentUser != null);
    }

    public void logout() {
        this.firebaseAuth.signOut();
        this.currentUser = null;
    }

    public void createAccount(final INewAccountDelegate delegate, final String nome, final String email, final String senha) {
        this.firebaseAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(nome).build();

                            user.updateProfile(profileUpdates);

                            String uid = user.getUid();

                            Map<String, Object> userData = new HashMap<>();
                            userData.put("nome", nome);

                            fireStore.collection(USER_COLLECTION)
                                    .document(uid)
                                    .set(userData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            delegate.onNewAccount(true, "");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            delegate.onNewAccount(false, e.getMessage());
                                        }
                                    });
                        } else {
                            String msg;

                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                msg = "A senha deve ter no mínimo 8 caracteres";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                msg = "E-mail inválido";
                            } catch (FirebaseAuthUserCollisionException e) {
                                msg = "O e-mail informado já está em uso";
                            } catch (Exception e) {
                                msg = "Erro não identificado";
                            }

                            delegate.onNewAccount(false, msg);
                        }
                    }
                });
    }

    public void getEmpresas(final IEmpresasDelegate delegate) {

        FirebaseUser user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();


        fireStore.collection(USER_COLLECTION)
                .document(uid)
                .collection(EMPRESAS_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            empresas.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getId();
                                String nome = document.get("nome").toString();
                                String logo = document.get("logo").toString();

                                Empresa empresa = new Empresa(id, nome, logo);
                                empresas.add(empresa);
                            }

                            delegate.onEmpresas(true);
                        } else {
                            delegate.onEmpresas(false);
                        }
                    }
                });
    }

    public void addEmpresa(final IEditEmpresaDelegate delegate, final Empresa empresa) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final String uid = user.getUid();

        Map<String, Object> data = new HashMap<>();
        data.put("nome", empresa.getNome());
        data.put("latlng", new GeoPoint(empresa.getLatitude(), empresa.getLongitide()));

        fireStore.collection(USER_COLLECTION)
                .document(uid)
                .collection(EMPRESAS_COLLECTION)
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String id = documentReference.getId();

                        String logo = id + ".jpg";

                        ContextWrapper cw = new ContextWrapper(context);
                        File tempDir = cw.getDir("temp", Context.MODE_PRIVATE);
                        if (!tempDir.exists()) {
                            tempDir.mkdir();
                        }

                        final File SourceFile = new File(tempDir.getAbsolutePath(), "temp.jpg");
                        final File DestinationFile = new File(tempDir.getAbsolutePath(), logo);
                        DestinationFile.delete();
                        if (SourceFile.renameTo(DestinationFile)) {
                            Log.v("Moving", "Moving file successful.");
                        } else {
                            Log.v("Moving", "Moving file failed.");
                        }

                        empresa.setId(id);
                        empresa.setLogo(logo);
                        empresas.add(empresa);

                        Map<String, Object> data = new HashMap<>();
                        data.put("logo", empresa.getLogo());

                        // Atualiza o nome do arquivo
                        fireStore.collection(USER_COLLECTION)
                                .document(uid)
                                .collection(EMPRESAS_COLLECTION)
                                .document(empresa.getId())
                                .update(data);

                        // Grava o arquivo na storage
                        StorageReference pathReference = storage.getReference().child("empresas");
                        Uri uri = Uri.fromFile(DestinationFile);
                        pathReference.child(logo).putFile(uri)
                                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        DestinationFile.delete();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        DestinationFile.delete();
                                    }
                                });

                        delegate.onEditEmpresa(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        delegate.onEditEmpresa(false);
                    }
                });
    }


    public void updateEmpresa(final IEditEmpresaDelegate delegate, final Empresa empresa, final int position) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final String uid = user.getUid();

        Map<String, Object> data = new HashMap<>();
        data.put("nome", empresa.getNome());
        data.put("latlng", new GeoPoint(empresa.getLatitude(), empresa.getLongitide()));

        fireStore.collection(USER_COLLECTION)
                .document(uid)
                .collection(EMPRESAS_COLLECTION)
                .document(empresa.getId())
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String id = empresa.getId();

                        String logo = id + ".jpg";

                        ContextWrapper cw = new ContextWrapper(context);
                        File tempDir = cw.getDir("temp", Context.MODE_PRIVATE);
                        if (!tempDir.exists()) {
                            tempDir.mkdir();
                        }

                        final File SourceFile = new File(tempDir.getAbsolutePath(), "temp.jpg");
                        if ( SourceFile.exists() ) {
                            final File DestinationFile = new File(tempDir.getAbsolutePath(), logo);
                            DestinationFile.delete();
                            if (SourceFile.renameTo(DestinationFile)) {
                                Log.v("Moving", "Moving file successful.");
                            } else {
                                Log.v("Moving", "Moving file failed.");
                            }

                            Map<String, Object> data = new HashMap<>();
                            data.put("logo", empresa.getLogo());

                            // Atualiza o nome do arquivo
                            fireStore.collection(USER_COLLECTION)
                                    .document(uid)
                                    .collection(EMPRESAS_COLLECTION)
                                    .document(empresa.getId())
                                    .update(data);

                            // Grava o arquivo na storage
                            StorageReference pathReference = storage.getReference().child("empresas");
                            Uri uri = Uri.fromFile(DestinationFile);
                            pathReference.child(logo).putFile(uri)
                                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            DestinationFile.delete();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            DestinationFile.delete();
                                        }
                                    });
                        }

                        empresas.set(position, empresa);
                        delegate.onEditEmpresa(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        delegate.onEditEmpresa(false);
                    }
                });
    }

    public void removeEmpresa(final int position, final IRemoveEmpresaDelegate delegate) {
        final Empresa empresa = this.empresas.get(position);
        if (empresa != null) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            String uid = user.getUid();

            fireStore.collection(USER_COLLECTION)
                    .document(uid)
                    .collection(EMPRESAS_COLLECTION)
                    .document(empresa.getId())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            empresas.remove(position);
                            removeImagemEmpresa(empresa);
                            delegate.onRemoveEmpresa(true, position);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            delegate.onRemoveEmpresa(false, position);
                        }
                    });
        } else {
            delegate.onRemoveEmpresa(false, position);
        }
    }

    public List<Empresa> getEmpresas() {
        return this.empresas;
    }

    public Empresa getEmpresa(int position) {
        return empresas.get(position);
    }

    public void removeImagemEmpresa(Empresa empresa) {
        StorageReference pathReference = storage.getReference().child("empresas");
        pathReference.child(empresa.getLogo()).delete();
    }


}
