package com.example.devde.rxgps;

import android.location.Address;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.rxgps.RxGps;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    /*@BindView(R.id.tv_current_location)
    TextView locationText;
    @BindView(R.id.tv_current_address)
    TextView addressText;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
     //   ButterKnife.bind(this);

        TextView locationText = findViewById(R.id.tv_current_location);
        TextView addressText = findViewById(R.id.tv_current_address);

        final RxGps rxGps = new RxGps(this);

        rxGps.lastLocation()
                .doOnSubscribe(this::addDisposable)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(location->{
                    locationText.setText(location.getLatitude()+ "," + location.getLongitude());
                },throwable->{
                    if(throwable instanceof RxGps.PermissionException) {
                        displayError(throwable.getMessage());
                    }else if(throwable instanceof RxGps.PlayServicesNotAvailableException){
                        displayError(throwable.getMessage());
                    }
                });
        rxGps.locationLowPower()
                .flatMapMaybe(rxGps::geocoding)
                .doOnSubscribe(this::addDisposable)
                .subscribe(address -> {
                    addressText.setText(getAddressText(address));
                }, throwable -> {
                    if(throwable instanceof RxGps.PermissionException){
                        displayError(throwable.getMessage());
                    }else  if (throwable instanceof RxGps.PlayServicesNotAvailableException){
                        displayError(throwable.getMessage());
                    }
                });
    }
    public void displayError(String messaage){
        Toast.makeText(this, messaage, Toast.LENGTH_SHORT).show();
    }
    private String getAddressText(Address address){
        String addressText = "";
        final int maxAddressLineIndex = address.getMaxAddressLineIndex();
        for(int i = 0;i <= maxAddressLineIndex; i++){
            addressText += address.getAddressLine(i);
            if (i != maxAddressLineIndex){
                addressText += "\n";
            }
        }
        return addressText;
    }
}
