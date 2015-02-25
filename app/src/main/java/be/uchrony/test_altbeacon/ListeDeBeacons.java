package be.uchrony.test_altbeacon;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by Abdelhalim on 23/02/2015.
 */
public class ListeDeBeacons extends BaseAdapter  {

    private final static String TAG_DEBUG = "TAG_DEBUG_ListeBeacons";
    private ArrayList<UBeacon> beacons;
    private LayoutInflater inflater;

    public ListeDeBeacons(Context context) {
        Log.d(TAG_DEBUG,"constructeur");
        this.inflater = LayoutInflater.from(context);
        this.beacons = new ArrayList<>();
    }

    public void remplacerLaListe(Collection<UBeacon> nouveausBeacons) {
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
    public Beacon getItem(int position) {
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

    private void bind(Beacon beacon, View view) {
        Log.d(TAG_DEBUG,"bind");
        ViewHolder holder = (ViewHolder) view.getTag();

        holder.macadresse.setText(String.format("%s", beacon.getBluetoothAddress()));
        holder.major.setText(Integer.toString(beacon.getId2().toInt()));
        holder.minor.setText(Integer.toString(beacon.getId3().toInt()));
        holder.mpower.setText(Integer.toString(beacon.getTxPower()));
        holder.rssi.setText(Integer.toString(beacon.getRssi()));
        holder.uuid.setText(beacon.getId1().toUuidString());
        holder.nomBeacon.setText(beacon.getBluetoothName());
        holder.distance.setText(String.format("%.2f mètre",beacon.getDistance()));

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

        ViewHolder(View view) {
            macadresse = (TextView) view.findViewById(R.id.macadresse);
            major = (TextView) view.findViewById(R.id.major);
            minor = (TextView)view.findViewById(R.id.minor);
            mpower = (TextView) view.findViewById(R.id.mpower);
            rssi = (TextView) view.findViewById(R.id.rssi);
            uuid = (TextView) view.findViewById(R.id.uuid);
            nomBeacon = (TextView) view.findViewById(R.id.nombeacon);
            distance = (TextView) view.findViewById(R.id.distanceBeacon);
        }
    }
}
