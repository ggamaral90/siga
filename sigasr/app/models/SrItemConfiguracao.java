package models;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Query;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import models.vo.PaginaItemConfiguracao;
import models.vo.SrItemConfiguracaoVO;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import br.gov.jfrj.siga.base.Texto;
import br.gov.jfrj.siga.cp.model.HistoricoSuporte;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.model.Assemelhavel;

import com.google.gson.JsonArray;

@Entity
@Table(name = "SR_ITEM_CONFIGURACAO", schema = "SIGASR")
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class SrItemConfiguracao extends HistoricoSuporte implements SrSelecionavel {


	private static final long serialVersionUID = 1L;
//	private static final int NETO = 3;
	
	@SuppressWarnings("unused")
	private static Comparator<SrItemConfiguracao> comparator = new Comparator<SrItemConfiguracao>() {
		@Override
		public int compare(SrItemConfiguracao o1, SrItemConfiguracao o2) {
			if (o1 != null && o2 != null
					&& o1.idItemConfiguracao == o2.idItemConfiguracao)
				return 0;
			return o1.siglaItemConfiguracao.compareTo(o2.siglaItemConfiguracao);
		}
	};

	private static String MASCARA_JAVA = "([0-9]{0,2})\\.?([0-9]{0,2})?\\.?([0-9]{0,2})?\\.?([0-9]{0,2})?";
	// "([0-9][0-9])?([.])?([0-9][0-9])?([.])?([0-9][0-9])";

	@Id
	@SequenceGenerator(sequenceName = "SIGASR.SR_ITEM_CONFIGURACAO_SEQ", name = "srItemSeq")
	@GeneratedValue(generator = "srItemSeq")
	@Column(name = "ID_ITEM_CONFIGURACAO")
	public Long idItemConfiguracao;

	@Column(name = "SIGLA_ITEM_CONFIGURACAO")
	public String siglaItemConfiguracao;

	@Column(name = "DESCR_ITEM_CONFIGURACAO")
	public String descrItemConfiguracao;

	@Column(name = "TITULO_ITEM_CONFIGURACAO")
	public String tituloItemConfiguracao;

	@Lob
	@Column(name = "DESCR_SIMILARIDADE", length = 8192)
	public String descricaoSimilaridade;
		
	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "ID_PAI")
	public SrItemConfiguracao pai;

	@OneToMany(targetEntity = SrItemConfiguracao.class, mappedBy = "pai", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	public List<SrItemConfiguracao> filhoSet;

	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "HIS_ID_INI", insertable = false, updatable = false)
	public SrItemConfiguracao itemInicial;

	@OneToMany(targetEntity = SrItemConfiguracao.class, mappedBy = "itemInicial", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@OrderBy("hisDtIni desc")
	public List<SrItemConfiguracao> meuItemHistoricoSet;
   
	@OneToMany(targetEntity = SrGestorItem.class, mappedBy = "itemConfiguracao")
    public Set<SrGestorItem> gestorSet;

	@Column(name = "NUM_FATOR_MULTIPLICACAO_GERAL")
	public int numFatorMultiplicacaoGeral = 1;
	
	@OneToMany(targetEntity = SrFatorMultiplicacao.class, mappedBy = "itemConfiguracao")
	public Set<SrFatorMultiplicacao> fatorMultiplicacaoSet; 
	
	@Transient
	public List<SrConfiguracao> designacoes;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name="SR_CONFIGURACAO_ITEM", schema = "SIGASR", joinColumns={@JoinColumn(name="ID_ITEM_CONFIGURACAO")}, inverseJoinColumns={@JoinColumn(name="ID_CONFIGURACAO")})
	public List<SrConfiguracao> designacoesSet;
	
	public SrItemConfiguracao() {
		this(null, null);
	}

	public SrItemConfiguracao(String descricao) {
		this(descricao, null);
	}

	public SrItemConfiguracao(String sigla, String descricao) {
		this.tituloItemConfiguracao = descricao;
		this.siglaItemConfiguracao = sigla;
	}

	public void adicionarDesignacao(SrConfiguracao designacao) throws Exception {
		if (designacoesSet == null) {
			designacoesSet = new ArrayList<SrConfiguracao>();
		}
		if (podeAdicionar(designacao)) {
			designacoesSet.add(designacao);
			salvar();
		}
	}

	private boolean podeAdicionar(SrConfiguracao designacao) {
		for (SrConfiguracao designacaoSalva : designacoesSet) {
			if (designacaoSalva.getId().equals(designacao.getId())) {
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}
	
	@Override
	public Long getId() {
		return idItemConfiguracao;
	}

	public String getSigla() {
		return siglaItemConfiguracao;
	}

	public String getDescricao() {
		return tituloItemConfiguracao;
	}

	@Override
	public void setId(Long id) {
		this.idItemConfiguracao = id;
	}

	public void setDescricao(String descricao) {
		this.tituloItemConfiguracao = descricao;
	}

	public List<SrItemConfiguracao> getHistoricoItemConfiguracao() {
		if (itemInicial != null)
			return itemInicial.meuItemHistoricoSet;
		return null;
	}

	public SrItemConfiguracao getAtual() {
		if (getHisDtFim() == null)
			return this;
		List<SrItemConfiguracao> sols = getHistoricoItemConfiguracao();
		if (sols == null)
			return null;
		return sols.get(0);
	}

	@Override
	public SrItemConfiguracao selecionar(String sigla) throws Exception {
		return selecionar(sigla, null);
	}

	public SrItemConfiguracao selecionar(String sigla,
			List<SrItemConfiguracao> listaBase) throws Exception {
		setSigla(sigla);
		List<SrItemConfiguracao> itens = buscar(listaBase, false);
		if (itens.size() == 0 || itens.size() > 1 || itens.get(0).isGenerico())
			return null;
		return itens.get(0);
	}

	@Override
	public List<SrItemConfiguracao> buscar() throws Exception {
		return buscar(null);
	}

	public List<SrItemConfiguracao> buscar(List<SrItemConfiguracao> listaBase)
			throws Exception {
		return buscar(listaBase, true);
	}

	public List<SrItemConfiguracao> buscar(List<SrItemConfiguracao> listaBase,
			boolean comHierarquia) throws Exception {

		List<SrItemConfiguracao> lista = new ArrayList<SrItemConfiguracao>();
		List<SrItemConfiguracao> listaFinal = new ArrayList<SrItemConfiguracao>();

		if (listaBase == null)
			lista = listar(Boolean.FALSE);
		else
			lista = listaBase;

		if ((siglaItemConfiguracao == null || siglaItemConfiguracao.equals(""))
				&& (tituloItemConfiguracao == null || tituloItemConfiguracao
						.equals("")))
			return lista;

		for (SrItemConfiguracao item : lista) {
			if (siglaItemConfiguracao != null
					&& !siglaItemConfiguracao.equals("")
					&& !(item.siglaItemConfiguracao.toLowerCase()
							.contains(getSigla())))
				continue;
			if (tituloItemConfiguracao != null
					&& !tituloItemConfiguracao.equals("")) {
				boolean naoAtende = false;
				for (String s : tituloItemConfiguracao.toLowerCase().split(
						"\\s")){
					if (!item.tituloItemConfiguracao.toLowerCase().contains(s)
							&& !(item.descricaoSimilaridade != null && item.descricaoSimilaridade
									.toLowerCase().contains(s)))
						naoAtende = true;
				}
				
				if (naoAtende)
					continue;
			}

			if (comHierarquia)
				do {
					if (!listaFinal.contains(item))
						listaFinal.add(item);
					item = item.pai;
					if (item != null)
						item = item.getAtual();
				} while (item != null);
			else
				listaFinal.add(item);
		}
		
		Collections.sort(listaFinal, new SrItemConfiguracaoComparator());
		return listaFinal;
	}

	@Override
	public void setSigla(String sigla) {
		if (sigla == null) {
			siglaItemConfiguracao = "";
			tituloItemConfiguracao = "";
		} else {
			final Pattern p1 = Pattern.compile("^" + MASCARA_JAVA + "$");
			final Matcher m1 = p1.matcher(sigla);
			if (m1.find()) {
				String s = "";
				for (int i = 1; i <= m1.groupCount(); i++) {
					s += m1.group(i);
					s += (i < m1.groupCount() - 1) ? "." : "";
				}
				siglaItemConfiguracao = s;
			} else
				tituloItemConfiguracao = sigla;
		}
	}

	public int getNivel() {
		int camposVazios = 0;
		if (getSigla() == null)
			return 0;
		int pos = getSigla().indexOf(".00", 0);
		while (pos > -1) {
			camposVazios++;
			pos = getSigla().indexOf(".00", pos + 1);
		}
		
		return 3 - camposVazios;
	}

	public boolean isEspecifico() {
		return getNivel() == 3;
	}

	public boolean isGenerico() {
		return getNivel() == 1;
	}

	public String getSiglaSemZeros() {
		int posFimComparacao = getSigla().indexOf(".00");
		if (posFimComparacao < 0)
			posFimComparacao = getSigla().length() - 1;
		return getSigla().substring(0, posFimComparacao + 1);
	}

	public SrItemConfiguracao getPaiPorSigla() {
		String sigla = getSiglaSemZeros();
		sigla = sigla.substring(0, sigla.length() - 1);
		if (sigla.lastIndexOf(".") == -1)
			return null;
		sigla = sigla.substring(0, sigla.lastIndexOf("."));
		for (int i = 0; i < 3 - (getNivel() - 1); i++) {
			sigla += ".00";
		}
		return SrItemConfiguracao.find(
				"byHisDtFimIsNullAndSiglaItemConfiguracao", sigla).first();
	}
	
	public boolean isPaiDeOuIgualA(SrItemConfiguracao outroItem) {
		if (outroItem == null || outroItem.getSigla() == null)
			return false;
		if (this.equals(outroItem))
			return true;

		return outroItem.getSigla().startsWith(getSiglaSemZeros());
	}

	public boolean isFilhoDeOuIgualA(SrItemConfiguracao outroItem) {
		return outroItem.isPaiDeOuIgualA(this);
	}

	public static List<SrItemConfiguracao> listar(boolean mostrarDesativados) {
		StringBuffer sb = new StringBuffer();
		
		if (!mostrarDesativados)
			sb.append(" hisDtFim is null ");
		else {
			sb.append(" idItemConfiguracao in (");
			sb.append(" SELECT max(idItemConfiguracao) as idItemConfiguracao FROM ");
			sb.append(" SrItemConfiguracao GROUP BY hisIdIni) ");
		}
		sb.append(" order by siglaItemConfiguracao ");
		return SrItemConfiguracao.find(sb.toString()).fetch();
	}
	
	@SuppressWarnings("unchecked")
	public static List<SrItemConfiguracao> listar(PaginaItemConfiguracao configuracao) {
		if (configuracao.precisaExecutarCount()) {
			configuracao.setCount(countAtivos(configuracao));
		}

		StringBuilder sb = querySelecionarAtivos("i", configuracao);
		if (configuracao.getOrderBy() != null) {
			sb.append(MessageFormat.format(" order by i.{0} ", configuracao.getOrderBy()));
		}
		if (configuracao.getDirecaoOrdenacao() != null) {
			sb.append(configuracao.getDirecaoOrdenacao());
		}
		
		Query query = em().createQuery(sb.toString());
		query.setFirstResult(configuracao.getFistResult());
		query.setMaxResults(configuracao.getTamanho());

		if(configuracao.possuiParametroConsulta()) {
			query.setParameter("tituloOuCodigo", "%" + configuracao.getTituloOuCodigo() + "%");
		}
		return query.getResultList();
	}

	private static Integer countAtivos(PaginaItemConfiguracao pagina) {
		StringBuilder sb = querySelecionarAtivos("count(i)", pagina);
		Query query = em().createQuery(sb.toString());
		
		if(pagina.possuiParametroConsulta()) {
			query.setParameter("tituloOuCodigo", "%" + pagina.getTituloOuCodigo() + "%");
		}
		return ((Long) query.getSingleResult()).intValue();
	}
	
	private static StringBuilder querySelecionarAtivos(String clause, PaginaItemConfiguracao pagina) {
		StringBuilder sb = new StringBuilder();
		sb.append(MessageFormat.format("SELECT {0} FROM SrItemConfiguracao i WHERE hisDtFim is null ", clause));
		
		if(pagina.possuiParametroConsulta()) {
			sb.append(" AND (UPPER(i.tituloItemConfiguracao) LIKE UPPER(:tituloOuCodigo) OR UPPER(i.siglaItemConfiguracao) LIKE UPPER(:tituloOuCodigo)) ");
		}
		sb.append(" AND idItemConfiguracao in (");
		sb.append(" SELECT max(idItemConfiguracao) as idItemConfiguracao FROM ");
		sb.append(" SrItemConfiguracao GROUP BY hisIdIni) ");
		
		return sb;
	}

	@Override
	public boolean semelhante(Assemelhavel obj, int profundidade) {
		return false;
	}

	@SuppressWarnings("unused")
	public String getGcTags() {
		int nivel = this.getNivel();
		String tags = "";
		SrItemConfiguracao pai = this.pai;
		if (pai != null)
			tags += pai.getGcTags();
		return tags + "&tags=@" + getTituloSlugify();
	}

	public String getGcTagAbertura() {
		String s = "^sr:" + getTituloSlugify();
		return s;
	}

    public String getTituloSlugify() {
		return Texto.slugify(tituloItemConfiguracao, true, false);
	}
    
    @Override
    public void salvar() throws Exception {
    	if (getNivel() > 1 && pai == null) {
			pai = getPaiPorSigla();
		}
		super.salvar();
		
        if (gestorSet != null)
            for (SrGestorItem gestor : gestorSet){
                gestor.itemConfiguracao = this;
                gestor.salvar();
            }
        
        if (fatorMultiplicacaoSet != null)
            for (SrFatorMultiplicacao fator : fatorMultiplicacaoSet){
                fator.itemConfiguracao = this;
                fator.salvar();
            }
        
        // DB1: precisa salvar item a item 
      	if (this.designacoes != null) {
      		for (SrConfiguracao designacao : this.designacoes) {
      			// se for uma configuração herdada
      			if (designacao.isHerdado) {
      				// se estiver marcada como "não Herdar"
      				if (!designacao.utilizarItemHerdado) {
      					// cria uma nova entrada na tabela, para que seja ignorada nas próximas vezes
      					SrConfiguracaoIgnorada.createNew(this, designacao).salvar();
      				}
      				
      				// verifica se existia entrada para "não Herdar", e remove (usuário marcou para usar herança)
      				else {
      					List<SrConfiguracaoIgnorada> itensIgnorados = SrConfiguracaoIgnorada.findByConfiguracao(designacao);
      					
      					for (SrConfiguracaoIgnorada igItem : itensIgnorados) {
      						// se a configuração for do Item ou de um de seus históricos, remove
      						if (igItem != null && this.getHistoricoItemConfiguracao() != null && this.getHistoricoItemConfiguracao().size() > 0) {
      							for (SrItemConfiguracao itemHist : this.getHistoricoItemConfiguracao()) {
      								if (itemHist.getId().equals(igItem.itemConfiguracao.getId())) {
      									igItem.delete();
      									break;
      								}
      							}
      						}
      					}
      				}
      			}
      			else {
      				designacao.salvarComoDesignacao();
      			}
      		}
      	}
	}

	public List<SrItemConfiguracao> getItemETodosDescendentes() {
		List<SrItemConfiguracao> lista = new ArrayList<SrItemConfiguracao>();
		lista.add(this);
		for (SrItemConfiguracao filho : filhoSet) {
			if (filho.getHisDtFim() == null)
				lista.addAll(filho.getItemETodosDescendentes());
		}
		return lista;
	}
	
	@Override
	public String toString() {
		return siglaItemConfiguracao + " - " + tituloItemConfiguracao;
	}
	
	public SrItemConfiguracaoVO toVO() {
		return SrItemConfiguracaoVO.createFrom(this);
	}
	
	/**
	 * Retorna um Json de {@link SrItemConfiguracao}.
	 */
	public String getSrItemConfiguracaoJson() {
		return this.toVO().toJson();
	}
	
	public SrItemConfiguracao getPai() {
		return pai;
	}
	
	/**
	 * Retorna a lista de {@link SrItemConfiguracao Pai} que este item possui.
	 */
	private List<SrItemConfiguracao> getListaPai() {
		List<SrItemConfiguracao> lista = new ArrayList<SrItemConfiguracao>();
		SrItemConfiguracao itemPai = this.getPai();
		
		while (itemPai != null) {
			if (!lista.contains(itemPai))
				lista.add(itemPai);
				
			itemPai = itemPai.getPai();
		}
		
		return lista;
	}
	
	/**
	 * Lista as Designações que são vinculadas aos {@link SrItemConfiguracao Pai} deste item.
	 */
	public List<SrConfiguracao> getDesignacoesPai() {
		List<SrConfiguracao> listasDesignacoesPai = new ArrayList<SrConfiguracao>();
		
		for (SrItemConfiguracao pai : this.getListaPai()) {
			for (SrConfiguracao confPai : pai.getDesignacoesAtivas()) {
				confPai.isHerdado = true;
				confPai.utilizarItemHerdado = true;
				
				listasDesignacoesPai.add(confPai);
			}
		}
		
		return listasDesignacoesPai;
	}
	
	
	/**
	 * Marca os itens como  herdados.
	 */
	public static List<SrConfiguracao> marcarComoHerdadas(List<SrConfiguracao> listasDesignacoesPai, SrItemConfiguracao item) {
		Iterator<SrConfiguracao> i = listasDesignacoesPai.iterator();
	
		while (i.hasNext()) {
			SrConfiguracao conf = i.next();
			boolean encontrou = false;
			
			conf.isHerdado = true;
			conf.utilizarItemHerdado = true;
			
			List<SrConfiguracaoIgnorada> itensIgnorados = SrConfiguracaoIgnorada.findByConfiguracao(conf);
			
			for (SrConfiguracaoIgnorada igItem : itensIgnorados) {
				// Se a configuração for do Item, vai como desmarcado
				if (item.getId().equals(igItem.itemConfiguracao.getId())) {
					conf.utilizarItemHerdado = false;
				}
				
				// se a configuração for do Item (histórico), vai como desmarcado
				else if (item.getHistoricoItemConfiguracao() != null && item.getHistoricoItemConfiguracao().size() > 0) {
					for (SrItemConfiguracao itemHist : item.getHistoricoItemConfiguracao()) {
						if (itemHist.getId().equals(igItem.itemConfiguracao.getId())) {
							conf.utilizarItemHerdado = false;
							encontrou = true;
							break;
						}
					}
				}
				
				else {
					SrItemConfiguracao itemPai = item.getPai();
					
					while(itemPai != null) {
						
						// Se for configuração do pai, não aparece na tela caso esteja marcada para Ignorar no Pai
						if (itemPai.getId().equals(igItem.itemConfiguracao.getId())) {
							i.remove();
							break;
						}
						else
							itemPai = itemPai.getPai();
					}
				}
				
				// Caso tenha encontrado a configuração correta, interrompe o loop
				if (encontrou)
					break;
			}
		}
		
		return listasDesignacoesPai;
	}
	
	public Map<String, SrDisponibilidade> buscarDisponibilidadesPorOrgao(List<CpOrgaoUsuario> orgaos) {
		return buscarDisponibilidadesPorOrgao(this, orgaos);
	}
	
	public Map<String, SrDisponibilidade> buscarDisponibilidadesPorOrgao(SrItemConfiguracao itemConfiguracao, List<CpOrgaoUsuario> orgaos) {
		if(itemConfiguracao != null) {
			return SrDisponibilidade.buscarTodos(itemConfiguracao, orgaos);
		}
		return new HashMap<String, SrDisponibilidade>();
	}

	public Collection<SrDisponibilidade> encontrarDisponibilidades(List<CpOrgaoUsuario> orgaos) {
		Map<String, SrDisponibilidade> disponibilidadesPai = buscarDisponibilidadesPaiPorOrgao(orgaos);
		Map<String, SrDisponibilidade> disponibilidades = buscarDisponibilidadesPorOrgao(orgaos);
		
		Map<String, SrDisponibilidade> disponibilidadesSelecionadas = selecionarDisponibilidades(disponibilidadesPai, disponibilidades, orgaos);
		return preencherDisponibilidadesVazias(disponibilidadesSelecionadas, orgaos);
	}
	
	private Collection<SrDisponibilidade> preencherDisponibilidadesVazias(Map<String, SrDisponibilidade> disponibilidadesSelecionadas, List<CpOrgaoUsuario> orgaos) {
		for (CpOrgaoUsuario cpOrgaoUsuario : orgaos) {
			if (!disponibilidadesSelecionadas.containsKey(cpOrgaoUsuario.getSigla())) {
				disponibilidadesSelecionadas.put(cpOrgaoUsuario.getSigla(), new SrDisponibilidade(this, cpOrgaoUsuario));
			}
		}
		return disponibilidadesSelecionadas.values();
	}

	public Map<String, SrDisponibilidade> buscarDisponibilidadesPaiPorOrgao(List<CpOrgaoUsuario> orgaos) {
		if(this.pai != null) {
			Map<String, SrDisponibilidade> disponibilidadesPai = buscarDisponibilidadesPorOrgao(this.pai, orgaos);
			Map<String, SrDisponibilidade> disponibilidadesAvo = buscarDisponibilidadesPorOrgao(this.pai.pai, orgaos);
			
			return selecionarDisponibilidades(disponibilidadesAvo, disponibilidadesPai, orgaos);
		}
		return new HashMap<String, SrDisponibilidade>();
	}
	
	private Map<String, SrDisponibilidade> selecionarDisponibilidades(Map<String, SrDisponibilidade> disponibilidades, Map<String, SrDisponibilidade> disponibilidadesPrioritarias, List<CpOrgaoUsuario> orgaos) {
		Map<String, SrDisponibilidade> disponibilidadesPeriorizadas = new HashMap<String, SrDisponibilidade>();
		for (CpOrgaoUsuario orgao : orgaos) {
			SrDisponibilidade disponibilidade = selecionarDisponibilidadePrioritaria(disponibilidades, disponibilidadesPrioritarias, orgao);
			
			if(disponibilidade != null) {
				SrDisponibilidade disponibilidadeSelecionada = disponibilidade.pertenceA(this) ? disponibilidade : disponibilidade.clonarParaCriarNovo(this);
				disponibilidadesPeriorizadas.put(orgao.getSigla(), disponibilidadeSelecionada);
			}
		}
		return disponibilidadesPeriorizadas;
	}
	
	private SrDisponibilidade selecionarDisponibilidadePrioritaria(Map<String, SrDisponibilidade> disponibilidades, Map<String, SrDisponibilidade> disponibilidadesPrioritarias, CpOrgaoUsuario orgao) {
		SrDisponibilidade disponibilidade = disponibilidadesPrioritarias.get(orgao.getSigla());
		
		/**
		 * Se o item nao tem disponibilidade para aquele orgao, entao retorna a disponibilidade do pai
		 */
		if (disponibilidade == null) {
			return disponibilidades.get(orgao.getSigla());
		} 
		/**
		 * Senao, se a disponibilidade do item eh nenhuma e o item pai possui disponibilidade, entao utiliza a disponibilidade do pai
		 */
		else if (disponibilidade.isNenhuma()) {
			SrDisponibilidade disponibilidadePai = disponibilidades.get(orgao.getSigla());
			if (disponibilidadePai != null) {
				return disponibilidade.clonarParaAtualizar(disponibilidadePai);
			}
		}
		return disponibilidade;
	}
	
	public JsonArray criarDisponibilidadesJSON(SrItemConfiguracao itemConfiguracao, List<CpOrgaoUsuario> orgaos) {
		JsonArray array = new JsonArray();
		for (SrDisponibilidade disponibilidade : itemConfiguracao.encontrarDisponibilidades(orgaos)) {
			array.add(disponibilidade.toJsonObject());
		}
		return array;
	}
	
	public List<SrItemConfiguracao> getFilhoSet() {
		List<SrItemConfiguracao> c = new ArrayList<SrItemConfiguracao>();
		
		if (this.filhoSet != null)
			c.addAll(filhoSet);
		
		if (this.itemInicial != null && !this.itemInicial.getId().equals(this.getId()))
			c.addAll(itemInicial.getFilhoSet());
		
		return c;
	}
	
	public Collection<SrConfiguracao> getDesignacoesAtivas() {
		Map<Long, SrConfiguracao> listaCompleta = new HashMap<Long, SrConfiguracao>();
		if (this.itemInicial != null)
			for (SrItemConfiguracao itenConf : getHistoricoItemConfiguracao())
				if (itenConf.designacoesSet != null)
					for (SrConfiguracao d : itenConf.designacoesSet)
						if (d.isAtivo() && d.isDesignacao())
							listaCompleta.put(d.getId(), d);
		
		return listaCompleta.values();
	}
}
