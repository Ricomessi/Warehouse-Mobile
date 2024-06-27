package Talent;

import static Talent.UpdateActivity.EXTRA_TALENT;

import com.example.holoners.TalentActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import model.Talent;

public class DetailActivity extends AppCompatActivity {

    private TextView txtName, txtBranch, txtDebut, txtImage, txtBioData;
    private ImageView imgDetailImage;
    private Talent talent;
    private String talentId;
    private Button btnEdit;
    public final int ALERT_DIALOG_CLOSE = 10;
    public final int ALERT_DIALOG_DELETE = 20;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailtalent);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        txtName = findViewById(R.id.txt_detail_name);
        txtBranch = findViewById(R.id.txt_detail_branch);
        txtDebut = findViewById(R.id.txt_detail_debut);
        imgDetailImage = findViewById(R.id.img_detail_image);
        txtBioData = findViewById(R.id.txt_detail_bio_data);
        btnEdit = findViewById(R.id.btn_edit);

        talent = getIntent().getParcelableExtra(EXTRA_TALENT);

        if (talent != null) {
            talentId = talent.getId();
        } else {
            talent = new Talent();
        }

        if (talent != null) {
            txtName.setText(talent.getName());
            txtBranch.setText(talent.getBranch());
            txtDebut.setText(talent.getDebut());
            loadImageFromUrl(talent.getImage());
            txtBioData.setText(talent.getBioData());
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
        // Pindah ke UpdateActivity dengan membawa data Talent
        Intent intent = new Intent(DetailActivity.this, UpdateActivity.class);
        intent.putExtra(UpdateActivity.EXTRA_TALENT, talent);
        startActivity(intent);
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
                            Intent intent = new Intent(DetailActivity.this, TalentActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // hapus data
                            DatabaseReference dbTalent =
                                    mDatabase.child("talent").child(talentId);

                            dbTalent.removeValue();

                            Toast.makeText(DetailActivity.this, "Deleting data...",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DetailActivity.this, TalentActivity.class);
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
