package be.uchrony.test_altbeacon;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
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
 * Created by Abdelhalim on 23/02/2015.
 */
public class ListeDeBeacons extends BaseAdapter  {

    private final static String TAG_DEBUG = "TAG_DEBUG_ListeBeacons";
    private ArrayList<BeaconDevice> beacons;
    private LayoutInflater inflater;

    public ListeDeBeacons(Context context) {
        Log.d(TAG_DEBUG,"constructeur");
        this.inflater = LayoutInflater.from(context);
        this.beacons = new ArrayList<>();
    }

    public void remplacerLaListe(Collection<BeaconDevice> nouveausBeacons) {
        Log.d(TAG_DEBUG,"remplacerLaliste");
        this.beacons.clear();
        this.beacons.addAll(nouveausBeacons);
        Collections.sort(this.beacons);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        Log.d(TAG_DEBUG,"getCount");
        return beacons.size();
    }

    @Override
    public BeaconDevice getItem(int position) {
        Log.d(TAG_DEBUG,"getItem");
        return beacons.get(position);
    }

    @Override
    public long getItemId(int position) {
        Log.d(TAG_DEBUG,"getItemId");
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Log.d(TAG_DEBUG,"getView");
        view = inflateIfRequired(view, position, parent);
        bind(getItem(position), view);
        return view;
    }

    private void bind(BeaconDevice beacon, View view) {
        Log.d(TAG_DEBUG,"bind");
        ViewHolder holder = (ViewHolder) view.getTag();

        holder.macadresse.setText(String.format(" %s", beacon.getAddress()));
        holder.major.setText(Integer.toString(beacon.getMajor()));
        holder.minor.setText(Integer.toString(beacon.getMinor()));
        holder.mpower.setText(Integer.toString(beacon.getTxPower()));
        holder.rssi.setText(Double.toString(beacon.getRssi()));
        holder.uuid.setText(beacon.getProximityUUID().toString());
        holder.nomBeacon.setText(" "+beacon.getName());
        holder.distance.setText(getDistance(beacon));
        //holder.distance.setText(String.format("%.2f mètre",beacon.getProximity().name()));
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

    private View inflateIfRequired(View view, int position, ViewGroup parent) {
        Log.d(TAG_DEBUG,"inflateIfRequired");
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
