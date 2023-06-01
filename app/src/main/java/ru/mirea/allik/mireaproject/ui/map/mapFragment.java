package ru.mirea.allik.mireaproject.ui.map;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import ru.mirea.allik.mireaproject.R;
import ru.mirea.allik.mireaproject.databinding.FragmentMapBinding;

public class mapFragment extends Fragment {

    private FragmentMapBinding binding;
    private Context mContext;

    private MapView mapView;

    private TextView textView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        mContext = inflater.getContext();
//        textView = binding.text1;
        mapView = binding.mapView;
        mapView.setZoomRounding(true);
        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);
        GeoPoint startPoint = new GeoPoint(55.794229, 37.700772);
        mapController.setCenter(startPoint);

        MyLocationNewOverlay locationNewOverlay = new MyLocationNewOverlay(new
                GpsMyLocationProvider(mContext), mapView);
        locationNewOverlay.enableMyLocation();
        mapView.getOverlays().add(locationNewOverlay);
//Compas
        CompassOverlay compassOverlay = new CompassOverlay(mContext, new
                InternalCompassOrientationProvider(mContext), mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);

        final DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mapView);
        scaleBarOverlay.setCentred(true);
        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        mapView.getOverlays().add(scaleBarOverlay);

        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(55.705001, 37.922335));
        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                Toast.makeText(mContext.getApplicationContext(),"Место силы(Дом)",
                        Toast.LENGTH_SHORT).show();
                binding.infoText.setText("Покровская 31 - место силы(дом)");
                return true;
            }
        });
        mapView.getOverlays().add(marker);
        marker.setIcon(ResourcesCompat.getDrawable(getResources(), org.osmdroid.library.R.drawable.osm_ic_follow_me_on, null));
        marker.setTitle("Дом родной");

        Marker marker2 = new Marker(mapView);
        marker2.setPosition(new GeoPoint(55.794259, 37.701448));
        marker2.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker2, MapView mapView) {
                Toast.makeText(mContext.getApplicationContext(),"Корпус РТУ Мирэа (крепкий)",
                        Toast.LENGTH_SHORT).show();
                binding.infoText.setText("улица Стромынка, 20 - Корпус РТУ Мирэа (крепкий)");
                return true;
            }
        });
        mapView.getOverlays().add(marker2);
        marker2.setIcon(ResourcesCompat.getDrawable(getResources(), org.osmdroid.library.R.drawable.osm_ic_follow_me_on, null));
        marker2.setTitle("Стромынка");

        Marker marker3 = new Marker(mapView);
        marker3.setPosition(new GeoPoint(55.616905, 37.673016));
        marker3.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker3, MapView mapView) {
                Toast.makeText(mContext.getApplicationContext(),"Царицыно: Я тут гулять люблю",
                        Toast.LENGTH_SHORT).show();
                binding.infoText.setText("улица Тюрина, 1 - Царицыно: Я тут гулять люблю");
                return true;
            }
        });
        mapView.getOverlays().add(marker3);
        marker3.setIcon(ResourcesCompat.getDrawable(getResources(), org.osmdroid.library.R.drawable.osm_ic_follow_me_on, null));

        marker3.setTitle("Царицыно");

        return binding.getRoot();
    }



    @Override
    public void onResume() {
        super.onResume();
        Configuration.getInstance().load(mContext,
                PreferenceManager.getDefaultSharedPreferences(mContext));
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Configuration.getInstance().save(mContext,

                PreferenceManager.getDefaultSharedPreferences(mContext));

        if (mapView != null) {
            mapView.onPause();
        }
    }
}