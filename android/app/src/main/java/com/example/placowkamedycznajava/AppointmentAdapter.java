package com.example.placowkamedycznajava;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {
    private ArrayList<Appointment> appointmentArrayList;
    private Context context;
    private final OnClickListener listener;

    public interface OnClickListener {
        public void onItemCLick(int position);
    }

    public AppointmentAdapter(ArrayList<Appointment> appointmentList, Context context, OnClickListener listener) {
        this.appointmentArrayList = appointmentList;
        this.context = context;
        this.listener = listener;

    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        public TextView personel, date, speciality;

        public AppointmentViewHolder(@NonNull View itemView, OnClickListener listener) {
            super(itemView);
            personel = itemView.findViewById(R.id.recycler_personel);
            date = itemView.findViewById(R.id.recycler_date);
            speciality = itemView.findViewById(R.id.recycler_speciality);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            listener.onItemCLick(getAdapterPosition());
                        }
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public AppointmentAdapter.AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new AppointmentViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentAdapter.AppointmentViewHolder holder, int position) {
        holder.personel.setText(appointmentArrayList.get(position).getPersonel());
        holder.speciality.setText(appointmentArrayList.get(position).getSpeciality());
        holder.date.setText(appointmentArrayList.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return appointmentArrayList.size();
    }
}
