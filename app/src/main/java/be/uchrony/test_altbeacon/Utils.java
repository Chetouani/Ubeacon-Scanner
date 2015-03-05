package be.uchrony.test_altbeacon;

import android.util.Log;

import com.kontakt.sdk.android.device.BeaconDevice;

import java.util.List;

/**
 * Crée par Abdel le 5/03/15.
 */
public class Utils {

    public static void triCroissantDistance(List<BeaconDevice> lBeacon) {
        triBulleCroissant(lBeacon);
    }

    private static void triBulleCroissant(List<BeaconDevice> lBeacon) {
        int longueur = lBeacon.size();
        BeaconDevice tampon ;
        boolean permut;

        do {

            permut = false;
            for (int i = 0; i < longueur - 1; i++) {
                // Teste si 2 éléments successifs sont dans le bon ordre ou non
                if (lBeacon.get(i).getAccuracy() > lBeacon.get(i+1).getAccuracy() ) {
                    // s'ils ne le sont pas, on échange leurs positions
                    tampon = lBeacon.get(i);
                    lBeacon.set(i,lBeacon.get(i+1));
                    lBeacon.set(i+1,tampon);
                    permut = true;
                }
            }
        } while (permut);

    }

    /*
    public static void triBulleDecroissant(int tableau[]) {
        int longueur = tableau.length;
        int tampon = 0;
        boolean permut;

        do {
            // hypothèse : le tableau est trié
            permut = false;
            for (int i = 0; i < longueur - 1; i++) {
                // Teste si 2 éléments successifs sont dans le bon ordre ou non
                if (tableau[i] < tableau[i + 1]) {
                    // s'ils ne le sont pas, on échange leurs positions
                    tampon = tableau[i];
                    tableau[i] = tableau[i + 1];
                    tableau[i + 1] = tampon;
                    permut = true;
                }
            }
        } while (permut);
    }

    */
}
