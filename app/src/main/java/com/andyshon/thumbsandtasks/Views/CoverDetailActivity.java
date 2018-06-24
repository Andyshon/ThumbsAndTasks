package com.andyshon.thumbsandtasks.Views;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.andyshon.thumbsandtasks.Entities.Task;
import com.andyshon.thumbsandtasks.Inrefaces.Contract;
import com.andyshon.thumbsandtasks.Models.GetModelImpl;
import com.andyshon.thumbsandtasks.Presenters.CoverDetailPresenterImpl;
import com.andyshon.thumbsandtasks.R;
import com.andyshon.thumbsandtasks.ImageUtils;

import java.io.ByteArrayOutputStream;
import java.util.Random;

import io.realm.RealmObject;

public class CoverDetailActivity extends AppCompatActivity implements Contract.CoverDetailView, View.OnClickListener {

    private Contract.PresenterCoverDetail presenter;

    public final int SELECT_IMAGE = 100;
    private ProgressBar progressBar;
    private ImageView ivCover;
    private Task currentTask;
    private EditText etName;
    private String image_Id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover_detail);

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
            bar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        image_Id = intent.getStringExtra("image_Id");

        Button btnUploadImage = findViewById(R.id.btnUploadImage);
        btnUploadImage.setOnClickListener(this);

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        Button btnRemove = findViewById(R.id.btnRemove);
        btnRemove.setVisibility(View.GONE);
        btnRemove.setOnClickListener(this);

        ivCover = findViewById(R.id.ivCover);
        ivCover.setImageResource(R.drawable.ic_compare_arrows_black_24dp);
        etName = findViewById(R.id.etName);
        progressBar = findViewById(R.id.spinner);
        progressBar.setIndeterminate(true);


        presenter = new CoverDetailPresenterImpl(this, new GetModelImpl(this));

        if (image_Id != null) {
            bar.setTitle("Изменить");
            btnRemove.setVisibility(View.VISIBLE);
            presenter.getTaskById(image_Id);
        }
        else {
            bar.setTitle("Новый");
            hideProgress();
        }
    }

    @Override
    public void setTask(RealmObject object) {
        currentTask = (Task) object;
        etName.setText(currentTask.getName());

        if (currentTask.getBitmap() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(currentTask.getBitmap(), 0, currentTask.getBitmap().length);
            ivCover.setImageBitmap(bitmap);
        }
    }

    @Override
    public void setUpdateTask(String name) {
        Toast.makeText(this, name + " was updated", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void addTaskResult(String name) {
        Toast.makeText(this, name + " was added", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void deleteTaskResult(String name) {
        Toast.makeText(this, name + " was deleted", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onResponseFailure(Throwable throwable) {
        Toast.makeText(CoverDetailActivity.this,
                "Something went wrong...Error message: " + throwable.getMessage(),
                Toast.LENGTH_LONG).show();
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnUploadImage:
                SelectImageFromGallery();
                break;
            case R.id.btnSave:
                SaveOrUpdateTask();
                break;
            case R.id.btnRemove:
                ConfirmDeleteTask();
                break;
        }
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
        }
        return super.onOptionsItemSelected(item);
    }


    /*
    * AlertDialog to confirm to delete task or to cancel
    * */

    private void ConfirmDeleteTask() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CoverDetailActivity.this);

        alertDialogBuilder
                .setMessage("Удалить " + currentTask.getName() + "?")
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

    private void SaveOrUpdateTask() {
        if (image_Id != null){
            if (CheckItem())
                UpdateTask();
            else
                Toast.makeText(this, "Отсутствует картинка или её название", Toast.LENGTH_SHORT).show();
        }
        else {
            if (CheckItem())
                SaveTask();
            else
                Toast.makeText(this, "Отсутствует картинка или её название", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean CheckItem(){
        return !((ivCover == null) || (etName.getText().length() == 0));
    }


    /*
    * Save task
    * */

    private void SaveTask() {
        try {
            BitmapDrawable drawable = (BitmapDrawable) ivCover.getDrawable();
            Bitmap bmp = drawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            final byte[] byteArray = stream.toByteArray();
            Task task = new Task(String.valueOf(new Random().nextInt(10000000) + System.currentTimeMillis()),
                    etName.getText().toString().trim(), byteArray);
            presenter.addTask(task);
        }
        catch (ClassCastException ex){
            Toast.makeText(this, "Картинка не добавлена", Toast.LENGTH_SHORT).show();
        }
    }


    /*
    * Update existing task
    * */

    private void UpdateTask(){
        BitmapDrawable drawable = (BitmapDrawable) ivCover.getDrawable();
        Bitmap bmp = drawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        final byte[] byteArray = stream.toByteArray();

        Task task = new Task(currentTask.getId(), etName.getText().toString().trim(), byteArray);
        presenter.updateTaskById(task);
    }

    private void SelectImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Выберите картинку"), SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = imageReturnedIntent.getData();

                    String scheme = imageUri.getScheme();
                    if(scheme.equals(ContentResolver.SCHEME_CONTENT)) {
                        try {
                            Bitmap bitmap = ImageUtils.decodeUri(imageUri, CoverDetailActivity.this);
                            if (bitmap != null)
                                ivCover.setImageBitmap(bitmap);
                        } catch (Exception e) {e.printStackTrace();}
                    }
                }
        }
    }
}
