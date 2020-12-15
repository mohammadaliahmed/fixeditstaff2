package com.fixedit.fixeditstaff.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.fixedit.fixeditstaff.Models.OrderModel;
import com.fixedit.fixeditstaff.Models.RatingModel;
import com.fixedit.fixeditstaff.R;
import com.fixedit.fixeditstaff.Utils.CommonUtils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by AliAh on 30/06/2018.
 */

public class RatingsAdapter extends RecyclerView.Adapter<RatingsAdapter.ViewHolder> {
    Context context;
    ArrayList<RatingModel> itemList;


    public RatingsAdapter(Context context, ArrayList<RatingModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rating_item_layout, parent, false);
        RatingsAdapter.ViewHolder viewHolder = new RatingsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final RatingModel model = itemList.get(position);
        holder.name.setText(model.getRatedByName());
        holder.comments.setText("Comments: " + model.getComments());
        holder.serviceName.setText("Service: " + model.getServiceName());
        holder.orderId.setText("Order Id: " + model.getOrderId());
        holder.name.setText(model.getRatedByName());
        holder.ratingbar.setRating(model.getRating());
        holder.time.setText(CommonUtils.getFormattedDate(model.getTime()));

    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, comments, orderId, serviceName, time;
        AppCompatRatingBar ratingbar;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            comments = itemView.findViewById(R.id.comments);
            orderId = itemView.findViewById(R.id.orderId);
            serviceName = itemView.findViewById(R.id.serviceName);
            ratingbar = itemView.findViewById(R.id.ratingbar);
            time = itemView.findViewById(R.id.time);

        }
    }


}
