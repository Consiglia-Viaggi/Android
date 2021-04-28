package com.ingsw.consigliaviaggi;

import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class RicercaAvanzata extends ToolbarActivity {
    static MenuItem staticItem;
    protected void configureTextView(int row, int column) {
        String textViewName = "textView" + String.valueOf(row);
        TextView view = findViewById(getResources().getIdentifier(textViewName, "id", getPackageName()));
        switch (row) {
            case 0:
                if (column == 0)
                    view.setText("Tipo struttura: qualsiasi");
                else
                if (column == 1)
                    view.setText("Tipo struttura: peggiore");
                else
                if (column == 2)
                    view.setText("Tipo struttura: media");
                else
                    view.setText("Tipo struttura: migliore");
                break;
            case 1:
                if (column == 0)
                    view.setText("Prezzo: qualsiasi");
                else
                if (column == 1)
                    view.setText("Prezzo: basso");
                else
                if (column == 2)
                    view.setText("Prezzo: medio");
                else
                    view.setText("Prezzo: alto");
                break;
            case 2:
                if (column == 0)
                    view.setText("Vicinanza: qualsiasi");
                else
                if (column == 1)
                    view.setText("Vicinanza: vicino");
                else
                if (column == 2)
                    view.setText("Vicinanza: medio");
                else
                    view.setText("Vicinanza: lontano");
        }
    }
    protected void configureSeekBar(int i) {
        String viewName = "seekBar" + String.valueOf(i);
        SeekBar bar = findViewById(getResources().getIdentifier(viewName, "id", getPackageName()));
        //if (CVUtility.getHasConfiguredSeekBar())
            bar.setProgress(CVUtility.progressAtIndex(i));
        final int j = i;
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub
                CVUtility.setIntAtIndex(progress, j);
                configureTextView(j, progress);
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ricerca_avanzata);
        super.installBackButtonIfNeeded();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Ricerca avanzata");
        CVUtility.initSeekBar();
        for (int i = 0; i < 3; i++) {
            configureSeekBar(i);
            configureTextView(i, CVUtility.progressAtIndex(i));
        }
        CVUtility.setHasConfiguredSeekBar(true);
    }
    public void servicesButtonTapped(View view) {
        Intent activity = new Intent(this,Servizi.class);
        startActivity(activity);
    }
    public void saveButtonTapped(MenuItem item) {
        // Save ....
        CVUtility.copyFilters();
        super.onBackPressed();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater(); // ottieni la lista dei menu
        inflater.inflate(R.menu.menu_ricerca_avanzata, menu); // aggiungi il menu.
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
        if (paramMenuItem.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        saveButtonTapped(paramMenuItem);
        return true;
    }
    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        MenuItem item = bottomNavigationView.getMenu().findItem(R.id.action_profilo);
        if (CVUser.isLogged)
            item.setTitle("Profilo");
        else
            item.setTitle("Accedi");
    }
    @Override
    public void onBackPressed() {
        if (CVUtility.hasFilterChanges()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this); // Costruisci una nuova finestra di dialogo.
            builder.setTitle("Vuoi davvero tornare indietro?"); // Imposta il titolo.
            builder.setMessage("Se non clicchi su Applica, i nuovi filtri selezionati non verranno abilitati nella ricerca e verranno utilizzati i precedenti."); // Imposta il messaggio.
            final RicercaAvanzata activity = this;
            builder.setCancelable(false).setPositiveButton("Sì", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    CVUtility.restoreFiltersToPreviousState();
                    activity.finish();
                }
            }).setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder.show(); // Mostra la finestra di dialogo
        }
        else
            super.onBackPressed();
    }
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        staticItem = menu.getItem(0);
        staticItem.setEnabled(CVUtility.hasNewFilters);
        return true;
    }
}
