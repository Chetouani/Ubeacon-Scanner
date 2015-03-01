package be.uchrony.test_altbeacon;


import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public class MainActivity extends ActionBarActivity  implements BeaconConsumer {

    protected static final String TAG_DEBUG = "TD_RangingActivity";
    private BeaconManager beaconManager;
    private TextView zoneText;
    private ListeDeBeacons listeDeBeacons;
    private Region regionBeacon;
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

        listeDeBeacons = new ListeDeBeacons(this);
        ListView listeVue = (ListView) findViewById(R.id.liste_de_beacons);
        listeVue.setAdapter(listeDeBeacons);

        regionBeacon = new Region("regionId",null,null,null);
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);
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
            beaconManager.startRangingBeaconsInRegion(regionBeacon);
            scanEstLancer = true;
        } catch (RemoteException e) {
            Log.d(TAG_DEBUG,e.getMessage());
        }
    }

    private void stopScan() {
        try {
            beaconManager.stopRangingBeaconsInRegion(regionBeacon);
            zoneText.setText("Le scan est éteint");
            scanEstLancer = false;
        } catch (RemoteException e) {
            Log.d(TAG_DEBUG,e.getMessage());
        }
    }

    private void viderListeBeacons() {
        listeDeBeacons.remplacerLaListe(Collections.<UBeacon>emptyList());
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CheckBox estimote,kontakt;
                        estimote = (CheckBox) findViewById(R.id.cb_estimote);
                        kontakt = (CheckBox) findViewById(R.id.cb_kontakt);
                        if (estimote.isChecked() || kontakt.isChecked()){
                            zoneText.setText("Nombre de beacons : "+beacons.size());
                            ArrayList<UBeacon> uBeacons = new ArrayList<>();
                            for (Beacon unB : beacons) {
                                uBeacons.add(new UBeacon(unB));
                            }
                            listeDeBeacons.remplacerLaListe(uBeacons);
                        } else {
                            zoneText.setText("Nombre de beacons : 0");
                            viderListeBeacons();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
        if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            BluetoothAdapter.getDefaultAdapter().disable();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Verifie que le bluetooth est activé
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this,"Votre gsm n'a pas le bluetooth LE", Toast.LENGTH_SHORT).show();
        }
        // Verifie que le bluetooth est activé
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            BluetoothAdapter.getDefaultAdapter().enable();
        }
    }

    public void clicSurRadioBouton(View view) {
        Log.d(TAG_DEBUG,"radio bouton");
        CheckBox estimote,kontakt;
        estimote = (CheckBox) findViewById(R.id.cb_estimote);
        kontakt = (CheckBox) findViewById(R.id.cb_kontakt);
        if ( (estimote.isChecked() && kontakt.isChecked())) {
            regionBeacon = new Region("regionId",null,null,null);
        } else if (estimote.isChecked()) {
            regionBeacon = new Region("regionId",Identifier.parse(uuidEstimote),null,null);
        } else if (kontakt.isChecked()) {
            regionBeacon = new Region("regionId",Identifier.parse(uuidKontakt),null,null);
        }
        if (scanEstLancer) {
            startScan();
        }
    }
}
