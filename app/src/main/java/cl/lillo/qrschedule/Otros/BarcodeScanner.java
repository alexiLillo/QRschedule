package cl.lillo.qrschedule.Otros;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.sourceforge.jtds.jdbc.DateTime;
import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import cl.lillo.qrschedule.R;

/**
 * Created by kvprasad on 10/3/2015.
 */
public class BarcodeScanner extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;

    private Button scanButton;
    private ImageScanner scanner;

    private boolean barcodeScanned = false;
    private boolean previewing = true;

    private ArrayList<String> lista = new ArrayList<>();
    private Object[] listaFinal = new Object[0];
    private int largo;

    private TextView titulo;
    private boolean entrada;

    static {
        System.loadLibrary("iconv");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_scanner);

        Bundle bundle = this.getIntent().getExtras();
        entrada = bundle.getBoolean("entrada");

        titulo = (TextView) findViewById(R.id.txttitulo);

        if (entrada) {
            titulo.setText("Escanear código: Entrada");
            titulo.setBackgroundColor(Color.parseColor("#4caf50"));
        } else {
            titulo.setText("Escanear código: Salida");
            titulo.setBackgroundColor(Color.parseColor("#d32f2f"));
        }

        initControls();
    }

    private void initControls() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        // Instance barcode scanner
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        mPreview = new CameraPreview(BarcodeScanner.this, mCamera, previewCb,
                autoFocusCB);
        FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
        preview.addView(mPreview);

        scanButton = (Button) findViewById(R.id.ScanButton);

        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //if (barcodeScanned) {
                //  barcodeScanned = false;
                //mCamera.setPreviewCallback(previewCb);
                //mCamera.startPreview();
                //previewing = true;
                //mCamera.autoFocus(autoFocusCB);
                //}
                eventoVolver(v);
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            releaseCamera();
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            //if (previewing)
            // mCamera.autoFocus(autoFocusCB);
        }
    };

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);

            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();

                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {

                    Log.i("<<<<<<Asset Code>>>>> ",
                            "<<<<Bar Code>>> " + sym.getData());
                    String scanResult = sym.getData().trim();

                    //showAlertDialog(scanResult);
                    //pop();
                    lista.add(scanResult);
                    Set<String> lista2 = new HashSet<>(lista);
                    listaFinal = lista2.toArray();

                    if (largo < listaFinal.length) {
                        pop();
                        largo = listaFinal.length;
                        Calendar c = Calendar.getInstance();
                        int day = c.get(Calendar.DAY_OF_MONTH);
                        String dia = "" + day;
                        int month = c.get(Calendar.MONTH) + 1;
                        String mes = "" + month;
                        int year = c.get(Calendar.YEAR);

                        if (day < 10)
                            dia = "0" + day;
                        if (month < 10)
                            mes = "0" + mes;

                        String fecha = dia + "/" + mes + "/" + year;
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int min = c.get(Calendar.MINUTE);
                        String hora = hour + ":" + min;
                        if (entrada)
                            showAlertDialog("Entrada: " + scanResult + "\nFecha: " + fecha + "\nHora:" + hora);
                        else
                            showAlertDialog("Salida: " + scanResult + "\nFecha: " + fecha + "\nHora:" + hora);
                    }

                    barcodeScanned = true;

                    if (barcodeScanned) {
                        barcodeScanned = false;
                        mCamera.setPreviewCallback(previewCb);
                        mCamera.startPreview();
                        previewing = true;
                        mCamera.autoFocus(autoFocusCB);
                        System.out.println(lista2);
                    }

                    break;
                }
            }
        }
    };

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    private void pop() {
        MediaPlayer mp = MediaPlayer.create(this, R.raw.pop);
        mp.start();
    }

    private void showAlertDialog(String message) {

        new AlertDialog.Builder(this)
                //.setTitle(getResources().getString(R.string.app_name))
                .setTitle("Registrar asistencia")
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(BarcodeScanner.this, "Asistencia registrada", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(BarcodeScanner.this, "Asistencia no registrada", Toast.LENGTH_SHORT).show();
                        largo = 0;

                    }
                }).show();
    }

    public void eventoVolver(View view) {
        super.onBackPressed();
        previewing = false;
        mPreview.getHolder().removeCallback(mPreview);
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCameraInstance();
    }
}
