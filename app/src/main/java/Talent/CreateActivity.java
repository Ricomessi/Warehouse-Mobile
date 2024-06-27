package Talent;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import com.example.holoners.R;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.holoners.TalentActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Music.DetailActivity;
import model.Talent; // Updated import statement

public class CreateActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtName, edtDebut, edtImage, edtBioData; // Updated field names
    private Spinner spinnerBranch; // Moved spinner declaration here
    private Button btnSave;
    private Calendar calendar;
    private Talent talent; // Updated model class

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createtalent);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        calendar = Calendar.getInstance();
        edtName = findViewById(R.id.edt_name);
        spinnerBranch = findViewById(R.id.edt_branch);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.branch_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBranch.setAdapter(adapter);
        edtDebut = findViewById(R.id.edt_debut);
        edtImage = findViewById(R.id.edt_image);
        edtBioData = findViewById(R.id.edt_bio_data);
        btnSave = findViewById(R.id.btn_save);

        btnSave.setOnClickListener(this);

        talent = new Talent(); // Updated model instantiation
        edtDebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
    }
    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                CreateActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDebutDate();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDebutDate() {
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
        edtDebut.setText(simpleDateFormat.format(calendar.getTime()));
    }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btn_save) {
            saveTalent();
        }
    }

    private void saveTalent() {
        String name = edtName.getText().toString().trim();
        String branch = spinnerBranch.getSelectedItem().toString().trim();
        String debut = edtDebut.getText().toString().trim();
        String image = edtImage.getText().toString().trim();
        String bioData = edtBioData.getText().toString().trim();

        boolean isEmptyFields = false;

        if (TextUtils.isEmpty(name)) {
            isEmptyFields = true;
            edtName.setError("Field ini tidak boleh kosong");
        }


        if (TextUtils.isEmpty(debut)) {
            isEmptyFields = true;
            edtDebut.setError("Field ini tidak boleh kosong");
        }

        if (TextUtils.isEmpty(image)) {
            isEmptyFields = true;
            edtImage.setError("Field ini tidak boleh kosong");
        }

        if (TextUtils.isEmpty(bioData)) {
            isEmptyFields = true;
            edtBioData.setError("Field ini tidak boleh kosong");
        }

        if (!isEmptyFields) {
            Toast.makeText(CreateActivity.this, "Saving Data...", Toast.LENGTH_SHORT).show();

            DatabaseReference dbTalent = mDatabase.child("talent"); // Updated database reference

            String id = dbTalent.push().getKey();
            talent.setId(id);
            talent.setName(name);
            talent.setBranch(branch);
            talent.setDebut(debut);
            talent.setImage(image);
            talent.setBioData(bioData);

            // insert data
            dbTalent.child(id).setValue(talent);
            Intent intent = new Intent(CreateActivity.this, TalentActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
