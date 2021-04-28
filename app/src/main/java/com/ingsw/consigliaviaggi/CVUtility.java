package com.ingsw.consigliaviaggi;

import android.content.Context;
import androidx.appcompat.app.AlertDialog;
import java.io.IOException;
import java.util.Arrays;

public class CVUtility  {
    static boolean shouldAddBack = false;
    static boolean initialized = false;
    static boolean hasNewFilters = false;
    // ----
    static boolean[] switches = new boolean[8];
    static boolean[] copiedSwitches = new boolean[8];
    static boolean hasConfiguredSwitches = false;
    // ----
    static boolean hasInitializedSeekBar = false;
    static int[] progresses = new int[3];
    static int[] copiedProgresses = new int[3];
    static boolean hasConfiguredSeekBar = false;
    static String[] switchNames = {"ANIMALS", "PARKING", "WIFI", "DISABLE_ACCESS", "SWIMMING_POOL", "VIEW", "SMOKING_AREA", "CHILD_AREA"};
    static String[] progressNames = {"quality", "price", "nearness"};
    static int statusBarHeight;
    static int actionBarHeight;
    static public void showAlertWithTitleAndMessageFromContext(String title, String message, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context); // Costruisci una nuova finestra di dialogo.
        builder.setTitle(title); // Imposta il titolo.
        builder.setMessage(message != null ? message : "\n"); // Imposta il messaggio.
        builder.setCancelable(false).setPositiveButton("OK", null);
        builder.show(); // Mostra la finestra di dialogo
    }
    static public boolean hasFilterChanges() {
        for (int i = 0; i < 8; i++)
            if (copiedSwitches[i] != switches[i])
                return true;
        for (int i = 0; i < 3; i++)
            if (copiedProgresses[i] != progresses[i])
                return true;
        return false;
    }
    static public void copyFilters() {
        for (int i = 0; i < 8; i++)
            copiedSwitches[i] = switches[i];
        for (int i = 0; i < 3; i++)
            copiedProgresses[i] = progresses[i];
    }
    static public void restoreFiltersToPreviousState() {
        for (int i = 0; i < 8; i++)
            switches[i] = copiedSwitches[i];
        for (int i = 0; i < 3; i++)
            progresses[i] = copiedProgresses[i];
    }
    static public void setShouldAddBack(boolean value) {
        shouldAddBack = value;
        initialized = true;
    }
    static public boolean getShouldAddBack() {
        return shouldAddBack;
    }
    static public boolean switchValueAtIndex(int index) {
        return switches[index];
    }
    static public int progressAtIndex(int index) {
        return progresses[index];
    }
    static public void setBoolAtIndex(boolean value, int index) {
        if (index < 8) {
            switches[index] = value;
            hasNewFilters = true;
            RicercaAvanzata.staticItem.setEnabled(hasFilterChanges());
        }
    }
    static public void setIntAtIndex(int value, int index) {
        if (index < 8) {
            progresses[index] = value;
            hasNewFilters = true;
            RicercaAvanzata.staticItem.setEnabled(hasFilterChanges());
        }
    }
    static public void destruct() {
        hasConfiguredSwitches = false;
        hasInitializedSeekBar = false;
        hasNewFilters = false;
        Arrays.fill(progresses, 0);
        Arrays.fill(copiedProgresses, 0);
        Arrays.fill(switches, Boolean.FALSE);
        Arrays.fill(copiedSwitches, Boolean.FALSE);
    }
    static public void initSeekBar() {
        if (!hasInitializedSeekBar) {
            Arrays.fill(progresses, 0);
            Arrays.fill(copiedProgresses, 0);
            hasInitializedSeekBar = true;
        }
    }
    static public void setHasConfiguredSeekBar(boolean value) {
        hasConfiguredSeekBar = value;
        //if (!hasConfiguredSeekBar)
            //Arrays.fill(progresses, 1);
    }
    static public boolean getHasConfiguredSeekBar() {
        return hasConfiguredSeekBar;
    }
    static public void setHasConfiguredSwitches(boolean value) {
        hasConfiguredSwitches = value;
        //if (!hasConfiguredSwitches)
            //Arrays.fill(switches, Boolean.FALSE);
    }
    static public boolean getHasConfiguredSwitches() {
        return hasConfiguredSwitches;
    }
    static public boolean shouldAskToAddBack() {
        return !initialized;
    }
    static public boolean hasInternetConnection() {
        Runtime runtime = Runtime.getRuntime();
        try {
            return (runtime.exec("/system/bin/ping -c 1 8.8.8.8").waitFor() == 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
