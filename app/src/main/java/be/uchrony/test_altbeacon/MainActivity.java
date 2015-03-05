package be.uchrony.test_altbeacon;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.RemoteException;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

/**
 * Activité principale qui permet de scanner les Ibeacons
 * qui se trouvent dans les alentours.
 *
 * @author  Chetouani Abdelhalim
 * @version 0.1
 */
public class MainActivity extends ActionBarActivity{

    // TAG pour le debbugage
    protected static final String TAG_DEBUG = "TAG_DEBUG_RangingActivity";
    // TAG pour le lancement de l'activité BeaconActivity
    protected static final String EXTRA_BEACON = "Extra_beacon";
    // identifient de la demande d'activation du blue
    // pour verifier que l'activation est ok
    private static final int CODE_ACTIVATION_BLUETOOTH = 1;
    private BeaconManager beaconManager;
    private ListeDeBeacons listeDeBeacons;
    private TextView etatDuScan;
    private ProgressBar barreChargement;
    private boolean  scanEstLancer = false;
    private String tabNavigation = "ESTIMOTE";

    /**
     * Première méthode appeler
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //TODO n'affiche pas le logo
        getSupportActionBar().setLogo(R.drawable.loupe_bleu);
        // ajoute les 3 onglets de navigation
        ajoutNavigationTab();
        // text qui affiche l'etat du scan et le nombre de ibeacons trouvé
        etatDuScan = (TextView) findViewById(R.id.nombre_beacons_trouver);
        etatDuScan.setText("Le scan est éteint");
        // petite barre de chargement pour montré que le scan est en cours
        barreChargement  = (ProgressBar) findViewById(R.id.bare_chargement);
        // permet de ne pas faire défiler la barre de chargement
        barreChargement.setIndeterminate(false);
        // tableau de la liste de beacons qui sera afficher
        listeDeBeacons = new ListeDeBeacons(this);
        // récupère la vue dans lequel on vas mettre la liste
        ListView listeVue = (ListView) findViewById(R.id.liste_de_beacons);
        listeVue.setAdapter(listeDeBeacons);
        // permet d'interagire à chaque clic sur un element de la liste
        listeVue.setOnItemClickListener(getListener());

        initBeacon();
        verificationBluetooth();
    }


    private AdapterView.OnItemClickListener getListener() {
        return new AdapterView.OnItemClickListener() {
            // içi on définie ce qu'on vas faire lorsque l'on clic sur un élement
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // on lance l'activité BeaconActivity pour voir les caractéristiques
                Intent intent = new Intent(MainActivity.this,BeaconActivity.class);
                intent.putExtra(EXTRA_BEACON,listeDeBeacons.getItem(position));
                startActivityForResult(intent,0,null);
            }
        };
    }

    /**
     * Ajoute la barre de navigation avec 3 onglets
     */
    private void ajoutNavigationTab() {
        // récupération de la barre d'action (rectangle en haut)
        ActionBar bar = getSupportActionBar() ;
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // on crée les onglets
        Tab tabEstimote = bar.newTab();
        Tab tabKontakt = bar.newTab();
        Tab tabTous = bar.newTab();
        // on les nommes
        tabEstimote.setText("Estimote");
        tabKontakt.setText("Kontakt");
        tabTous.setText("Estimote &\nKontakt");
        // on implemente les écouteurs
        tabEstimote.setTabListener(getTabListener("ESTIMOTE"));
        tabKontakt.setTabListener(getTabListener("KONTAKT"));
        tabTous.setTabListener(getTabListener("TOUS"));
        // on ajoute les onglets
        bar.addTab(tabEstimote);
        bar.addTab(tabKontakt);
        bar.addTab(tabTous);
        // mets de la couleur Orange sur la barre d'action
        bar.setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.Orange)));
        // mets de la couleur Orange sur les onglets de navigation
        bar.setStackedBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.Orange)));
    }

    /**
     * Méthodes executé lorsque on clic sur un onglet
     * @param type le type de l'onglet KONTAKT ESTIMOTE TOUS
     *
     */
    private TabListener getTabListener(final String type) {
        return new TabListener() {
            @Override
            public void onTabSelected(Tab tab
                    , FragmentTransaction fragmentTransaction) {
                if (type.equals("KONTAKT"))
                    tabNavigation = "KONTAKT";
                if (type.equals("ESTIMOTE"))
                    tabNavigation = "ESTIMOTE";
                if (type.equals("TOUS"))
                    tabNavigation = "TOUS";
                if (scanEstLancer) {
                    stopScan();
                    startScan();
                }
            }

            @Override
            public void onTabUnselected(Tab tab
                    , FragmentTransaction fragmentTransaction) {

            }

            @Override
            public void onTabReselected(Tab tab
                    , FragmentTransaction fragmentTransaction) {

            }
        };
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
        // Içi on définit ce qu'il faut faire lorsque l'on clic
        // sur les 3 boutons Play,Pausse et Poubelle
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

    /**
     * Démarre le scan de Ibeacons et la barre de chargement
     */
    private void startScan() {
        try {
            if (beaconManager != null)
                beaconManager.startRanging();
            if (barreChargement != null)
                barreChargement.setIndeterminate(true);
            scanEstLancer = true;
        } catch (RemoteException e) {
            Log.d(TAG_DEBUG,"Erreur de démarrage Scan");
        }
    }


    /**
     * Stope le scan de Ibeacons et la barre de chargement
     */
    private void stopScan() {
        if (beaconManager != null)
            beaconManager.stopRanging();
        if (barreChargement != null)
            barreChargement.setIndeterminate(false);
        scanEstLancer = false;
    }

    /**
     * Vide la liste de beacons qui est afficher
     */
    private void viderListeBeacons() {
        if (listeDeBeacons != null)
            listeDeBeacons.remplacerLaListe(Collections.<BeaconDevice>emptyList());
    }


    /**
     * Lorsque l'application est détruite
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScan();
        if (beaconManager != null)
            beaconManager.disconnect();
    }

    /**
     * Lorsque l'application démarre
     */
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

    /**
     * Lorsque l'application redémarre
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        if (!scanEstLancer)
            startScan();
    }

    /**
     * Initialise l'utilisation des beacons,
     * Et c'est içi que je définie ce qu'il faut faire chaque fois que je détecte des beacons
     */
    private void initBeacon() {
        beaconManager = BeaconManager.newInstance(this);
        // trie la liste de beacons par ordre croisant sur la distance
        //TODO Marche pas terrible
        beaconManager.setDistanceSort(BeaconDevice.DistanceSort.ASC);
        // implement la méthode qui vas être appelé chaque fois que des beacons
        // sont trouvé
        beaconManager.registerRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<BeaconDevice> beaconDevices) {
                // si il y'a au moins un beacon trouvé
                if (beaconDevices.size() >0) {
                    if (tabNavigation.equals("TOUS")) {
                        etatDuScan.setText("Nombre de beacons : "+beaconDevices.size());
                        listeDeBeacons.remplacerLaListe(beaconDevices);
                    } else if (tabNavigation.equals("ESTIMOTE")) {
                        ArrayList<BeaconDevice> listeEstimote = new ArrayList<>();
                        for (int i=0 ; i < beaconDevices.size(); i++) {
                            if (beaconDevices.get(i).getName().equals("estimote"))
                                listeEstimote.add(beaconDevices.get(i));
                        }
                        etatDuScan.setText("Nombre de beacons : "+listeEstimote.size());
                        listeDeBeacons.remplacerLaListe(listeEstimote);
                    } else if (tabNavigation.equals("KONTAKT")) {
                        ArrayList<BeaconDevice> listeKontakt = new ArrayList<>();
                        for (int i=0 ; i < beaconDevices.size();i++) {
                            if (beaconDevices.get(i).getName().equals("Kontakt"))
                                listeKontakt.add(beaconDevices.get(i));
                        }
                        etatDuScan.setText("Nombre de beacons : "+listeKontakt.size());
                        listeDeBeacons.remplacerLaListe(listeKontakt);
                    } else {
                        etatDuScan.setText("Nombre de beacons : 0");
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
                getSupportActionBar().setSubtitle("Erreur activation Bluetooth");
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
            if (beaconManager!= null)
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
