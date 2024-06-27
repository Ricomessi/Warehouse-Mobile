package Article;

import static Article.UpdateActivity.EXTRA_ARTICLE;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.holoners.ArticleActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.holoners.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import model.Article;

public class DetailActivity extends AppCompatActivity {

    private TextView txtTitle, txtBranch, txtImage, txtArticle;
    private ImageView imgDetailImage;
    private Article article;
    private String articleId;
    private Button btnEdit;
    public final int ALERT_DIALOG_CLOSE = 10;
    public final int ALERT_DIALOG_DELETE = 20;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailmusic);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        txtTitle = findViewById(R.id.txt_detail_title);
        txtBranch = findViewById(R.id.txt_detail_branch);
        imgDetailImage = findViewById(R.id.img_detail_images);
        txtArticle = findViewById(R.id.txt_detail_article);
        btnEdit = findViewById(R.id.btn_edit);

        article = getIntent().getParcelableExtra(EXTRA_ARTICLE);

        if (article != null) {
            articleId = article.getId();
        } else {
            article = new Article();
        }

        if (article != null) {
            txtTitle.setText(article.getTitle());
            txtBranch.setText(article.getBranch());
            loadImageFromUrl(article.getImage());
            txtArticle.setText(article.getArticle());
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Detail Data");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Tambahkan listener untuk tombol "Edit"
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Panggil metode untuk membuka UpdateActivity
                openUpdateActivity();
            }
        });
    }
    private void loadImageFromUrl(String imageUrl) {
        Picasso.get().load(imageUrl).error(R.drawable.user).into(imgDetailImage, new Callback() {
            @Override
            public void onSuccess() {
                // Gambar berhasil dimuat
            }

            @Override
            public void onError(Exception e) {
                // Terjadi kesalahan saat memuat gambar
                // Tampilkan gambar default dari drawable
                imgDetailImage.setImageResource(R.drawable.user);
            }
        });
    }



    // Metode untuk membuka UpdateActivity
    private void openUpdateActivity() {
        // Pindah ke UpdateActivity dengan membawa data Article
        Intent intent = new Intent(DetailActivity.this, UpdateActivity.class);
        intent.putExtra(UpdateActivity.EXTRA_ARTICLE, article);
        startActivity(intent);
        finish();
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
            dialogMessage = "Apakah anda ingin kembali";
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
                            Intent intent = new Intent(DetailActivity.this, ArticleActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // hapus data
                            DatabaseReference dbArticle =
                                    mDatabase.child("article").child(articleId);

                            dbArticle.removeValue();

                            Toast.makeText(DetailActivity.this, "Deleting data...",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DetailActivity.this, ArticleActivity.class);
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
