package com.udacity.stockhawk.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.Date;


public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    GraphView graphView;
    TextView symbolTextView;
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        mUri = intent.getData();
        graphView = (GraphView) findViewById(R.id.graphView);
        symbolTextView = (TextView) findViewById(R.id.symbol_text_view);
        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, mUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() == 0) {
            return;
        }
        if (data.moveToFirst()) {
            String history = data.getString(Contract.Quote.POSITION_HISTORY);
            setGraphView(history);
            symbolTextView.setText(data.getString(Contract.Quote.POSITION_SYMBOL));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private LineGraphSeries<DataPoint> getDataSeries(String history) {
        String[] singleData = history.split("\n");
        DataPoint[] dataPoints = new DataPoint[singleData.length];
        int len = singleData.length;
        for (int i = len - 1; i >= 0; i--) {
            String[] dataArray = singleData[i].split(",");
            String dateString = dataArray[0].trim();
            String priceString = dataArray[1].trim();
            Date date = new Date(Long.parseLong(dateString));
            float priceFloat = Float.parseFloat(priceString);
            dataPoints[len - 1 - i] = new DataPoint(date, priceFloat);
        }
        return new LineGraphSeries<>(dataPoints);
    }

    private void setGraphView(String history) {
        LineGraphSeries<DataPoint> series = getDataSeries(history);
        graphView.addSeries(series);
        graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graphView.getGridLabelRenderer().setNumHorizontalLabels(3);
        graphView.getGridLabelRenderer().setHumanRounding(false);
    }
}
