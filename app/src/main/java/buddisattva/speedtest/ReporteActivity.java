package buddisattva.speedtest;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by diego on 07/11/16.
 */
public class ReporteActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte);
        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("Reportar estado del servicio");
        actionBar.setDisplayHomeAsUpEnabled(true);
        Spinner empresa_spinner = (Spinner) findViewById(R.id.Empresa_spinner);
        String[] arraySpinner = new String[] { "Arnet", "Fibertel"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_spinner, arraySpinner);
        empresa_spinner.setAdapter(adapter);

        String descarga = (String) getIntent().getExtras().get("descarga");
        String subida  = (String) getIntent().getExtras().get("subida");
        TextView txSubida = (TextView) findViewById(R.id.txtSubida);
        txSubida.setText(subida);
        TextView txBajada = (TextView) findViewById(R.id.txtDescarga);
        txBajada.setText(descarga);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
