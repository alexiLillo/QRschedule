package cl.lillo.qrschedule;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import cl.lillo.qrschedule.Otros.BarcodeScanner;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void entrada(View view) {
        Intent intent = new Intent(this, BarcodeScanner.class);
        intent.putExtra("entrada", true);
        startActivity(intent);
    }

    public void salida(View view) {
        Intent intent = new Intent(this, BarcodeScanner.class);
        intent.putExtra("entrada", false);
        startActivity(intent);
    }
}
