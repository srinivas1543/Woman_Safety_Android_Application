package com.example.womensafe;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private Button btnSendSOS, btnAddContact;
    private RecyclerView rvContacts;
    private ContactAdapter adapter;
    private EditText etContact;

    private ArrayList<String> contacts;
    private DatabaseHelper databaseHelper;

    private String locationText = "Location unavailable";
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        btnSendSOS = findViewById(R.id.btnSendSOS);
        btnAddContact = findViewById(R.id.btnAddContact);
        etContact = findViewById(R.id.etContact);
        rvContacts = findViewById(R.id.rvContacts);

        // Initialize database and load contacts
        databaseHelper = new DatabaseHelper(this);
        contacts = databaseHelper.getAllContacts();

        // Set up RecyclerView and adapter
        adapter = new ContactAdapter(contacts, this::deleteContact);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.setAdapter(adapter);

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Request necessary permissions
        requestPermissions();

        // Button click listeners
        btnAddContact.setOnClickListener(v -> addContact());
        btnSendSOS.setOnClickListener(v -> startLocationUpdates());
    }

    private void requestPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        if (!hasPermissions(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
        }
    }

    private boolean hasPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void addContact() {
        String contact = etContact.getText().toString().trim();
        if (!contact.isEmpty()) {
            if (contact.matches("\\d{10}")) { // Check if the contact is exactly 10 digits
                if (databaseHelper.addContact(contact)) {
                    contacts.add(contact);
                    adapter.notifyItemInserted(contacts.size() - 1);
                    etContact.setText("");
                    Toast.makeText(this, "Contact added successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to add contact.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter a valid 10-digit number.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter a contact number.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteContact(int position) {
        String contact = contacts.get(position);
        if (databaseHelper.deleteContact(contact)) {
            contacts.remove(position);
            adapter.notifyItemRemoved(position);
            Toast.makeText(this, "Contact deleted successfully.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to delete contact.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // Update location every 10 seconds
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    locationText = "https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            captureImage();
        } else {
            Toast.makeText(this, "Location permission not granted.", Toast.LENGTH_SHORT).show();
        }
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageUri = getImageUri(imageBitmap);
            shareOnWhatsApp();
        }
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "WomenSafe SOS Image", null);
        return Uri.parse(path);
    }

    private void shareOnWhatsApp() {
        String batteryPercentage = getBatteryPercentage();
        String emergencyMessage = "Emergency! I need help.\nLocation: " + locationText + "\nBattery: " + batteryPercentage;
        for (String contact : contacts) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, emergencyMessage);
            if (imageUri != null) {
                intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                intent.setType("image/jpeg");
            } else {
                intent.setType("text/plain");
            }
            intent.setPackage("com.whatsapp");
            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "WhatsApp is not installed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getBatteryPercentage() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);
        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : 1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : 1;
        int batteryPct = (int) ((level / (float) scale) * 100);
        return batteryPct + "%";
    }
}
