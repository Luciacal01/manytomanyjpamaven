package it.manytomanyjpamaven.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.management.RuntimeErrorException;

import it.manytomanyjpamaven.dao.EntityManagerUtil;
import it.manytomanyjpamaven.model.Ruolo;
import it.manytomanyjpamaven.model.StatoUtente;
import it.manytomanyjpamaven.model.Utente;
import it.manytomanyjpamaven.service.MyServiceFactory;
import it.manytomanyjpamaven.service.RuoloService;
import it.manytomanyjpamaven.service.UtenteService;

public class ManyToManyTest {

	public static void main(String[] args) {
		UtenteService utenteServiceInstance = MyServiceFactory.getUtenteServiceInstance();
		RuoloService ruoloServiceInstance = MyServiceFactory.getRuoloServiceInstance();

		// ora passo alle operazioni CRUD
		try {

			// inizializzo i ruoli sul db
			initRuoli(ruoloServiceInstance);

			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			// testInserisciNuovoUtente(utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			// testCollegaUtenteARuoloEsistente(ruoloServiceInstance,
			// utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			// testModificaStatoUtente(utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			// testRimuoviRuoloDaUtente(ruoloServiceInstance, utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			// deleteRuolo(ruoloServiceInstance);

			testCercaUtentiDataCreazioneGiugno2021(utenteServiceInstance);

			// testDeleteUtente(utenteServiceInstance);

		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			// questa è necessaria per chiudere tutte le connessioni quindi rilasciare il
			// main
			EntityManagerUtil.shutdown();
		}

	}

	private static void initRuoli(RuoloService ruoloServiceInstance) throws Exception {
		if (ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", "ROLE_ADMIN") == null) {
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Administrator", "ROLE_ADMIN"));
		}

		if (ruoloServiceInstance.cercaPerDescrizioneECodice("Classic User", "ROLE_CLASSIC_USER") == null) {
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Classic User", "ROLE_CLASSIC_USER"));
		}

		if (ruoloServiceInstance.cercaPerDescrizioneECodice("Giocatore", "GIOCATORE_USER") == null) {
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Giocatore", "GIOCATORE_USER"));
		}

	}

	private static void testInserisciNuovoUtente(UtenteService utenteServiceInstance) throws Exception {
		System.out.println(".......testInserisciNuovoUtente inizio.............");

		Utente utenteNuovo = new Utente("pippo.rossi", "xxx", "pippo", "rossi", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testInserisciNuovoUtente fallito ");

		System.out.println(".......testInserisciNuovoUtente fine: PASSED.............");
	}

	private static void testCollegaUtenteARuoloEsistente(RuoloService ruoloServiceInstance,
			UtenteService utenteServiceInstance) throws Exception {
		System.out.println(".......testCollegaUtenteARuoloEsistente inizio.............");

		Ruolo ruoloEsistenteSuDb = ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", "ROLE_ADMIN");
		if (ruoloEsistenteSuDb == null)
			throw new RuntimeException("testCollegaUtenteARuoloEsistente fallito: ruolo inesistente ");

		// mi creo un utente inserendolo direttamente su db
		Utente utenteNuovo = new Utente("mario.bianchi", "JJJ", "mario", "bianchi", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testInserisciNuovoUtente fallito: utente non inserito ");

		utenteServiceInstance.aggiungiRuolo(utenteNuovo, ruoloEsistenteSuDb);
		// per fare il test ricarico interamente l'oggetto e la relazione
		Utente utenteReloaded = utenteServiceInstance.caricaUtenteSingoloConRuoli(utenteNuovo.getId());
		if (utenteReloaded.getRuoli().size() != 1)
			throw new RuntimeException("testInserisciNuovoUtente fallito: ruoli non aggiunti ");

		System.out.println(".......testCollegaUtenteARuoloEsistente fine: PASSED.............");
	}

	private static void testModificaStatoUtente(UtenteService utenteServiceInstance) throws Exception {
		System.out.println(".......testModificaStatoUtente inizio.............");

		// mi creo un utente inserendolo direttamente su db
		Utente utenteNuovo = new Utente("mario1.bianchi1", "JJJ", "mario1", "bianchi1", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testModificaStatoUtente fallito: utente non inserito ");

		// proviamo a passarlo nello stato ATTIVO ma salviamoci il vecchio stato
		StatoUtente vecchioStato = utenteNuovo.getStato();
		utenteNuovo.setStato(StatoUtente.ATTIVO);
		utenteServiceInstance.aggiorna(utenteNuovo);

		if (utenteNuovo.getStato().equals(vecchioStato))
			throw new RuntimeException("testModificaStatoUtente fallito: modifica non avvenuta correttamente ");

		System.out.println(".......testModificaStatoUtente fine: PASSED.............");
	}

	private static void testRimuoviRuoloDaUtente(RuoloService ruoloServiceInstance, UtenteService utenteServiceInstance)
			throws Exception {
		System.out.println(".......testRimuoviRuoloDaUtente inizio.............");

		// carico un ruolo e lo associo ad un nuovo utente
		Ruolo ruoloEsistenteSuDb = ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", "ROLE_ADMIN");
		if (ruoloEsistenteSuDb == null)
			throw new RuntimeException("testRimuoviRuoloDaUtente fallito: ruolo inesistente ");

		// mi creo un utente inserendolo direttamente su db
		Utente utenteNuovo = new Utente("aldo.manuzzi", "pwd@2", "aldo", "manuzzi", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testRimuoviRuoloDaUtente fallito: utente non inserito ");
		utenteServiceInstance.aggiungiRuolo(utenteNuovo, ruoloEsistenteSuDb);

		// ora ricarico il record e provo a disassociare il ruolo
		Utente utenteReloaded = utenteServiceInstance.caricaUtenteSingoloConRuoli(utenteNuovo.getId());
		boolean confermoRuoloPresente = false;
		for (Ruolo ruoloItem : utenteReloaded.getRuoli()) {
			if (ruoloItem.getCodice().equals(ruoloEsistenteSuDb.getCodice())) {
				confermoRuoloPresente = true;
				break;
			}
		}

		if (!confermoRuoloPresente)
			throw new RuntimeException("testRimuoviRuoloDaUtente fallito: utente e ruolo non associati ");

		// ora provo la rimozione vera e propria ma poi forzo il caricamento per fare un
		// confronto 'pulito'
		utenteServiceInstance.rimuoviRuoloDaUtente(utenteReloaded.getId(), ruoloEsistenteSuDb.getId());
		utenteReloaded = utenteServiceInstance.caricaUtenteSingoloConRuoli(utenteNuovo.getId());
		if (!utenteReloaded.getRuoli().isEmpty())
			throw new RuntimeException("testRimuoviRuoloDaUtente fallito: ruolo ancora associato ");

		System.out.println(".......testRimuoviRuoloDaUtente fine: PASSED.............");
	}

	public static void updateRuolo(RuoloService ruoloServiceInstance, UtenteService utenteServiceIService)
			throws Exception {
		System.out.println(".......testModificoRuolo inizio.............");

		// carico un ruolo e lo associo ad un nuovo utente
		Ruolo ruoloEsistenteSuDb = ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", "ROLE_ADMIN");
		if (ruoloEsistenteSuDb == null)
			throw new RuntimeException("testRimuoviRuoloDaUtente fallito: ruolo inesistente ");

		// Ruolo ruoloDaModificare= ruoloServiceInstance.listAll().get(1);

	}

	public static void deleteRuolo(RuoloService ruoloServiceInstance) throws Exception {
		System.out.println("...........testCancellaRuolo inizio.......");

		List<Ruolo> listaRuoliPresenti = ruoloServiceInstance.listAll();

		if (listaRuoliPresenti.size() == 0)
			throw new RuntimeException(" testCancellaRuolo FAILED: non sono presenti ruoli sul db");
		ruoloServiceInstance.rimuovi(listaRuoliPresenti.get(2).getId());

		if (listaRuoliPresenti.get(2).getId() != null)
			throw new RuntimeException("testCancellaRuolo FAILED: il ruolo non è stato cancellato");

		System.out.println(".......testCancellaRuolo PASSED.........");

	}

	public static void testDeleteUtente(UtenteService utenteServiceInstance) throws Exception {
		System.out.println(".............testDeleteUtente inizio..........");
		List<Utente> listaUtentiPresenti = utenteServiceInstance.listAll();
		int size = listaUtentiPresenti.size();

		utenteServiceInstance.rimuovi(listaUtentiPresenti.get(4).getId());
		if (listaUtentiPresenti.size() == size)
			throw new RuntimeException("test FAILED: l'elemento non è stato eliminato");

		System.out.println(".............testDeleteUtente Passed..........");
	}

	public static void testCercaUtentiDataCreazioneGiugno2021(UtenteService utenteServiceInstance) throws Exception {
		System.out.println("........testCercaUtentiDataCreazioneGiugno2021 inizio..........");

		Utente utenteNuovo = new Utente("pippo.rossi", "xxx", "pippo", "rossi",
				new SimpleDateFormat("dd-MM-yyyy").parse("10-06-2021"));
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testInserisciNuovoUtente fallito ");

		Utente utenteNuovo1 = new Utente("verdisss", "ghk", "gianni", "verdi",
				new SimpleDateFormat("dd-MM-yyyy").parse("30-06-2021"));
		utenteServiceInstance.inserisciNuovo(utenteNuovo1);
		if (utenteNuovo1.getId() == null)
			throw new RuntimeException("testInserisciNuovoUtente fallito ");

		List<Utente> listaUtentiCreatiAGiugno2021 = utenteServiceInstance.CercaUtentiCreatiAGiugno2021();

		if (listaUtentiCreatiAGiugno2021.size() != 2)
			throw new RuntimeException(
					"testCercaUtentiDataCreazioneGiugno2021 FAILED, la ricerca non è andata a buon fine");

		utenteServiceInstance.rimuovi(utenteNuovo.getId());
		utenteServiceInstance.rimuovi(utenteNuovo1.getId());

		System.out.println("........testCercaUtentiDataCreazioneGiugno2021 PASSED..........");
	}
}

//utentiCreatiAGiugno2021
//numeroDiUtentiAdmin
//listaDiDescrizioniDistinteDeiRuoliConUtentiAssociati
//listaDiUtentiConPasswordcConMenoDi8Caratteri
//SeTraGliUtentiDisabilitatiAlmenoUnAdmin
