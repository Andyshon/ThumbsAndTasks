package com.andyshon.thumbsandtasks.Presenters;

import com.andyshon.thumbsandtasks.Entities.Task;
import com.andyshon.thumbsandtasks.Inrefaces.Contract;

import io.realm.RealmObject;

/**
 * Created by andyshon on 12.05.18.
 */

public class CoverDetailPresenterImpl
        implements Contract.PresenterCoverDetail, Contract.Model.OnFinishedListenerCoverDetail {


    private Contract.CoverDetailView view;
    private Contract.Model model;

    public CoverDetailPresenterImpl(Contract.CoverDetailView view, Contract.Model model){
        this.view = view;
        this.model = model;
    }


    @Override
    public void getTaskById(String id) {
        if (view != null) {
            view.showProgress();
        }
        model.getTaskById(this, id);
    }

    @Override
    public void addTask(Task task) {
        if (view != null) {
            view.showProgress();
        }
        model.addTask(this, task);
    }

    @Override
    public void updateTaskById(Task task) {
        if (view != null) {
            view.showProgress();
        }
        model.updateTaskById(this, task);
    }

    @Override
    public void deleteTaskById(String id) {
        if (view != null) {
            view.showProgress();
        }
        model.deleteTaskById(this, id);
    }

    @Override
    public void onDestroy() {
        view = null;
    }

    @Override
    public void onFinishedAdd(String name) {
        if (view != null) {
            view.addTaskResult(name);
            view.hideProgress();
        }
    }

    @Override
    public void onFinishedGet(RealmObject object) {
        if (view != null) {
            view.setTask(object);
            view.hideProgress();
        }
    }

    @Override
    public void onFinishedUpdate(String name) {
        if (view != null) {
            view.setUpdateTask(name);
            view.hideProgress();
        }
    }

    @Override
    public void onFinishedDelete(String name) {
        if (view != null) {
            view.deleteTaskResult(name);
            view.hideProgress();
        }
    }

    @Override
    public void onFailure(Throwable t) {
        if (view != null) {
            view.onResponseFailure(t);
            view.hideProgress();
        }
    }
}
