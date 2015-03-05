package be.uchrony.test_altbeacon;

import android.graphics.Color;
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
        TextView distance = (TextView) findViewById(R.id.distance);
        TextView macAdresse = (TextView) findViewById(R.id.mac_adresse);
        TextView batterie = (TextView) findViewById(R.id.batterie);
        TextView beaconID = (TextView) findViewById(R.id.beacon_id);
        TextView firmware = (TextView) findViewById(R.id.firmware_version);
        TextView major = (TextView) findViewById(R.id.beacon_major);
        TextView minor = (TextView) findViewById(R.id.beacon_minor);
        TextView nom = (TextView) findViewById(R.id.beacon_nom);
        TextView mdp = (TextView) findViewById(R.id.beacon_mdp);
        TextView uuid = (TextView) findViewById(R.id.beacon_uuid);
        TextView rssi = (TextView) findViewById(R.id.beacon_rssi);
        TextView txPower = (TextView) findViewById(R.id.beacon_tx_power);
        TextView timesstamp = (TextView) findViewById(R.id.beacon_timestamp);
        ImageView imageBeacon = (ImageView) findViewById(R.id.beacon_image);

        macAdresse.setText(b.getAddress());
        firmware.setText(""+b.getFirmwareVersion());
        major.setText(""+b.getMajor());
        minor.setText(""+b.getMinor());
        nom.setText(b.getName());
        uuid.setText(b.getProximityUUID().toString());
        txPower.setText(""+b.getTxPower());
        rssi.setText(""+b.getRssi());
        timesstamp.setText(""+b.getTimestamp());
        distance.setText(String.format("%.2f mètre",b.getAccuracy()));
        if (b.getName().equals("estimote")) {
            imageBeacon.setImageResource(R.drawable.estimote_logo);
            batterie.setText("???");
            batterie.setTextColor(Color.RED);
            beaconID.setText("???");
            beaconID.setTextColor(Color.RED);
            mdp.setText("???");
            mdp.setTextColor(Color.RED);
        } else if (b.getName().equals("Kontakt")) {
            imageBeacon.setImageResource(R.drawable.kontakt_io_logo);
            batterie.setText(b.getBatteryPower()+" %");
            batterie.setTextColor(Color.WHITE);
            beaconID.setText(b.getBeaconUniqueId());
            beaconID.setTextColor(Color.GRAY);
            mdp.setText(String.valueOf(b.getPassword()));
            mdp.setTextColor(Color.WHITE);
        } else {
            imageBeacon.setImageResource(R.drawable.ibeacon_logo);
            batterie.setText("???");
            batterie.setTextColor(Color.RED);
            beaconID.setText("???");
            beaconID.setTextColor(Color.RED);
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
