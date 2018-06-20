package flightlive.controller;

import com.interactivemesh.jfx.importer.ImportException;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import flightlive.geometry.Fx3DGroup;
import flightlive.geometry.Geometry3D;
import flightlive.model.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.PickResult;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.stage.Modality;
import javafx.stage.Popup;

import java.net.URL;
import java.util.*;

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
    @FXML private ListView<String> lvFlights;
    @FXML private Label flightLabel;
    @FXML private Button btnGo;
    @FXML private Button btnClear;
    @FXML private Pane paneEarth;
    @FXML private ColorPicker cpDepAirport;
    @FXML private ColorPicker cpArrAirport;
    @FXML private ColorPicker cpFlight;
    @FXML private ColorPicker cpPath;

    // Current values are stored here
    private Country currentCountryFrom = null;
    private Country currentCountryTo = null;
    private City currentCityFrom = null;
    private City currentCityTo = null;
    private Airport currentAirportFrom = null;
    private Airport currentAirportTo = null;
    private FlightList currentFlightList = null;
    private Flight currentFlight = null;
    private Fx3DGroup currentPlane = null;

    private PhongMaterial planesMaterial;
    private PhongMaterial airportsDepMaterial;
    private PhongMaterial airportsArrMaterial;
    private PhongMaterial pathMaterialLow;
    private PhongMaterial pathMaterialHigh;

    // Used for 3D stuff
    private Geometry3D geo3D = new Geometry3D();


    /* /////////////////////////////////////////////////////////////////////////////// */
    /* ---------------------------------- METHODS ------------------------------------ */
    /* /////////////////////////////////////////////////////////////////////////////// */


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = new FlightLive();                          // Creating the model
        Group root3D = new Group();                        // Groups the 3D objects within the pane
        Group earthGroup = setEarth(paneEarth, root3D);    // Loading the earth
        // Creating the group of planes
        Group planesGroup = new Group();
        earthGroup.getChildren().add(planesGroup);
        // Creating the group of cities
        Group citiesGroup = new Group();
        earthGroup.getChildren().add(citiesGroup);
        // Creating the group for the path
        Group pathGroup = new Group();
        earthGroup.getChildren().add(pathGroup);
        // Creating the group for the radius
        Group radiusGroup = new Group();
        earthGroup.getChildren().add(radiusGroup);

        // Setting the materials for the planes, path and cities
        cpFlight.setValue(Color.RED);
        cpDepAirport.setValue(Color.YELLOW);
        cpArrAirport.setValue(Color.GREEN);
        cpPath.setValue(Color.ORANGE);
        planesMaterial = new PhongMaterial(cpFlight.getValue());
        airportsDepMaterial = new PhongMaterial(cpDepAirport.getValue());
        airportsArrMaterial = new PhongMaterial(cpArrAirport.getValue());
        pathMaterialHigh = new PhongMaterial(cpPath.getValue());
        pathMaterialLow = new PhongMaterial(pathMaterialHigh.getDiffuseColor().deriveColor(20, 1, 1, 1));

        initializeCountryCbx(); // Loading the countries list in the ComboBoxes
        displayAirports(citiesGroup);

        // Updating the ComboBoxes of cities according to the selected country
        cbxToCountry.setOnAction(event -> updateCurrentCountryTo());
        cbxFromCountry.setOnAction(event -> updateCurrentCountryFrom());
        // Updating the ComboBoxes of airports according to the selected city
        cbxToCity.setOnAction(event -> updateCurrentCityTo());
        cbxFromCity.setOnAction(event -> updateCurrentCityFrom());

        // Updating the current airports
        cbxToAirport.setOnAction(event -> {
            if (currentCityTo != null)
                currentAirportTo = currentCityTo.getAirportByName(cbxToAirport.getValue());
            else
                currentAirportTo = null;
        });
        cbxFromAirport.setOnAction(event -> {
            if (currentCityFrom != null)
                currentAirportFrom = currentCityFrom.getAirportByName(cbxFromAirport.getValue());
            else
                currentAirportFrom = null;
        });

        // When clicking on a button
        btnGo.setOnAction(event -> executeRequest(planesGroup, citiesGroup));
        btnClear.setOnAction(event -> clear(planesGroup, citiesGroup, pathGroup));

        // When changing the color
        cpDepAirport.setOnAction(event -> airportsDepMaterial.setDiffuseColor(cpDepAirport.getValue()));
        cpArrAirport.setOnAction(event -> airportsArrMaterial.setDiffuseColor(cpArrAirport.getValue()));
        cpFlight.setOnAction(event -> planesMaterial.setDiffuseColor(cpFlight.getValue()));
        cpPath.setOnAction(event -> {
            pathMaterialHigh.setDiffuseColor(cpPath.getValue());
            pathMaterialLow.setDiffuseColor(pathMaterialHigh.getDiffuseColor().deriveColor(20, 1, 1 , 1));
        });

        // When clicking on the list of flights
        lvFlights.setOnMousePressed(event -> updateLabelAndGlobe(planesGroup, pathGroup));

        // Picking the plane
        planesGroup.setOnMousePressed(event -> {
            PickResult res = event.getPickResult();
            if (res.getIntersectedNode() instanceof MeshView) {
                // Getting the plane Fx3DGroup object
                Node plane = res.getIntersectedNode().getParent().getParent().getParent();
                if (plane instanceof Fx3DGroup) {
                    if (currentPlane != null)
                        currentPlane.set3DScale(0.5);
                    // Updating the current flight and plane
                    currentFlight = currentFlightList.getFlightById(Integer.parseInt(plane.getId()));
                    currentPlane = (Fx3DGroup)plane;
                    // Updating the label
                    flightLabel.setText(currentFlight.toString());
                    // Showing the path and making the plane bigger
                    geo3D.displayPath(currentFlight, currentPlane, pathGroup, pathMaterialLow, pathMaterialHigh);
                    updateListViewSelection();
                }
            }
        });

        // Picking the airport
        citiesGroup.setOnMousePressed(event -> {
            PickResult res = event.getPickResult();
            if (res.getIntersectedNode() instanceof Sphere) {
                Airport a = model.getAirportByIcao(res.getIntersectedNode().getId());
                if (a != null) {
                    if (currentCityFrom == null) {
                        cbxFromCountry.setValue(a.getCity().getCountry().getName());
                        cbxFromCity.setValue(a.getCity().getName());
                        cbxFromAirport.setValue(a.getName());
                    } else if (currentCityTo == null) {
                        cbxToCountry.setValue(a.getCity().getCountry().getName());
                        cbxToCity.setValue(a.getCity().getName());
                        cbxToAirport.setValue(a.getName());
                    }
                }
            }
        });

        earthGroup.setOnMouseClicked(event -> {
            PickResult res = event.getPickResult();
            Point3D p = res.getIntersectedPoint();
            double lat = java.lang.Math.toDegrees(java.lang.Math.asin(-p.getY())) + 0.2f;
            // First case issue: lon between 0° and 180°
            double lon = java.lang.Math.toDegrees(java.lang.Math.acos(res.getIntersectedPoint().getZ()
                    / java.lang.Math.cos(java.lang.Math.asin(-res.getIntersectedPoint().getY())))) - 2.8f;

            // Second case issue: lon between -90° and 90°
            //double lon = java.lang.Math.toDegrees(java.lang.Math.asin(-p.getX()
            //        / java.lang.Math.cos(java.lang.Math.asin(-p.getY())))) - 2.8f;

            //geo3D.displayTown(citiesGroup, "MON POINT", (float)lat, (float)lon, airportsDepMaterial);
            geo3D.displayRadius(radiusGroup, 1, p);
        });
    }


    /*private void askRadius(double lat, double lon, Group citiesGroup, Group planesGroup) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("In which radius do you want to search for the flights ?");
        alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
        System.err.println(alert.showAndWait().get());

        // SEE ALSO: Popup class, DialogPane class
    }*/


    /**
     * Displays all airports affected by at least one flight in progress
     * @param citiesGroup the group containing all the cities
     */
    private void displayAirports(Group citiesGroup) {
        ArrayList<Country> countries = model.getCountries();
        for (Country c : countries) {
            for (City ci : c.getCities()) {
                for (Airport a : ci.getAirports()) {
                    double lat = a.getLatitude();
                    double lon = a.getLongitude();
                    geo3D.displayTown(citiesGroup, a.getIcao(),
                            (float)lat, (float)lon, airportsArrMaterial);
                }
            }
        }
    }


    /**
     * Updates the selected ListView item based on the currently selected plane
     * @return
     */
    private int updateListViewSelection() {
        for (int i = 0 ; i < lvFlights.getItems().size() ; i++) {
            String s = lvFlights.getItems().get(i);
            if (s.length() > 2) {
                if (s.split(" ")[1].equals(currentPlane.getId())) {
                    lvFlights.getSelectionModel().select(i);
                    return 0;
                }
            }
        }
        return -1;
    }


    /**
     * Clears all the ComboBoxes, label, listview, globe...
     * @param planesGroup the group of planes
     * @param citiesGroup the group of cities
     */
    private void clear(Group planesGroup, Group citiesGroup, Group pathGroup) {
        cbxToAirport.getSelectionModel().clearSelection();
        cbxToAirport.getItems().clear();

        cbxFromAirport.getSelectionModel().clearSelection();
        cbxFromAirport.getItems().clear();

        cbxFromCity.getSelectionModel().clearSelection();
        cbxFromCity.getItems().clear();

        cbxToCity.getSelectionModel().clearSelection();
        cbxToCity.getItems().clear();

        cbxFromCountry.getSelectionModel().clearSelection();
        cbxFromCountry.getItems().clear();

        cbxToCountry.getSelectionModel().clearSelection();
        cbxToCountry.getItems().clear();

        initializeCountryCbx(); // Loading the countries list in the ComboBoxes

        currentCountryFrom = null;
        currentCountryTo = null;
        currentCityFrom = null;
        currentCityTo = null;
        currentAirportFrom = null;
        currentAirportTo = null;

        lvFlights.getItems().clear();
        flightLabel.setText("");
        citiesGroup.getChildren().clear();
        planesGroup.getChildren().clear();
        pathGroup.getChildren().clear();
        displayAirports(citiesGroup);
    }


    /**
     * Updates the label with information about the currently selected flight and updates the globe
     * with the currently selected plane
     * @param planesGroup the group of planes
     */
    private void updateLabelAndGlobe(Group planesGroup, Group pathGroup) {
        // Retrieving the selected item
        String selectedItem = lvFlights.getSelectionModel().getSelectedItems().toString();

        // If it's not an empty item (i.e. '[]')
        if (selectedItem.length() > 2) {
            String selectedFlightId = selectedItem.split(" ")[1];
            // Updating the label
            currentFlight = currentFlightList.getFlightById(Integer.parseInt(selectedFlightId));
            if (currentFlight != null) {
                flightLabel.setText(currentFlight.toString());
            } else
                flightLabel.setText("");

            if (currentPlane != null)
                currentPlane.set3DScale(0.5);

            // Picking the plane
            for (Node n : planesGroup.getChildren()) {
                if (n instanceof Fx3DGroup) {
                    if (n.getId().equals(selectedFlightId)) {
                        currentPlane = (Fx3DGroup)n;
                    }
                }
            }

            geo3D.displayPath(currentFlight, currentPlane, pathGroup, pathMaterialLow, pathMaterialHigh);

        } else
            flightLabel.setText("");
    }


    /**
     * Executes the right request based on the currently selected fields (e.g. not looking
     * for an airport if no airport is selected)
     * @param planesGroup a group containing all the planes
     * @param citiesGroup a group containing all the cities
     */
    private int executeRequest(Group planesGroup, Group citiesGroup) {

        // Not enough information given
        if (currentCityFrom == null && currentCityTo == null) {
            createDialogBox();
            return -1;
        }
        // Resetting the whole interface
        planesGroup.getChildren().clear();
        citiesGroup.getChildren().clear();
        lvFlights.getItems().clear();
        flightLabel.setText("");

        if (currentCountryFrom != null && currentCityFrom != null) {
            if (currentAirportFrom == null) {
                if (currentCountryTo == null) {
                    try {
                        // x x o
                        // o o o
                        currentFlightList = model.getFlightsCityFrom(currentCityFrom.getName());
                    } catch (Exception e) {
                        currentFlightList = null;
                        return -1;
                    }
                } else {
                    if (currentCityTo == null) {
                        try {
                            // x x o
                            // x o o
                            currentFlightList = model.getFlightsFromCityToCountry(currentCityFrom.getName(), currentCountryTo.getName());
                        } catch (Exception e) {
                            currentFlightList = null;
                            return -1;
                        }
                    } else {
                        if (currentAirportTo == null) {
                            try {
                                // x x o
                                // x x o
                                currentFlightList = model.getFlightsCities(currentCityFrom.getName(), currentCityTo.getName());
                            } catch (Exception e) {
                                currentFlightList = null;
                                return -1;
                            }
                        } else {
                            try {
                                // x x o
                                // x x x
                                currentFlightList = model.getFlightsFromCityToAirport(currentCityFrom.getName(), currentAirportTo.getName());
                            } catch (Exception e) {
                                currentFlightList = null;
                                return -1;
                            }
                        }
                    }
                }
            } else {
                if (currentCountryTo == null) {
                    try {
                        // x x x
                        // o o o
                        currentFlightList = model.getFlightsAirportFrom(currentAirportFrom.getName());
                    } catch (Exception e) {
                        currentFlightList = null;
                        return -1;
                    }
                } else {
                    if (currentCityTo == null) {
                        try {
                            // x x x
                            // x o o
                            currentFlightList = model.getFlightsFromAirportToCountryOrCity(currentAirportFrom.getName(), currentCountryTo.getName());
                        } catch (Exception e) {
                            currentFlightList = null;
                            return -1;
                        }
                    } else {
                        if (currentAirportTo == null) {
                            try {
                                // x x x
                                // x x o
                                currentFlightList = model.getFlightsFromAirportToCountryOrCity(currentAirportFrom.getName(), currentCityTo.getName());
                            } catch (Exception e) {
                                currentFlightList = null;
                                return -1;
                            }
                        } else {
                            try {
                                // x x x
                                // x x x
                                currentFlightList = model.getFlightsBetweenAirports(currentAirportFrom.getName(), currentAirportTo.getName());
                            } catch (Exception e) {
                                currentFlightList = null;
                                return -1;
                            }
                        }
                    }
                }
            }
        } else {
            if (currentAirportTo == null) {
                if (currentCountryFrom == null) {
                    try {
                        // o o o
                        // x x o
                        currentFlightList = model.getFlightsCityTo(currentCityTo.getName());
                    } catch (Exception e) {
                        currentFlightList = null;
                        return -1;
                    }
                } else {
                    if (currentCityFrom == null) {
                        try {
                            // x o o
                            // x x o
                            currentFlightList = model.getFlightsFromCountryOrCityToCity(currentCountryFrom.getName(), currentCityTo.getName());
                        } catch (Exception e) {
                            currentFlightList = null;
                            return -1;
                        }
                    }
                }
            } else {
                if (currentCountryFrom == null) {
                    try {
                        // o o o
                        // x x x
                        currentFlightList = model.getFlightsAirportTo(currentAirportTo.getName());
                    } catch (Exception e) {
                        currentFlightList = null;
                        return -1;
                    }
                } else {
                    if (currentCityFrom == null) {
                        try {
                            // x o o
                            // x x x
                            currentFlightList = model.getFlightsFromCountryOrCityToAirport(currentCountryFrom.getName(), currentAirportTo.getName());
                        } catch (Exception e) {
                            currentFlightList = null;
                            return -1;
                        }
                    } else {
                        try {
                            // x x o
                            // x x x
                            currentFlightList = model.getFlightsFromCountryOrCityToAirport(currentCityFrom.getName(), currentAirportTo.getName());
                        } catch (Exception e) {
                            currentFlightList = null;
                            return -1;
                        }
                    }
                }
            }
        }

        if (currentFlightList == null || currentFlightList.getAcList().length == 0)
            return -1;

        updateListView(); // Updating the ListView with the list of flights
        updateEarth(planesGroup, citiesGroup); // Updating the earth
        return 0;
    }


    /**
     * Adds the cities and planes to the earth geometry
     * @param planesGroup a group containing all the planes
     * @param citiesGroup a group containing all the cities
     */
    private void updateEarth(Group planesGroup, Group citiesGroup) {
        citiesGroup.getChildren().clear();
        // If the current departure city is given
        if (currentCityFrom != null) {
            double latCityFrom = currentCityFrom.getAirports().get(0).getLatitude();
            double lonCityFrom = currentCityFrom.getAirports().get(0).getLongitude();
            geo3D.displayTown(citiesGroup, currentCityFrom.getName(),
                    (float)latCityFrom, (float)lonCityFrom, airportsDepMaterial);
        // If not
        } else {
            ArrayList<String> icaos = new ArrayList<>();
            for (Flight f : currentFlightList.getAcList()) {
                String temp = f.From.substring(0,4);
                if (!icaos.contains(temp)) icaos.add(temp);
            }
            for (String icao : icaos) {
                Airport temp = model.getAirportByIcao(icao);
                if (temp != null) {
                    geo3D.displayTown(citiesGroup, temp.getName(), (float)temp.getLatitude(),
                            (float)temp.getLongitude(), airportsDepMaterial);
                }
            }
        }
        // If the current arrival city is given
        if (currentCityTo != null) {
            double latCityTo = currentCityTo.getAirports().get(0).getLatitude();
            double lonCityTo = currentCityTo.getAirports().get(0).getLongitude();
            geo3D.displayTown(citiesGroup, currentCityTo.getName(),
                    (float)latCityTo, (float)lonCityTo, airportsArrMaterial);
        } else {
            ArrayList<String> icaos = new ArrayList<>();
            for (Flight f : currentFlightList.getAcList()) {
                String temp = f.To.substring(0,4);
                if (!icaos.contains(temp)) icaos.add(temp);
            }
            for (String icao : icaos) {
                Airport temp = model.getAirportByIcao(icao);
                if (temp != null) {
                    geo3D.displayTown(citiesGroup, temp.getName(), (float)temp.getLatitude(),
                            (float)temp.getLongitude(), airportsArrMaterial);
                }
            }
        }

        planesGroup.getChildren().clear();
        for (Flight f : currentFlightList.getAcList())
            geo3D.createPlane(planesGroup, String.valueOf(f.Id),f.Lat, f.Long, f.Trak, planesMaterial);
    }


    /**
     * Updates the ListView with all the flights found by the request
     */
    private void updateListView() {
        if (currentFlightList != null) {
            lvFlights.getItems().clear();
            for (Flight f : currentFlightList.getAcList()) {
                if (currentCityFrom != null && currentCityTo != null) {
                    lvFlights.getItems().add(f.toStringShort(currentCityFrom.getName(), currentCityTo.getName()));
                } else if (currentCityFrom == null && currentCityTo != null) {
                    Airport temp = model.getAirportByIcao(f.From.substring(0,4));
                    String departureCity;
                    if (temp == null) departureCity = "city not found";
                    else departureCity = temp.getCity().getName();

                    lvFlights.getItems().add(f.toStringShort(departureCity, currentCityTo.getName()));
                } else if (currentCityFrom != null && currentCityTo == null) {
                    Airport temp = model.getAirportByIcao(f.To.substring(0,4));
                    String arrivalCity;
                    if (temp == null) arrivalCity = "city not found";
                    else arrivalCity = temp.getCity().getName();
                    lvFlights.getItems().add(f.toStringShort(currentCityFrom.getName(), arrivalCity));
                }
            }
        } else
            lvFlights.getItems().clear();
    }


    /**
     * Creates a dialog box specifically for the error encountered
     */
    private void createDialogBox() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Missing Information");
        alert.setHeaderText(null);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContentText("Please specify at least one city");
        alert.showAndWait();
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

            // Sorting cities
            ArrayList<City> cities = currentCountryTo.getCities();
            Collections.sort(cities, new Comparator<City>() {
                @Override
                public int compare(City c1, City c2) {
                    return c1.getName().compareTo(c2.getName());
                }
            });

            // Adding the new cities
            for (City c : cities)
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

            // Sorting cities
            ArrayList<City> cities = currentCountryFrom.getCities();
            Collections.sort(cities, new Comparator<City>() {
                @Override
                public int compare(City c1, City c2) {
                    return c1.getName().compareTo(c2.getName());
                }
            });

            // Adding the new cities
            for (City c : cities)
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
    private Group setEarth(Pane pane, Group root3D) {
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

        // Add ambient light
        AmbientLight ambientLight = new AmbientLight(Color.WHITE);
        ambientLight.getScope().addAll(root3D);
        root3D.getChildren().add(ambientLight);

        // Creating a subscene
        SubScene subScene = new SubScene(root3D, 500, 500, true, DISABLED);
        subScene.setCamera(camera);
        subScene.setFill(Color.rgb(248, 249, 250));
        pane.getChildren().add(subScene);
        return earth;
    }
}
