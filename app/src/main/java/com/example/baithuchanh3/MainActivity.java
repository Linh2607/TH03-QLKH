package com.example.baithuchanh3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvTotalProducts;
    private TextView tvTotalValue;
    private TextView tvLowStockCount;
    private RecyclerView rvProducts;
    private TextView tvEmpty;
    private Button btnAddProduct;

    private DatabaseHelper dbHelper;
    private ProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        decimalFormat = new DecimalFormat("#,###", symbols);

        tvTotalProducts = findViewById(R.id.tvTotalProducts);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        tvLowStockCount = findViewById(R.id.tvLowStockCount);
        rvProducts = findViewById(R.id.rvProducts);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnAddProduct = findViewById(R.id.btnAddProduct);

        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(this, productList, product -> {
            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", product.getId());
            startActivity(intent);
        });
        rvProducts.setAdapter(adapter);

        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataAndStatistics();
    }

    private void loadDataAndStatistics() {
        productList = dbHelper.getAllProducts();

        if (productList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvProducts.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvProducts.setVisibility(View.VISIBLE);
        }

        adapter.updateList(productList);

        // Update statistics
        tvTotalProducts.setText(String.valueOf(productList.size()));
        tvTotalValue.setText(decimalFormat.format(dbHelper.getTotalValue()) + " đ");
        tvLowStockCount.setText(String.valueOf(dbHelper.countLowStock()));
    }
}
