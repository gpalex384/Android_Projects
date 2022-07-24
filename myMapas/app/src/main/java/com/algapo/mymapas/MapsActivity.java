package com.algapo.mymapas;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.algapo.mymapas.databinding.ActivityMapsBinding;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private FusedLocationProviderClient mFusedLocationClient;
    private double mLatitude = 0.0, mLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private Drawer mDrawer;

    public MapsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(16000);///
        locationRequest.setFastestInterval(8000);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        mLatitude = location.getLatitude();
                        mLongitude = location.getLongitude();
                        Toast.makeText(MapsActivity.this, "Lat: " + mLatitude + " Lon: " +
                                mLongitude, Toast.LENGTH_LONG).show();
                        Location parqueMeridiano=new Location("parqueMeridiano");
                        parqueMeridiano.setLatitude(40.0);
                        parqueMeridiano.setLongitude(0.0);
                        Toast.makeText(MapsActivity.this,
                                "Distancia: " + String.format("%.2f", location.distanceTo(parqueMeridiano)/1000)
                                        + " Km", Toast.LENGTH_LONG).show();
                    }
                }
            }
        };

        new DrawerBuilder().withActivity(this).build();
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.mipmap.ic_launcher)
                .addProfiles(
                        new ProfileDrawerItem().withName("myMapasv1.0")
                                .withEmail("mymapas@mapas.com")
                                .withIcon(getResources().getDrawable(R.mipmap.ic_launcher_round, getTheme()))
                )
                .build();
        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(1).withName("Opción 1"),
                        new PrimaryDrawerItem().withIdentifier(2).withName("Opción 2"),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withIdentifier(3).withName("Cerrar menú"),
                        new SecondaryDrawerItem().withIdentifier(4).withName("Salir App")
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch ((int)drawerItem.getIdentifier()) {
                            case 1:
                                Toast.makeText(MapsActivity.this, "Tit(1): " + leerLocalizaciones().get(1).getTitulo(), Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                Toast.makeText(MapsActivity.this, "Opción 2 pulsada", Toast.LENGTH_SHORT).show();
                                break;
                            case 3:
                                break;
                            case 4:
                                finish();
                                break;
                        }
                        mDrawer.closeDrawer();
                        return true;
                    }
                }).build();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng castellon = new LatLng(40, 0);
        //mMap.addMarker(new MarkerOptions().position(castellon).title("Parque del Meridiano").snippet("Habitantes: 180000"));
        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(40, 0)).title("Parque")
                .snippet("del meridiano").icon(BitmapDescriptorFactory.fromResource(R.mipmap.parque)));
        marker.setTag("parques");
        // Movemos la cámara
        mMap.moveCamera(CameraUpdateFactory.newLatLng(castellon));

        mMap.setOnInfoWindowClickListener(this);

        // Otras funcionalidades
        //mMap.clear();
        //marker.remove();

        //Cambia el zoom (0 por defecto, rango 0-21, los float se describen mediante una f,
        //ejemplo 6.5f. admite entero al hacer cast implícito)
        //Relación entre el zoom y lo que se espera visualizar: 1-World 5- Continent 10-City 15-Streets 20-Buildings
        //mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        // MIZONA
        LatLngBounds MIZONA = new LatLngBounds(new LatLng(37.5, -3), new LatLng(41.5, 2));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MIZONA.getCenter(),7.5f));

        Toast.makeText(this, "Zoom min: " + mMap.getMinZoomLevel(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Zoom max: " + mMap.getMaxZoomLevel(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Zoom actual: " + mMap.getCameraPosition().zoom, Toast.LENGTH_SHORT).show();

        // BLOQUEAR ZOOM
        //mMap.setMinZoomPreference(8.0f);
        //mMap.setMaxZoomPreference(16.0f);

        // Guardar cambios de zoom
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Toast.makeText(MapsActivity.this, "Zoom cambiado a: " + mMap.getCameraPosition().zoom,
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Cambiar tipo de mapa
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // (des)Habilitar controles de ZOOM (y otros controles)
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //mMap.getUiSettings().setCompassEnabled(false);
        //mMap.getUiSettings().setRotateGesturesEnabled(false);
        //mMap.getUiSettings().setMapToolbarEnabled(false);

        // Listener en el propio mapa
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //Toast.makeText(MapsActivity.this, "Mapa pulsado", Toast.LENGTH_SHORT).show();
                showDialog("Programador: " + "\n\n" + "Alejandro García");
            }
        });

        // Animaciones de la cámara
        /*
        LatLng SYDNEY = new LatLng(-33.88,151.21);
        LatLng MOUNTAIN_VIEW = new LatLng(37.4, -122.1);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 15)); //Inicio con 15 de zoom
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 5000, null); //5 segundos
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(MOUNTAIN_VIEW) //Destino final
                .zoom(17) //Nuevo zoom final
                .bearing(90) //Orientación de la cámara al este
                .tilt(30) //Cámara a 30 grados
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        */

        // Estilos de mapa
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));

        // Icono Mi Ubicación (ej1)
        //if(ContextCompat.checkSelfPermission(this,
        //        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        //    mMap.setMyLocationEnabled(true);
        //}

        // Icono Mi Ubicación (ej2)
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
        } else {
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Escuchado el clic en: " + marker.getPosition().latitude + " " +
                marker.getPosition().longitude, Toast.LENGTH_SHORT).show();
    }

    private void requestLocations() {
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
        } else {
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }
    private void removeLocations() {
        mFusedLocationClient.removeLocationUpdates(locationCallback);
    }

    public void showDialog(String mMensaje) { //crea diálogo modal
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //Añade botones
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss(); //Cierra si OK
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss(); //Cierra si CANCEL
            }
        });
        //Inserta elementos
        builder.setTitle(R.string.app_name);
        builder.setMessage(mMensaje);
        builder.setIcon(R.mipmap.ic_launcher);
        //Crea diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public List<Localizacion> leerLocalizaciones() {
        List<Localizacion> localizaciones = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(getResources().openRawResource(R.raw.localizaciones));
            Element raiz = doc.getDocumentElement();
            NodeList items = raiz.getElementsByTagName("localizacion");
            for( int i = 0; i < items.getLength(); i++ ) { //recorre todos los elementos
                Node nodoLocalizacion = items.item(i);
                Localizacion localizacion = new Localizacion();
                for(int j = 0; j < nodoLocalizacion.getChildNodes().getLength(); j++ ) {//recorre hijos
                    Node nodoActual = nodoLocalizacion.getChildNodes().item(j);
//comprueba si es un elemento
                    if( nodoActual.getNodeType() == Node.ELEMENT_NODE ) {
                        if( nodoActual.getNodeName().equalsIgnoreCase("titulo") )
                            localizacion.setTitulo(nodoActual.getChildNodes().item(0)
                                    .getNodeValue());
                        else if( nodoActual.getNodeName().equalsIgnoreCase("fragmento") )
                            localizacion.setFragmento(nodoActual.getChildNodes().item(0)
                                    .getNodeValue());
                        else if( nodoActual.getNodeName().equalsIgnoreCase("etiqueta") )
                            localizacion.setEtiqueta(nodoActual.getChildNodes().item(0)
                                    .getNodeValue());
                        else if( nodoActual.getNodeName().equalsIgnoreCase("latitud") ) {
                            String latitud = nodoActual.getChildNodes().item(0)
                                    .getNodeValue();
                            localizacion.setLatitud(Double.parseDouble(latitud));
                        }
                        else if( nodoActual.getNodeName().equalsIgnoreCase("longitud") ) {
                            String longitud = nodoActual.getChildNodes().item(0)
                                    .getNodeValue();
                            localizacion.setLongitud(Double.parseDouble(longitud));
                        }
                    }
                }//fin for 2 (hijos)
                localizaciones.add(localizacion);
            }//fin for 1 (elementos)
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return localizaciones;
    }//fin leerLocalizaciones

}