package com.example.baithuchanh3;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    private final Context context;
    private List<Product> productList;
    private final OnItemClickListener listener;
    private final DecimalFormat decimalFormat;

    public ProductAdapter(Context context, List<Product> productList, OnItemClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        this.decimalFormat = new DecimalFormat("#,###", symbols);
    }

    public void updateList(List<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvProductName.setText(product.getName());
        holder.tvProductPrice.setText("Giá bán: " + decimalFormat.format(product.getPrice()) + " đ");
        holder.tvProductCategory.setText(product.getCategory());

        int quantity = product.getQuantity();
        holder.tvProductQuantity.setText("SL: " + quantity);

        if (quantity < 5) {
            holder.tvProductQuantity.setTextColor(Color.parseColor("#D32F2F"));
            holder.tvLowStockBadge.setVisibility(View.VISIBLE);
        } else {
            holder.tvProductQuantity.setTextColor(Color.parseColor("#212121"));
            holder.tvLowStockBadge.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice, tvProductQuantity, tvProductCategory, tvLowStockBadge;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
            tvProductCategory = itemView.findViewById(R.id.tvProductCategory);
            tvLowStockBadge = itemView.findViewById(R.id.tvLowStockBadge);
        }
    }
}
