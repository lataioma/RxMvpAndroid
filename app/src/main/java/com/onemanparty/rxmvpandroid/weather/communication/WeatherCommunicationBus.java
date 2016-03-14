 package com.onemanparty.rxmvpandroid.weather.communication;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.onemanparty.rxmvpandroid.core.persistence.viewstate.HasViewState;
import com.onemanparty.rxmvpandroid.core.view.PerFragment;
import com.onemanparty.rxmvpandroid.weather.presenter.WeatherPresenter;
import com.onemanparty.rxmvpandroid.weather.view.WeatherView;
import com.onemanparty.rxmvpandroid.weather.view.model.WeatherViewModel;
import com.onemanparty.rxmvpandroid.weather.view.model.WeatherViewState;

import javax.inject.Inject;

/**
 * Object for establishing communication between view and presenter
 */
@PerFragment
/* It better to think about it as PresenterProxy, I guess */
public class WeatherCommunicationBus extends WeatherPresenter implements WeatherView,
                                                                       HasViewState<WeatherViewState> {

    private static final String VIEW_STATE_KEY = "VIEW_STATE";
    private final WeatherPresenter mPresenter;
    private WeatherViewState mViewState;

    @Override
    public WeatherViewState createViewState() {
        return new WeatherViewState();
    }

    @Inject
    public WeatherCommunicationBus(WeatherPresenter presenter) {
        mPresenter = presenter;
        mViewState = createViewState();
        attachView(DetachedWeatherView.instance());
        mPresenter.attachView(this);
    }

    @Override
    public void showLoading() {
        mViewState.setStateShowLoading();
        getView().showLoading();
    }

    @Override
    public void hideLoading() {
        mViewState.setStateHideLoading();
        getView().hideLoading();
    }

    @Override
    public void setData(WeatherViewModel data) {
        mViewState.setData(data);
        getView().setData(data);
    }

    @Override
    public void showContent() {
        mViewState.setStateShowContent();
        getView().showContent();
    }

    @Override
    public void showError(WeatherError error) {
        mViewState.setStateShowError(error);
        getView().showError(error);
    }

    // presenter
    @Override
    public void loadWeather() {
        mPresenter.loadWeather();
    }

    @Override
    public void attachView(WeatherView view) {
        super.attachView(view);
        mViewState.apply(view);
    }

    @Override
    public void detachView() {
        super.attachView(DetachedWeatherView.instance());
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        mPresenter.onCreate(arguments, savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(VIEW_STATE_KEY)) {
                mViewState = savedInstanceState.getParcelable(VIEW_STATE_KEY);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        mPresenter.onSaveInstanceState(bundle);
        bundle.putParcelable(VIEW_STATE_KEY, mViewState);
    }

    @Override
    public void onDestroy() {
        mPresenter.detachView();
        mPresenter.onDestroy();
    }

    private static class DetachedWeatherView implements WeatherView {

        private static final DetachedWeatherView view = new DetachedWeatherView();

        public static DetachedWeatherView instance() {
            return view;
        }
        @Override
        public void showLoading() {

        }

        @Override
        public void hideLoading() {

        }

        @Override
        public void setData(WeatherViewModel data) {

        }

        @Override
        public void showContent() {

        }

        @Override
        public void showError(WeatherError error) {

        }
    }
}
