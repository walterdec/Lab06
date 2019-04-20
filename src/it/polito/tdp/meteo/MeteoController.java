package it.polito.tdp.meteo;

import java.net.URL;
import java.time.Month;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import it.polito.tdp.meteo.bean.Citta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

public class MeteoController {
	
	Model model;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private ChoiceBox<Month> boxMese;

	@FXML
	private Button btnCalcola;

	@FXML
	private Button btnUmidita;

	@FXML
	private TextArea txtResult;

	@FXML
	void doCalcolaSequenza(ActionEvent event) {
		model.reset();
		txtResult.clear();
		Month mese = boxMese.getValue();
		for(Citta c : model.calcolaSequenza(mese)) {
			txtResult.appendText(c.getNome()+"\n");
		}
	}

	@FXML
	void doCalcolaUmidita(ActionEvent event) {
		txtResult.clear();
		Month m = boxMese.getValue();
		if(m==null)
			return;
		
		for(Citta c : model.getCitta()) {
			Double u = model.getUmiditaMedia(m, c);
			txtResult.appendText(String.format("Città %s con umidità %2.2f\n", c.getNome(), u));
		}
		
	}

	@FXML
	void initialize() {
		assert boxMese != null : "fx:id=\"boxMese\" was not injected: check your FXML file 'Meteo.fxml'.";
		assert btnCalcola != null : "fx:id=\"btnCalcola\" was not injected: check your FXML file 'Meteo.fxml'.";
		assert btnUmidita != null : "fx:id=\"btnUmidita\" was not injected: check your FXML file 'Meteo.fxml'.";
		assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Meteo.fxml'.";
	
	
		for(int mese=1; mese<=12; mese++) {
			boxMese.getItems().add(Month.of(mese));
		}
	}
	
	public void setModel(Model model){
		this.model = model;
	}
}
