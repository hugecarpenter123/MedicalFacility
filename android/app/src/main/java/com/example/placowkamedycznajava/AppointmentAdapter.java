package com.example.placowkamedycznajava;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {
    ArrayList<Appointment> appointmentArrayList;
    Context context;

    public interface OnClickListener {
        public void onItemCLick(int position);
    }

    private List<Appointment> appointmentList;
    public AppointmentAdapter(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        public int id;
        public String personel, date;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public AppointmentAdapter.AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentAdapter.AppointmentViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
