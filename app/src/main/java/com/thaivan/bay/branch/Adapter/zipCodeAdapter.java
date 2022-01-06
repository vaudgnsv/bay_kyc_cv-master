package com.thaivan.bay.branch.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thaivan.bay.branch.R;
import java.util.List;

public class zipCodeAdapter extends RecyclerView.Adapter<zipCodeAdapter.ViewHolder> {

    private Context context;
    private List<String> ItemCode;

    OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(ViewHolder holder, View view, int position);
    }

    public zipCodeAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.item_zipcode, parent, false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        if(position == 0)
            holder.divider.setVisibility(View.GONE);

        holder.codeLabel.setText(ItemCode.get(position));
        holder.codeLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(holder,v, position);
            }
        });
        holder.setOnItemClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return ItemCode != null ? ItemCode.size() : 0;
    }

    public List<String> getItem(){
        return  ItemCode;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void setItem(List<String> item) {
        ItemCode = item;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView codeLabel;
        private View divider;
        OnItemClickListener listenr;

        public ViewHolder(View itemView) {
            super(itemView);
            codeLabel = itemView.findViewById( R.id.codeLabel);
            divider = itemView.findViewById(R.id.divider);
        }

        public void setOnItemClickListener(OnItemClickListener listener){
            this.listenr = listener;
        }
    }
}
