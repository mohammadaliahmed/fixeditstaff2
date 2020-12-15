package com.fixedit.fixeditstaff.Activities;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fixedit.fixeditstaff.Models.ServiceCountModel;
import com.fixedit.fixeditstaff.R;


import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by AliAh on 30/06/2018.
 */

public class OrderedServicesAdapter extends RecyclerView.Adapter<OrderedServicesAdapter.ViewHolder> {
    Context context;
    ArrayList<ServiceCountModel> productList;

    public OrderedServicesAdapter(Context context, ArrayList<ServiceCountModel> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ordered_service_layout, parent, false);
        OrderedServicesAdapter.ViewHolder viewHolder = new OrderedServicesAdapter.ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ServiceCountModel model = productList.get(position);

        holder.title.setText(model.getService().getName());
        holder.count.setText("Service Qty: "+model.getQuantity());
        holder.serial.setText(""+(position+1)+") ");

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle, price, count,serial;
        ImageView image, increase, decrease;
        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            count = itemView.findViewById(R.id.quantity);
            serial = itemView.findViewById(R.id.serial);


        }
    }
}
