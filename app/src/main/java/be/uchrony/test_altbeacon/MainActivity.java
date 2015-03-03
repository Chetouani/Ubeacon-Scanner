package be.uchrony.test_altbeacon;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.kontakt.sdk.android.connection.OnServiceBoundListener;
import com.kontakt.sdk.android.device.BeaconDevice;
import com.kontakt.sdk.android.device.Region;
import com.kontakt.sdk.android.manager.BeaconManager;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends ActionBarActivity{

    protected static final String TAG_DEBUG = "TAG_DEBUG_RangingActivity";
    // identifient de la demande d'activation du blue
    // pour verifier que l'activation est ok
    private static final int CODE_ACTIVATION_BLUETOOTH = 1;
    private BeaconManager beaconManager;
    private TextView zoneText;
    private ProgressBar bareChargement;
    private ListeDeBeacons listeDeBeacons;
    private String uuidEstimote = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    private String uuidKontakt = "F7826DA6-4FA2-4E98-8024-BC5B71E0893E";
    private boolean  scanEstLancer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //TODO n'affiche pas le logo
        getSupportActionBar().setLogo(R.drawable.loupe_bleu);

        zoneText = (TextView) findViewById(R.id.nombre_beacons_trouver);
        zoneText.setText("Le scan est éteint");
        bareChargement  = (ProgressBar) findViewById(R.id.bare_chargement);
        bareChargement.setIndeterminate(false);

        listeDeBeacons = new ListeDeBeacons(this);
        ListView listeVue = (ListView) findViewById(R.id.liste_de_beacons);
        listeVue.setAdapter(listeDeBeacons);

        initBeacon();
        verificationBluetooth();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        // Handle presses on the action bar items
        switch (id) {
            case R.id.action_start_scan:
                startScan();
                return true;
            case R.id.action_stop_scan:
                stopScan();
                return true;
            case R.id.action_vider_liste:
                viderListeBeacons();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startScan() {
        try {
            beaconManager.startRanging();
            bareChargement.setIndeterminate(true);
            scanEstLancer = true;
            Log.d(TAG_DEBUG,"start");
        } catch (RemoteException e) {
            Log.d(TAG_DEBUG,"Erreur de démarrage Scan");
        }
    }

    private void stopScan() {
        beaconManager.stopRanging();
        bareChargement.setIndeterminate(false);
        scanEstLancer = false;
        Log.d(TAG_DEBUG,"stop");
    }

    private void viderListeBeacons() {
        listeDeBeacons.remplacerLaListe(Collections.<BeaconDevice>emptyList());
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScan();
        beaconManager.disconnect();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Vérifie si votre GSM posséde le BLE et si il est allumé.
     * Si vous ne l'êtes pas elle tente de l'allumer.
     */
    private void verificationBluetooth() {
        // Verifie que on posséde le bluetooth LE
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Votre gsm n'a pas le bluetooth LE", Toast.LENGTH_SHORT).show();
            //TODO quitter l'application si pas de BLE
        }

        if(!beaconManager.isBluetoothEnabled()) {
            final Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, CODE_ACTIVATION_BLUETOOTH);
        } else if(beaconManager.isConnected()) {
            startScan();
        } else {
            connect();
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        if (!scanEstLancer)
            startScan();
    }



    /**
     * Initialise l'utilisation des beacons, j'ai limité la Region au beacon de chez
     * Kontakt avec un Uuid F7826DA6-4FA2-4E98-8024-BC5B71E0893E.
     * Et c'est içi que je définie ce qu'il faut faire chaque fois que je détécte des beacons
     */
    private void initBeacon() {
        beaconManager = BeaconManager.newInstance(this);

        // trie la liste de beacons par ordre croisant sur la distance
        beaconManager.setDistanceSort(BeaconDevice.DistanceSort.ASC);
        // implement la méthode qui vas être appelé chaque fois que des beacons
        // sont trouvé
        beaconManager.registerRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<BeaconDevice> beaconDevices) {
                // si il y'a au moins un beacon trouvé
                if (beaconDevices.size() >0) {
                    CheckBox estimote,kontakt;
                    estimote = (CheckBox) findViewById(R.id.cb_estimote);
                    kontakt = (CheckBox) findViewById(R.id.cb_kontakt);
                    if (estimote.isChecked() && kontakt.isChecked()){
                        zoneText.setText("Nombre de beacons : "+beaconDevices.size());
                        listeDeBeacons.remplacerLaListe(beaconDevices);
                    } else if (estimote.isChecked()) {
                        ArrayList<BeaconDevice> listeEstimote = new ArrayList<BeaconDevice>();
                        for (BeaconDevice bd : beaconDevices) {
                            if (bd.getName().equals("estimote"))
                                listeEstimote.add(bd);
                        }
                        zoneText.setText("Nombre de beacons : "+listeEstimote.size());
                        listeDeBeacons.remplacerLaListe(listeEstimote);
                    } else if (kontakt.isChecked()) {
                        ArrayList<BeaconDevice> listeKontakt = new ArrayList<BeaconDevice>();
                        for (BeaconDevice bd : beaconDevices) {
                            if (bd.getName().equals("Kontakt"))
                                listeKontakt.add(bd);
                        }
                        zoneText.setText("Nombre de beacons : "+listeKontakt.size());
                        listeDeBeacons.remplacerLaListe(listeKontakt);
                    } else {
                        zoneText.setText("Nombre de beacons : 0");
                        viderListeBeacons();
                    }
                }
            }
        });
    }

    /**
     * //TODO
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == CODE_ACTIVATION_BLUETOOTH) {
            if(resultCode == Activity.RESULT_OK) {
                connect();
            } else {
                Toast.makeText(this, "Erreur activation Bluetooth", Toast.LENGTH_LONG).show();
                getActionBar().setSubtitle("Erreur activation Bluetooth");
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Lance la connection du BeaconManager et démarre le scan
     */
    private void connect() {
        try {
            beaconManager.connect(new OnServiceBoundListener() {
                @Override
                public void onServiceBound() throws RemoteException {
                    //startScan();
                }
            });
        } catch (RemoteException e) {
            Log.d(TAG_DEBUG,e.getMessage());
        }
    }
}
