package star.iota.sakura.base;


import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okrx2.adapter.ObservableResponse;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public abstract class StringPresenter<T> implements PVContract.Presenter {

    private final PVContract.View<T> view;

    private final CompositeDisposable mCompositeDisposable;

    @SuppressWarnings("unchecked")
    protected StringPresenter(PVContract.View view) {
        this.view = view;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void unsubscribe() {
        mCompositeDisposable.clear();
    }

    @Override
    public void get(String url) {
        mCompositeDisposable.add(
                OkGo.<String>get(url)
                        .converter(new StringConvert())
                        .adapt(new ObservableResponse<>())
                        .subscribeOn(Schedulers.io())
                        .map(s -> {
                            if (s.isFromCache()) {
                                view.isCache();
                            }
                            return dealResponse(s.body());
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(view::success, throwable -> view.error(throwable.getMessage()))
        );
    }

    protected abstract T dealResponse(String s);
}
