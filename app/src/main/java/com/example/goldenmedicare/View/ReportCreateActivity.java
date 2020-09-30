package com.example.goldenmedicare.View;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goldenmedicare.Model.Patient;
import com.example.goldenmedicare.R;
import com.example.goldenmedicare.Controller.Utilities;
import com.example.goldenmedicare.Controller.WebHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ReportCreateActivity extends AppCompatActivity {

    private final Calendar myCalendar = Calendar.getInstance();
    private EditText txtTitle, txtSummary, txtRemarks, txtDate;
    Patient patient;

    private int reportId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        txtTitle = findViewById(R.id.activity_report_include_title);
        txtSummary = findViewById(R.id.activity_report_include_summary);
        txtRemarks = findViewById(R.id.activity_report_include_remarks);
        txtDate = findViewById(R.id.activity_report_include_date);
        txtDate.setFocusable(false);

        patient = (Patient) getIntent().getParcelableExtra("patient");

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        txtDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ReportCreateActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        findViewById(R.id.activity_report_include_createButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = txtTitle.getText().toString().trim();
                String summary = txtSummary.getText().toString().trim();
                String remarks = txtRemarks.getText().toString().trim();
                String date = txtDate.getText().toString().trim();
                String query = "INSERT INTO tbl_report (report_title, report_summary, report_remarks, report_date, user_id) "
                        + "VALUES ('" + title + "', '" + summary + "', '" + remarks + "', '" + date
                        + "', " + patient.getPatientId() + ");";
                new AsyncReport().execute(query, "http://192.168.1.132/golden_medicare/mysql_query_executor.php");

                query = "SELECT * FROM tbl_report WHERE report_title = '" + title + "' ORDER BY report_id DESC LIMIT 1;";
                new AsyncReportId().execute(query, "http://192.168.1.132/golden_medicare/mysql_query_executor.php");

                if(reportId>0) {
                    try {
                        JSONArray jsonArray = new JSONArray();
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("report_id", reportId);
                        jsonObject.put("report_title", title);
                        jsonObject.put("report_summary", summary);
                        jsonObject.put("report_remarks", remarks);
                        jsonObject.put("report_date", date);
                        jsonObject.put("token", patient.getPatientToken());
                        jsonArray.put(jsonObject);
                        new AsyncReport().execute(query, "http://192.168.1.132/golden_medicare/notification_pusher.php");
                    } catch (JSONException e){}
                }
            }
        });

    }

    private void updateLabel() {
        String myFormat = "yy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        txtDate.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Using switch statement
        switch (item.getItemId()) {
            case R.id.logout:
                // Call or crate object of LoginActivity when log out
                Utilities.handleLogout(this);
                return true;
            case android.R.id.home:
                Intent intent = new Intent(this, DoctorActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class  AsyncReport extends AsyncTask<String, String , String>{

        ProgressDialog progressDialog = new ProgressDialog(ReportCreateActivity.this);
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog.setMessage("\tLoading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected String doInBackground(String... params){
            try {
                return WebHandler.doPostRequest(params[0], params[1]).body().string();
            } catch (Exception e) {
                return "exception";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            if (result.equalsIgnoreCase("exception")) {
                Toast.makeText(ReportCreateActivity.this, "Oops! Connection Problem", Toast.LENGTH_LONG).show();
            } else if (result.equals("1")) {
                Toast.makeText(ReportCreateActivity.this, "Report Creation Successful!", Toast.LENGTH_LONG).show();
                Intent intent =  new Intent(ReportCreateActivity.this, DoctorActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(ReportCreateActivity.this, "Oops! An error occured.\nPlease try again..", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class  AsyncReportId extends AsyncTask<String, String , String>{

        ProgressDialog progressDialog = new ProgressDialog(ReportCreateActivity.this);
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog.setMessage("\tLoading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected String doInBackground(String... params){
            try {
                return WebHandler.doPostRequest(params[0], params[1]).body().string();
            } catch (Exception e) {
                return "exception";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            if (result.equalsIgnoreCase("exception")) {
                Toast.makeText(ReportCreateActivity.this, "Oops! Connection Problem", Toast.LENGTH_LONG).show();
            } else if (!result.equals("[]")) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    reportId = jsonObject.getInt("report_id");
                } catch (JSONException e) {
                    reportId = 0;
                }
            }else {
                reportId = 0;
            }
        }
    }

}
