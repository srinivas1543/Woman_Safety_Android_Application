package com.example.womensafe;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
public class ContactAdapter extends
        RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private ArrayList<String> contacts;
    private OnContactClickListener listener;
    public interface OnContactClickListener {
        void onContactClick(int position);
    }
    public ContactAdapter(ArrayList<String> contacts, OnContactClickListener listener) {
        this.contacts = contacts;
        this.listener = listener;
    }
    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int
            viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent,
                        false);
        return new ContactViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.bind(contacts.get(position), listener);
    }
    @Override
    public int getItemCount() {
        return contacts.size();
    }
    class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView contactName;
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            contactName = itemView.findViewById(android.R.id.text1);
        }
        public void bind(String contact, OnContactClickListener listener) {
            contactName.setText(contact);
            itemView.setOnClickListener(v -> listener.onContactClick(getAdapterPosition()));
        }
    }
}
