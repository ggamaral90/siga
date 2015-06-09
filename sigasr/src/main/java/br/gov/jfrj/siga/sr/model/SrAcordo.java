package br.gov.jfrj.siga.sr.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import edu.emory.mathcs.backport.java.util.Collections;
import br.gov.jfrj.siga.base.util.Catalogs;
import br.gov.jfrj.siga.model.ActiveRecord;
import br.gov.jfrj.siga.model.Assemelhavel;
import br.gov.jfrj.siga.model.Selecionavel;
import br.gov.jfrj.siga.sr.model.vo.SrAcordoVO;
import br.gov.jfrj.siga.vraptor.converter.ConvertableEntity;
import br.gov.jfrj.siga.vraptor.entity.HistoricoSuporteVraptor;

@Entity
@Table(name = "SR_ACORDO", schema = Catalogs.SIGASR)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class SrAcordo extends HistoricoSuporteVraptor implements Selecionavel, ConvertableEntity{
	//public class SrAcordo extends HistoricoSuporteVraptor implements ConvertableEntity {
	
	private static final long serialVersionUID = 1L;
	
	public static final ActiveRecord<SrAcordo> AR = new ActiveRecord<>(SrAcordo.class);	

	@Id
	@SequenceGenerator(sequenceName = Catalogs.SIGASR +".SR_ACORDO_SEQ", name = "srAcordoSeq")
	@GeneratedValue(generator = "srAcordoSeq")
	@Column(name = "ID_ACORDO")
	private Long idAcordo;

	@Column(name = "NOME_ACORDO")
	private String nomeAcordo;

	@Column(name = "DESCR_ACORDO")
	private String descrAcordo;

	@ManyToOne()
	@JoinColumn(name = "HIS_ID_INI", insertable = false, updatable = false)
	private SrAcordo acordoInicial;

	@OneToMany(targetEntity = SrAcordo.class, mappedBy = "acordoInicial", fetch = FetchType.LAZY)
	public List<SrAcordo> meuAcordoHistoricoSet;

	@OneToMany(targetEntity = SrAtributoAcordo.class, mappedBy = "acordo", fetch = FetchType.LAZY)
	private List<SrAtributoAcordo> atributoAcordoSet;

	public SrAcordo() {
	    this.atributoAcordoSet = new ArrayList<>();
	    this.meuAcordoHistoricoSet = new ArrayList<>();
	}

	@Override
	public Long getId() {
		return getIdAcordo();
	}

	@Override
	public void setId(Long idAcordo) {
		this.setIdAcordo(idAcordo);
	}

	@Override
	public boolean semelhante(Assemelhavel obj, int profundidade) {
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<SrAcordo> getHistoricoAcordo() {
		if (getAcordoInicial() != null)
			return getAcordoInicial().meuAcordoHistoricoSet;
		return Collections.emptyList();
	}

	public SrAcordo getAcordoAtual() {
		if (getHisDtFim() == null)
			return this;
		List<SrAcordo> acordos = getHistoricoAcordo();
		if (acordos == null)
			return null;
		return acordos.get(0);
	}

	public SrAcordo selecionar(String sigla) {
		List<SrAcordo> acordos = buscar(sigla);
		return acordos.size() == 1 ? acordos.get(0) : null;
	}

	public List<SrAcordo> buscar(String sigla) {
		return SrAcordo.AR.find("byHisDtFimIsNullAndNomeAcordo", sigla).fetch();
	}

	@SuppressWarnings("unchecked")
	public static List<SrAcordo> listar(boolean mostrarDesativados) {
		if (!mostrarDesativados) {
			return SrAcordo.AR.find("byHisDtFimIsNull").fetch();
		} else {
			StringBuilder str = new StringBuilder();
			str.append("SELECT p FROM SrAcordo p where p.idAcordo IN (");
			str.append("SELECT MAX(idAcordo) FROM SrAcordo GROUP BY hisIdIni)");

			return AR.em().createQuery(str.toString()).getResultList();
		}
	}

	// Edson: Não consegui fazer com que esse cascade fosse automático.
	@Override
	public void salvar() throws Exception {
		super.salvar();
		if (getAtributoAcordoSet() != null)
			for (SrAtributoAcordo atributoAcordo : getAtributoAcordoSet()) {
				atributoAcordo.setAcordo(this);
				atributoAcordo.salvar();
			}
	}

	// Edson: Não consegui fazer com que esse cascade fosse automático.
	@Override
	public void finalizar() throws Exception {
		super.finalizar();
		if (getAtributoAcordoSet() != null)
			for (SrAtributoAcordo atributoAcordo : getAtributoAcordoSet()) {
				atributoAcordo.finalizar();
			}
	}

	public SrAtributoAcordo getAtributo(SrAtributo att) {
		for (SrAtributoAcordo pa : getAtributoAcordoSet())
			if (pa.getAtributo().equals(att))
				return pa;
		return null;
	}

	private SrAtributoAcordo getAtributo(String codigo) {
		if (getAtributoAcordoSet() == null)
			return null;
		SrAtributo att = SrAtributo.get(codigo);
		if (att == null)
			return null;
		return getAtributo(att);
	}

	public Long getAtributoEmSegundos(String codigo) {
		SrAtributoAcordo pa = getAtributo(codigo);
		if (pa == null)
			return null;
		return pa.getValorEmSegundos();
	}

	public String getSigla() {
		return getIdAcordo().toString();
	}

	public String getDescricao() {
		return getNomeAcordo();
	}
	
	public SrAcordoVO toVO() {
		return SrAcordoVO.createFrom(this);
	}
	
	public String toJson() {
		return this.toVO().toJson();
	}
	
	public Long getIdAcordo() {
		return idAcordo;
	}

	public void setIdAcordo(Long idAcordo) {
		this.idAcordo = idAcordo;
	}

	public String getNomeAcordo() {
		return nomeAcordo;
	}

	public void setNomeAcordo(String nomeAcordo) {
		this.nomeAcordo = nomeAcordo;
	}

	public String getDescrAcordo() {
		return descrAcordo;
	}

	public void setDescrAcordo(String descrAcordo) {
		this.descrAcordo = descrAcordo;
	}

	public SrAcordo getAcordoInicial() {
		return acordoInicial;
	}

	public void setAcordoInicial(SrAcordo acordoInicial) {
		this.acordoInicial = acordoInicial;
	}

	@Override
	public void setSigla(String sigla) {
		
	}

	public List<SrAtributoAcordo> getAtributoAcordoSet() {
		return atributoAcordoSet;
	}

	public void setAtributoAcordoSet(List<SrAtributoAcordo> atributoAcordoSet) {
		this.atributoAcordoSet = atributoAcordoSet;
	}	
	
}