package com.andyshon.thumbsandtasks.Models;

import android.content.Context;
import android.support.annotation.NonNull;

import com.andyshon.thumbsandtasks.Database.RealmConfiguration;
import com.andyshon.thumbsandtasks.Entities.Task;
import com.andyshon.thumbsandtasks.Inrefaces.Contract;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by andyshon on 12.05.18.
 */

public class GetModelImpl implements Contract.Model {

    private Realm realm;

    public GetModelImpl(Context context){
        RealmConfiguration.init(context);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void getTasks(OnFinishedListenerMain onFinishedListenerMain) {
        RealmResults<Task> tasks = realm.where(Task.class).findAll();
        if (tasks != null)
            onFinishedListenerMain.onFinished(tasks);
        else
            onFinishedListenerMain.onFailure(new Throwable("Something went wrong..."));
    }

    @Override
    public void getTaskById(OnFinishedListenerCoverDetail onFinishedListenerCoverDetail, String id) {
        RealmObject object = realm.where(Task.class).equalTo("id", id).findFirst();
        if (object != null)
            onFinishedListenerCoverDetail.onFinishedGet(object);
        else
            onFinishedListenerCoverDetail.onFailure(new Throwable("Something went wrong..."));
    }

    @Override
    public void addTask(OnFinishedListenerCoverDetail onFinishedListenerCoverDetail, Task task) {
        realm.beginTransaction();
        realm.copyToRealm(task);
        realm.commitTransaction();
        onFinishedListenerCoverDetail.onFinishedAdd(task.getName());
        //onFinishedListenerCoverDetail.onFailure(new Throwable("Something went wrong..."));
    }

    @Override
    public void deleteTaskById(final OnFinishedListenerCoverDetail onFinishedListenerCoverDetail, final String id) {

        // Asynchronously delete task on a background thread
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                RealmResults<Task> result = realm.where(Task.class).equalTo("id", id).findAll();
                String name = result.first().getName();
                result.deleteAllFromRealm();
                if (result.isEmpty()) {
                    onFinishedListenerCoverDetail.onFinishedDelete(name);
                }
                else
                    onFinishedListenerCoverDetail.onFailure(new Throwable("Something went wrong..."));
            }
        });
    }

    @Override
    public void updateTaskById(final OnFinishedListenerCoverDetail onFinishedListenerCoverDetail, final Task existingTask) {

        // Asynchronously update task on a background thread
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                Task task = realm.where(Task.class).equalTo("id", existingTask.getId()).findFirst();
                if (task != null) {
                    task.setName(existingTask.getName());
                    task.setBitmap(existingTask.getBitmap());
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                onFinishedListenerCoverDetail.onFinishedUpdate(existingTask.getName());
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(@NonNull Throwable error) {
                onFinishedListenerCoverDetail.onFailure(new Throwable("Something went wrong..."));
            }
        });
    }

    @Override
    public void getTaskById(OnFinishedListenerCover onFinishedListenerCover, String id) {
        RealmObject object = realm.where(Task.class).equalTo("id", id).findFirst();
        if (object != null)
            onFinishedListenerCover.onFinished(object);
        else
            onFinishedListenerCover.onFailure(new Throwable("Something went wrong..."));
    }

    @Override
    public void deleteTaskById(final OnFinishedListenerCover onFinishedListenerCover, final String id) {

        // Asynchronously delete task on a background thread
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                RealmResults<Task> result = realm.where(Task.class).equalTo("id", id).findAll();
                String name = result.first().getName();
                result.deleteAllFromRealm();
                if (result.isEmpty())
                    onFinishedListenerCover.onFinished(name);
                else
                    onFinishedListenerCover.onFailure(new Throwable("Something went wrong..."));
            }
        });
    }

}
