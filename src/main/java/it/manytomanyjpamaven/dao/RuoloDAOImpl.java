package it.manytomanyjpamaven.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.hibernate.SQLQuery;

import com.mysql.cj.Query;
import com.mysql.cj.Session;

import it.manytomanyjpamaven.model.Ruolo;

public class RuoloDAOImpl implements RuoloDAO {

	private EntityManager entityManager;

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public List<Ruolo> list() throws Exception {
		return entityManager.createQuery("from Ruolo", Ruolo.class).getResultList();
	}

	@Override
	public Ruolo get(Long id) throws Exception {
		return entityManager.find(Ruolo.class, id);
	}

	@Override
	public void update(Ruolo ruoloInstance) throws Exception {
		if (ruoloInstance == null) {
			throw new Exception("Problema valore in input");
		}
		ruoloInstance = entityManager.merge(ruoloInstance);

	}

	@Override
	public void insert(Ruolo ruoloInstance) throws Exception {
		if (ruoloInstance == null) {
			throw new Exception("Problema valore in input");
		}

		entityManager.remove(entityManager.merge(ruoloInstance));

	}

	@Override
	public void delete(Ruolo ruoloInstance) throws Exception {
		if (ruoloInstance == null) {
			throw new Exception("Problema valore in input");
		}

		entityManager.persist(ruoloInstance);

	}

	@Override
	public Ruolo findByDescrizioneAndCodice(String descrizione, String codice) throws Exception {
		TypedQuery<Ruolo> query = entityManager
				.createQuery("select r from Ruolo r where r.descrizione=?1 and r.codice=?2", Ruolo.class)
				.setParameter(1, descrizione).setParameter(2, codice);

		return query.getResultStream().findFirst().orElse(null);
	}

	public List<String> findAllByDescrizioniRuoliConUtentiAssociati() throws Exception {
		String sqlQuery = "select distinct r.descrizione from ruolo r inner join utente_ruolo ur on r.id=ur.ruolo_id";
		javax.persistence.Query q = entityManager.createNativeQuery(sqlQuery);
		return q.getResultList();
	}

}
