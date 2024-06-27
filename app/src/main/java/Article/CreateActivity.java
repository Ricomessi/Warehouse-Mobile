package Article;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.holoners.ArticleActivity;
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

import Music.DetailActivity;
import model.Article;

public class CreateActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtTitle, edtImage, edtArticle; // Updated field names
    private Button btnSave;

    private Article article; // Updated model class

    private Spinner spinnerBranch; // Moved spinner declaration here

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createarticle); // Ganti dengan layout yang sesuai

        mDatabase = FirebaseDatabase.getInstance().getReference();

        edtTitle = findViewById(R.id.edt_title); // Sesuaikan dengan ID di layout
        spinnerBranch = findViewById(R.id.edt_branch); // Updated this line
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.branch_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edtImage = findViewById(R.id.edt_image); // Sesuaikan dengan ID di layout
        edtArticle = findViewById(R.id.edt_article); // Sesuaikan dengan ID di layout
        btnSave = findViewById(R.id.btn_save);

        btnSave.setOnClickListener(this);

        article = new Article(); // Updated model instantiation
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_save) {
            saveArticle();
        }
    }

    private void saveArticle() {
        String title = edtTitle.getText().toString().trim();
        String branch = spinnerBranch.getSelectedItem().toString().trim();
        String image = edtImage.getText().toString().trim();
        String articleText = edtArticle.getText().toString().trim();

        boolean isEmptyFields = false;

        if (TextUtils.isEmpty(title)) {
            isEmptyFields = true;
            edtTitle.setError("Field ini tidak boleh kosong");
        }

        if (TextUtils.isEmpty(image)) {
            isEmptyFields = true;
            edtImage.setError("Field ini tidak boleh kosong");
        }

        if (TextUtils.isEmpty(articleText)) {
            isEmptyFields = true;
            edtArticle.setError("Field ini tidak boleh kosong");
        }

        if (!isEmptyFields) {
            Toast.makeText(CreateActivity.this, "Saving Data...", Toast.LENGTH_SHORT).show();

            DatabaseReference dbArticle = mDatabase.child("article");

            String id = dbArticle.push().getKey();
            article.setId(id);
            article.setTitle(title);
            article.setBranch(branch);
            article.setImage(image);
            article.setArticle(articleText);

            // insert data
            dbArticle.child(id).setValue(article);
            Intent intent = new Intent(CreateActivity.this, ArticleActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
