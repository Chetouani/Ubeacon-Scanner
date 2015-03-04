package be.uchrony.test_altbeacon;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kontakt.sdk.android.device.BeaconDevice;
import com.kontakt.sdk.core.Proximity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Représente une Liste de Beacons qui pourra être afficher dans Une ListeView.
 *
 * @author  Chetouani Abdelhalim
 * @version 0.1
 */
public class ListeDeBeacons extends BaseAdapter  {

    private final static String TAG_DEBUG = "TAG_DEBUG_ListeBeacons";
    private ArrayList<BeaconDevice> beacons;
    private LayoutInflater inflater;

    public ListeDeBeacons(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.beacons = new ArrayList<>();
    }

    public void remplacerLaListe(Collection<BeaconDevice> nouveausBeacons) {
        this.beacons.clear();
        this.beacons.addAll(nouveausBeacons);
        Collections.sort(this.beacons);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return beacons.size();
    }

    @Override
    public BeaconDevice getItem(int position) {
        return beacons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflateIfRequired(view, position, parent);
        bind(getItem(position), view);
        return view;
    }

    private void bind(BeaconDevice beacon, View view) {
        ViewHolder holder = (ViewHolder) view.getTag();

        holder.macadresse.setText(String.format(" %s", beacon.getAddress()));
        holder.major.setText(Integer.toString(beacon.getMajor()));
        holder.minor.setText(Integer.toString(beacon.getMinor()));
        holder.mpower.setText(Integer.toString(beacon.getTxPower()));
        holder.rssi.setText(Double.toString(beacon.getRssi()));
        holder.uuid.setText(beacon.getProximityUUID().toString());
        holder.nomBeacon.setText(" "+beacon.getName());
        holder.distance.setText(" "+beacon.getAccuracy());
        if ( beacon.getBatteryPower() > 0) {
            holder.niveauBatterie.setProgress(beacon.getBatteryPower());
            holder.niveauBatterie.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            holder.pourcentageBatterie.setText(beacon.getBatteryPower() + " %");
            holder.pourcentageBatterie.setTextColor(Color.GREEN);
        } else {
            holder.niveauBatterie.setProgress(0);
            holder.pourcentageBatterie.setText("??? %");
            holder.pourcentageBatterie.setTextColor(Color.RED);
            holder.niveauBatterie.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        }
    }

    private String getDistance(BeaconDevice bd) {
        if (bd.getProximity() == Proximity.FAR) {
            return "LOIN";
        } else if (bd.getProximity() == Proximity.IMMEDIATE) {
            return "Très proche";
        } else if (bd.getProximity() == Proximity.NEAR) {
            return "Proche";
        } else {
            return "Je sais pas :°)";
        }
    }

    private double getDistanceMetre(int txPower, double rssi) {
            double ratio_db = txPower - rssi;
            double ratio_linear = Math.pow(10, ratio_db / 10);

            double r = Math.sqrt(ratio_linear);
            return r;
    }

    private View inflateIfRequired(View view, int position, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.un_beacon, null);
            view.setTag(new ViewHolder(view));
        }
        return view;
    }


    static class ViewHolder {
        final TextView macadresse;
        final TextView major;
        final TextView minor;
        final TextView mpower;
        final TextView rssi;
        final TextView uuid;
        final TextView nomBeacon;
        final TextView distance;
        final ProgressBar niveauBatterie;
        final TextView pourcentageBatterie;

        ViewHolder(View view) {
            macadresse = (TextView) view.findViewById(R.id.macadresse);
            major = (TextView) view.findViewById(R.id.major);
            minor = (TextView)view.findViewById(R.id.minor);
            mpower = (TextView) view.findViewById(R.id.mpower);
            rssi = (TextView) view.findViewById(R.id.rssi);
            uuid = (TextView) view.findViewById(R.id.uuid);
            nomBeacon = (TextView) view.findViewById(R.id.nombeacon);
            distance = (TextView) view.findViewById(R.id.distanceBeacon);
            niveauBatterie = (ProgressBar) view.findViewById(R.id.niveau_batterie);
            pourcentageBatterie  = (TextView) view.findViewById(R.id.pourcent_batterie);
        }
    }
}
