package it.polito.tdp.meteo;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.Rilevamento;
import it.polito.tdp.meteo.bean.SimpleCity;
import it.polito.tdp.meteo.db.MeteoDAO;

public class Model {

	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	
	private List<Citta> citta;
	private List<Citta> best;
	MeteoDAO meteoDAO;
	
	double costoMin = Double.MAX_VALUE;
	public Model() {
		meteoDAO = new MeteoDAO();
		this.citta = meteoDAO.getAllCitta();
	}

	public Double getUmiditaMedia(Month mese, Citta citta) {
		return meteoDAO.getUmiditaMedia(mese, citta);
	}
	
	//schermo pubblico alla ricorsione
	public List<Citta> calcolaSequenza(Month mese) {
		List<Citta> parziale = new ArrayList<Citta>();
		List<Citta> best = new ArrayList<Citta>();
		
		cerca(parziale, 0, best, mese);
		
		return best;
	}
	
	//ricorsione privata
	private void cerca(List<Citta> parziale, int livello, List<Citta> best, Month mese) {
		
		
		//caso terminale
		if(livello==NUMERO_GIORNI_TOTALI) {
			Double costo = calcolaCosto(parziale, mese);
			if(costo<costoMin) {
				best.clear();
				costoMin = costo;
				for(Citta c : parziale) {
					best.add(c);
				}
			}
			return;
		}
		else {
			for(Citta prova : citta) {
				if(aggiuntaValida(prova, parziale)) {
					parziale.add(prova);
					cerca(parziale, livello+1, best, mese);
					parziale.remove(parziale.size()-1);
				}
			}
		}
		
		//
	}


	private Double calcolaCosto(List<Citta> parziale, Month mese) {
		double costo = 0.0;
		
		for(int giorno = 1; giorno<=NUMERO_GIORNI_TOTALI; giorno++) {
			
			//dove mi trovo
			Citta c = parziale.get(giorno-1);
			List<Rilevamento> rilev = meteoDAO.getAllRilevamentiLocalitaMese(mese.getValue(), c.getNome());
			
			//che umidità ho in quella città in quel giorno
			double umidita = rilev.get(giorno-1).getUmidita();
			costo +=umidita;
		}
		
		for(int giorno = 2; giorno<=NUMERO_GIORNI_TOTALI; giorno++) {
			if(!parziale.get(giorno-1).equals(parziale.get(giorno-2))) {
				costo+=COST;
			}
		}
		
		return costo;
	}

	private boolean aggiuntaValida(Citta prova, List<Citta> parziale) {
		
		//verifica giorni max
		int conta=0;
		for(Citta precedente : parziale) {
			if(precedente.equals(prova)) {
				conta++;
			}
		}
		if(conta>=NUMERO_GIORNI_CITTA_MAX) {
			return false;
		}
		
		//verifica giorni minimi
		if(parziale.size()==0) {
			return true;
		}
		if(parziale.size()==1 || parziale.size()==2) { //secondo o terzo giorno, non posso cambiare
			return parziale.get(parziale.size()-1).equals(prova);
		}
		if(parziale.get(parziale.size()-1).equals(prova)) { //giorni successivi e posso rimanere
			return true;
		}
		if(parziale.get(parziale.size()-1).equals(parziale.get(parziale.size()-2)) && parziale.get(parziale.size()-2).equals(parziale.get(parziale.size()-3))) { //
			return true;
		}
		
		return false;
	}
	
	public List<Citta> getCitta(){
		return citta;
	}

	public void reset() {
		costoMin = Double.MAX_VALUE;
		
	}

}
