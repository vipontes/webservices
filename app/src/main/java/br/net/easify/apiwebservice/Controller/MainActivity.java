package br.net.easify.apiwebservice.Controller;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import br.net.easify.apiwebservice.Model.DataFactory;
import br.net.easify.apiwebservice.R;
import br.net.easify.apiwebservice.View.TabAdapter;

public class MainActivity extends AppCompatActivity {
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.ic_laptop,
            R.drawable.ic_laptop_mac,
            R.drawable.ic_view_list,
            R.drawable.ic_person
    };
    private final int PAGES = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new RestFragment(), "REST");
        adapter.addFragment(new SoapFragment(), "SOAP");
        adapter.addFragment(new FireStoreFragment(), "FIRESTORE");
        adapter.addFragment(new ProfileFragment(), "PERFIL");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        viewPager.setOffscreenPageLimit(PAGES);
    }
}
