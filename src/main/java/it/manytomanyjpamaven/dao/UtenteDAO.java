package it.manytomanyjpamaven.dao;

import java.util.List;

import it.manytomanyjpamaven.model.Ruolo;
import it.manytomanyjpamaven.model.Utente;

public interface UtenteDAO extends IBaseDAO<Utente> {

	public List<Utente> findAllByRuolo(Ruolo ruoloInput);

	public Utente findByIdFetchingRuoli(Long id);

	public List<Utente> findAllCreatedAtGiugno2021() throws Exception;

	public Long CountAllUtentsAdmin() throws Exception;
	
	public List<Utente> findAllByLenghtPasswordMinoreDi8() throws Exception;
	
	public boolean checkUtentiDisabilitatiandAdmin() throws Exception;

}
