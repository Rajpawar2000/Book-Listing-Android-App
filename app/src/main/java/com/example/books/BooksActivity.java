package com.example.books;

import androidx.appcompat.app.AppCompatActivity;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BooksActivity extends AppCompatActivity implements LoaderCallbacks<List<Books>> {

    /** URL for earthquake data from the USGS dataset */
    private static final String BOOK_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=java";


    public static final String LOG_TAG = BooksActivity.class.getName();

    /** Adapter for the list of earthquakes */
    private BooksAdapter mAdapter;

    private static final int BOOKS_LOADER_ID = 1;

    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);


        // Find a reference to the {@link ListView} in the layout
        ListView bookListView = (ListView) findViewById(R.id.list);


        // Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new BooksAdapter(this, new ArrayList<Books>());

        // Create a new {@link EarthquakeAdapter} of earthquakes;

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        bookListView.setAdapter(mAdapter);

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Find the current earthquake that was clicked on
                Books currentBook = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri bookUri = Uri.parse(currentBook.getUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });


        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(BOOKS_LOADER_ID,null,this);

        mEmptyStateTextView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(mEmptyStateTextView);
    }

    @Override
    public Loader<List<Books>> onCreateLoader(int id, Bundle args) {
        return new BooksLoader(this,BOOK_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Books>> loader, List<Books> books) {

        View loaderIndicator = findViewById(R.id.loading_indicator);
        loaderIndicator.setVisibility(View.GONE);


        mEmptyStateTextView.setText(R.string.no_book);

        mAdapter.clear();

        if(books!=null && !books.isEmpty()){
            mAdapter.addAll(books);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Books>> loader) {
        mAdapter.clear();
    }

}



