package Talent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.holoners.TalentActivity;
import com.example.holoners.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import model.Talent;

public class UpdateActivity extends AppCompatActivity implements View.OnClickListener  {

    private EditText edtName, edtDebut, edtImage, edtBioData;
    private Button btnUpdate;
    private Spinner spinnerBranch;
    public static final String EXTRA_TALENT = "extra_talent";
    public final int ALERT_DIALOG_CLOSE = 10;
    public final int ALERT_DIALOG_DELETE = 20;
    private DatePickerDialog datePickerDialog;
    private Talent talent;
    private String talentId;

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatetalent);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        edtName = findViewById(R.id.edt_edit_nama);
        spinnerBranch = findViewById(R.id.edt_edit_branch);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.branch_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBranch.setAdapter(adapter);
        edtDebut = findViewById(R.id.edt_edit_debut);
        edtImage = findViewById(R.id.edtImage);
        edtBioData = findViewById(R.id.edt_edit_bio_data);
        btnUpdate = findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(this);
        edtDebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        talent = getIntent().getParcelableExtra(EXTRA_TALENT);

        if (talent != null) {
            talentId = talent.getId();
        } else {
            talent = new Talent();
        }

        if (talent != null) {
            edtName.setText(talent.getName());
            spinnerBranch.setSelection(adapter.getPosition(talent.getBranch()));
            edtDebut.setText(talent.getDebut());
            edtImage.setText(talent.getImage());
            edtBioData.setText(talent.getBioData());
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Data");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Set tanggal terpilih pada EditText edtDebut
                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                edtDebut.setText(selectedDate);
            }
        }, year, month, dayOfMonth);

        datePickerDialog.show();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_update) {
            updateTalent();
        }
    }

    public void updateTalent() {
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

        // Periksa atribut lainnya
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
            Toast.makeText(UpdateActivity.this, "Updating Data...", Toast.LENGTH_SHORT).show();

            talent.setName(name);
            talent.setBranch(branch);
            talent.setDebut(debut);
            talent.setImage(image);
            talent.setBioData(bioData);

            DatabaseReference dbTalent = mDatabase.child("talent");

            // Update data
            dbTalent.child(talentId).setValue(talent);
            Toast.makeText(UpdateActivity.this, "Data Updated Successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UpdateActivity.this, TalentActivity.class);
            startActivity(intent);
            finish();  // Optional: Close UpdateActivity to avoid going back to it when pressing back in MainActivity
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_form, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_delete) {
            // Handle delete action
            showAlertDialog(ALERT_DIALOG_DELETE);
            return true;
        } else if (itemId == android.R.id.home) {
            // Handle close action
            showAlertDialog(ALERT_DIALOG_CLOSE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAlertDialog(int type) {
        final boolean isDialogClose = type == ALERT_DIALOG_CLOSE;
        String dialogTitle, dialogMessage;

        if (isDialogClose) {
            dialogTitle = "Cancel Changes";
            dialogMessage = "Do you want to cancel the changes?";
        } else {
            dialogTitle = "Delete Data";
            dialogMessage = "Do you want to delete this data?";
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(dialogTitle);
        alertDialogBuilder.setMessage(dialogMessage);
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (isDialogClose) {
                    finish();
                } else {
                    DatabaseReference dbTalent = mDatabase.child("talent").child(talentId);
                    dbTalent.removeValue();

                    Toast.makeText(UpdateActivity.this, "Data Deleted Successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(UpdateActivity.this, TalentActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}