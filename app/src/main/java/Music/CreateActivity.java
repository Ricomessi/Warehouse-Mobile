package Music;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.holoners.MusicActivity;
import com.example.holoners.R;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import model.Music;

public class CreateActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText edtTitle, edtName, edtImage, edtDesc;
    private Button btnSave;

    private Music music;

    DatabaseReference mDatabase;

    private Spinner spinnerBranch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createmusic);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        edtTitle = findViewById(R.id.edt_title);
        edtName = findViewById(R.id.edt_name);
        spinnerBranch = findViewById(R.id.edt_branch); // Updated this line
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.branch_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBranch.setAdapter(adapter);
        edtImage = findViewById(R.id.edt_image);
        edtDesc = findViewById(R.id.edt_edit_desc);
        btnSave = findViewById(R.id.btn_save);

        btnSave.setOnClickListener(this);

        music = new Music(); // Updated model instantiation
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_save) {
            saveMusic();
        }
    }

    private void saveMusic() {
        String title = edtTitle.getText().toString().trim();
        String name = edtName.getText().toString().trim();
        String branch = spinnerBranch.getSelectedItem().toString().trim();
        String image = edtImage.getText().toString().trim();
        String desc = edtDesc.getText().toString().trim();

        boolean isEmptyFields = false;

        if (TextUtils.isEmpty(title)) {
            isEmptyFields = true;
            edtTitle.setError("Field ini tidak boleh kosong");
        }

        if (TextUtils.isEmpty(name)) {
            isEmptyFields = true;
            edtName.setError("Field ini tidak boleh kosong");
        }

        if (TextUtils.isEmpty(image)) {
            isEmptyFields = true;
            edtImage.setError("Field ini tidak boleh kosong");
        }

        if (TextUtils.isEmpty(desc)) {
            isEmptyFields = true;
            edtDesc.setError("Field ini tidak boleh kosong");
        }

        if (!isEmptyFields) {
            Toast.makeText(CreateActivity.this, "Saving Data...", Toast.LENGTH_SHORT).show();

            DatabaseReference dbMusic = mDatabase.child("music");

            String id = dbMusic.push().getKey();
            music.setId(id);
            music.setTitle(title);
            music.setName(name);
            music.setBranch(branch);
            music.setImage(image);
            music.setDesc(desc);

            // insert data
            dbMusic.child(id).setValue(music);
            Intent intent = new Intent(CreateActivity.this, MusicActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
