package models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import models.vo.SrListaVO;
import play.db.jpa.JPA;
import util.AtualizacaoLista;
import br.gov.jfrj.siga.cp.CpTipoConfiguracao;
import br.gov.jfrj.siga.cp.model.HistoricoSuporte;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.model.Assemelhavel;

@Entity
@Table(name = "SR_LISTA", schema = "SIGASR")
public class SrLista extends HistoricoSuporte {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private class SrSolicitacaoListaComparator implements
	Comparator<SrSolicitacao> {

		private SrLista lista;

		public SrSolicitacaoListaComparator(SrLista lista) {
			this.lista = lista;
		}

		@Override
		public int compare(SrSolicitacao s1, SrSolicitacao s2) {
			try {
				return Long.valueOf(s1.getPrioridadeNaLista(lista)).compareTo(
						Long.valueOf(s2.getPrioridadeNaLista(lista)));
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}
		}

	}

	@Id
	@SequenceGenerator(sequenceName = "SIGASR.SR_LISTA_SEQ", name = "srListaSeq")
	@GeneratedValue(generator = "srListaSeq")
	@Column(name = "ID_LISTA")
	public Long idLista;

	@Column(name = "NOME_LISTA")
	public String nomeLista;

	@Lob
	@Column(name = "DESCR_ABRANGENCIA", length = 8192)
	public String descrAbrangencia;

	@Lob
	@Column(name = "DESCR_JUSTIFICATIVA", length = 8192)
	public String descrJustificativa;

	@Lob
	@Column(name = "DESCR_PRIORIZACAO", length = 8192)
	public String descrPriorizacao;

	@ManyToOne
	@JoinColumn(name = "ID_LOTA_CADASTRANTE", nullable = false)
	public DpLotacao lotaCadastrante;

	@OneToMany(targetEntity = SrPrioridadeSolicitacao.class, mappedBy = "lista", fetch = FetchType.LAZY)
	@OrderBy("numPosicao")
	protected Set<SrPrioridadeSolicitacao> meuPrioridadeSolicitacaoSet = new HashSet<SrPrioridadeSolicitacao>();

	@ManyToOne()
	@JoinColumn(name = "HIS_ID_INI", insertable = false, updatable = false)
	public SrLista listaInicial;

	@OneToMany(targetEntity = SrLista.class, mappedBy = "listaInicial", fetch = FetchType.LAZY)
	@OrderBy("hisDtIni desc")
	public List<SrLista> meuListaHistoricoSet;

	public static List<SrLista> listar(boolean mostrarDesativado) {
		StringBuffer sb = new StringBuffer();

		if (!mostrarDesativado)
			sb.append(" hisDtFim is null ");
		else {
			sb.append(" idLista in (");
			sb.append(" SELECT max(idLista) as idLista FROM ");
			sb.append(" SrLista GROUP BY hisIdIni) ");
		}

		sb.append(" order by idLista ");

		return SrLista.find(sb.toString()).fetch();
	}

	public static List<SrLista> getCriadasPelaLotacao(DpLotacao lota) {
		return SrLista.find(
				"hisDtFim is null and lotaCadastrante.idLotacaoIni = "
						+ lota.getIdLotacaoIni()).fetch();
	}

	public Long getId() {
		return this.idLista;
	}

	public void setId(Long id) {
		idLista = id;
	}

	@Override
	public boolean semelhante(Assemelhavel obj, int profundidade) {
		return false;
	}

	public List<SrLista> getHistoricoLista() {
		if (listaInicial != null)
			return listaInicial.meuListaHistoricoSet;
		return null;
	}

	public SrLista getListaAtual() {
		if (getHisDtFim() == null)
			return this;
		List<SrLista> listas = getHistoricoLista();
		if (listas == null)
			return null;
		return listas.get(0);
	}

	public Set<SrPrioridadeSolicitacao> getPrioridadeSolicitacaoSet() {
		return getPrioridadeSolicitacaoSet(true);
	}

	public Set<SrPrioridadeSolicitacao> getPrioridadeSolicitacaoSetOrdemCrescente() {
		return getPrioridadeSolicitacaoSet(true);
	}
	
	public Set<SrPrioridadeSolicitacao> getPrioridadeSolicitacaoSet(boolean ordemCrescente) {
		TreeSet<SrPrioridadeSolicitacao> listaCompleta = new TreeSet<SrPrioridadeSolicitacao>(new SrPrioridadeSolicitacaoComparator(ordemCrescente));
		if (listaInicial != null)
			for (SrLista lista : getHistoricoLista())
				if (lista.meuPrioridadeSolicitacaoSet != null)
					for (SrPrioridadeSolicitacao prioridadeSolicitacao : lista.meuPrioridadeSolicitacaoSet)
							listaCompleta.add(prioridadeSolicitacao);
		return listaCompleta;
	}	
	
	public boolean podeEditar(DpLotacao lotaTitular, DpPessoa pess) {
		return (lotaTitular.equivale(lotaCadastrante)) || possuiPermissao(lotaTitular, pess, SrTipoPermissaoLista.GESTAO);
	}
	
	public boolean podeIncluir(DpLotacao lotaTitular, DpPessoa pess) {
		return (lotaTitular.equivale(lotaCadastrante)) || possuiPermissao(lotaTitular, pess, SrTipoPermissaoLista.INCLUSAO);
	}

	public boolean podeConsultar(DpLotacao lotaTitular, DpPessoa pess) {
		return (lotaTitular.equivale(lotaCadastrante)) || possuiPermissao(lotaTitular, pess, SrTipoPermissaoLista.CONSULTA);
	}

	public boolean podeRemover(DpLotacao lotaTitular, DpPessoa pess) throws Exception {
		return (lotaTitular.equivale(lotaCadastrante)) || possuiPermissao(lotaTitular, pess, SrTipoPermissaoLista.GESTAO);
	}
	
	public boolean podePriorizar(DpLotacao lotaTitular, DpPessoa pess) throws Exception {
		return (lotaTitular.equivale(lotaCadastrante)) || possuiPermissao(lotaTitular, pess, SrTipoPermissaoLista.PRIORIZACAO);
	}

	private boolean possuiPermissao(DpLotacao lotaTitular, DpPessoa pess, Long tipoPermissaoLista) {
		List<SrConfiguracao> permissoesEncontradas = getPermissoes(lotaTitular, pess);
		for (SrConfiguracao srConfiguracao : permissoesEncontradas) {
			for (SrTipoPermissaoLista permissao: srConfiguracao.tipoPermissaoSet) {
				if (tipoPermissaoLista == permissao.getIdTipoPermissaoLista()) {
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}

	public List<SrConfiguracao> getPermissoes(DpLotacao lotaTitular, DpPessoa pess) {
		try {
			SrConfiguracao confFiltro = new SrConfiguracao();
			confFiltro.setLotacao(lotaTitular);
			confFiltro.setDpPessoa(pess);
			confFiltro.listaPrioridade = this;
			confFiltro.setCpTipoConfiguracao(JPA.em().find(
					CpTipoConfiguracao.class,
					CpTipoConfiguracao.TIPO_CONFIG_SR_PERMISSAO_USO_LISTA));
			return SrConfiguracao.listar(confFiltro);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<SrConfiguracao> getPermissoes() {
		return getPermissoes(null, null);
	}
		
	protected long getPosicaoParaEncaixe(SrPrioridadeSolicitacao prioridadeSolicitacao) throws Exception {
		List <SrPrioridadeSolicitacao> prioridades = new ArrayList<SrPrioridadeSolicitacao>(getPrioridadeSolicitacaoSet());
		
		if (prioridades.isEmpty()) {
			return 1;
		}
		
		if (prioridadeSolicitacao.getPrioridade() != null) {
			
			for (int i = prioridades.size() - 1; i >= 0; i--) {
				SrPrioridadeSolicitacao prioridadeSolic = prioridades.get(i);
				if (prioridadeSolicitacao.getPrioridade().equals(prioridadeSolic.getPrioridade())) {
					return prioridadeSolic.getNumPosicao() + 1;
				}
			}				
		}
		return buscarPosicaoPorPrioridade(prioridades, prioridadeSolicitacao);
	}	

	private Long buscarPosicaoPorPrioridade(List<SrPrioridadeSolicitacao> prioridades, SrPrioridadeSolicitacao prioridadeSolicitacao) {
		if (prioridades.get(prioridades.size() - 1).getNumPosicao() == null) {
			return 0L;
		}
		
		for (int i = 0; i <= prioridades.size() - 1; i++) {	
			SrPrioridadeSolicitacao prioridadeSolic = prioridades.get(i);
			if(prioridadeSolicitacao.getPrioridade() != null) {
				if (prioridadeSolic.getPrioridade() == null || prioridadeSolicitacao.getPrioridade().idPrioridade > prioridadeSolic.getPrioridade().idPrioridade) {
					return prioridadeSolic.getNumPosicao();
				}
			}
		}

		return prioridades.get(prioridades.size() - 1).getNumPosicao() + 1;
	}

	public void priorizar(DpPessoa cadastrante, DpLotacao lotaCadastrante, List<AtualizacaoLista> listaPrioridadeSolicitacao) throws Exception {
		Map<Long, AtualizacaoLista> atualizacoesAgrupadas = agruparAtualizacoes(listaPrioridadeSolicitacao);
		
		for (SrPrioridadeSolicitacao prioridadeSolicitacao : getPrioridadeSolicitacaoSet()) {
			if (!prioridadeSolicitacao.getSolicitacao().isEmLista(this))
				throw new IllegalArgumentException("A solicitação " + prioridadeSolicitacao.getSolicitacao().getCodigo() + " não faz parte da lista");

			AtualizacaoLista atualizacaoLista = atualizacoesAgrupadas.get(prioridadeSolicitacao.getId());
			if (atualizacaoLista != null) {
				prioridadeSolicitacao.atualizar(atualizacaoLista);
			}
		}
		this.refresh();
	}

	private Map<Long, AtualizacaoLista> agruparAtualizacoes(List<AtualizacaoLista> listaPrioridadeSolicitacao) {
		Map<Long, AtualizacaoLista> atualizacoesAgrupadas = new HashMap <Long, AtualizacaoLista>();
		for (AtualizacaoLista atualizacaoLista : listaPrioridadeSolicitacao) {
			atualizacoesAgrupadas.put(atualizacaoLista.getIdPrioridadeSolicitacao(), atualizacaoLista);
		}
		return atualizacoesAgrupadas;
	}

	protected void recalcularPrioridade(DpPessoa pessoa, DpLotacao lota) throws Exception {
		Long posicao = 0L;
		for (SrPrioridadeSolicitacao prioridadeSolicitacao : getPrioridadeSolicitacaoSet()) {
			posicao++;
			if (!posicao.equals(prioridadeSolicitacao.numPosicao)){
				prioridadeSolicitacao.setNumPosicao(posicao);
				prioridadeSolicitacao.salvar();
			}
		}
	}
	
	public String toJson() {
		return this.toVO().toJson();
	}

	public SrListaVO toVO() {
		return new SrListaVO(this);
	}
	
	public void validarPodeExibirLista(DpLotacao lotacao, DpPessoa cadastrante) throws Exception {
		if (!podeConsultar(lotacao, cadastrante)) {
			throw new Exception("Exibição não permitida");
		}
	}
	
    public SrPrioridadeSolicitacao getSrPrioridadeSolicitacao(SrSolicitacao solicitacao) {
        for (SrPrioridadeSolicitacao prioridadeSolicitacao : getPrioridadeSolicitacaoSet()) {
        	if (solicitacao.getIdInicial().equals(prioridadeSolicitacao.getSolicitacao().getIdInicial())) {
        		return prioridadeSolicitacao;
        	}
        }
    	return null;
    }	
    
    public void incluir(SrSolicitacao solicitacao, SrPrioridade prioridade, boolean naoReposicionarAutomatico) throws Exception {
    	SrPrioridadeSolicitacao prioridadeSolicitacao = new SrPrioridadeSolicitacao(this, solicitacao, prioridade, naoReposicionarAutomatico);
		long posicao = getPosicaoParaEncaixe(prioridadeSolicitacao);

		for (SrPrioridadeSolicitacao prioridadeSolic : getPrioridadeSolicitacaoSet()) {
			if (prioridadeSolic.getNumPosicao() >= posicao) {
				prioridadeSolic.incrementarPosicao();
				prioridadeSolic.salvar();
			}
		}
		prioridadeSolicitacao.setNumPosicao(posicao);
		meuPrioridadeSolicitacaoSet.add(prioridadeSolicitacao);
    	prioridadeSolicitacao.salvar();
    }
	
	/**
	 * Retorna um Json de {@link SrLista}.
	 */
	public String getSrListaJson() {
		return this.toVO().toJson();
	}

	public void retirar(SrSolicitacao solicitacao, DpPessoa pessoa, DpLotacao lotacao) throws Exception {
		for (SrPrioridadeSolicitacao prioridadeSolicitacao : getPrioridadeSolicitacaoSet()) {
			if (prioridadeSolicitacao.getSolicitacao().getId().equals(solicitacao.getId())) {
				prioridadeSolicitacao.delete();
				excluir(prioridadeSolicitacao);
				break;
			}
		}
		salvar();
		recalcularPrioridade(pessoa, lotacao);
	}

	private void excluir(SrPrioridadeSolicitacao prioridadeSolicitacao) {
		for (SrLista listaHistorico : getHistoricoLista()) {
			boolean removido = listaHistorico.meuPrioridadeSolicitacaoSet.remove(prioridadeSolicitacao);
			
			if(removido) {
				break;
			}
		}
	}
}
