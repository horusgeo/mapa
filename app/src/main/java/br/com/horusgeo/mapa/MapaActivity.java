package br.com.horusgeo.mapa;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.EventListener;
import java.util.Map;


public class MapaActivity extends AppCompatActivity {
    WebView webview;

    FloatingActionButton floatingReturn;
    FloatingActionButton floatingLocation;
    FloatingActionButton floatingRegua;
    FloatingActionButton fabReguaNew;
    FloatingActionButton fabReguaCancel;
    FloatingActionButton floatingPin;
    FloatingActionButton floatingDesenho;
    FloatingActionButton fabPointsCancel;
    FloatingActionButton fabPointsNew;
    FloatingActionButton fabPointsOk;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);


        webview = (WebView) findViewById(R.id.webview);
        assert webview != null;
        webview.loadUrl("file:///android_asset/www/map.html");

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAllowFileAccess(true);

        webview.setWebViewClient(new WebViewClient() {


            @Override
            public void onPageFinished(WebView view, String url) {
                populateMap();
            }
        });

        webview.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });

        webview.addJavascriptInterface(new WebAppInterface(this), "Android");

        floatingReturn = (FloatingActionButton) findViewById(R.id.floatingReturn);
        floatingReturn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MapaActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        floatingLocation = (FloatingActionButton) findViewById(R.id.floatingLocation);
        floatingLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webview.loadUrl("javascript:findLocation()");
            }
        });

        /* ----------------------------------------------------------------------------------*/
        /* PIN*/

        floatingPin = (FloatingActionButton) findViewById(R.id.floatingPin);
        floatingPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webview.loadUrl("javascript:startPin()");
            }
        });

        /* ----------------------------------------------------------------------------------*/
        /* REGUA*/

        floatingRegua = (FloatingActionButton) findViewById(R.id.floatingRegua);
        floatingRegua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingReturn.setVisibility(View.INVISIBLE);
                floatingReturn.setClickable(false);

                fabReguaNew.setVisibility(View.VISIBLE);
                fabReguaNew.setClickable(true);

                fabReguaCancel.setVisibility(View.VISIBLE);
                fabReguaCancel.setClickable(true);
                clickRegua(true);
            }
        });

        fabReguaNew = (FloatingActionButton) findViewById(R.id.fabReguaNew);
        fabReguaNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webview.loadUrl("javascript:clickPinRegua()");
            }
        });


        fabReguaCancel = (FloatingActionButton) findViewById(R.id.fabReguaCancel);
        fabReguaCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingReturn.setVisibility(View.VISIBLE);
                floatingReturn.setClickable(false);
                fabReguaNew.setVisibility(View.INVISIBLE);
                fabReguaNew.setClickable(false);
                floatingReturn.setVisibility(View.VISIBLE);
                floatingReturn.setClickable(true);
                fabReguaCancel.setVisibility(View.INVISIBLE);
                fabReguaCancel.setClickable(false);
                clickRegua(false);
            }
        });

        /* ----------------------------------------------------------------------------------*/
        /*Pontos Propriedade*/
        floatingDesenho = (FloatingActionButton) findViewById(R.id.floatingDesenho);
        floatingDesenho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                floatingReturn.setVisibility(View.INVISIBLE);
                floatingReturn.setClickable(false);
                floatingDesenho.setVisibility(View.INVISIBLE);
                floatingDesenho.setClickable(false);
                floatingRegua.setVisibility(View.INVISIBLE);
                floatingRegua.setClickable(false);
                floatingPin.setVisibility(View.INVISIBLE);
                floatingPin.setClickable(false);



                fabPointsNew.setVisibility(View.VISIBLE);
                fabPointsNew.setClickable(true);

                fabPointsCancel.setVisibility(View.VISIBLE);
                fabPointsCancel.setClickable(true);

                fabPointsOk.setVisibility(View.VISIBLE);
                fabPointsOk.setClickable(true);

            }
        });

        fabPointsOk = (FloatingActionButton) findViewById(R.id.fabPointsOk);
        fabPointsOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        fabPointsCancel = (FloatingActionButton) findViewById(R.id.fabPointsCancel);
        fabPointsCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
        fabPointsNew = (FloatingActionButton)findViewById((R.id.fabPointsNew));
        fabPointsNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webview.loadUrl("javascript:clickPoints()");

            }
        });
    }

    private void clickRegua(Boolean which){
        if(which)
            webview.loadUrl("javascript:clickRegua()");
        else
            webview.loadUrl("javascript:closeRegua()");
    }

    public void populateMap() {

        fabReguaCancel.setVisibility(View.INVISIBLE);
        fabReguaCancel.setClickable(false);
        fabPointsCancel.setVisibility(View.INVISIBLE);
        fabPointsCancel.setClickable(false);
        fabPointsNew.setVisibility(View.INVISIBLE);
        fabPointsNew.setClickable(false);
        fabPointsOk.setVisibility(View.INVISIBLE);
        fabPointsOk.setClickable(false);

        webview.loadUrl("javascript:loadImg('/storage/extSdCard/www')");
        webview.loadUrl("javascript:loadImg('/storage/E84C-FF83/www')");
        //webview.loadUrl("javascript:loadKml()");
    }
}


class WebAppInterface {
    Context mContext;

    /**
     * Instantiate the interface and set the context
     */
    WebAppInterface(Context c) {
        mContext = c;
    }


    @JavascriptInterface
    public void paintProperty() {

    }
}

class LatLngPoints {
    public double lat;
    public double lng;
}

