package flightlive.controller;

import com.interactivemesh.jfx.importer.ImportException;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import flightlive.model.Airport;
import flightlive.model.City;
import flightlive.model.Country;
import flightlive.model.FlightLive;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.MeshView;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static javafx.scene.SceneAntialiasing.DISABLED;

public class Controller implements Initializable {
    /* /////////////////////////////////////////////////////////////////////////////// */
    /* -------------------------------- ATTRIBUTES ----------------------------------- */
    /* /////////////////////////////////////////////////////////////////////////////// */

    private FlightLive model;
    @FXML private ComboBox<String> cbxFromCountry;
    @FXML private ComboBox<String> cbxFromCity;
    @FXML private ComboBox<String> cbxFromAirport;
    @FXML private ComboBox<String> cbxToCountry;
    @FXML private ComboBox<String> cbxToCity;
    @FXML private ComboBox<String> cbxToAirport;
    @FXML private Button btnGo;
    @FXML private ListView lvFlights;
    @FXML private Pane paneEarth;

    private Country currentCountryFrom;
    private Country currentCountryTo;
    private City currentCityFrom;
    private City currentCityTo;


    /* /////////////////////////////////////////////////////////////////////////////// */
    /* ---------------------------------- METHODS ------------------------------------ */
    /* /////////////////////////////////////////////////////////////////////////////// */


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        model = new FlightLive();       // Creating the model
        Group root3D = new Group();     // Groups the 3D objects within the pane
        setEarth(paneEarth, root3D);    // Loading the earth
        initializeCountryCbx();         // Loading the countries list

        // Updating the ComboBoxes of cities according to the selected country
        cbxToCountry.setOnAction(event -> updateCurrentCountryTo());
        cbxFromCountry.setOnAction(event -> updateCurrentCountryFrom());

        cbxToCity.setOnAction(event -> updateCurrentCityTo());
        cbxFromCity.setOnAction(event -> updateCurrentCityFrom());
    }


    /**
     * Updates the currentCityFrom object and sets the corresponding airports list
     */
    private void updateCurrentCityFrom() {
        // Retrieving current city
        currentCityFrom = currentCountryFrom.getCityByName(cbxFromCity.getValue());
        if (currentCityFrom != null) {
            cbxFromAirport.getItems().clear();    // Clearing the current list
            for (Airport a : currentCityFrom.getAirports())
                cbxFromAirport.getItems().add(a.getName());
        } else
            cbxFromAirport.getItems().clear();
    }


    /**
     * Updates the currentCityTo object and sets the corresponding airports list
     */
    private void updateCurrentCityTo() {
        // Retrieving current city
        currentCityTo = currentCountryTo.getCityByName(cbxToCity.getValue());
        if (currentCityTo != null) {
            cbxToAirport.getItems().clear();    // Clearing the current list
            for (Airport a : currentCityTo.getAirports())
                cbxToAirport.getItems().add(a.getName());
        } else
            cbxToAirport.getItems().clear();
    }


    /**
     * Updates the currentCountryTo object and sets the corresponding cities list
     */
    private void updateCurrentCountryTo() {
        // Retrieving current country
        currentCountryTo = model.getCountryByName(cbxToCountry.getValue());
        if (currentCountryTo != null) {
            cbxToCity.getItems().clear();   // Clearing the current list
            // Adding the new cities
            for (City c : currentCountryTo.getCities())
                cbxToCity.getItems().add(c.getName());
        } else
            cbxToCity.getItems().clear();
    }


    /**
     * Updates the currentCountryFrom object and sets the corresponding cities list
     */
    private void updateCurrentCountryFrom() {
        // Retrieving current country
        currentCountryFrom = model.getCountryByName(cbxFromCountry.getValue());
        if (currentCountryFrom != null) {
            cbxFromCity.getItems().clear();   // Clearing the current list
            // Adding the new cities
            for (City c : currentCountryFrom.getCities())
                cbxFromCity.getItems().add(c.getName());
        } else
            cbxFromCity.getItems().clear();
    }


    /**
     * Initializes the ComboBox for the countries
     */
    private void initializeCountryCbx() {
        ArrayList<Country> countries = model.getCountries();
        for (Country c : countries) {
            cbxFromCountry.getItems().add(c.getName());
            cbxToCountry.getItems().add(c.getName());
        }
    }


    /**
     * Adds the earth 3D object on the pane
     * @param pane the pane in which the earth is placed
     * @param root3D the group containing all the 3D
     */
    private void setEarth(Pane pane, Group root3D) {
        // Load geometry
        ObjModelImporter objImporter = new ObjModelImporter();
        try {
            URL modelUrl = this.getClass().getResource("/flightlive/res/Earth/earth.obj");
            objImporter.read(modelUrl);
        } catch(ImportException e) {
            System.out.println(e.getMessage());
        }
        MeshView[] meshViews = objImporter.getImport();
        Group earth = new Group(meshViews);
        root3D.getChildren().add(earth);

        // Add a camera group
        PerspectiveCamera camera = new PerspectiveCamera(true);
        new CameraManager(camera, pane, root3D);

        // Add point light
        /*PointLight light = new PointLight(Color.WHITE);
        light.setTranslateX(-180);
        light.setTranslateY(-90);
        light.setTranslateZ(-120);
        light.getScope().addAll(root3D);
        root3D.getChildren().add(light);*/

        // Add ambient light
        AmbientLight ambientLight = new AmbientLight(Color.WHITE);
        ambientLight.getScope().addAll(root3D);
        root3D.getChildren().add(ambientLight);

        // Creating a subscene
        SubScene subScene = new SubScene(root3D, 500, 500, true, DISABLED);
        subScene.setCamera(camera);
        subScene.setFill(Color.rgb(248, 249, 250));

        pane.getChildren().add(subScene);
    }
}
