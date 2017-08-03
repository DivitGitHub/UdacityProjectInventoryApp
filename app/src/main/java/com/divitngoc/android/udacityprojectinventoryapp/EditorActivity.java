package com.divitngoc.android.udacityprojectinventoryapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.divitngoc.android.udacityprojectinventoryapp.data.InventoryContract;

/**
 * Created by DxAlchemistv1 on 13/05/2017.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the inventory data loader
     */
    private static final int EXISITING_INVENTORY_LOADER = 0;

    /**
     * Identifier for activity result after picking an image
     */
    private static final int RESULT_LOAD_IMAGE = 100;

    /**
     * Content URI for the existing item in the inventory (null if it's a new inventory)
     */
    private Uri mCurrentInventoryUri;

    /**
     * Uri of image selected from EXTERNAL_STORAGE or image uri from database
     */
    private Uri imageUri;

    private ImageView mImageView;
    private EditText mNameView;
    private EditText mPriceView;
    private EditText mSupplierEmailView;
    private TextView mStockView;
    private Button mMinusBtn;
    private Button mAddBtn;

    // keep tracking of stock
    private int mStock;

    /**
     * Boolean flag that keeps track of whether the inventory has been edited (true) or not (false)
     */
    private boolean mInventoryHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mInventoryHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mInventoryHasChanged = true;
            return false;
        }
    };

    /**
     * OnClickListenter that listens when users click on the buttons
     */
    private View.OnClickListener mOnClickListenerBtn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case (R.id.btn_minus):
                    if (mStock > 0) {
                        mStock -= 1;
                    } else {
                        Toast.makeText(EditorActivity.this, R.string.cannot_have_less_than_0_stock, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case (R.id.btn_add):
                    mStock += 1;
                    break;
            }
            display();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentInventoryUri = intent.getData();
        TextView photoTextViewHelp = (TextView) findViewById(R.id.photo_text_help);

        // If the intent DOES NOT contain a inventory content URI, then we know that we are
        // creating a new item.
        if (mCurrentInventoryUri == null) {
            // This is a new item, so change the app bar to say "Add a item"
            setTitle(getString(R.string.editor_activity_title_new_item));
            photoTextViewHelp.setText(getString(R.string.click_below_to_add_a_photo));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing inventory, so change app bar to say "Edit item"
            setTitle(getString(R.string.editor_activity_title_edit_item));
            photoTextViewHelp.setText(getString(R.string.click_below_to_update_photo));

            TextView stockWordView = (TextView) findViewById(R.id.stock_word);
            stockWordView.setVisibility(View.VISIBLE);

            View setStockView = findViewById(R.id.set_stock);
            setStockView.setVisibility(View.VISIBLE);

            // Initialize a loader to read the inventory data from the database
            // and display the current values in the editor
            getSupportLoaderManager().initLoader(EXISITING_INVENTORY_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mImageView = (ImageView) findViewById(R.id.editor_image);
        mNameView = (EditText) findViewById(R.id.editor_name);
        mPriceView = (EditText) findViewById(R.id.editor_price);
        mSupplierEmailView = (EditText) findViewById(R.id.editor_supplier_email);
        mStockView = (TextView) findViewById(R.id.editor_stock);

        mNameView.setOnTouchListener(mTouchListener);
        mPriceView.setOnTouchListener(mTouchListener);
        mSupplierEmailView.setOnTouchListener(mTouchListener);
        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                mInventoryHasChanged = true;
                return false;
            }
        });

        mMinusBtn = (Button) findViewById(R.id.btn_minus);
        mAddBtn = (Button) findViewById(R.id.btn_add);

        mMinusBtn.setOnClickListener(mOnClickListenerBtn);
        mAddBtn.setOnClickListener(mOnClickListenerBtn);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            mImageView.setImageURI(imageUri);
            mImageView.invalidate();
        }
    }

    /**
     * Get user input from editor and save item into database.
     */
    private boolean saveItem() {
        String nameString = mNameView.getText().toString().trim();
        String priceString = mPriceView.getText().toString().trim();
        String supplierEmailString = mSupplierEmailView.getText().toString().trim();
        String stockString = mStockView.getText().toString().trim();

        String toastMessage = "";
        // checking if all the fields in the editor needs input
        if (imageUri == null) {
            toastMessage += getString(R.string._image);
        }

        toastMessage = checkIfFieldAreEmpty(nameString, toastMessage, getString(R.string.name), getString(R.string._name));
        toastMessage = checkIfFieldAreEmpty(supplierEmailString, toastMessage, getString(R.string.supplier_email_address),  getString(R.string._supplier_email_address));
        toastMessage = checkIfFieldAreEmpty(priceString, toastMessage,getString(R.string.price), getString(R.string._price));

        // shows user message of the required fields
        if (!TextUtils.isEmpty(toastMessage)) {
            Toast.makeText(this, toastMessage + " " +getString(R.string.requires_input), Toast.LENGTH_SHORT).show();
            return false;
        }

        double price = Double.parseDouble(priceString);

        int stock = 0;
        if (!TextUtils.isEmpty(stockString)) {
            stock = Integer.parseInt(stockString);
        }

        // Create a ContentValues object where column names are the keys,
        // and item attributes from the editor are the values.
        ContentValues values = new ContentValues();

        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PICTURE, imageUri.toString());
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME, nameString);
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER_EMAIL, supplierEmailString);
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE, price);
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_STOCK, stock);

        // Determine if this is a new or existing item by checking if mCurrentInventoryUri is null or not
        if (mCurrentInventoryUri == null) {
            // This is a NEW item, so insert item into the provider,
            // returning the content URI for the new inventory.
            Uri newUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING item in the inventory database, so update the item with content URI
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentInventoryUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentInventoryUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    private String checkIfFieldAreEmpty(String field, String toastMessage, String ifEmpty, String notEmpty) {
        if (TextUtils.isEmpty(field)) {
            toastMessage = makeRequireMessage(toastMessage, ifEmpty, notEmpty);
        }
        return toastMessage;
    }

    //function for toast message
    private String makeRequireMessage(String toastMessage, String ifEmpty, String notEmpty) {
        if (TextUtils.isEmpty(toastMessage)) {
            toastMessage += ifEmpty;
        } else {
            toastMessage += notEmpty;
        }
        return toastMessage;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new item, hide the "Delete" menu item.
        if (mCurrentInventoryUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save item to inventory database if required field(s) are entered
                if (saveItem()) {
                    // Exit activity
                    finish();
                }
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Order More" menu option
            case R.id.order_more:
                orderMore();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the item hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogueActivity}.
                if (!mInventoryHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mInventoryHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_PICTURE,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER_EMAIL,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_STOCK};

        // This loader will execute the ContentProvider's query method on a background thread
        return new android.support.v4.content.CursorLoader(this,   // Parent activity context
                mCurrentInventoryUri,         // Query the content URI for the current inventory row
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Exit early if the cursor is null or there is less than 1 row in the cursor
        if (data == null || data.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        if (data.moveToFirst()) {
            // Find the columns of inventory attributes that we're interested in
            int imageColumnIndex = data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PICTURE);
            int nameColumnIndex = data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME);
            int priceColumnIndex = data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE);
            int supplierEmailColumnIndex = data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER_EMAIL);
            int stockColumnIndex = data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_STOCK);

            // Extract out the value from the Cursor for the given column index
            String imageUriString = data.getString(imageColumnIndex);
            String name = data.getString(nameColumnIndex);
            double price = data.getDouble(priceColumnIndex);
            String supplierEmail = data.getString(supplierEmailColumnIndex);
            int stock = data.getInt(stockColumnIndex);

            // Update the views on the screen with the values from the database
            if (!TextUtils.isEmpty(imageUriString) && imageUriString != null) {
                imageUri = Uri.parse(imageUriString);
                mImageView.setImageURI(imageUri);
            }

            mNameView.setText(name);
            mPriceView.setText(String.valueOf(price));
            mSupplierEmailView.setText(supplierEmail);
            mStockView.setText(String.valueOf(stock));
            mStock = stock;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameView.setText("");
        mPriceView.setText("");
        mSupplierEmailView.setText("");
        mStockView.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                // User clicked the "Keep editing" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this item.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the item in the inventory database.
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the item.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the item in the inventory database.
     */
    private void deleteItem() {
        // Only perform the delete if this is an existing item.
        if (mCurrentInventoryUri != null) {
            // Call the ContentResolver to delete the item at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentInventoryUri
            // content URI already identifies the item that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentInventoryUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }

    //displays stock value
    private void display() {
        mInventoryHasChanged = true;
        mStockView.setText(String.valueOf(mStock));
    }

    public void orderMore() {
        Intent intent = new Intent(android.content.Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.setData(Uri.parse("mailto:" + mSupplierEmailView.getText().toString().trim()));
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "New Order of " + mNameView.getText().toString());
        startActivity(intent);
    }

}
