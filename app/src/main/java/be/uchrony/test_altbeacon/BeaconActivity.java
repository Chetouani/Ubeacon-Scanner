package be.uchrony.test_altbeacon;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.kontakt.sdk.android.device.BeaconDevice;
import com.kontakt.sdk.core.Proximity;

/**
 * Activité pour l'affichage d'un Ibeacon et ces caractéristiques.
 *
 * @author  Chetouani Abdelhalim
 * @version 0.1
 */
public class BeaconActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);

        // active le retour à l'activity parent MainActivity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // met en couleur l'action bar
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.Orange)));
        // on récupère le beaconDevice que MainActivity nous à passer
        Bundle extra = getIntent().getExtras();
        Object o = extra.get(MainActivity.EXTRA_BEACON);
        if ( o != null) {
            BeaconDevice beacon = (BeaconDevice) o;
            // affiche les informations liée à ce Ibeacon
            initActivity(beacon);
        }
    }

    /**
     * Met a jour l'affichage des caractérisques du Ibeacon
     *
     * @param b le Ibeacon dont on veut afficher les caractéristiques
     */
    private void initActivity(BeaconDevice b) {
        TextView precision = (TextView) findViewById(R.id.precision);
        precision.setText(""+b.getAccuracy());
        TextView macAdresse = (TextView) findViewById(R.id.mac_adresse);
        macAdresse.setText(b.getAddress());
        TextView batterie = (TextView) findViewById(R.id.batterie);
        batterie.setText(""+b.getBatteryPower()+" %");
        TextView beaconID = (TextView) findViewById(R.id.beacon_id);
        beaconID.setText(b.getBeaconUniqueId());
        TextView firmware = (TextView) findViewById(R.id.firmware_version);
        firmware.setText(""+b.getMajor());
        TextView major = (TextView) findViewById(R.id.beacon_major);
        major.setText(""+b.getMajor());
        TextView minor = (TextView) findViewById(R.id.beacon_minor);
        minor.setText(""+b.getMinor());
        TextView nom = (TextView) findViewById(R.id.beacon_nom);
        nom.setText(b.getName());
        TextView mdp = (TextView) findViewById(R.id.beacon_mdp);
        mdp.setText(String.valueOf(b.getPassword()));
        TextView distance = (TextView) findViewById(R.id.beacon_distance);
        distance.setText(getDistance(b));
        TextView uuid = (TextView) findViewById(R.id.beacon_uuid);
        uuid.setText(b.getProximityUUID().toString());
        TextView rssi = (TextView) findViewById(R.id.beacon_rssi);
        rssi.setText(""+b.getRssi());
        TextView txPower = (TextView) findViewById(R.id.beacon_tx_power);
        txPower.setText(""+b.getTxPower());
        TextView timesstamp = (TextView) findViewById(R.id.beacon_timestamp);
        timesstamp.setText(""+b.getTimestamp());

        ImageView imageBeacon = (ImageView) findViewById(R.id.beacon_image);
        if (b.getName().equals("estimote")) {
            imageBeacon.setImageResource(R.drawable.estimote_logo);
        } else if (b.getName().equals("Kontakt")) {
            imageBeacon.setImageResource(R.drawable.kontakt_io_logo);
        } else {
            imageBeacon.setImageResource(R.drawable.ibeacon_logo);
        }
    }

    /**
     * Donne la proximité du Ibeacon (Loin, trés proche et proche)
     * @param bd le Ibeacon concerné
     * @return la proximité sous forme de chaine de caractére
     */
    private String getDistance(BeaconDevice bd) {
        if (bd.getProximity() == Proximity.FAR) {
            return "LOIN";
        } else if (bd.getProximity() == Proximity.IMMEDIATE) {
            return "Très proche";
        } else {
            return "Proche";
        }
    }
}
