package br.gov.jfrj.siga.sr.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.gov.jfrj.siga.base.util.Catalogs;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.sinc.lib.NaoRecursivo;
import br.gov.jfrj.siga.sr.model.vo.SrFatorMultiplicacaoVO;
import br.gov.jfrj.siga.vraptor.entity.ObjetoVraptor;

@Entity
@Table(name = "SR_FATOR_MULTIPLICACAO", schema = Catalogs.SIGASR)
public class SrFatorMultiplicacao extends ObjetoVraptor {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(sequenceName = Catalogs.SIGASR +".SR_FATOR_MULTIPLICACAO_SEQ", name = "srFatorMultiplicacao")
	@GeneratedValue(generator = "srFatorMultiplicacao")
	@Column(name = "ID_FATOR_MULTIPLICACAO")
	private Long idFatorMultiplicacao;
	
	@Column(name = "NUM_FATOR_MULTIPLICACAO")
	private int numFatorMultiplicacao = 1;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_PESSOA")
	@NaoRecursivo
	private DpPessoa dpPessoa;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_LOTACAO")
	@NaoRecursivo
	private DpLotacao dpLotacao;

	@ManyToOne()
	@JoinColumn(name = "ID_ITEM_CONFIGURACAO")
	private SrItemConfiguracao itemConfiguracao;
	
	
	public SrFatorMultiplicacao() {
		super();
	}

	public DpPessoa getDpPessoa() {
		return dpPessoa;
	}
	
	public void setDpPessoa(DpPessoa dpPessoa) {
		this.dpPessoa = dpPessoa;
	}
	
	public DpLotacao getDpLotacao() {
		return dpLotacao;
	}
	
	public void setDpLotacao(DpLotacao dpLotacao) {
		this.dpLotacao = dpLotacao;
	}
	
	public SrFatorMultiplicacaoVO toVO() {
		if (this.dpPessoa != null && this.dpPessoa.getId() != null && (this.dpPessoa.getSigla() == null || this.dpPessoa.getDescricao() == null))
			this.dpPessoa = DpPessoa.findById(this.dpPessoa.getId());
		
		if (this.dpLotacao != null && this.dpLotacao.getId() != null && (this.dpLotacao.getSigla() == null || this.dpLotacao.getDescricao() == null))
			this.dpLotacao = DpLotacao.findById(this.dpLotacao.getId());
		
		return new SrFatorMultiplicacaoVO(this.idFatorMultiplicacao, this.numFatorMultiplicacao, this.dpPessoa, this.dpLotacao);
	}

	public Long getIdFatorMultiplicacao() {
		return idFatorMultiplicacao;
	}

	public void setIdFatorMultiplicacao(Long idFatorMultiplicacao) {
		this.idFatorMultiplicacao = idFatorMultiplicacao;
	}

	public int getNumFatorMultiplicacao() {
		return numFatorMultiplicacao;
	}

	public void setNumFatorMultiplicacao(int numFatorMultiplicacao) {
		this.numFatorMultiplicacao = numFatorMultiplicacao;
	}

	public SrItemConfiguracao getItemConfiguracao() {
		return itemConfiguracao;
	}

	public void setItemConfiguracao(SrItemConfiguracao itemConfiguracao) {
		this.itemConfiguracao = itemConfiguracao;
	}

	@Override
	protected Long getId() {
		return this.getIdFatorMultiplicacao();
	}

}
