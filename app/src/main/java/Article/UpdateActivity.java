package Article;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.holoners.ArticleActivity;
import com.example.holoners.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import model.Article;

public class UpdateActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtTitle, edtBranch, edtImage, edtArticle;
    private Button btnUpdate;
    private Spinner spinnerBranch;
    public static final String EXTRA_ARTICLE = "extra_article";
    public final int ALERT_DIALOG_CLOSE = 10;
    public final int ALERT_DIALOG_DELETE = 20;

    private Article article;
    private String articleId;

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatearticle);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        edtTitle = findViewById(R.id.edt_title);
        spinnerBranch = findViewById(R.id.edt_edit_branch);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.branch_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBranch.setAdapter(adapter);
        edtImage = findViewById(R.id.edt_edit_image);
        edtArticle = findViewById(R.id.edt_article);
        btnUpdate = findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(this);

        article = getIntent().getParcelableExtra(EXTRA_ARTICLE);

        if (article != null) {
            articleId = article.getId();
        } else {
            article = new Article();
        }

        if (article != null) {
            edtTitle.setText(article.getTitle());
            spinnerBranch.setSelection(adapter.getPosition(article.getBranch()));
            edtImage.setText(article.getImage());
            edtArticle.setText(article.getArticle());
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Data");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_update) {
            updateArticle();
        }
    }

    public void updateArticle() {
        String title = edtTitle.getText().toString().trim();
        String branch = spinnerBranch.getSelectedItem().toString().trim();
        String image = edtImage.getText().toString().trim();
        String articleText = edtArticle.getText().toString().trim();

        boolean isEmptyFields = false;

        if (TextUtils.isEmpty(title)) {
            isEmptyFields = true;
            edtTitle.setError("Field ini tidak boleh kosong");
        }

        if (TextUtils.isEmpty(branch)) {
            isEmptyFields = true;
            edtBranch.setError("Field ini tidak boleh kosong");
        }

        // Check other attributes
        if (TextUtils.isEmpty(image)) {
            isEmptyFields = true;
            edtImage.setError("Field ini tidak boleh kosong");
        }

        if (TextUtils.isEmpty(articleText)) {
            isEmptyFields = true;
            edtArticle.setError("Field ini tidak boleh kosong");
        }

        if (!isEmptyFields) {
            Toast.makeText(UpdateActivity.this, "Updating Data...", Toast.LENGTH_SHORT).show();

            article.setTitle(title);
            article.setBranch(branch);
            article.setImage(image);
            article.setArticle(articleText);

            DatabaseReference dbArticle = mDatabase.child("article");

            // Update data
            dbArticle.child(articleId).setValue(article);
            Intent intent = new Intent(UpdateActivity.this, ArticleActivity.class);
            startActivity(intent);
            finish();
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
            // Handle home action
            showAlertDialog(ALERT_DIALOG_CLOSE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void showAlertDialog(int type) {
        final boolean isDialogClose = type == ALERT_DIALOG_CLOSE;
        String dialogTitle, dialogMessage;

        if (isDialogClose) {
            dialogTitle = "Batal";
            dialogMessage = "Apakah anda ingin membatalkan perubahan pada form";
        } else {
            dialogTitle = "Hapus Data";
            dialogMessage = "Apakah anda yakin ingin menghapus item ini";
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(dialogTitle);
        alertDialogBuilder.setMessage(dialogMessage)
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (isDialogClose) {
                            Intent intent = new Intent(UpdateActivity.this, ArticleActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // delete data
                            DatabaseReference dbArticle =
                                    mDatabase.child("article").child(articleId);

                            dbArticle.removeValue();

                            Toast.makeText(UpdateActivity.this, "Deleting data...",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(UpdateActivity.this, ArticleActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
