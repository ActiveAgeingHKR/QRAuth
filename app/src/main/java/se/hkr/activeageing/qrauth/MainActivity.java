package se.hkr.activeageing.qrauth;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_QR = 436;
    private TextView mTextView;
    private WebView mWebView;
    private final String QRServer = "www.example.com"; //TODO: change this to the correct server

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.tv_main_qr);
        mWebView = (WebView) findViewById(R.id.wv_main);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem subMenu1Item = menu.getItem(0);
        final Drawable camera = getResources().getDrawable(R.drawable.ic_camera);
        camera.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        subMenu1Item.setIcon(camera);
        subMenu1Item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_camera:
               callQRApp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void callQRApp() {
        try {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
            startActivityForResult(intent, REQUEST_QR);
        } catch (Exception e) {
            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            startActivity(marketIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_QR) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                mTextView.setText(contents);
                switch (checkURL(contents)){
                    case Correct:
                        mWebView.loadUrl(contents);
                        break;
                    case NotHTTP:
                        Toast.makeText(getApplicationContext(), "Not HTTP protocol", Toast.LENGTH_SHORT).show();
                        break;
                    case WrongServer:
                        Toast.makeText(getApplicationContext(), "Wrong Server, our server is: " + QRServer, Toast.LENGTH_SHORT).show();
                        break;
                }
            }else if(resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "QR Canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private CheckURL checkURL(String contents) {

        final String protocol = "http://";

        if (!contents.toLowerCase().startsWith(protocol)) {
            return CheckURL.NotHTTP;
        }else if (!contents.toLowerCase().substring(protocol.length(), protocol.length() + QRServer.length()).equalsIgnoreCase(QRServer)){
            return CheckURL.WrongServer;
        }else{
            return CheckURL.Correct;
        }
    }

    private enum CheckURL{
        Correct, NotHTTP, WrongServer
    }
}
