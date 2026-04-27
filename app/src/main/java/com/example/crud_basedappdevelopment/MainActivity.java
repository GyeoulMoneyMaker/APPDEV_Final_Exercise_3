package com.example.crud_basedappdevelopment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ContactAdapter adapter;
    private List<Contact> contactList = new ArrayList<>();
    private Contact selectedContact = null;

    private EditText etSearch;
    private Button btnShowAll, btnCreate, btnRead, btnUpdate, btnDelete;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        initViews();
        setupRecyclerView();
        setupListeners();
        
        loadContacts();
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        btnShowAll = findViewById(R.id.btnShowAll);
        btnCreate = findViewById(R.id.btnCreate);
        btnRead = findViewById(R.id.btnRead);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void setupRecyclerView() {
        adapter = new ContactAdapter(contactList, contact -> {
            selectedContact = contact;
            Toast.makeText(this, "Selected: " + contact.getName(), Toast.LENGTH_SHORT).show();
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        btnCreate.setOnClickListener(v -> showContactDialog(null));
        
        btnRead.setOnClickListener(v -> loadContacts());

        btnUpdate.setOnClickListener(v -> {
            if (selectedContact != null) {
                showContactDialog(selectedContact);
            } else {
                Toast.makeText(this, "Please select a contact to update", Toast.LENGTH_SHORT).show();
            }
        });

        btnDelete.setOnClickListener(v -> {
            if (selectedContact != null) {
                showDeleteConfirmation();
            } else {
                Toast.makeText(this, "Please select a contact to delete", Toast.LENGTH_SHORT).show();
            }
        });

        btnShowAll.setOnClickListener(v -> {
            etSearch.setText("");
            loadContacts();
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterContacts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadContacts() {
        contactList = dbHelper.getAllContacts();
        adapter.updateList(contactList);
        selectedContact = null;
    }

    private void filterContacts(String query) {
        contactList = dbHelper.searchContacts(query);
        adapter.updateList(contactList);
        selectedContact = null;
    }

    private void showContactDialog(Contact contact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_contact, null);
        builder.setView(view);

        EditText etName = view.findViewById(R.id.etName);
        EditText etPhone = view.findViewById(R.id.etPhone);
        
        if (contact != null) {
            builder.setTitle("Update Contact");
            etName.setText(contact.getName());
            etPhone.setText(contact.getPhoneNumber());
        } else {
            builder.setTitle("Create Contact");
        }

        builder.setPositiveButton(contact != null ? "Update" : "Save", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (validateInput(name, phone)) {
                if (contact != null) {
                    dbHelper.updateContact(contact.getId(), name, phone);
                    Toast.makeText(this, "Contact updated", Toast.LENGTH_SHORT).show();
                } else {
                    dbHelper.addContact(name, phone);
                    Toast.makeText(this, "Contact saved", Toast.LENGTH_SHORT).show();
                }
                loadContacts();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private boolean validateInput(String name, String phone) {
        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!phone.matches("\\d+")) {
            Toast.makeText(this, "Phone number must contain only digits", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Contact")
                .setMessage("Are you sure you want to delete " + selectedContact.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteContact(selectedContact.getId());
                    Toast.makeText(this, "Contact deleted", Toast.LENGTH_SHORT).show();
                    loadContacts();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}