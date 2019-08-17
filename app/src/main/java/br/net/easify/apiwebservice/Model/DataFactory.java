package br.net.easify.apiwebservice.Model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedReader;
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
import java.util.List;

import br.net.easify.apiwebservice.interfaces.IChuckNorrisJokeDelegate;
import br.net.easify.apiwebservice.interfaces.IOrgaoDelegate;

public class DataFactory {

    private static DataFactory instance = null;
    private Context context;
    private List<Orgao> orgaos = new ArrayList<>();

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

    public List<Orgao> getOrgaos() { return this.orgaos; }

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

            SoapObject ObterOrgaosResult = (SoapObject)result.getProperty(0);
            SoapObject orgaosObject = (SoapObject)ObterOrgaosResult.getProperty(0);
            for (int i = 0; i < orgaosObject.getPropertyCount(); i++) {

                SoapObject property = (SoapObject)orgaosObject.getProperty(i);

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
}
