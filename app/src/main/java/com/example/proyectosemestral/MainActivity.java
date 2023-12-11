package com.example.proyectosemestral;

import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //declarando los controles a utilizar
    TextView  TvMinutosDelCiclo,TvNumciclos,TvTiempo,TvTiempo2;
    EditText EtNumciclos;
    Button BtnMinutosTrabajo,BtnMinutosDescanso,BtnPlayPause,BtnReset;
    ImageButton BtnSalir;
    ProgressBar PBTiempo;
    //variables globales
    private int minutoSeleccionado,minutoSeleccionadoDescanso,ciclosRestantes,cicloCentinela;
    int centinela=1;
    String tiempo,tiempo2;
    private CountDownTimer cCountDownTimer,cCountDownTimer2;
    private boolean cTimerRunning,dTimerRunning;
    private long TiempoEnMilisegundos,TiempoInicial,TiempoInicialDescanso,TiempoDescansoEnMilisegundos,TiempoInicialTrabajando,TiempoTrabajandoenMilisegundos;
    //todo que se completen los ciclos
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ciclosRestantes = 0;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //recibiendo valores de los controles
        TvNumciclos=findViewById(R.id.TvNumciclos);
        TvMinutosDelCiclo=findViewById(R.id.TvMinutosDelCiclo);
        EtNumciclos=findViewById(R.id.EtNumciclos);
        BtnMinutosTrabajo=findViewById(R.id.BtnMinutosTrabajo);
        BtnSalir=findViewById(R.id.btnSalir);
        PBTiempo=findViewById(R.id.PBTiempo);
        BtnMinutosDescanso=findViewById(R.id.BtnMinutosDescanso);
        TvTiempo=findViewById(R.id.TvTiempo);
        TvTiempo2=findViewById(R.id.TvTiempo2);


        BtnReset=findViewById(R.id.BtnReset);
        BtnPlayPause=findViewById(R.id.BtnPlayPause);
        BtnSalir.setOnClickListener(new View.OnClickListener() {//para cerrar la aplicacion
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });
        //cuando se pulsa el boton reset
        BtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(centinela==1)
                {
                    ResetearTemporizador();

                }
                else {
                    ResetearTemporizador2();


                }

            }
        });



        //cuando se pulsan el boton para los minutos de descanso
        BtnMinutosDescanso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //llama la funcion tiempo pasandole un valor de 0 para indicarle que es El boton de minutos de descanso
                TiempoDescanso();
            }
        });

        //cuando se pulsan los botones para los minutos del trabajo
        BtnMinutosTrabajo.setOnClickListener(new View.OnClickListener() {//llama la funcion tiempo pasandole un valor de 1 para indicarle que es el boton de minutos de trabajo
            @Override
            public void onClick(View v) {
                Tiempo();

            }
        });
        //probando nuevo metodo para cambiar el texto en tiempo real
        EtNumciclos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Este método se llama para notificar que el texto en EtNumciclos está a punto de cambiar
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Este método se llama para notificar que el texto en EtNumciclos ha cambiado
                String numSesionesStr = charSequence.toString();
                if(numSesionesStr.isEmpty())
                {
                    TvNumciclos.setText("0");

                }
                else {
                    try {
                        int numSesiones = Integer.parseInt(numSesionesStr);
                        TvNumciclos.setText(numSesionesStr);
                        cicloCentinela = numSesiones;
                    } catch (NumberFormatException e) {
                        // Manejar la excepción si el texto no es un número válido o si está vacio
                        // Puedes mostrar un mensaje de error o tomar otras medidas apropiadas.
                    }
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Este método se llama después de que el texto en EtNumciclos ha cambiado
            }
        });
        //tiempo real
        BtnMinutosTrabajo.addTextChangedListener(new TextWatcher() {//para revisar si se cambio y agregarlo de una vez al tiempo
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                String numMinutosStr= s.toString();
                int minutos=Integer.parseInt(numMinutosStr);
                int seconds=0;
                int horas=0;
                String formatoTiempo=String.format(Locale.getDefault(),"%02d:%02d:%02d",horas,minutos,seconds);
                TvTiempo.setText(formatoTiempo);
                tiempo=formatoTiempo;

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //tiempo real
        BtnMinutosDescanso.addTextChangedListener(new TextWatcher() {//para revisar si se cambio y agregarlo de una vez al tiempo
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                String numMinutosStr= s.toString();
                int minutos=Integer.parseInt(numMinutosStr);
                int seconds=0;
                int horas=0;
                String formatoTiempo=String.format(Locale.getDefault(),"%02d:%02d:%02d",horas,minutos,seconds);
                TvTiempo2.setText(formatoTiempo);
                tiempo2=formatoTiempo;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //si le doy play o pausa
        BtnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(cTimerRunning){
                    //volver a activar los botones al quitar la pausa
                    BtnMinutosTrabajo.setEnabled(true);
                    BtnMinutosDescanso.setEnabled(true);
                    EtNumciclos.setEnabled(true);

                    PausarTemporizador();
                }
                else if(dTimerRunning){
                    BtnMinutosTrabajo.setEnabled(true);
                    BtnMinutosDescanso.setEnabled(true);
                    EtNumciclos.setEnabled(true);

                    PausarTemporizador2();

                }

                else
                {
                    //recoger los datos de los botones en forma de cadena
                    String numCiclosStr=EtNumciclos.getText().toString();
                    String minutosDescansoStr=BtnMinutosDescanso.getText().toString();
                    String minutosTrabajoStr=BtnMinutosTrabajo.getText().toString();
                    //si alguno de los campos estan vacios no deja continuar y despliega el mensaje
                    if(numCiclosStr.isEmpty()||minutosTrabajoStr.isEmpty()||minutosDescansoStr.isEmpty())
                    {
                        Toast.makeText(MainActivity.this,"Rellena los campos vacios",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if(centinela==1)
                        {
                            TvNumciclos.setText(String.valueOf(cicloCentinela));

                            ComenzarTemporizador();


                        }
                        else{
                            ComenzarTemporizador2();
                        }
                        //desactivamos los botones cuando esta corriendo el temporizador
                        BtnMinutosTrabajo.setEnabled(false);
                        BtnMinutosDescanso.setEnabled(false);
                        EtNumciclos.setEnabled(false);

                    }
                }

            }
        });
    }
    private void TiempoDescanso() {
        //se intento con el timepicker pero el time picker mostraba horas y no combiene con el actual
        // Crear un nuevo NumberPicker
        NumberPicker minuteNumberPicker2 = new NumberPicker(this);
        // Configurar el rango de minutos (de 0 a 59)
        minuteNumberPicker2.setMinValue(1);
        minuteNumberPicker2.setMaxValue(60);
        // Mostrar el NumberPicker en un AlertDialog
        new AlertDialog.Builder(this)
                .setTitle("Seleccione minutos")
                .setView(minuteNumberPicker2)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Obtener el valor seleccionado del NumberPicker
                        minutoSeleccionadoDescanso = minuteNumberPicker2.getValue();

                        // Actualizar el texto en función del botón presionado
                        BtnMinutosDescanso.setText(String.valueOf(minutoSeleccionadoDescanso));
                        TiempoInicialDescanso=minutoSeleccionadoDescanso*60*1000;
                        TiempoDescansoEnMilisegundos=minutoSeleccionadoDescanso*60*1000;
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
    private void Tiempo() {
        //se intento con el timepicker pero el time picker mostraba horas y no combiene con el actual
        // Crear un nuevo NumberPicker
        NumberPicker minuteNumberPicker = new NumberPicker(this);
        // Configurar el rango de minutos (de 0 a 59)
        minuteNumberPicker.setMinValue(1);
        minuteNumberPicker.setMaxValue(60);
        // Mostrar el NumberPicker en un AlertDialog
        new AlertDialog.Builder(this)
                .setTitle("Seleccione minutos")
                .setView(minuteNumberPicker)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Obtener el valor seleccionado del NumberPicker
                        minutoSeleccionado = minuteNumberPicker.getValue();

                        // Actualizar el texto en función del botón presionado
                        BtnMinutosTrabajo.setText(String.valueOf(minutoSeleccionado));
                        TvMinutosDelCiclo.setText(String.valueOf(minutoSeleccionado)+"m");
                        TiempoInicial=minutoSeleccionado*60*1000;
                        TiempoEnMilisegundos=minutoSeleccionado*60*1000;

                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }



    private void PausarTemporizador(){ //encargada de pausar el temporizador
        cCountDownTimer.cancel();
        cTimerRunning=false;
        BtnPlayPause.setText("PLAY");
        BtnReset.setVisibility(View.VISIBLE);
    }
    private void PausarTemporizador2(){
        cCountDownTimer2.cancel();
        dTimerRunning=false;
        BtnPlayPause.setText("PLAY");
        BtnReset.setVisibility(View.VISIBLE);
    }
    private void ComenzarTemporizador(){
        PBTiempo.setProgressDrawable(getResources().getDrawable(R.drawable.circulo));//para poner la linea de la barra en el color amarillo
        centinela=1;
        ciclosRestantes = Integer.parseInt(TvNumciclos.getText().toString());
        ciclosRestantes--;
        TvNumciclos.setText(String.valueOf(ciclosRestantes));

        cCountDownTimer=new CountDownTimer(TiempoEnMilisegundos,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                TiempoEnMilisegundos = millisUntilFinished;//agarra el valor nuevo restante de los milisegundos
                ActualizarTiempo();
            }
            @Override
            public void onFinish() {
                cTimerRunning=false;
                BtnPlayPause.setText("PLAY");
                BtnPlayPause.setVisibility(View.VISIBLE);
                BtnReset.setVisibility(View.VISIBLE);
                BtnMinutosTrabajo.setEnabled(false);
                BtnMinutosDescanso.setEnabled(false);
                EtNumciclos.setEnabled(false);
                Alarma();
                cicloCentinela--;
                ComenzarTemporizador2();



            }
        }.start();
        cTimerRunning=true;
        BtnPlayPause.setText("PAUSE");
        BtnReset.setVisibility(View.INVISIBLE);
    }
    private void ComenzarTemporizador2(){
        PBTiempo.setProgressDrawable(getResources().getDrawable(R.drawable.circuloazul));//para poner la linea de la barra en el color azul
        centinela=2;

        cCountDownTimer2=new CountDownTimer(TiempoDescansoEnMilisegundos,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                TiempoDescansoEnMilisegundos = millisUntilFinished;//agarra el valor nuevo restante de los milisegundos
                ActualizarTiempo2();
            }
            @Override
            public void onFinish() {
                dTimerRunning=false;
                BtnPlayPause.setText("PLAY");
                BtnPlayPause.setVisibility(View.VISIBLE);
                BtnReset.setVisibility(View.VISIBLE);
                BtnMinutosTrabajo.setEnabled(false);
                BtnMinutosDescanso.setEnabled(false);
                EtNumciclos.setEnabled(false);
                Alarma();
                centinela=1;
                if(ciclosRestantes>0)
                {
                    ResetearTemporizador();
                    ComenzarTemporizador();
                }
                else {
                    BtnMinutosTrabajo.setEnabled(true);
                    BtnMinutosDescanso.setEnabled(true);
                    EtNumciclos.setEnabled(true);

                    //vuelve todo a la normalidad
                    ciclosRestantes = Integer.parseInt(EtNumciclos.getText().toString());
                    TvNumciclos.setText(String.valueOf(ciclosRestantes));
                    TvTiempo.setText(tiempo);
                    TvTiempo2.setText(tiempo2);
                    TiempoInicial=minutoSeleccionado*60*100;
                    TiempoEnMilisegundos=minutoSeleccionado*60*100;
                    TiempoInicialDescanso=minutoSeleccionadoDescanso*60*100;
                    TiempoDescansoEnMilisegundos=minutoSeleccionadoDescanso*60*100;
                    PBTiempo.setProgress(0);
                    cicloCentinela=Integer.parseInt(EtNumciclos.getText().toString());



                }



            }
        }.start();
        dTimerRunning=true;
        BtnPlayPause.setText("PAUSE");
        BtnReset.setVisibility(View.INVISIBLE);
    }
    private void ActualizarTiempo(){
        int minutes=(int)(TiempoEnMilisegundos/1000)/60;//transformar los milisegundos en minutos
        int seconds=(int)(TiempoEnMilisegundos/1000)%60;//que te de el resto de lo que falta en segundos
        int horas=00;
        String formatoTiempo=String.format(Locale.getDefault(),"%02d:%02d:%02d",horas,minutes,seconds);
        TvTiempo.setText(formatoTiempo);
        LlenadoDeBarra();
    }
    private void ActualizarTiempo2(){
        int minutes=(int)(TiempoDescansoEnMilisegundos/1000)/60;//transformar los milisegundos en minutos
        int seconds=(int)(TiempoDescansoEnMilisegundos/1000)%60;//que te de el resto de lo que falta en segundos
        int horas=00;
        String formatoTiempo=String.format(Locale.getDefault(),"%02d:%02d:%02d",horas,minutes,seconds);
        TvTiempo2.setText(formatoTiempo);
        LlenadoDeBarra2();
    }

    private void ResetearTemporizador()//para resetear el temporizador
    {TiempoEnMilisegundos=TiempoInicial;
        ActualizarTiempo();
        BtnReset.setVisibility(View.INVISIBLE);
        BtnPlayPause.setVisibility(View.VISIBLE);
        PBTiempo.setProgress(0);
        ResetearTemporizador2();
    }
    private void ResetearTemporizador2()//para resetear el temporizador
    {TiempoDescansoEnMilisegundos=TiempoInicialDescanso;
        ActualizarTiempo2();
        BtnReset.setVisibility(View.INVISIBLE);
        BtnPlayPause.setVisibility(View.VISIBLE);
        PBTiempo.setProgress(0);
    }
    private void LlenadoDeBarra(){

        int progreso = (int) (((double) TiempoEnMilisegundos / TiempoInicial) * 100);

        PBTiempo.setProgress(progreso);

    }
    private void LlenadoDeBarra2() {
        int progreso = (int) ((1.0 - (double) TiempoDescansoEnMilisegundos / TiempoInicialDescanso) * 100);
        PBTiempo.setProgress(progreso);
    }



    private void Alarma() {

        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.sonido1);//sonido1 esta en la carpeta raw

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();

            }
        });

        mediaPlayer.start();
    }









}