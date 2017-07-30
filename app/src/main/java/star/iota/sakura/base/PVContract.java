package star.iota.sakura.base;


public interface PVContract {
    interface Presenter {
        void unsubscribe();

        void get(String url);
    }

    interface View<T> {

        void success(T result);

        void error(String error);

    }
}
