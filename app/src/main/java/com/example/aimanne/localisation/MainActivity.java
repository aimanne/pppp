package com.example.aimanne.localisation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    int[]viewCoords=new int[2];
    TextView text = null;
    String valeurs;
    /*Dans cette partie on calcul la distance du mobile à la borne WIFI*/
    public double calculateDistance(double levelInDb, double freqInMhz){
        double exp=(27.55-(20*Math.log10(freqInMhz))+Math.abs(levelInDb))/20.0;
         return Math.pow(10.0,exp);/*cela permet de retourner la distance*/
    }/*levelInDb : C'est la puissance du signal reçu
       freqInMhz : Fréquence du signal reçu*/

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R .layout.activity_main);
        text=(TextView)findViewById(R.id.textView1);
        ImageView image=(ImageView)findViewById(R.id.imageView);
        image.getLocationOnScreen(viewCoords);
        /*Lorsque l'on clique sur un endroit sur notre plan, on affiche les coordonnées de cette endroit, et on calcul la distance du point d'accès */
        image.setOnTouchListener(new View.OnTouchListener(){
            @Override
                    public boolean onTouch(View v, MotionEvent event){
                        int touchX=(int)event.getX(); /* Enregistre dans la variables les coordonnées de X et Y*/
                        int touchY=(int)event.getY();
                        int imageX=touchX-viewCoords[0];/*Delimite les parties de la carte afin que l'on recupere les coordonnées uniquement sur le plan*/
                        int imageY=touchY-viewCoords[1];
                        valeurs="Position X :"+imageX+"Position Y :"+imageY;
                        text.setText(valeurs);
                        /* Dans cette partie on effectue un Scan WIFI afin d'afficher les résultats suivant : @MAC de la borne, Distance en m*/
                        final WifiManager wifi=(WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                        registerReceiver(new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                List<ScanResult>results=wifi.getScanResults();
                                int rssi=100; /*valeurs de test*/
                                int chan=1;
                                String mac="";
                                /* dans cette partie, le prog va rechercher uniquement le BSSID etudiant paris 12 et va nous donner la distance*/
                                for(ScanResult s:results)
                                {
                                    if(s.SSID.equals("Etudiants-Paris12")&Math.abs(s.level)<rssi)
                                        rssi=Math.abs(s.level);/*Recupere le RSSI*/
                                        mac=s.BSSID; /*Recupere le BSSID*/
                                        chan=s.frequency;/*recupere la frequence*/
                                }
                                DecimalFormat df =new DecimalFormat("#,##");
                                /* Dans cette partie le programme se base uniquement sur le Point d'Accès Etudiant Paris12. Puis il nous donne l'adresse MAC, le BSSID, la distance du signal, et le canal*/
                                text.setText("Etudiants-Paris12 BSSID :"+mac+"RSSI :"+rssi+",Distance :"+ df.format(calculateDistance((double)rssi, chan))+"m --"+"Canal :"+chan);
                                text.setText("Canal :"+chan);
                            }

                        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                wifi.startScan();
                return true;

            }
        });
    }
}