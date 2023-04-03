package com.example.placowkamedycznajava;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserAppointmentAdapter extends RecyclerView.Adapter<UserAppointmentAdapter.AppointmentViewHolder> {
    private ArrayList<UserAppointment> appointmentArrayList;
    private Context context;
    private final OnClickListener listener;

    public interface OnClickListener {
        void onItemCLick(int position);
        void onDeleteClick(int position);
    }

    public UserAppointmentAdapter(ArrayList<UserAppointment> appointmentList, OnClickListener listener) {
        this.appointmentArrayList = appointmentList;
        this.context = context;
        this.listener = listener;
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        public TextView personel, date, speciality;
        public ImageButton deleteButton;

        public AppointmentViewHolder(@NonNull View itemView, OnClickListener listener) {
            super(itemView);
            personel = itemView.findViewById(R.id.recycler_personel);
            date = itemView.findViewById(R.id.recycler_date);
            speciality = itemView.findViewById(R.id.recycler_speciality);
            deleteButton = itemView.findViewById(R.id.recycler_delete_icon);

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

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(getAdapterPosition());
                        }
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public UserAppointmentAdapter.AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_userpanel_item, parent, false);
        return new AppointmentViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAppointmentAdapter.AppointmentViewHolder holder, int position) {
        holder.personel.setText(appointmentArrayList.get(position).getPersonel());
        holder.speciality.setText(appointmentArrayList.get(position).getSpeciality());
        holder.date.setText(appointmentArrayList.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return appointmentArrayList.size();
    }
}
