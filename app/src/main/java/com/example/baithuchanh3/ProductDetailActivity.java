package com.example.baithuchanh3;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView btnBackHeader;
    private TextView tvDetailName;
    private TextView tvDetailCategory;
    private TextView tvDetailPrice;
    private TextView tvDetailQuantity;
    private TextView tvDetailLowStockBadge;
    private TextView tvDetailStockValue;
    private TextView tvDetailDescription;

    private Button btnAddStock;
    private Button btnSellStock;
    private Button btnBack;
    private Button btnDeleteProduct;

    private DatabaseHelper dbHelper;
    private int productId = -1;
    private Product currentProduct;
    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        dbHelper = new DatabaseHelper(this);

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        decimalFormat = new DecimalFormat("#,###", symbols);

        productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        if (productId == -1) {
            Toast.makeText(this, "Không tìm thấy sản phẩm!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnBackHeader = findViewById(R.id.btnBackHeader);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailCategory = findViewById(R.id.tvDetailCategory);
        tvDetailPrice = findViewById(R.id.tvDetailPrice);
        tvDetailQuantity = findViewById(R.id.tvDetailQuantity);
        tvDetailLowStockBadge = findViewById(R.id.tvDetailLowStockBadge);
        tvDetailStockValue = findViewById(R.id.tvDetailStockValue);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);

        btnAddStock = findViewById(R.id.btnAddStock);
        btnSellStock = findViewById(R.id.btnSellStock);
        btnBack = findViewById(R.id.btnBack);
        btnDeleteProduct = findViewById(R.id.btnDeleteProduct);

        btnBackHeader.setOnClickListener(v -> finish());
        btnBack.setOnClickListener(v -> finish());

        btnAddStock.setOnClickListener(v -> addStock());
        btnSellStock.setOnClickListener(v -> sellStock());
        btnDeleteProduct.setOnClickListener(v -> deleteProduct());

        loadProductDetails();
    }

    private void loadProductDetails() {
        currentProduct = dbHelper.getProductById(productId);
        if (currentProduct == null) {
            Toast.makeText(this, "Sản phẩm không tồn tại!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvDetailName.setText(currentProduct.getName());
        tvDetailCategory.setText(currentProduct.getCategory());
        tvDetailPrice.setText(decimalFormat.format(currentProduct.getPrice()) + " đ");

        int qty = currentProduct.getQuantity();
        tvDetailQuantity.setText(String.valueOf(qty));

        long totalStockVal = (long) currentProduct.getPrice() * qty;
        tvDetailStockValue.setText(decimalFormat.format(totalStockVal) + " đ");

        if (qty < 5) {
            tvDetailLowStockBadge.setVisibility(View.VISIBLE);
        } else {
            tvDetailLowStockBadge.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(currentProduct.getDescription())) {
            tvDetailDescription.setText("Chưa có mô tả.");
        } else {
            tvDetailDescription.setText(currentProduct.getDescription());
        }
    }

    private void addStock() {
        if (currentProduct == null) return;
        int newQty = currentProduct.getQuantity() + 1;
        boolean updated = dbHelper.updateQuantity(productId, newQty);
        if (updated) {
            Toast.makeText(this, "Đã nhập thêm 1 sản phẩm", Toast.LENGTH_SHORT).show();
            loadProductDetails();
        } else {
            Toast.makeText(this, "Cập nhật số lượng thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    private void sellStock() {
        if (currentProduct == null) return;
        int currentQty = currentProduct.getQuantity();
        if (currentQty <= 0) {
            Toast.makeText(this, "Hết hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        int newQty = currentQty - 1;
        boolean updated = dbHelper.updateQuantity(productId, newQty);
        if (updated) {
            Toast.makeText(this, "Đã bán 1 sản phẩm", Toast.LENGTH_SHORT).show();
            loadProductDetails();
        } else {
            Toast.makeText(this, "Cập nhật số lượng thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteProduct() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này khỏi cơ sở dữ liệu?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    boolean deleted = dbHelper.deleteProduct(productId);
                    if (deleted) {
                        Toast.makeText(ProductDetailActivity.this, "Xóa sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ProductDetailActivity.this, "Lỗi khi xóa sản phẩm!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
