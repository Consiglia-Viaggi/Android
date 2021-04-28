package com.ingsw.consigliaviaggi;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.ingsw.consigliaviaggi.ui.main.SectionsPagerAdapter;

import org.json.JSONException;
import org.json.JSONObject;

public class PaginaStruttura extends ToolbarActivity {
    public boolean constraintFixed = false;
    private void initActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        if (constraintFixed)
            return;
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        MenuItem item = bottomNavigationView.getMenu().findItem(R.id.action_profilo);
        if (CVUser.isLogged)
            item.setTitle("Profilo");
        else
            item.setTitle("Accedi");
        ListView listView = findViewById(R.id.listReviewsView);
        if (listView != null) {
            ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
            layoutParams.height = listView.getHeight() - bottomNavigationView.getHeight();
            listView.setLayoutParams(layoutParams);
            constraintFixed = true;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_struttura);
        final CVStructure structure = (CVStructure) getIntent().getSerializableExtra("structure");
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(structure.name);
        setSupportActionBar(toolbar);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        final PaginaStruttura context = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CVUser.isLogged) {
                    Intent activity = new Intent(context, ScriviRecensioni.class);
                    activity.putExtra("structure", structure);
                    startActivity(activity);
                }
                else
                    CVUtility.showAlertWithTitleAndMessageFromContext("Ooops! Non sei più loggato.", "Questo succede quando cambi credenziali da un altro dispositivo.\nSe vuoi effettuare nuovamente l'accesso, clicca su \"Accedi\" nella barra sottostante.", context);

            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1 && CVUser.isLogged){
                    FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
                    floatingActionButton.setVisibility(View.VISIBLE);
                }
                else
                {
                    FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
                    floatingActionButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        initActionBar();
    }
}