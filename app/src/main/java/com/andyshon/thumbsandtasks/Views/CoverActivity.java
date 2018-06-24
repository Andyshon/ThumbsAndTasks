package com.andyshon.thumbsandtasks.Views;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andyshon.thumbsandtasks.Entities.Task;
import com.andyshon.thumbsandtasks.Inrefaces.Contract;
import com.andyshon.thumbsandtasks.Models.GetModelImpl;
import com.andyshon.thumbsandtasks.Presenters.CoverPresenterImpl;
import com.andyshon.thumbsandtasks.R;

import io.realm.RealmObject;

public class CoverActivity extends AppCompatActivity implements Contract.CoverView{

    private Contract.PresenterCover presenter;

    private ProgressBar progressBar;
    private ImageView ivCover;
    private Task currentItem;
    private String image_Id;
    private TextView etName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover);

        initUI();
    }


    /*
    * Destroy presenter when activity is closed in purpose to avoid memory-leak
    * */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }


    /*
    * Initialize base UI components and presenter
    * */

    private void initUI() {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#a0ff8000")));
            bar.setTitle("Детали");
            bar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        image_Id = intent.getStringExtra("image_Id");

        ivCover = findViewById(R.id.ivCover);
        ivCover.setImageResource(R.drawable.ic_compare_arrows_black_24dp);
        etName = findViewById(R.id.tvName);
        progressBar = findViewById(R.id.spinner);
        progressBar.setIndeterminate(true);


        presenter = new CoverPresenterImpl(this, new GetModelImpl(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (image_Id != null)
            presenter.getTaskById(image_Id);
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setTask(RealmObject object) {
        if (object == null)
            finish();
        else {
            currentItem = (Task) object;
            etName.setText(currentItem.getName());

            if (currentItem.getBitmap() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(currentItem.getBitmap(), 0, currentItem.getBitmap().length);
                ivCover.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public void onResponseDeleteTask(String name) {
        Toast.makeText(this, name + " was deleted", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onResponseFailure(Throwable throwable) {
        Toast.makeText(CoverActivity.this,
                "Something went wrong...Error message: " + throwable.getMessage(),
                Toast.LENGTH_LONG).show();
    }


    /*
    * AlertDialog to confirm to delete task or to cancel
    * */

    private void ConfirmDeleteImage() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CoverActivity.this);

        alertDialogBuilder
                .setMessage("Удалить " + currentItem.getName() + "?")
                .setCancelable(false)
                .setPositiveButton("Да",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        presenter.deleteTaskById(image_Id);
                    }
                })
                .setNegativeButton("Нет",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();
    }


    /*
    * For menu purpose
    * */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.updateDetail:
                Intent intent = new Intent(CoverActivity.this, CoverDetailActivity.class);
                intent.putExtra("image_Id", image_Id);
                intent.putExtra("image_Name", currentItem.getName());
                startActivity(intent);
                return true;
            case R.id.deleteDetail:
                ConfirmDeleteImage();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /*
    * Inflate menu
    * */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_simple, menu);
        return true;
    }

}
