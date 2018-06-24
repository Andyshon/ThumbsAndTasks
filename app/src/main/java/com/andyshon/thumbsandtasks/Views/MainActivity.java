package com.andyshon.thumbsandtasks.Views;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.andyshon.thumbsandtasks.Adapter.TaskAdapter;
import com.andyshon.thumbsandtasks.Entities.Task;
import com.andyshon.thumbsandtasks.Inrefaces.Contract;
import com.andyshon.thumbsandtasks.Models.GetModelImpl;
import com.andyshon.thumbsandtasks.Presenters.MainPresenterImpl;
import com.andyshon.thumbsandtasks.R;

import io.realm.OrderedRealmCollection;

public class MainActivity extends AppCompatActivity implements Contract.MainView{

    private Contract.PresenterMain presenter;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CoverDetailActivity.class);
                startActivity(intent);
            }
        });

        progressBar = findViewById(R.id.spinner);
        progressBar.setIndeterminate(true);

        presenter = new MainPresenterImpl(this, new GetModelImpl(this));
        presenter.getTasks();
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.INVISIBLE);
    }


    /*
    * Create adapter and set tasks to GridView
    * */

    @Override
    public void setTasksToGridView(final OrderedRealmCollection<Task> tasks) {
        Toast.makeText(this, "size:" + tasks.size(), Toast.LENGTH_SHORT).show();
        GridView gridView = (GridView) findViewById(R.id.gridView);
        TaskAdapter mAdapter = new TaskAdapter(tasks);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Task task = tasks.get(i);
                String imageId = task.getId();
                String imageName = task.getName();

                Intent intent = new Intent(MainActivity.this, CoverActivity.class);
                intent.putExtra("image_Id", imageId);
                intent.putExtra("image_Name", imageName);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResponseFailure(Throwable throwable) {
        Toast.makeText(MainActivity.this,
                "Something went wrong...Error message: " + throwable.getMessage(),
                Toast.LENGTH_LONG).show();
    }
}
