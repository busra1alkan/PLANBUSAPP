package com.bee.planbus.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bee.planbus.activities.MapActivity;
import com.bee.planbus.databinding.RecyclerRowBinding;
import com.bee.planbus.model.Place;

import java.util.List;


public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceHolder> {


    List<Place> placeList;
    public PlaceAdapter(List<Place> placeList){
        this.placeList = placeList;
    }

    @NonNull
    @Override
    public PlaceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PlaceHolder(recyclerRowBinding);
    }
    @Override

    public void onBindViewHolder(@NonNull PlaceHolder holder, int position) {
        holder.recyclerRowBinding.recyclerViewTextView.setText(placeList.get(position).name);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), MapActivity.class);
                intent.putExtra("info", "old");
                intent.putExtra("place", placeList.get(position));
                holder.itemView.getContext().startActivity(intent );

            }
        });
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    public class PlaceHolder extends RecyclerView.ViewHolder{

        RecyclerRowBinding recyclerRowBinding;
        public PlaceHolder(RecyclerRowBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding = recyclerRowBinding;
        }
    }

}
