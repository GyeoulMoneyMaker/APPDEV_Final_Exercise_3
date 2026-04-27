package com.example.crud_basedappdevelopment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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

    private TextView tvContactCount;
    private ImageButton btnSort, btnDarkMode;
    private boolean isListVisible = false;
    private String currentSortOrder = DatabaseHelper.COLUMN_NAME + " ASC";
    private boolean isDarkMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        initViews();
        setupRecyclerView();
        setupListeners();
        updateContactCount();
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        btnShowAll = findViewById(R.id.btnShowAll);
        btnCreate = findViewById(R.id.btnCreate);
        btnRead = findViewById(R.id.btnRead);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnSort = findViewById(R.id.btnSort);
        btnDarkMode = findViewById(R.id.btnDarkMode);
        tvContactCount = findViewById(R.id.tvContactCount);
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void setupRecyclerView() {
        adapter = new ContactAdapter(new ArrayList<>(), contact -> {
            selectedContact = contact;
            Toast.makeText(this, "Selected: " + contact.getName(), Toast.LENGTH_SHORT).show();
        });

        adapter.setOnFavoriteClickListener(contact -> {
            contact.setFavorite(!contact.isFavorite());
            dbHelper.toggleFavorite(contact.getId(), contact.isFavorite());
            if (isListVisible) {
                refreshCurrentList();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        btnCreate.setOnClickListener(v -> showContactDialog(null));
        
        btnRead.setOnClickListener(v -> {
            if (isListVisible) {
                adapter.updateList(new ArrayList<>());
                isListVisible = false;
            } else {
                loadContacts();
                isListVisible = true;
            }
        });

        btnUpdate.setOnClickListener(v -> {
            if (selectedContact != null) {
                showContactDialog(selectedContact);
            } else {
                Toast.makeText(this, "Please select a contact to update", Toast.LENGTH_SHORT).show();
            }
        });

        btnDelete.setOnClickListener(v -> {
            if (selectedContact != null) {
                new AlertDialog.Builder(this)
                        .setTitle("Delete Contact")
                        .setMessage("Are you sure you want to delete " + selectedContact.getName() + "?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            dbHelper.deleteContact(selectedContact.getId());
                            loadContacts();
                            updateContactCount();
                            Toast.makeText(this, "Contact deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                Toast.makeText(this, "Please select a contact to delete", Toast.LENGTH_SHORT).show();
            }
        });

        btnSort.setOnClickListener(v -> {
            if (currentSortOrder.contains("ASC")) {
                currentSortOrder = DatabaseHelper.COLUMN_DATE_ADDED + " DESC";
                btnSort.setImageResource(android.R.drawable.ic_menu_today);
                Toast.makeText(this, "Sorting by: Newest", Toast.LENGTH_SHORT).show();
            } else {
                currentSortOrder = DatabaseHelper.COLUMN_NAME + " ASC";
                btnSort.setImageResource(android.R.drawable.ic_menu_sort_alphabetically);
                Toast.makeText(this, "Sorting by: A-Z", Toast.LENGTH_SHORT).show();
            }
            if (isListVisible) loadContacts();
        });

        btnDarkMode.setOnClickListener(v -> {
            isDarkMode = !isDarkMode;
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                btnDarkMode.setImageResource(android.R.drawable.ic_menu_recent_history);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                btnDarkMode.setImageResource(android.R.drawable.ic_menu_day);
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
        contactList = dbHelper.getAllContacts(currentSortOrder);
        adapter.updateList(contactList);
        selectedContact = null;
        isListVisible = true;
    }

    private void filterContacts(String query) {
        contactList = dbHelper.searchContacts(query, currentSortOrder);
        adapter.updateList(contactList);
        selectedContact = null;
        isListVisible = true;
    }

    private void refreshCurrentList() {
        String query = etSearch.getText().toString();
        if (query.isEmpty()) loadContacts();
        else filterContacts(query);
    }

    private void updateContactCount() {
        int count = dbHelper.getContactCount();
        tvContactCount.setText("Total Contacts: " + count);
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
                    contact.setName(name);
                    contact.setPhoneNumber(phone);
                    dbHelper.updateContact(contact);
                    Toast.makeText(this, "Contact updated", Toast.LENGTH_SHORT).show();
                } else {
                    dbHelper.addContact(new Contact(name, phone));
                    Toast.makeText(this, "Contact saved", Toast.LENGTH_SHORT).show();
                }
                loadContacts();
                updateContactCount();
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
}