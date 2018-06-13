package flightlive.controller;

import flightlive.model.FlightLive;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    /* /////////////////////////////////////////////////////////////////////////////// */
    /* -------------------------------- ATTRIBUTES ----------------------------------- */
    /* /////////////////////////////////////////////////////////////////////////////// */

    private FlightLive model;
    @FXML private ComboBox cbxFromCountry;
    @FXML private ComboBox cbxFromCity;
    @FXML private ComboBox cbxFromAirport;
    @FXML private ComboBox cbxToCountry;
    @FXML private ComboBox cbxToCity;
    @FXML private ComboBox cbxToAirport;
    @FXML private Button btnGo;
    @FXML private ListView lvFlights;
    @FXML private Pane paneEarth;


    /* /////////////////////////////////////////////////////////////////////////////// */
    /* -------------------------------- CONSTRUCTOR ---------------------------------- */
    /* /////////////////////////////////////////////////////////////////////////////// */

    public Controller(FlightLive model) { this.model = model; }


    /* /////////////////////////////////////////////////////////////////////////////// */
    /* ---------------------------------- METHODS ------------------------------------ */
    /* /////////////////////////////////////////////////////////////////////////////// */


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
