# UdacityProjectInventoryApp

Here is an application for the Udacity Android Basics Inventory Project. This applications keeps track of their items/products in a list(RecyclerView).

## PROJECT SPECIFICATION

### User Interface - Layout

- The app contains a list of current products and a button to add a new product.
- Each list item displays the product name, current quantity, and price. Each list item also contains a Sale Button that reduces the quantity by one (include logic so that no negative quantities are displayed).
- The Detail Layout for each item displays the remainder of the information stored in the database.
- The Detail Layout contains buttons that increase and decrease the available quantity displayed.
- The Detail Layout contains a button to order from the supplier.
- The detail view contains a button to delete the product record entirely.
- When there is no information to display in the database, the layout displays a TextView with instructions on how to populate the database.

### Functionality

- The code runs without errors. For example, when user inputs product information (quantity, price, name, image), instead of erroring out, the app includes logic to validate that no null values are accepted. If a null value is inputted, add a Toast that prompts the user to input the correct information before they can continue.
- The listView populates with the current products stored in the table.
- The Add product button prompts the user for information about the product and a picture, each of which are then properly stored in the table.
- User input is validated. In particular, empty product information is not accepted. If user inputs product information (quantity, price, name, image), instead of erroring out, the app includes logic to validate that no null values are accepted. If a null value is inputted, add a Toast that prompts the user to input the correct information before they can continue.
- In the activity that displays a list of all available inventory, each List Item contains a Sale Button which reduces the available quantity for that particular product by one (include logic so that no negative quantities are displayed).
- Clicking on the rest of each list item sends the user to the detail screen for the correct product.
- The Modify Quantity Buttons in the detail view properly increase and decrease the quantity available for the correct product.
- The ‘order more’ button sends an intent to either a phone app or an email app to contact the supplier using the information stored in the database.
- The delete button prompts the user for confirmation and, if confirmed, deletes the product record entirely and sends the user back to the main activity.

## Screenshots

![screenshot_1](https://cloud.githubusercontent.com/assets/26686429/26178863/269ebedc-3b58-11e7-87a5-d5a7fab1e007.png)

![screenshot_5](https://cloud.githubusercontent.com/assets/26686429/26178893/4cb1725e-3b58-11e7-88ab-58ddaa9e78ad.png)

![screenshot_3](https://cloud.githubusercontent.com/assets/26686429/26178894/51547590-3b58-11e7-993a-2cfcfaa381c3.png)

![screenshot_4](https://cloud.githubusercontent.com/assets/26686429/26178900/54a19c46-3b58-11e7-8166-1942cb9f09f2.png)
