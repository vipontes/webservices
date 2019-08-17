package br.net.easify.apiwebservice.Controller;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.net.easify.apiwebservice.R;
import br.net.easify.apiwebservice.View.OrgaoAdapter;

public class SoapFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrgaoAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_soap, container, false);

        recyclerView = v.findViewById(R.id.orgaos);

        adapter = new OrgaoAdapter(getContext());

        recyclerView.setAdapter(adapter);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);

        return v;
    }

}
