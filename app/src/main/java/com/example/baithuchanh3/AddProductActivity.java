package com.example.baithuchanh3;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class AddProductActivity extends AppCompatActivity {

    private EditText etProductName;
    private EditText etProductPrice;
    private EditText etProductQuantity;
    private Spinner spinnerCategory;
    private SwitchMaterial switchActive;
    private EditText etProductDescription;
    private Button btnSaveProduct;
    private ImageView btnBack;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        dbHelper = new DatabaseHelper(this);

        etProductName = findViewById(R.id.etProductName);
        etProductPrice = findViewById(R.id.etProductPrice);
        etProductQuantity = findViewById(R.id.etProductQuantity);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        switchActive = findViewById(R.id.switchActive);
        etProductDescription = findViewById(R.id.etProductDescription);
        btnSaveProduct = findViewById(R.id.btnSaveProduct);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());
        btnSaveProduct.setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {
        String name = etProductName.getText().toString().trim();
        String priceStr = etProductPrice.getText().toString().trim();
        String quantityStr = etProductQuantity.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem() != null ? spinnerCategory.getSelectedItem().toString() : "";
        int active = switchActive.isChecked() ? 1 : 0;
        String description = etProductDescription.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etProductName.setError("Vui lòng nhập tên sản phẩm");
            etProductName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(priceStr)) {
            etProductPrice.setError("Vui lòng nhập giá bán");
            etProductPrice.requestFocus();
            return;
        }

        int price;
        try {
            price = Integer.parseInt(priceStr);
            if (price <= 0) {
                etProductPrice.setError("Giá bán phải lớn hơn 0");
                etProductPrice.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etProductPrice.setError("Giá bán không hợp lệ");
            etProductPrice.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(quantityStr)) {
            etProductQuantity.setError("Vui lòng nhập số lượng");
            etProductQuantity.requestFocus();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity < 0) {
                etProductQuantity.setError("Số lượng không được âm");
                etProductQuantity.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etProductQuantity.setError("Số lượng không hợp lệ");
            etProductQuantity.requestFocus();
            return;
        }

        Product product = new Product(name, price, quantity, category, active, description);
        long result = dbHelper.insertProduct(product);

        if (result != -1) {
            Toast.makeText(this, "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Lỗi khi thêm sản phẩm!", Toast.LENGTH_SHORT).show();
        }
    }
}
