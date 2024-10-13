## Stock Lookup Android App
* This Android app allows users to search for stock data (current price, percentage change, and company name) using the Alpha Vantage API. The app includes a clean UI with search functionality, a button to clear the input, and displays the stock information in a card view.

## Functionalities
# 1. Stock Search Feature
Users can enter a stock symbol (like AAPL, GOOGL, or TSLA) into the input field.
The app sends a request to the Alpha Vantage API to fetch the stock data.
If the stock symbol is valid, the app displays the current price, percentage change, and company name in a futuristic card UI.
If the stock symbol is invalid or there is a network error, appropriate error messages are displayed.
# 2. Clear Search Field
The clear button allows users to reset the search input field and hide the stock result card.
##Code Explanation
# 1. MainActivity Class
The MainActivity class contains the core logic for the app:

public class MainActivity extends AppCompatActivity {}
This extends the AppCompatActivity to support modern UI and features.

# 2. Variables Declaration
The following views are declared:

## stockSymbolInput: An EditText where the user types the stock symbol.
## stockInfo: A TextView where the fetched stock data is displayed.
## searchButton: A Button that triggers the stock search when clicked.
## clearButton: A Button that clears the input and hides the stock information.
## resultCard: A CardView that wraps the stock data for a sleek UI.

private EditText stockSymbolInput;
private TextView stockInfo;
private Button searchButton, clearButton;
private CardView resultCard;
#3. API Configuration
The app uses the Alpha Vantage API. You need to insert your own API key in the following line:


private static final String API_KEY = "200DB3OWJ78BLEKW";
private static final String BASE_URL = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=";
This constructs the URL to fetch stock data based on the user’s input symbol.

# 4. onCreate() Method
In onCreate, the app sets the initial UI components:


setContentView(R.layout.activity_main);
The result card is hidden initially until the user performs a successful search:


resultCard.setVisibility(View.GONE);
# 5. Search and Clear Button Listeners
The app listens for the search button click:


searchButton.setOnClickListener(v -> {
    String symbol = stockSymbolInput.getText().toString().trim();
    if (!symbol.isEmpty()) {
        fetchStockData(symbol);
    } else {
        Toast.makeText(MainActivity.this, "Please enter a stock symbol", Toast.LENGTH_SHORT).show();
    }
});
If the input is valid, the fetchStockData() method is triggered to retrieve stock data from the API.

The clear button resets the input field and hides the result card:


clearButton.setOnClickListener(v -> {
    stockSymbolInput.setText("");
    resultCard.setVisibility(View.GONE);
});
# 6. Fetch Stock Data from Alpha Vantage API
The fetchStockData() method sends a request to the Alpha Vantage API using OkHttpClient:


OkHttpClient client = new OkHttpClient();
String url = BASE_URL + symbol + "&apikey=" + API_KEY;

Request request = new Request.Builder()
    .url(url)
    .build();
# 7. Handling API Response
The response from the API is processed asynchronously:


client.newCall(request).enqueue(new Callback() {
    @Override
    public void onFailure(Call call, IOException e) {
        runOnUiThread(() -> stockInfo.setText("Network Error: " + e.getMessage()));
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (response.isSuccessful()) {
            String jsonResponse = response.body().string();
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

            if (jsonObject.has("Error Message")) {
                String errorDescription = jsonObject.get("Error Message").getAsString();
                runOnUiThread(() -> stockInfo.setText("Error: " + errorDescription));
                return;
            }

            JsonObject globalQuote = jsonObject.getAsJsonObject("Global Quote");
            if (globalQuote == null || globalQuote.size() == 0) {
                runOnUiThread(() -> stockInfo.setText("No data found for symbol."));
                return;
            }

            // Parse the data
            String companyName = symbol.toUpperCase();
            double currentPrice = globalQuote.get("05. price").getAsDouble();
            double percentageChange = globalQuote.get("10. change percent")
                .getAsString().replace("%", "").isEmpty() ? 0.0 : Double.parseDouble(globalQuote.get("10. change percent").getAsString().replace("%", ""));

            String info = "Company: " + companyName + "\n" +
                    "Price: $" + currentPrice + "\n" +
                    "Change: " + percentageChange + "%";

            runOnUiThread(() -> {
                stockInfo.setText(info);
                resultCard.setVisibility(View.VISIBLE); // Display the card with stock info
            });
        } else {
            runOnUiThread(() -> stockInfo.setText("Error fetching data. Response code: " + response.code()));
        }
    }
});
# 8. Displaying Stock Data
The response contains the stock data in JSON format. The relevant information like company name, current price, and percentage change is extracted and displayed in the TextView inside the CardView:


String info = "Company: " + companyName + "\n" +
              "Price: $" + currentPrice + "\n" +
              "Change: " + percentageChange + "%";

runOnUiThread(() -> {
    stockInfo.setText(info);
    resultCard.setVisibility(View.VISIBLE);
});
The stock data is shown in a card for a modern UI.

## How to Set Up and Run the App
# 1. Clone the Repository
Clone the repository to your local machine.

# 2. Open in Android Studio
Open the project in Android Studio.

# 3. Add Your API Key
Add your Alpha Vantage API key in MainActivity.java:


private static final String API_KEY = "200DB3OWJ78BLEKW";
# 4. Connect Device or Emulator
Connect a physical Android device or set up an emulator.

#5. Run the Project
Run the project on your device/emulator by clicking Run in Android Studio
