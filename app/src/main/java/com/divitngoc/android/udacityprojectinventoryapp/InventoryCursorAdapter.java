package com.divitngoc.android.udacityprojectinventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.divitngoc.android.udacityprojectinventoryapp.data.InventoryContract;

public class InventoryCursorAdapter extends CursorRecyclerViewAdapter<InventoryCursorAdapter.ViewHolder> {

    private CatalogueActivity activity;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView itemImageView;
        protected TextView nameTextView;
        protected TextView priceTextView;
        protected TextView stockTextView;
        protected ImageView saleImageView;

        public ViewHolder(View view) {
            super(view);
            itemImageView = (ImageView) view.findViewById(R.id.image_item_inventory);
            nameTextView = (TextView) view.findViewById(R.id.name_item);
            priceTextView = (TextView) view.findViewById(R.id.price_item);
            stockTextView = (TextView) view.findViewById(R.id.stock_item);
            saleImageView = (ImageView) view.findViewById(R.id.image_sale);
        }
    }

    public InventoryCursorAdapter(CatalogueActivity context, Cursor c) {
        super(context, c);
        this.activity = context;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {

        final long id;

        id = cursor.getLong(cursor.getColumnIndex(InventoryContract.InventoryEntry._ID));
        // Find the columns of inventory that we're interested in
        int pictureColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PICTURE);
        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE);
        int stockColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_STOCK);

        // Read the pet attributes from the Cursor for the current item
        String imageString = cursor.getString(pictureColumnIndex);

        Uri imageUri = Uri.parse(imageString);

        String itemName = cursor.getString(nameColumnIndex);
        Double itemPrice = cursor.getDouble(priceColumnIndex);
        int itemStock = cursor.getInt(stockColumnIndex);

        //Keep price to two decimal places
        java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");

        // If the inventory stock is empty, then use some default text
        // that says "Out of stock", so the TextView isn't blank.
        String strItemStock = activity.getString(R.string.in_stock) + " " + itemStock;
        if (itemStock <= 0) {
            strItemStock = activity.getString(R.string.out_of_stock);
        }

        viewHolder.itemImageView.setImageURI(imageUri);
        viewHolder.nameTextView.setText(itemName);
        viewHolder.priceTextView.setText("£" + df.format(itemPrice));
        viewHolder.stockTextView.setText(strItemStock);

        final int stock = itemStock;
        viewHolder.saleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // only calls this method when stock is above 0
                if (stock > 0) {
                    activity.onSaleClick(id, stock);
                } else {
                    final Toast outOfStockToast = Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.out_of_stock), Toast.LENGTH_SHORT);
                    outOfStockToast.show();

                    //To stop toast message queuing up if user spam clicks sale button
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            outOfStockToast.cancel();
                        }
                    }, 1000);

                }
            }
        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //moves to EditorActivity
                activity.onItemClick(id);
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inventory_list_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

}
