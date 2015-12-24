package com.thoughtworks.myapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.thoughtworks.myapplication.domain.PM25;
import com.thoughtworks.myapplication.service.AirServiceClient;

import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity {

    private EditText cityEditText;
    private EditText locationNumText;
    private TextView pm25TextView;
    private TextView sthTodo;
    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityEditText = (EditText) findViewById(R.id.edit_view_input);
        locationNumText = (EditText) findViewById(R.id.edit_num_input);
        pm25TextView = (TextView) findViewById(R.id.text_view_pm25);
        sthTodo = (TextView) findViewById(R.id.text_view_todo);
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage(getString(R.string.loading_message));

        findViewById(R.id.button_query_pm25).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onQueryPM25Click();
            }
        });

    }


    private void onQueryPM25Click() {
        final String city = cityEditText.getText().toString();

        if (!TextUtils.isEmpty(city)) {
            showLoading();
            AirServiceClient.getInstance().requestPM25(city, new Callback<List<PM25>>() {
                @Override
                public void onResponse(Response<List<PM25>> response, Retrofit retrofit) {
                    showSuccessScreen(response);
                }

                @Override
                public void onFailure(Throwable t) {
                    showErrorScreen();
                }
            });
        }
    }

    private void showSuccessScreen(Response<List<PM25>> response) {
        hideLoading();
        if (response != null) {
            populate(response.body());
        }
    }

    private void showErrorScreen() {
        hideLoading();
        pm25TextView.setText(R.string.error_message_query_pm25);
    }

    private void showLoading() {
        loadingDialog.show();
    }

    private void hideLoading() {
        loadingDialog.dismiss();
    }

    private void populate(List<PM25> data) {
        if (data != null && !data.isEmpty()) {
            String numm = locationNumText.getText().toString();
            int num = Integer.parseInt(numm);
            PM25 pm25 = data.get(num - 1);
            pm25TextView.setText(pm25.getPositionName() + " aqi:" + pm25.getAqi() + " " + pm25.getQuality());
            if (pm25.getAqi() <= 100) {
                sthTodo.setText("Excellent ! Hold on !");
            }
            if ((pm25.getAqi() > 100) && (pm25.getAqi() <= 150)) {
                sthTodo.setText("Slightly polluted ! Do something to protect the environment !");
            }
            if ((pm25.getAqi() > 150) && (pm25.getAqi() <= 200)) {
                sthTodo.setText("Moderately polluted ! Fighting to protect the environment !");
            }
            if ((pm25.getAqi() > 200) && (pm25.getAqi() <= 250)) {
                sthTodo.setText("Heavily polluted ! Hurry up to save our planet !");
            }
            if (pm25.getAqi() > 250) {
                sthTodo.setText("Terribly polluted ! Please don't hurt earth anymore !");
            }


        }

    }
}
