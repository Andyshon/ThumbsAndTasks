package com.andyshon.thumbsandtasks.Inrefaces;

import com.andyshon.thumbsandtasks.Entities.Task;

import io.realm.OrderedRealmCollection;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by andyshon on 12.05.18.
 */

public interface Contract {

    interface PresenterMain {
        void getTasks();
        void onDestroy();
    }

    interface PresenterCoverDetail {
        void addTask(Task task);
        void getTaskById(String id);
        void updateTaskById(Task task);
        void deleteTaskById(String id);
        void onDestroy();
    }

    interface PresenterCover {
        void getTaskById(String id);
        void deleteTaskById(String id);
        void onDestroy();
    }

    interface MainView {
        void showProgress();
        void hideProgress();
        void setTasksToGridView(OrderedRealmCollection<Task> noticeArrayList);
        void onResponseFailure(Throwable throwable);
    }

    interface CoverView {
        void showProgress();
        void hideProgress();
        void setTask(RealmObject object);
        void onResponseDeleteTask(String name);
        void onResponseFailure(Throwable throwable);
    }

    interface CoverDetailView {
        void showProgress();
        void hideProgress();
        void setTask(RealmObject object);
        void setUpdateTask(String name);
        void addTaskResult(String name);
        void deleteTaskResult(String name);
        void onResponseFailure(Throwable throwable);
    }

    interface Model {
        void getTasks(OnFinishedListenerMain onFinishedListenerMain);

        void getTaskById(OnFinishedListenerCover onFinishedListenerCover, String id);
        void deleteTaskById(OnFinishedListenerCover onFinishedListenerCover, String id);

        void getTaskById(OnFinishedListenerCoverDetail onFinishedListenerCoverDetail, String id);
        void addTask(OnFinishedListenerCoverDetail onFinishedListenerCoverDetail, Task task);
        void deleteTaskById(OnFinishedListenerCoverDetail onFinishedListenerCoverDetail, String id);
        void updateTaskById(OnFinishedListenerCoverDetail onFinishedListenerCoverDetail, Task task);


        interface OnFinishedListenerMain {
            void onFinished(RealmResults<Task> noticeArrayList);
            void onFailure(Throwable t);
        }

        interface OnFinishedListenerCoverDetail {
            void onFinishedGet(RealmObject object);
            void onFinishedDelete(String name);
            void onFinishedUpdate(String name);
            void onFinishedAdd(String name);
            void onFailure(Throwable throwable);
        }

        interface OnFinishedListenerCover {
            void onFinished(RealmObject object);
            void onFinished(String name);
            void onFailure(Throwable throwable);
        }
    }
}
