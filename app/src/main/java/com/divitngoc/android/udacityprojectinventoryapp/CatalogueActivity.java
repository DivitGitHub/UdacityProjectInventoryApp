package com.divitngoc.android.udacityprojectinventoryapp;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.divitngoc.android.udacityprojectinventoryapp.data.InventoryContract.*;

public class CatalogueActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the inventory data loader
     */
    private static final int INVENTORY_LOADER = 0;

    private View emptyView;

    private RecyclerView.LayoutManager mLayoutManager;

    /**
     * Adapter for the ListView
     */
    private InventoryCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogue);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //when the fab button is clicked, the activity is moved to the AddAnItemActivity
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CatalogueActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);

        //setting for empty list view
        emptyView = findViewById(R.id.empty_view);

        mLayoutManager = new LinearLayoutManager(CatalogueActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        mCursorAdapter = new InventoryCursorAdapter(this, null);

        //Populate the list with cursor
        recyclerView.setAdapter(mCursorAdapter);

        //initialise the loader
        getSupportLoaderManager().initLoader(INVENTORY_LOADER, null, this);
    }

    /**
     * Gets position of the row and moves the user to the EditorActivity when clicked on
     */
    public void onItemClick(long id) {
        Uri currentInventoryUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);

        Intent intent = new Intent(CatalogueActivity.this, EditorActivity.class);

        // Set the URI on the data field of the intent
        intent.setData(currentInventoryUri);

        // Launch the {@link EditorActivity} to display the data for the current item.
        startActivity(intent);
    }

    /**
     * Decrease stock by 1 and update the database when called
     */
    public void onSaleClick(long id, int stock) {
        Uri currentProductUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
        Log.v("CatalogActivity", "Uri: " + currentProductUri);
        stock--;
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_INVENTORY_STOCK, stock);
        int rowsEffected = getContentResolver().update(currentProductUri, values, null, null);
    }

    /**
     * Helper method to insert hardcoded inventory data into the database. For debugging purposes only.
     */
    private void insertItem() {
        // Create a ContentValues object where column names are the keys
        ContentValues values = new ContentValues();

        values.put(InventoryEntry.COLUMN_INVENTORY_PICTURE, "android.resource://com.divitngoc.android.udacityprojectinventoryapp/drawable/mug");
        values.put(InventoryEntry.COLUMN_INVENTORY_NAME, "Mug");
        values.put(InventoryEntry.COLUMN_INVENTORY_PRICE, 10);
        values.put(InventoryEntry.COLUMN_INVENTORY_SUPPLIER_EMAIL, "divitngoc@gmail.com");
        values.put(InventoryEntry.COLUMN_INVENTORY_STOCK, 3);

        Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
    }

    private void deleteAllItems() {
        int rowsDeleted = getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from inventory database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalogue, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertItem();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_INVENTORY_PICTURE,
                InventoryEntry.COLUMN_INVENTORY_NAME,
                InventoryEntry.COLUMN_INVENTORY_PRICE,
                InventoryEntry.COLUMN_INVENTORY_SUPPLIER_EMAIL,
                InventoryEntry.COLUMN_INVENTORY_STOCK
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new android.support.v4.content.CursorLoader(this,   // Parent activity context
                InventoryEntry.CONTENT_URI,   // Provider content URI to query
                projection,                   // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }

        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_entire_entries);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the inventory.
                deleteAllItems();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
