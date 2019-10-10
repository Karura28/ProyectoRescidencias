package com.halo.loginui2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import static java.lang.Math.toIntExact;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class UserActivity extends AppCompatActivity {
    PieChart pieChart;
    private final String DEVICE_ADDRESS="98:D3:32:20:CC:0C";
    //private final String DEVICE_ADDRESS="54:4A:16:3A:01:1E";
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    Button startButton , sendButton, clearButton, stopButton;
    TextView textView;
    EditText editText;
    boolean deviceConnected=false;
    Thread thread;
    byte buffer[];
    // int bufferPosition;
    boolean stopThread;
    int numero =0;
    int Dato ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

    startButton = findViewById(R.id.buttonStart);
    sendButton =    findViewById(R.id.buttonSend);
    clearButton =    findViewById(R.id.buttonClear);
    stopButton =     findViewById(R.id.buttonStop);
    editText =      findViewById(R.id.editText);
    textView =  findViewById(R.id.textView);
    pieChart = findViewById(R.id.PieChart);

        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(1f);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(android.R.color.white);
        pieChart.setTransparentCircleRadius(60f);

    int contenedor100 = 100;
    int faltante = 45;
    int restante = contenedor100-faltante;

    ArrayList<PieEntry> yValues = new ArrayList<>();
        yValues.add(new PieEntry((float) contenedor100, "Restante" +":" + contenedor100));
    // yValues.add(new PieEntry((float) contenedor100, "lleno"));
     yValues.add(new PieEntry((restante), "Faltante" + ":" + restante));


        pieChart.animateY(1500, Easing.EaseInBounce);

    PieDataSet dataSet = new PieDataSet(yValues, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

    PieData data = new PieData(dataSet);
        data.setValueTextColor(android.R.color.holo_orange_light);
        data.setValueTextSize(10f);
        pieChart.setData(data);

}




    public void setUiEnabled(boolean bool)
    {  startButton.setEnabled(!bool);
       //  sendButton.setEnabled(bool);
        stopButton.setEnabled(bool);
        textView.setEnabled(bool);

    }

    public boolean BTinit()
    {
        boolean found=false;
        BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
           // Toast.makeText(getApplicationContext(),"Device doesnt Support Bluetooth",Toast.LENGTH_SHORT).show();
        }
        if(!bluetoothAdapter.isEnabled())
        {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if(bondedDevices.isEmpty())
        {
           // Toast.makeText(getApplicationContext(),"Emparejar el dispositivo ",Toast.LENGTH_SHORT).show();
        }
        else
        {
            for (BluetoothDevice iterator : bondedDevices)
            {
                if(iterator.getAddress().equals(DEVICE_ADDRESS))
                {
                    device=iterator;
                    found=true;
                    break;
                }
            }
        }
        return found;
    }

    public boolean BTconnect()
    {
        boolean connected=true;
        try {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            connected=false;
        }
        if(connected)
        {
            try {
                outputStream=socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream=socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        return connected;
    }

    public void onClickStart(View view) {
        if(BTinit())
        {
            if(BTconnect())
            {
                setUiEnabled(true);
                deviceConnected=true;
                beginListenForData();
               Toast.makeText(getApplicationContext(),"Conectado!",Toast.LENGTH_SHORT).show();

            }

        }
    }

    void beginListenForData()
    {

        final Handler handler = new Handler();
        stopThread = false;
        buffer = new byte[1024];

        Thread thread  = new Thread(new Runnable()
        {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopThread)
                {
                    try
                    {
                        final int byteCount = inputStream.available();
                        if(byteCount > 0)
                        {
                            final byte[] rawBytes = new byte[byteCount];

                            inputStream.read(rawBytes);

                            // best, if you're using Java 7 (thanks to @ColinD):
                            final String decoded = new String(rawBytes, "UTF-8");

                            boolean isNumeric = decoded.chars().allMatch( Character::isDigit );

                            System.out.println(isNumeric);

                            //double doubleValue = Double.parseDouble(decoded);
                            //long longVal = (long)doubleValue ;
                           // System.out.println(longVal);


                           // final String data=new String(rawBytes,"UTF-8");
                          // final int i =  Integer.valueOf(decoded);
                          //  System.out.println(i + i - i - i);

                            handler.post(new Runnable() {
                                public void run() {

                                    textView.append(decoded);


                                    pieChart.setUsePercentValues(true);
                                    pieChart.getDescription().setEnabled(false);
                                    pieChart.setExtraOffsets(5, 10, 5, 5);

                                    pieChart.setDragDecelerationFrictionCoef(2f);

                                    pieChart.setDrawHoleEnabled(true);
                                    pieChart.setHoleColor(android.R.color.white);
                                    pieChart.setTransparentCircleRadius(61f);



                                    if (decoded == null) {
                                        System.out.println("CADENA VACIA");

                                    } else {
                                        decoded.trim();
                                        decoded.replaceAll(" ", "");
                                        Integer number =10;
                                      //  System.out.println(number);
                                        //float number = Float.parseFloat((data));

                                        double Total = number;
                                        double lleno = 100;
                                        double Actual = (Total);
                                        double Restante = ((lleno) - Actual);


                                        ArrayList<PieEntry> yValues = new ArrayList<>();
                                        //yValues.add(new PieEntry(Restante, "Restante" + Restante));
                                        yValues.add(new PieEntry((float) Actual, "Faltante" + Actual));
                                        yValues.add(new PieEntry((float) Restante, "Restantante" + Restante));
                                        //yValues.add(new PieEntry ((float) Double.parseDouble(String.valueOf((numeros)))),"");


                                        pieChart.animateY(900, Easing.EaseInCubic);

                                        PieDataSet dataSet = new PieDataSet(yValues, "");
                                        dataSet.setSliceSpace(3f);
                                        dataSet.setSelectionShift(5f);
                                        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

                                        PieData data = new PieData(dataSet);
                                        data.setValueTextColor(android.R.color.holo_purple);
                                        data.setValueTextSize(10f);
                                        pieChart.setData(data);
                                    }
                                }
                                    {






                                }

                            });

                        }

                    }
                    catch (IOException ex)
                    {
                        stopThread = true;
                    }
                }
            }
        });

        thread.start();
    }

    public void onClickSend(View view) {
        int Actualizar = 1;

        try {
            outputStream.write(Actualizar);


           setUiEnabled(false);
        } catch (IOException e) {
            e.printStackTrace();

        }

    }


    public void onClickStop(View view) throws IOException {
        stopThread = true;
        outputStream.close();
        inputStream.close();
        socket.close();
      setUiEnabled(false);
        deviceConnected=false;

       Toast.makeText(getApplicationContext(),"Desconectado",Toast.LENGTH_SHORT).show();

    }

    public void onClickClear(View view) {
        textView.setText("");
    }



}
