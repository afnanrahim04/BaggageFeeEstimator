package com.example.assignment1;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;

public class MainActivity extends AppCompatActivity {

    private static final double TAX_RATE = 0.08;
    private static final double ECONOMY_LIMIT = 20.0;
    private static final double BUSINESS_LIMIT = 40.0;

    private EditText etWeight, etRate;
    private RadioGroup rgClass;
    private TextView tvExcess, tvCharge, tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowHomeEnabled(true);
            bar.setDisplayUseLogoEnabled(true);
            bar.setLogo(R.mipmap.ic_launcher);
        }

        etWeight = findViewById(R.id.etWeight);
        etRate = findViewById(R.id.etRate);
        rgClass = findViewById(R.id.rgClass);
        tvExcess = findViewById(R.id.tvExcess);
        tvCharge = findViewById(R.id.tvCharge);
        tvTotal = findViewById(R.id.tvTotal);

        Button btnCalculate = findViewById(R.id.btnCalculate);
        Button btnReset = findViewById(R.id.btnReset);

        btnCalculate.setOnClickListener(v -> calculate());
        btnReset.setOnClickListener(v -> reset());
    }

    private void calculate() {
        Double weight = parseInput(etWeight, R.string.err_weight_empty);
        Double rate = parseInput(etRate, R.string.err_rate_empty);
        if (weight == null || rate == null) {
            return;
        }

        int checkedId = rgClass.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(this, R.string.err_class, Toast.LENGTH_SHORT).show();
            return;
        }
        double limit = (checkedId == R.id.rbBusiness) ? BUSINESS_LIMIT : ECONOMY_LIMIT;

        double excessWeight = Math.max(0, weight - limit);
        double charge = excessWeight * rate;
        double total = charge + (TAX_RATE * charge);

        tvExcess.setText(getString(R.string.result_excess, excessWeight));
        tvCharge.setText(getString(R.string.result_charge, charge));
        tvTotal.setText(getString(R.string.result_total, total));
    }

    /** Returns the parsed value, or null after showing an error on the field. */
    private Double parseInput(EditText field, int emptyMsg) {
        String text = field.getText().toString().trim();
        if (text.isEmpty()) {
            field.setError(getString(emptyMsg));
            field.requestFocus();
            return null;
        }
        try {
            double value = Double.parseDouble(text);
            if (value < 0) {
                field.setError(getString(R.string.err_negative));
                field.requestFocus();
                return null;
            }
            return value;
        } catch (NumberFormatException e) {
            field.setError(getString(R.string.err_invalid));
            field.requestFocus();
            return null;
        }
    }

    private void reset() {
        etWeight.setText("");
        etRate.setText("");
        etWeight.setError(null);
        etRate.setError(null);
        rgClass.check(R.id.rbEconomy);
        tvExcess.setText("");
        tvCharge.setText("");
        tvTotal.setText("");
        etWeight.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
            Intent send = new Intent(Intent.ACTION_SEND);
            send.setType("text/plain");
            send.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
            send.putExtra(Intent.EXTRA_TEXT,
                    getString(R.string.share_text, getString(R.string.github_url)));
            startActivity(Intent.createChooser(send, getString(R.string.share_chooser)));
            return true;
        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}