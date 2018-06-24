package com.andyshon.thumbsandtasks.Presenters;

import com.andyshon.thumbsandtasks.Inrefaces.Contract;

import io.realm.RealmObject;

/**
 * Created by andyshon on 12.05.18.
 */

public class CoverPresenterImpl implements Contract.PresenterCover, Contract.Model.OnFinishedListenerCover {

    private Contract.CoverView view;
    private Contract.Model model;

    public CoverPresenterImpl(Contract.CoverView view, Contract.Model model) {
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
    public void onFinished(RealmObject object) {
        if (view != null) {
            view.setTask(object);
            view.hideProgress();
        }
    }

    @Override
    public void onFinished(String name) {
        if (view != null) {
            view.onResponseDeleteTask(name);
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
