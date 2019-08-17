package br.net.easify.apiwebservice.Controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import br.net.easify.apiwebservice.Model.ChuckNorrisJoke;
import br.net.easify.apiwebservice.Model.DataFactory;
import br.net.easify.apiwebservice.R;
import br.net.easify.apiwebservice.interfaces.IChuckNorrisJokeDelegate;


public class RestFragment extends Fragment implements IChuckNorrisJokeDelegate {

    private Button btnGetJoke;
    private ImageView img;
    private TextView value;
    private TextView createdAt;
    private TextView url;

    private Boolean imgLoaded = false;

    private ChuckNorrisJoke currentJoke;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_rest, container, false);

        btnGetJoke = v.findViewById(R.id.btnGetJoke);
        img = v.findViewById(R.id.img);
        value = v.findViewById(R.id.value);
        createdAt = v.findViewById(R.id.createdAt);
        url = v.findViewById(R.id.url);


        btnGetJoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataFactory.sharedInstance().getChuckNorrisJoke(RestFragment.this);
            }
        });

        url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( currentJoke != null ) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentJoke.getUrl()));
                    startActivity(browserIntent);
                }
            }
        });

        return v;
    }

    @Override
    public void onChuckNorrisJoke(ChuckNorrisJoke joke) {
        if ( joke != null ) {
            currentJoke = joke;
            if ( !imgLoaded ) {
                Picasso.get().load(joke.getIconUrl()).into(img);
                imgLoaded = true;
            }
            value.setText(joke.getValue());
            createdAt.setText(joke.getCreatedAt());
            url.setText(joke.getUrl());
        }
    }
}
