package buddisattva.speedtest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

//import akinaru撰寫的JSpeedTest Library來使用
import fr.bmartel.speedtest.ISpeedTestListener;
import fr.bmartel.speedtest.SpeedTestError;
import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;

public class MainActivity extends AppCompatActivity {

    TextView labelNetworkStatus;
    String stringNetworkStatus;
    TextView labelDownloadRate;
    double downloadRate;
    TextView labelUploadRate;
    double uploadRate;

    boolean isDownload = false;
    boolean isUpload = false;

    TextView labelDownloadTime;
    TextView labelUploadTime;
    TextView labelTotalTime;
    double downloadTime;
    double uploadTime;
    double totalTime;
    ProgressBar progressBarSpeedTest;
    Button buttonStart;

    boolean isFinished;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        //ConnectivityManager detect WiFi,Mobile o no Network
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiConn = networkInfo.isConnected();
        networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isMobileConn = networkInfo.isConnected();

        if(isWifiConn)
            stringNetworkStatus = "WiFi Network";
        else if(isMobileConn)
            stringNetworkStatus = "Mobile Network";
        else
            stringNetworkStatus = "No Network";

        labelNetworkStatus = (TextView)findViewById(R.id.labelNetworkStatus);
        labelNetworkStatus.setText(stringNetworkStatus);

        labelDownloadRate = (TextView)findViewById(R.id.labelDownloadRate);
        labelUploadRate = (TextView)findViewById(R.id.labelUploadRate);
        labelDownloadTime = (TextView)findViewById(R.id.labelDownloadTime);
        labelUploadTime = (TextView)findViewById(R.id.labelUploadTime);
        labelTotalTime = (TextView)findViewById(R.id.labelTotalTime);

        progressBarSpeedTest = (ProgressBar)findViewById(R.id.progressBarSpeedTest);

        progressBarSpeedTest.setScaleX(3f);
        progressBarSpeedTest.setScaleY(4f);


        buttonStart = (Button)findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                labelDownloadRate.setText("");
                labelUploadRate.setText("");
                labelDownloadTime.setText("");
                labelUploadTime.setText("");
                labelTotalTime.setText("");


                new SpeedTestTask().execute();
                isFinished = false;
                buttonStart.setEnabled(false);
                buttonStart.setText("Testeando");
            }
        });
        Button btn_reporte = (Button) findViewById(R.id.reporte_btn);
        btn_reporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ReporteActivity.class);
                if((labelDownloadRate.getText().length() > 0) && (labelUploadRate.getText().length()> 0)){
                    intent.putExtra("descarga",labelDownloadRate.getText() );
                    intent.putExtra("subida",labelDownloadRate.getText() );
                    startActivity(intent);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setIcon(R.drawable.attention);
                    builder.setMessage("Realizar test antes de reportar");
                    builder.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        } });
                    AlertDialog dialog = builder.create();
                    dialog.setIcon(R.drawable.attention);
                    dialog.show();

                }


            }
        });

    }

    //AsyncTask
    class SpeedTestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            final SpeedTestSocket speedTestSocket = new SpeedTestSocket();

            //JSpeedTest
            speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

                @Override //Download
                public void onDownloadFinished(SpeedTestReport report) {
                    downloadRate = report.getTransferRateBit().doubleValue()/ 1024;

                    downloadTime = ((double)report.getReportTime() - (double)report.getStartTime()) / 1000;
                    totalTime = uploadTime + downloadTime;
                    //onProgressUpdate
                    publishProgress(String.format("%.2f kbps" , downloadRate), downloadTime + " seg" ,
                            String.format("%.2f seg" ,totalTime), "100");

                    //Upload
                    speedTestSocket.startUpload("cdn.speedof.me", "/", 1000000);
                }

                @Override
                public void onDownloadError(SpeedTestError speedTestError, String errorMessage) {

                }

                @Override
                public void onUploadFinished(SpeedTestReport report) {

                    isFinished = true;

                    uploadRate = report.getTransferRateBit().doubleValue() / 1024;
                    uploadTime = ((double)report.getReportTime() - (double)report.getStartTime()) / 1000;
                    totalTime = uploadTime + downloadTime;
                    publishProgress(String.format("%.2f kbps" , uploadRate), uploadTime + " seg" ,
                            String.format("%.2f seg" ,totalTime), 100 + "");
                }

                @Override
                public void onUploadError(SpeedTestError speedTestError, String errorMessage) {

                }

                @Override
                public void onDownloadProgress(float percent, SpeedTestReport report) {
                    isDownload = true;
                    isUpload = false;

                    downloadRate = report.getTransferRateBit().doubleValue() / 1024;
                    downloadTime = ((double)report.getReportTime() - (double)report.getStartTime()) / 1000;
                    totalTime = uploadTime + downloadTime;
                    publishProgress(String.format("%.2f kbps" , downloadRate), downloadTime + " seg",
                            String.format("%.2f seg" ,totalTime),(int)percent + "");
                }

                @Override
                public void onUploadProgress(float percent, SpeedTestReport report) {

                    isDownload = false;
                    isUpload = true;

                    uploadRate = report.getTransferRateBit().doubleValue()/ 1024;
                    uploadTime = ((double)report.getReportTime() - (double)report.getStartTime()) / 1000;
                    totalTime = uploadTime + downloadTime;
                    publishProgress(String.format("%.2f kbps" , uploadRate), uploadTime + " seg",
                            String.format("%.2f seg" ,totalTime), (int)percent + "");
                }

                @Override
                public void onInterruption() {
                    // AsyncTask被強迫終止時要做啥
                }
            });
            //開始進行Download測速，Upload測速的啟動指令放在onDownloadFinished生命週期中
            speedTestSocket.startDownload("cdn.speedof.me", "/sample8192k.bin?r=0.8775063492053139");

            return null;
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            //Download測速時，只更新Download部分UI
            if(isDownload) {
                labelDownloadRate.setText(values[0]);
                labelDownloadTime.setText(values[1]);
                labelTotalTime.setText(values[2]);
                progressBarSpeedTest.setProgress(Integer.parseInt(values[3]));
            }
            //Upload測速時，只更新Upload部分UI
            else if(isUpload) {
                labelUploadRate.setText(values[0]);
                labelUploadTime.setText(values[1]);
                labelTotalTime.setText(values[2]);
                progressBarSpeedTest.setProgress(Integer.parseInt(values[3]));
            }

            //測速全部完成時，讓按鈕再度可以按
            if(isFinished) {
                buttonStart.setText("Testear");
                buttonStart.setEnabled(true);
            }
        }
    }
}