package com.example.amechy.mechinealejandrotrabajoficherosacda;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class principalActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etxFicheroImagen, etxFicheroFrase;
    Button btnDescargar;
    ImageView imgImagen;
    TextView txvResultadoFrase;
    ArrayList<String> listaUrlImagenes;
    private ArrayList<String> frases;
    private int imagenActual = 0;
    private int fraseActual = 0;

    private static final int MAX_TIMEOUT = 2000;
    private static final int RETRIES = 1;
    private static final int TIMEOUT_BETWEEN_RETRIES = 5000;

    private boolean exitoFicheroImagenes = false;
    private boolean exitoFicheroFrases = false;
    private long intervalo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        etxFicheroFrase = (EditText) findViewById(R.id.etxFicheroFrase);
        etxFicheroImagen = (EditText) findViewById(R.id.etxFicheroImg);
        imgImagen = (ImageView) findViewById(R.id.imgImage);

        listaUrlImagenes = new ArrayList<String>();
        frases = new ArrayList<String>();

        txvResultadoFrase = (TextView) findViewById(R.id.txvResultadoFrase);
        btnDescargar = (Button) findViewById(R.id.btnDescargar);
        btnDescargar.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        descargarImagenes();
        descargarFrases();
        leerIntervalo();


        if (view == btnDescargar) {
            iniciarContador();
        }

    }

    private void mostrarImagenYFrase() {

        if (exitoFicheroImagenes && exitoFicheroFrases) {
            btnDescargar.setEnabled(false);

            txvResultadoFrase.setText(frases.get(fraseActual % (frases.size() - 1)));
            fraseActual++;

            Picasso.with(principalActivity.this)
                    .load(listaUrlImagenes.get(imagenActual % (listaUrlImagenes.size() - 1)))
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder_error).
                    into(imgImagen);
            imagenActual++;


            btnDescargar.setEnabled(true);
        }

    }

    private void leerIntervalo() {

        try {

            InputStream fileInputStream = getResources().openRawResource(R.raw.intervalo);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            intervalo = (Long.parseLong(bufferedReader.readLine())) * 1000;

        } catch (Exception e) {
            String message = "No se ha encontrado el fichero Intervalo.txt";
            Toast.makeText(principalActivity.this, message, Toast.LENGTH_SHORT).show();
        }

    }

    private void descargarImagenes() {

        final ProgressDialog progreso = new ProgressDialog(this);

        final AsyncHttpClient cliente = new AsyncHttpClient();
        cliente.setTimeout(MAX_TIMEOUT);
        cliente.setMaxRetriesAndTimeout(RETRIES, TIMEOUT_BETWEEN_RETRIES);

        if (URLUtil.isValidUrl(etxFicheroImagen.getText().toString())) {
            cliente.get(etxFicheroImagen.getText().toString(), new FileAsyncHttpResponseHandler(this) {

                @Override
                public void onStart() {
                    progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progreso.setMessage("Descargando fichero de imagenes");
                    progreso.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            cliente.cancelAllRequests(true);
                        }
                    });
                    progreso.show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                    progreso.dismiss();
                    Toast.makeText(principalActivity.this, "No se puede descargar el fichero de imagenes: " + statusCode, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, File file) {
                    String linea;
                    try {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        while ((linea = bufferedReader.readLine()) != null) {
                            listaUrlImagenes.add(linea);
                        }
                        bufferedReader.close();
                    } catch (FileNotFoundException e) {
                        Toast.makeText(principalActivity.this, "Fichero no encontrado.", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(principalActivity.this, "Error de Entrada/Salida", Toast.LENGTH_SHORT).show();
                    }
                    exitoFicheroImagenes = true;
                    progreso.dismiss();
                }
            });

        } else {
            Toast.makeText(principalActivity.this, "Url del fichero invalida..", Toast.LENGTH_SHORT).show();
        }
    }

    private void descargarFrases() {

        final ProgressDialog progreso = new ProgressDialog(this);

        final AsyncHttpClient cliente = new AsyncHttpClient();
        cliente.setTimeout(MAX_TIMEOUT);
        cliente.setMaxRetriesAndTimeout(RETRIES, TIMEOUT_BETWEEN_RETRIES);

        if (URLUtil.isValidUrl(etxFicheroFrase.getText().toString())) {
            cliente.get(etxFicheroFrase.getText().toString(), new FileAsyncHttpResponseHandler(this) {

                @Override
                public void onStart() {
                    progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progreso.setMessage("Descargando fichero frases...");
                    progreso.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            cliente.cancelAllRequests(true);
                        }
                    });
                    progreso.show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                    progreso.dismiss();
                    Toast.makeText(principalActivity.this, "No se puede descargar el fichero de frases...: " + statusCode, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, File file) {
                    String linea;
                    try {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        while ((linea = bufferedReader.readLine()) != null) {
                            frases.add(linea);
                        }
                        bufferedReader.close();
                    } catch (FileNotFoundException e) {
                        Toast.makeText(principalActivity.this, "Fichero no encontrado.", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(principalActivity.this, "Error de Entrada/Salida", Toast.LENGTH_SHORT).show();
                    }
                    exitoFicheroFrases = true;
                    progreso.dismiss();
                }
            });

        } else {
            Toast.makeText(principalActivity.this, "Url del fichero invalida", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Método que crea un contador para que se vayan cargando las frases e imágenes.
     */
    public void iniciarContador() {

        new CountDownTimer(999999, 4000) {

            @Override
            public void onTick(long l) {
                mostrarImagenYFrase();
            }

            @Override
            public void onFinish() {

            }

        }.start();
    }




}
