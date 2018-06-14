package flightlive.controller;

import com.interactivemesh.jfx.importer.ImportException;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import flightlive.model.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.MeshView;
import javafx.stage.Modality;

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

    private Country currentCountryFrom = null;
    private Country currentCountryTo = null;
    private City currentCityFrom = null;
    private City currentCityTo = null;
    private Airport currentAirportFrom = null;
    private Airport currentAirportTo = null;
    private FlightList currentFlightList = null;


    /* /////////////////////////////////////////////////////////////////////////////// */
    /* ---------------------------------- METHODS ------------------------------------ */
    /* /////////////////////////////////////////////////////////////////////////////// */


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = new FlightLive();       // Creating the model
        Group root3D = new Group();     // Groups the 3D objects within the pane
        setEarth(paneEarth, root3D);    // Loading the earth
        initializeCountryCbx();         // Loading the countries list in the ComboBoxes

        // Updating the ComboBoxes of cities according to the selected country
        cbxToCountry.setOnAction(event -> updateCurrentCountryTo());
        cbxFromCountry.setOnAction(event -> updateCurrentCountryFrom());
        // Updating the ComboBoxes of airports according to the selected city
        cbxToCity.setOnAction(event -> updateCurrentCityTo());
        cbxFromCity.setOnAction(event -> updateCurrentCityFrom());

        // Updating the current airports
        cbxToAirport.setOnAction(event ->
                currentAirportTo = currentCityTo.getAirportByName(cbxToAirport.getValue()));
        cbxFromAirport.setOnAction(event ->
                currentAirportFrom = currentCityFrom.getAirportByName(cbxFromAirport.getValue()));

        btnGo.setOnAction(event -> executeRequest()); // Executing request when button pressed
    }


    /**
     * Executes the right request based on the currently selected fields (e.g. not looking
     * for an airport if no airport is selected)
     */
    private int executeRequest() {
        // Checking for any missing information
        if (currentCountryFrom == null) {
            createDialogBox(1);
            return -1;
        }
        else if (currentCountryTo == null) {
            createDialogBox(2);
            return -1;
        }
        else if (currentCityFrom == null) {
            createDialogBox(3);
            return -1;
        }
        else if (currentCityTo == null) {
            createDialogBox(4);
            return -1;
        }

        // First case: all information provided
        if (currentAirportFrom != null && currentAirportTo != null) {
            try {
                currentFlightList = model.getFlightsBetweenAirports(currentAirportFrom.getName(), currentAirportTo.getName());
            } catch (Exception e) {
                currentFlightList = null;
                return -1;
            }
        }
        updateListView();   // Updating the ListView with the list of flights
        return 0;
    }


    private void updateListView() {
        if (currentFlightList != null) {
            lvFlights.getItems().clear();
            for (Flight f : currentFlightList.getAcList())
                lvFlights.getItems().add(f.toStringShort(currentCityFrom.getName(), currentCityTo.getName()));
        } else
            lvFlights.getItems().clear();
    }


    /**
     * Creates a dialog box specifically for the error encountered
     * @param param describes the error type
     */
    private void createDialogBox(int param) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Missing Information");
        alert.setHeaderText(null);
        alert.initModality(Modality.APPLICATION_MODAL);
        switch (param) {
            case 1:
                alert.setContentText("Please specify a country of departure");
                alert.showAndWait();
                break;
            case 2:
                alert.setContentText("Please specify a country of arrival");
                alert.showAndWait();
                break;
            case 3:
                alert.setContentText("Please specify a city of departure");
                alert.showAndWait();
                break;
            case 4:
                alert.setContentText("Please specify a city of arrival");
                alert.showAndWait();
                break;
            default:
                break;
        }
    }


    /**
     * Updates the currentCityFrom object and sets the corresponding airports list
     */
    private void updateCurrentCityFrom() {
        // Retrieving current city
        currentCityFrom = currentCountryFrom.getCityByName(cbxFromCity.getValue());
        currentAirportFrom = null;    // Resetting current value
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
        currentAirportTo = null;    // Resetting current value
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
        currentCityTo = null; currentAirportTo = null; // Resetting current values
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
        currentCityFrom = null; currentAirportFrom = null; // Resetting current values
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
