package com.example.sla;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private EditText stockSymbolInput;
    private TextView stockInfo;
    private Button searchButton, clearButton;
    private CardView resultCard;

    // Replace with your Alpha Vantage API key
    private static final String API_KEY = "200DB3OWJ78BLEKW"; // Add your actual API key here
    private static final String BASE_URL = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stockSymbolInput = findViewById(R.id.stock_symbol);
        stockInfo = findViewById(R.id.stock_info);
        searchButton = findViewById(R.id.search_button);
        clearButton = findViewById(R.id.clear_button);
        resultCard = findViewById(R.id.result_card);

        resultCard.setVisibility(View.GONE); // Hide the card initially

        searchButton.setOnClickListener(v -> {
            String symbol = stockSymbolInput.getText().toString().trim();
            if (!symbol.isEmpty()) {
                fetchStockData(symbol);
            } else {
                Toast.makeText(MainActivity.this, "Please enter a stock symbol", Toast.LENGTH_SHORT).show();
            }
        });

        clearButton.setOnClickListener(v -> {
            stockSymbolInput.setText(""); // Clear the input field
            resultCard.setVisibility(View.GONE); // Hide the card
        });
    }

    private void fetchStockData(String symbol) {
        OkHttpClient client = new OkHttpClient();
        String url = BASE_URL + symbol + "&apikey=" + API_KEY;

        Request request = new Request.Builder()
                .url(url)
                .build();

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

                    String companyName = symbol.toUpperCase();
                    double currentPrice = globalQuote.get("05. price").getAsDouble();
                    double percentageChange = globalQuote.get("10. change percent").getAsString()
                            .replace("%", "").isEmpty() ? 0.0 : Double.parseDouble(globalQuote.get("10. change percent").getAsString().replace("%", ""));

                    String info = "Company: " + companyName + "\n" +
                            "Price: $" + currentPrice + "\n" +
                            "Change: " + percentageChange + "%";

                    runOnUiThread(() -> {
                        stockInfo.setText(info);
                        resultCard.setVisibility(View.VISIBLE); // Show the card with stock info
                    });
                } else {
                    runOnUiThread(() -> stockInfo.setText("Error fetching data. Response code: " + response.code()));
                }
            }
        });
    }
}
