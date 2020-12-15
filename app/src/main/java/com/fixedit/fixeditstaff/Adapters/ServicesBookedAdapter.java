package com.fixedit.fixeditstaff.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.fixedit.fixeditstaff.Models.ServiceCountModel;
import com.fixedit.fixeditstaff.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ServicesBookedAdapter extends RecyclerView.Adapter<ServicesBookedAdapter.ViewHolder> {
    Context context;
    ArrayList<ServiceCountModel> itemlist;

    public ServicesBookedAdapter(Context context, ArrayList<ServiceCountModel> itemlist) {
        this.context = context;
        this.itemlist = itemlist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.service_book_item_layout, viewGroup, false);
        ServicesBookedAdapter.ViewHolder viewHolder = new ServicesBookedAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int i) {
        final ServiceCountModel model = itemlist.get(i);
        holder.serviceName.setText(model.getService().getName());
        holder.serviceCount.setText(""+model.getQuantity());

    }

    @Override
    public int getItemCount() {
        return itemlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView serviceName,serviceCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            serviceCount = itemView.findViewById(R.id.serviceCount);
            serviceName = itemView.findViewById(R.id.serviceName);

        }
    }


}
