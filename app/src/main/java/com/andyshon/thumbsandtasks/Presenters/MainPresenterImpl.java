package com.andyshon.thumbsandtasks.Presenters;

import com.andyshon.thumbsandtasks.Entities.Task;
import com.andyshon.thumbsandtasks.Inrefaces.Contract;

import io.realm.RealmResults;

/**
 * Created by andyshon on 12.05.18.
 */

public class MainPresenterImpl implements Contract.PresenterMain, Contract.Model.OnFinishedListenerMain {

    private Contract.MainView view;
    private Contract.Model model;

    public MainPresenterImpl(Contract.MainView mainView, Contract.Model model) {
        this.view = mainView;
        this.model = model;
    }

    @Override
    public void getTasks() {
        if (view != null) {
            view.showProgress();
        }
        model.getTasks(this);
    }

    @Override
    public void onDestroy() {
        view = null;
    }

    @Override
    public void onFinished(RealmResults<Task> tasks) {
        if (view != null) {
            view.setTasksToGridView(tasks);
            view.hideProgress();
        }
    }

    @Override
    public void onFailure(Throwable t) {
        if(view != null){
            view.onResponseFailure(t);
            view.hideProgress();
        }
    }
}
