package com.example.crud_basedappdevelopment;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private final List<Contact> contactList;
    private final OnContactClickListener clickListener;
    private OnFavoriteClickListener favoriteClickListener;

    public interface OnContactClickListener {
        void onContactClick(Contact contact);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Contact contact);
    }

    public ContactAdapter(List<Contact> contactList, OnContactClickListener clickListener) {
        this.contactList = contactList;
        this.clickListener = clickListener;
    }

    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.favoriteClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.tvName.setText(contact.getName());
        holder.tvPhone.setText(contact.getPhoneNumber());
        holder.tvDate.setText(contact.getDateAdded());

        if (contact.isFavorite()) {
            holder.btnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
            holder.btnFavorite.setColorFilter(Color.parseColor("#FFD700")); // Gold
        } else {
            holder.btnFavorite.setImageResource(android.R.drawable.btn_star_big_off);
            holder.btnFavorite.setColorFilter(null);
        }

        holder.itemView.setOnClickListener(v -> clickListener.onContactClick(contact));

        holder.btnFavorite.setOnClickListener(v -> {
            if (favoriteClickListener != null) {
                favoriteClickListener.onFavoriteClick(contact);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public void updateList(List<Contact> newList) {
        this.contactList.clear();
        this.contactList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvDate;
        ImageButton btnFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }
}
