package br.gov.jfrj.siga.sr.model;

import static br.gov.jfrj.siga.sr.model.SrTipoMovimentacao.TIPO_MOVIMENTACAO_ALTERACAO_PRIORIDADE_LISTA;
import static br.gov.jfrj.siga.sr.model.SrTipoMovimentacao.TIPO_MOVIMENTACAO_ANDAMENTO;
import static br.gov.jfrj.siga.sr.model.SrTipoMovimentacao.TIPO_MOVIMENTACAO_ANEXACAO_ARQUIVO;
import static br.gov.jfrj.siga.sr.model.SrTipoMovimentacao.TIPO_MOVIMENTACAO_AVALIACAO;
import static br.gov.jfrj.siga.sr.model.SrTipoMovimentacao.TIPO_MOVIMENTACAO_CANCELAMENTO_DE_SOLICITACAO;
import static br.gov.jfrj.siga.sr.model.SrTipoMovimentacao.TIPO_MOVIMENTACAO_DESENTRANHAMENTO;
import static br.gov.jfrj.siga.sr.model.SrTipoMovimentacao.TIPO_MOVIMENTACAO_ESCALONAMENTO;
import static br.gov.jfrj.siga.sr.model.SrTipoMovimentacao.TIPO_MOVIMENTACAO_FECHAMENTO;
import static br.gov.jfrj.siga.sr.model.SrTipoMovimentacao.TIPO_MOVIMENTACAO_INCLUSAO_LISTA;
import static br.gov.jfrj.siga.sr.model.SrTipoMovimentacao.TIPO_MOVIMENTACAO_INICIO_ATENDIMENTO;
import static br.gov.jfrj.siga.sr.model.SrTipoMovimentacao.TIPO_MOVIMENTACAO_INICIO_PENDENCIA;
import static br.gov.jfrj.siga.sr.model.SrTipoMovimentacao.TIPO_MOVIMENTACAO_JUNTADA;
import static br.gov.jfrj.siga.sr.model.SrTipoMovimentacao.TIPO_MOVIMENTACAO_REABERTURA;
import static br.gov.jfrj.siga.sr.model.SrTipoMovimentacao.TIPO_MOVIMENTACAO_RETIRADA_DE_LISTA;
import static br.gov.jfrj.siga.sr.model.SrTipoMovimentacao.TIPO_MOVIMENTACAO_VINCULACAO;
import static org.joda.time.format.DateTimeFormat.forPattern;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import play.db.jpa.JPA;
import play.mvc.Router;
import util.SigaPlayCalendar;
import br.gov.jfrj.siga.base.Par;
import br.gov.jfrj.siga.base.Texto;
import br.gov.jfrj.siga.base.util.Catalogs;
import br.gov.jfrj.siga.cp.CpComplexo;
import br.gov.jfrj.siga.cp.CpTipoConfiguracao;
import br.gov.jfrj.siga.cp.CpUnidadeMedida;
import br.gov.jfrj.siga.cp.model.HistoricoSuporte;
import br.gov.jfrj.siga.dp.CpMarcador;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.model.ActiveRecord;
import br.gov.jfrj.siga.model.Assemelhavel;
import br.gov.jfrj.siga.sr.model.vo.ListaInclusaoAutomatica;
import br.gov.jfrj.siga.sr.notifiers.Correio;
import br.gov.jfrj.siga.sr.util.Cronometro;
import br.gov.jfrj.siga.sr.util.Util;

@Entity
@Table(name = "SR_SOLICITACAO", schema = Catalogs.SIGASR)
public class SrSolicitacao extends HistoricoSuporte implements SrSelecionavel {
	private static final long serialVersionUID = 1L;

	public static ActiveRecord<SrSolicitacao> AR = new ActiveRecord<>(SrSolicitacao.class);

	@Id
	@SequenceGenerator(sequenceName = Catalogs.SIGASR +".SR_SOLICITACAO_SEQ", name = "srSolicitacaoSeq")
	@GeneratedValue(generator = "srSolicitacaoSeq")
	@Column(name = "ID_SOLICITACAO")
	private Long idSolicitacao;

	@ManyToOne()
	@JoinColumn(name = "ID_SOLICITANTE")
	private DpPessoa solicitante;

	@ManyToOne
	@JoinColumn(name = "ID_INTERLOCUTOR")
	private DpPessoa interlocutor;

	@ManyToOne
	@JoinColumn(name = "ID_LOTA_SOLICITANTE")
	private DpLotacao lotaSolicitante;

	@ManyToOne
	@JoinColumn(name = "ID_CADASTRANTE")
	private DpPessoa cadastrante;

	@ManyToOne
	@JoinColumn(name = "ID_LOTA_CADASTRANTE")
	private DpLotacao lotaCadastrante;

	@ManyToOne
	@JoinColumn(name = "ID_TITULAR")
	private DpPessoa titular;

	@ManyToOne
	@JoinColumn(name = "ID_LOTA_TITULAR")
	private DpLotacao lotaTitular;

	@ManyToOne
	@JoinColumn(name = "ID_DESIGNACAO")
	private SrConfiguracao designacao;

	@Transient
	private DpLotacao atendenteNaoDesignado;

	@Transient
	private Cronometro cron;

	@ManyToOne
	@JoinColumn(name = "ID_ORGAO_USU")
	private CpOrgaoUsuario orgaoUsuario;

	@ManyToOne
	@JoinColumn(name = "ID_SOLICITACAO_PAI")
	private SrSolicitacao solicitacaoPai;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "SR_SOLICITACAO_ACORDO", schema = Catalogs.SIGASR, joinColumns = { @JoinColumn(name = "ID_SOLICITACAO") }, inverseJoinColumns = { @JoinColumn(name = "ID_ACORDO") })
	private List<SrAcordo> acordos;

	@Enumerated
	private SrFormaAcompanhamento formaAcompanhamento;

	@Enumerated
	private SrMeioComunicacao meioComunicacao;

	@ManyToOne
	@JoinColumn(name = "ID_ITEM_CONFIGURACAO")
	private SrItemConfiguracao itemConfiguracao;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "ID_ARQUIVO")
	private SrArquivo arquivo;

	@ManyToOne
	@JoinColumn(name = "ID_ACAO")
	private SrAcao acao;

	@Lob
	@Column(name = "DESCR_SOLICITACAO", length = 8192)
	private String descrSolicitacao;

	@Enumerated
	private SrGravidade gravidade;

	@Enumerated
	private SrTendencia tendencia;

	@Enumerated
	private SrUrgencia urgencia;

	@Enumerated
	private SrPrioridade prioridade;

	@Column(name = "DT_REG")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dtReg;

	@Column(name = "DT_EDICAO_INI")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dtIniEdicao;

	@Column(name = "DT_ORIGEM")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dtOrigem;

	@ManyToOne
	@JoinColumn(name = "ID_COMPLEXO")
	private CpComplexo local;

	@Column(name = "TEL_PRINCIPAL")
	private String telPrincipal;

	@Transient
	private boolean fecharAoAbrir;

	@Transient
	private String motivoFechamentoAbertura;

	@Column(name = "NUM_SOLICITACAO")
	private Long numSolicitacao;

	@Column(name = "NUM_SEQUENCIA")
	private Long numSequencia;

	@Column(name = "DESCR_CODIGO")
	private String codigo;

	@ManyToOne()
	@JoinColumn(name = "HIS_ID_INI", insertable = false, updatable = false)
	private SrSolicitacao solicitacaoInicial;

	@OneToMany(targetEntity = SrSolicitacao.class, mappedBy = "solicitacaoInicial", fetch = FetchType.LAZY)
	//@OrderBy("hisDtIni desc")
	private List<SrSolicitacao> meuSolicitacaoHistoricoSet;

	@OneToMany(targetEntity = SrAtributoSolicitacao.class, mappedBy = "solicitacao", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	protected List<SrAtributoSolicitacao> meuAtributoSolicitacaoSet;

	@OneToMany(targetEntity = SrMovimentacao.class, mappedBy = "solicitacao", fetch = FetchType.LAZY)
	//@OrderBy("dtIniMov DESC")
	private Set<SrMovimentacao> meuMovimentacaoSet;

	@OneToMany(mappedBy = "solicitacaoPai", fetch = FetchType.LAZY)
	//@OrderBy("numSequencia asc")
	private Set<SrSolicitacao> meuSolicitacaoFilhaSet;

	@OneToMany(targetEntity = SrMovimentacao.class, mappedBy = "solicitacaoReferencia", fetch = FetchType.LAZY)
	private Set<SrMovimentacao> meuMovimentacaoReferenciaSet;

	// Edson: O where abaixo teve de ser explicito porque os id_refs conflitam
	// entre os modulos, e o Hibernate acaba trazendo tambem marcas do Siga-Doc
	@OneToMany(mappedBy = "solicitacao", fetch = FetchType.LAZY)
	@Where(clause = "ID_TP_MARCA=2")
	private Set<SrMarca> meuMarcaSet;

	@Column(name = "FG_RASCUNHO")
	@Type(type = "yes_no")
	private Boolean rascunho;

	@Column(name = "FECHADO_AUTOMATICAMENTE")
	@Type(type = "yes_no")
	private Boolean fechadoAutomaticamente;

	public SrSolicitacao() {

	}

	public class SrTarefa {
		public SrAcao acao;
		public SrConfiguracao conf;

		public SrTarefa() {

		}
		public SrAcao getAcao() {
			return acao;
		}
		public void setAcao(SrAcao acao) {
			this.acao = acao;
		}
		public SrConfiguracao getConf() {
			return conf;
		}
		public void setConf(SrConfiguracao conf) {
			this.conf = conf;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((acao == null) ? 0 : acao.hashCode());
			result = prime * result + ((conf == null) ? 0 : conf.getAtendente().hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SrTarefa other = (SrTarefa) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (acao == null) {
				if (other.acao != null)
					return false;
			} else if (!acao.equals(other.acao))
				return false;
			if (conf == null) {
				if (other.conf != null)
					return false;
			} else if (!conf.getAtendente().equals(other.conf.getAtendente()))
				return false;
			return true;
		}
		private SrSolicitacao getOuterType() {
			return SrSolicitacao.this;
		}
	}

	@Override
	public Long getId() {
		return getIdSolicitacao();
	}

	@Override
	public void setId(Long id) {
		this.setIdSolicitacao(id);
	}

	@Override
	public String getSigla() {
		return getCodigo();
	}

	@Override
	public void setSigla(String sigla) {
		sigla = sigla.trim().toUpperCase();
		Map<String, CpOrgaoUsuario> mapAcronimo = new TreeMap<String, CpOrgaoUsuario>();
		for (Object ou : AR.all().fetch()) {
			CpOrgaoUsuario cpOu = (CpOrgaoUsuario) ou;
			mapAcronimo.put(cpOu.getAcronimoOrgaoUsu(), cpOu);
		}
		String acronimos = "";
		for (String s : mapAcronimo.keySet()) {
			acronimos += "|" + s;
		}
		final Pattern p = Pattern
				.compile("^([A-Za-z0-9]{2}"
						+ acronimos
						+ ")?-?SR{1}-?(?:([0-9]{4})/?)??([0-9]{1,5})(?:[.]{1}([0-9]{1,3}))?$");
		final Matcher m = p.matcher(sigla);

		if (m.find()) {

			if (m.group(1) != null) {
				try {
					CpOrgaoUsuario orgaoUsuario = new CpOrgaoUsuario();
					orgaoUsuario.setSiglaOrgaoUsu(m.group(1));
					orgaoUsuario = (CpOrgaoUsuario) AR
							.em()
							.createQuery(
									"from CpOrgaoUsuario where acronimoOrgaoUsu = '"
											+ m.group(1) + "'")
							.getSingleResult();
					this.setOrgaoUsuario(orgaoUsuario);
				} catch (final Exception ce) {

				}
			}

			if (m.group(2) != null) {
				Calendar c1 = Calendar.getInstance();
				c1.set(Calendar.YEAR, Integer.valueOf(m.group(2)));
				c1.set(Calendar.DAY_OF_YEAR, 1);
				this.setDtReg(c1.getTime());
			} else
				this.setDtReg(new Date());

			if (m.group(3) != null)
				setNumSolicitacao(Long.valueOf(m.group(3)));

			if (m.group(4) != null)
				setNumSequencia(Long.valueOf(m.group(4)));

		}

	}

	@Override
	public String getDescricao() {
		if (getDescrSolicitacao() == null || getDescrSolicitacao().length() == 0) {
			if (isFilha())
				return getSolicitacaoPai().getDescricao();
			else
				return "Descrição não informada";
		} else
			return getDescrSolicitacao();
	}

	public List<SrAtributoSolicitacao> getMeuAtributoSolicitacaoSet() {
		if (meuAtributoSolicitacaoSet == null
				|| meuAtributoSolicitacaoSet.size() == 0) {
			if (isFilha())
				return getSolicitacaoPai().getMeuAtributoSolicitacaoSet();
		}

		return meuAtributoSolicitacaoSet;

	}

	public void setMeuAtributoSolicitacaoSet(
			List<SrAtributoSolicitacao> meuAtributoSolicitacaoSet) {
		this.meuAtributoSolicitacaoSet = meuAtributoSolicitacaoSet;
	}

	@Override
	public void setDescricao(String descricao) {
		this.setDescrSolicitacao(descricao);
	}

	public Boolean isFechadoAutomaticamente() {
		return fechadoAutomaticamente != null ? fechadoAutomaticamente : false;
	}

	public void setFechadoAutomaticamente(Boolean fechadoAutomaticamente) {
		this.fechadoAutomaticamente = fechadoAutomaticamente;
	}

	@Override
	public SrSelecionavel selecionar(String sigla) throws Exception {
		setSigla(sigla);
		if (getOrgaoUsuario() == null && getLotaTitular() != null)
			setOrgaoUsuario(getLotaTitular().getOrgaoUsuario());

		String query = "from SrSolicitacao where hisDtFim is null ";

		if (getOrgaoUsuario() != null) {
			query += " and orgaoUsuario.idOrgaoUsu = "
					+ getOrgaoUsuario().getIdOrgaoUsu();
		}
		if (getDtReg() != null) {
			Calendar c1 = Calendar.getInstance();
			c1.setTime(getDtReg());
			int year = c1.get(Calendar.YEAR);
			query += " and dtReg between to_date('01/01/" + year
					+ " 00:01', 'dd/mm/yyyy HH24:mi') and to_date('31/12/"
					+ year + " 23:59','dd/mm/yyyy HH24:mi')";
		}
		if (getNumSolicitacao() != null)
			query += " and numSolicitacao = " + getNumSolicitacao();
		if (getNumSequencia() == null)
			query += " and numSequencia is null";
		else
			query += " and numSequencia = " + getNumSequencia();

		SrSolicitacao sol = (SrSolicitacao) AR.em().createQuery(query)
				.getSingleResult();

		return sol;
	}

	@Override
	public List<? extends SrSelecionavel> buscar() throws Exception {
		return null;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getCodigo() {
		return codigo;
	}

	public void atualizarCodigo() {
		if (isRascunho() || getNumSolicitacao() == null) {
			codigo = "TMPSR-"
					+ (getSolicitacaoInicial() != null ? getSolicitacaoInicial().getIdSolicitacao()
							: getIdSolicitacao());
			return;
		}

		if (getSolicitacaoPai() != null) {
			codigo = getSolicitacaoPai().getCodigo() + getNumSequenciaString();
			return;
		}

		if (getNumSolicitacao() == null) {
			codigo = "";
			return;
		}

		String numString = getNumSolicitacao().toString();

		while (numString.length() < 5)
			numString = "0" + numString;

		codigo = getOrgaoUsuario().getAcronimoOrgaoUsu() + "-SR-" + getAnoEmissao()
				+ "/" + numString;
	}

	public List<SrAtributoSolicitacao> getAtributoSolicitacaoSet() {
		if (meuAtributoSolicitacaoSet == null || meuAtributoSolicitacaoSet.isEmpty()) {
			if (isFilha())
				return getSolicitacaoPai().getAtributoSolicitacaoSet();
			else
				return new ArrayList<SrAtributoSolicitacao>();
		} else
			return meuAtributoSolicitacaoSet;
	}

	public String getDtRegString() {
		SigaPlayCalendar cal = new SigaPlayCalendar();
		cal.setTime(getDtReg());
		return "<!--" + getDtReg().getTime() + "-->" + cal.getTempoTranscorridoString(false);
	}

	public String getAtributosString() {
		String s = "";
		for (SrAtributoSolicitacao att : getAtributoSolicitacaoSet()) {
			if (att.valorAtributoSolicitacao != null)
				s += att.atributo.getNomeAtributo() + ": "
						+ att.valorAtributoSolicitacao + ". ";
		}
		return s;
	}

	// Edson: Necess�rio porque nao h� binder para arquivo
	public void setArquivo(File file) {
		this.arquivo = SrArquivo.newInstance(file);
	}

	public int getGUT() {
		return getGravidade().nivelGravidade * getUrgencia().nivelUrgencia
				* getTendencia().nivelTendencia;
	}

	public String getGUTString() {
		return getGravidade().descrGravidade + " " + getUrgencia().descrUrgencia + " "
				+ getTendencia().descrTendencia;
	}

	public String getGUTPercent() {
		return (int) ((getGUT() / 125.0) * 100) + "%";
	}

	public void associarPrioridadePeloGUT() {
		int valorGUT = getGUT();
		if (Util.isbetween(1, 24, valorGUT))
			setPrioridade(SrPrioridade.PLANEJADO);
		else if (Util.isbetween(25, 49, valorGUT))
			setPrioridade(SrPrioridade.BAIXO);
		else if (Util.isbetween(50, 74, valorGUT))
			setPrioridade(SrPrioridade.MEDIO);
		else if (Util.isbetween(75, 99, valorGUT))
			setPrioridade(SrPrioridade.ALTO);
		else if (Util.isbetween(100, 125, valorGUT))
			setPrioridade(SrPrioridade.IMEDIATO);
	}

	public String getDtRegDDMMYYYYHHMM() {
		if (getDtReg() != null) {
			final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			return df.format(getDtReg());
		}
		return "";
	}

	public String getDtRegDDMMYYYY() {
		if (getDtReg() != null) {
			final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			return df.format(getDtReg());
		}
		return "";
	}

	public String getDtRegHHMM() {
		if (getDtReg() != null) {
			final SimpleDateFormat df = new SimpleDateFormat("HH:mm");
			return df.format(getDtReg());
		}
		return "";
	}

	public Long getProximoNumero() {
		if (getOrgaoUsuario() == null)
			return 0L;
		Long num = AR.find(
				"select max(numSolicitacao)+1 from SrSolicitacao where orgaoUsuario.idOrgaoUsu = "
						+ getOrgaoUsuario().getIdOrgaoUsu()).first();
		return (num != null) ? num : 1;
	}

	public String getAnoEmissao() {
		if (getDtReg() == null)
			return null;
		return new SimpleDateFormat("yyyy").format(getDtReg());
	}

	public String getNumSequenciaString() {
		if (getNumSequencia() == null)
			return null;
		return "." + (getNumSequencia() < 10 ? "0" : "") + getNumSequencia().toString();
	}

	public List<SrSolicitacao> getHistoricoSolicitacao() {
		if (getSolicitacaoInicial() != null)
			return getSolicitacaoInicial().getMeuSolicitacaoHistoricoSet();
		return null;
	}

	public SrSolicitacao getSolicitacaoAtual() {
		if (getHisDtFim() == null)
			return this;
		List<SrSolicitacao> sols = getHistoricoSolicitacao();
		if (sols == null)
			return null;
		return sols.get(0);

	}

	public SrMovimentacao getUltimoAndamento() {
		return getUltimaMovimentacaoPorTipo(TIPO_MOVIMENTACAO_ANDAMENTO);
	}

	public SrMovimentacao getUltimaMovimentacao() {
		for (SrMovimentacao movimentacao : getMovimentacaoSet())
			return movimentacao;
		return null;
	}

	public SrMovimentacao getUltimaMovimentacaoMesmoSeCanceladaTodoOContexto() {
		for (SrMovimentacao movimentacao : getMovimentacaoSetComCanceladosTodoOContexto())
			return movimentacao;
		return null;
	}

	public SrMovimentacao getUltimaMovimentacaoQuePossuaDescricao() {
		for (SrMovimentacao mov : getMovimentacaoSet()) {
			if (mov.descrMovimentacao != null
					&& mov.descrMovimentacao.length() > 0)
				return mov;
		}
		return null;
	}

	public SrMovimentacao getUltimaMovimentacaoPorTipo(Long idTpMov) {
		for (SrMovimentacao movimentacao : getMovimentacaoSetPorTipo(idTpMov))
			return movimentacao;
		return null;
	}

	public SrMovimentacao getUltimaMovimentacaoCancelavel() {
		for (SrMovimentacao mov : getMovimentacaoSet()) {
			if (mov.numSequencia > 1
					&& mov.tipoMov.idTipoMov != TIPO_MOVIMENTACAO_RETIRADA_DE_LISTA
					&& mov.tipoMov.idTipoMov != TIPO_MOVIMENTACAO_INCLUSAO_LISTA
					&& mov.tipoMov.idTipoMov != TIPO_MOVIMENTACAO_ALTERACAO_PRIORIDADE_LISTA
					&& mov.tipoMov.idTipoMov != TIPO_MOVIMENTACAO_AVALIACAO)
				return mov;
		}
		return null;
	}

	public Set<SrMovimentacao> getMovimentacaoSet() {
		return getMovimentacaoSet(false, null, false, false, false, false);
	}

	public Set<SrMovimentacao> getMovimentacaoSetPorTipo(Long tipoMov) {
		return getMovimentacaoSet(false, tipoMov, false, false, false, false);
	}

	public Set<SrMovimentacao> getMovimentacaoReferenciaSetPorTipo(Long tipoMov) {
		return getMovimentacaoSet(false, tipoMov, false, false, false, true);
	}

	public Set<SrMovimentacao> getMovimentacaoSetComCancelados() {
		return getMovimentacaoSet(true, null, false, false, false, false);
	}

	public Set<SrMovimentacao> getMovimentacaoSetComCanceladosTodoOContexto() {
		return getMovimentacaoSet(true, null, false, true, false, false);
	}

	public Set<SrMovimentacao> getMovimentacaoSetOrdemCrescente() {
		return getMovimentacaoSet(false, null, true, false, false, false);
	}

	public Set<SrMovimentacao> getMovimentacaoSet(boolean considerarCanceladas,
			Long tipoMov, boolean ordemCrescente, boolean todoOContexto,
			boolean apenasPrincipais, boolean inversoJPA) {

		List<Long> tiposPrincipais = Arrays.asList(TIPO_MOVIMENTACAO_ANDAMENTO,
				TIPO_MOVIMENTACAO_INICIO_ATENDIMENTO,
				TIPO_MOVIMENTACAO_FECHAMENTO, TIPO_MOVIMENTACAO_AVALIACAO, TIPO_MOVIMENTACAO_ESCALONAMENTO);

		return getMovimentacaoSet(considerarCanceladas, tipoMov,
				ordemCrescente, todoOContexto, apenasPrincipais, inversoJPA,
				tiposPrincipais);
	}

	public Set<SrMovimentacao> getMovimentacaoSet(boolean considerarCanceladas,
			Long tipoMov, boolean ordemCrescente, boolean todoOContexto,
			boolean apenasPrincipais, boolean inversoJPA,
			List<Long> tiposPrincipais) {

		TreeSet<SrMovimentacao> listaCompleta = new TreeSet<SrMovimentacao>(
				new SrMovimentacaoComparator(ordemCrescente));

		Set<SrSolicitacao> solsAConsiderar = new LinkedHashSet<SrSolicitacao>();
		if (todoOContexto) {
			solsAConsiderar.addAll(getPaiDaArvore()
					.getSolicitacaoFilhaSetRecursivo());
		} else
			solsAConsiderar.add(this);

		for (SrSolicitacao sol : solsAConsiderar) {
			if (sol.getSolicitacaoInicial() != null)
				for (SrSolicitacao instancia : sol.getHistoricoSolicitacao()) {
					Set<SrMovimentacao> movSet = inversoJPA ? instancia.getMeuMovimentacaoReferenciaSet()
							: instancia.getMeuMovimentacaoSet();
					if (movSet != null)
						for (SrMovimentacao movimentacao : movSet) {
							if (!considerarCanceladas
									&& movimentacao.isCanceladoOuCancelador())
								continue;
							if (tipoMov != null
									&& movimentacao.tipoMov.idTipoMov != tipoMov)
								continue;
							if (apenasPrincipais
									&& !tiposPrincipais
											.contains(movimentacao.tipoMov.idTipoMov))
								continue;

							listaCompleta.add(movimentacao);
						}
				}
		}
		return listaCompleta;
	}

	public boolean jaFoiDesignada() {
		for (SrMovimentacao mov : getMovimentacaoSetOrdemCrescente()) {
			if (mov.tipoMov.idTipoMov == TIPO_MOVIMENTACAO_INICIO_ATENDIMENTO
					|| mov.tipoMov.idTipoMov == TIPO_MOVIMENTACAO_FECHAMENTO)
				return true;
		}
		return false;
	}

	public DpLotacao getLotaAtendente() {
		if (getUltimaMovimentacao() != null)
			return getUltimaMovimentacao().lotaAtendente;
		else
			return null;
	}

	public DpPessoa getAtendente() {
		return getUltimaMovimentacao().atendente;
	}

	public boolean isFilha() {
		return (this.getSolicitacaoPai() != null);
	}

	public Set<SrMovimentacao> getPendenciasEmAberto() {
		Set<SrMovimentacao> setIni = getMovimentacaoSetPorTipo(TIPO_MOVIMENTACAO_INICIO_PENDENCIA);
		Set<SrMovimentacao> setPendentes = new HashSet<SrMovimentacao>();

		for (SrMovimentacao ini : setIni) {
			if ((!ini.isFinalizada())
					&& (ini.dtAgenda == null || ini.dtAgenda.after(new Date())))
				setPendentes.add(ini);
		}

		return setPendentes;
	}

	// Edson: ver comentario abaixo, em getTiposAtributoAssociados()
	public HashMap<Long, Boolean> getObrigatoriedadeTiposAtributoAssociados()
			throws Exception {
		HashMap<Long, Boolean> map = new HashMap<Long, Boolean>();
		getAtributoAssociados(map);
		return map;
	}

	public List<SrAtributo> getAtributoAssociados() throws Exception {
		return getAtributoAssociados(null);
	}

	// Edson: isso esta esquisito. A funcao esta praticamente com dois retornos.
	// Talvez ficasse melhor se o SrAtributo ja tivesse a informacao sobre
	// a obrigatoriedade dele
	private List<SrAtributo> getAtributoAssociados(HashMap<Long, Boolean> map)
			throws Exception {
		List<SrAtributo> listaFinal = new ArrayList<SrAtributo>();

		SrConfiguracao confFiltro = new SrConfiguracao();
		confFiltro.setDpPessoa(getSolicitante());
		confFiltro.setComplexo(getLocal());
		confFiltro.setItemConfiguracaoFiltro(getItemConfiguracao());
		confFiltro.setAcaoFiltro(getAcao());

		for (SrAtributo t : SrAtributo.listarParaSolicitacao(Boolean.FALSE)) {
			confFiltro.setAtributo(t);
			SrConfiguracao conf = SrConfiguracao.buscarAssociacao(confFiltro);
			if (conf != null) {
				listaFinal.add(t);
				if (map != null)
					map.put(t.getIdAtributo(), conf.isAtributoObrigatorio());
			}
		}

		return listaFinal;
	}

	public DpLotacao getUltimoAtendenteEtapaAtendimento() {
		for (SrMovimentacao mov : getMovimentacaoSet()) {
			if (mov.tipoMov.idTipoMov == TIPO_MOVIMENTACAO_ANDAMENTO)
				return mov.lotaAtendente;
		}
		return null;
	}

	// Edson: poderia também guardar num HashMap transiente e, ao salvar(),
	// mandar criar os atributos, caso se quisesse permitir um
	// solicitacao.getAtributoSet().put...
	public void setAtributoSolicitacaoMap(
			HashMap<Long, String> atributosSolicitacao) {
		if (atributosSolicitacao != null) {
			meuAtributoSolicitacaoSet = new ArrayList<SrAtributoSolicitacao>();
			for (Long idAtt : atributosSolicitacao.keySet()) {
				SrAtributo att = SrAtributo.findById(idAtt);
				SrAtributoSolicitacao attSolicitacao = new SrAtributoSolicitacao(
						att, atributosSolicitacao.get(idAtt), this);
				meuAtributoSolicitacaoSet.add(attSolicitacao);
			}
		}
	}

	public HashMap<Long, String> getAtributoSolicitacaoMap() {
		HashMap<Long, String> map = new LinkedHashMap<Long, String>(); // Para
																		// manter
																		// a
																		// ordem
																		// de
																		// insercao
		if (meuAtributoSolicitacaoSet != null)
			for (SrAtributoSolicitacao att : meuAtributoSolicitacaoSet) {
				map.put(att.atributo.getIdAtributo(), att.valorAtributoSolicitacao);
			}
		return map;
	}

	private Set<SrSolicitacao> getSolicitacaoFilhaSet() {
		TreeSet<SrSolicitacao> listaCompleta = new TreeSet<SrSolicitacao>(
				new Comparator<SrSolicitacao>() {
					@Override
					public int compare(SrSolicitacao s1, SrSolicitacao s2) {
						return s1.getNumSequencia().compareTo(s2.getNumSequencia());
					}
				});

		if (getSolicitacaoInicial() != null) {
			for (SrSolicitacao sol : getHistoricoSolicitacao()) {
				if (sol.getMeuSolicitacaoFilhaSet() != null)
					for (SrSolicitacao filha : sol.getMeuSolicitacaoFilhaSet())
						if (filha.getHisDtFim() == null)
							listaCompleta.add(filha);
			}
		}
		return listaCompleta;
	}

	public Set<SrSolicitacao> getSolicitacaoFilhaSetRecursivo() {
		Set<SrSolicitacao> listaCompleta = new LinkedHashSet<SrSolicitacao>();
		listaCompleta.add(this);
		for (SrSolicitacao filha : getSolicitacaoFilhaSet())
			listaCompleta.addAll(filha.getSolicitacaoFilhaSetRecursivo());
		return listaCompleta;
	}

	public boolean isPai() {
		return getSolicitacaoFilhaSet() != null
				&& getSolicitacaoFilhaSet().size() > 0;
	}

	public Set<SrMarca> getMarcaSet() {
		TreeSet<SrMarca> listaCompleta = new TreeSet<SrMarca>();
		if (getSolicitacaoInicial() != null)
			for (SrSolicitacao sol : getHistoricoSolicitacao())
				if (sol.getMeuMarcaSet() != null)
					listaCompleta.addAll(sol.getMeuMarcaSet());
		return listaCompleta;
	}

	public Set<SrMarca> getMarcaSetAtivas() {
		Set<SrMarca> set = new TreeSet<SrMarca>();
		Date agora = new Date();
		for (SrMarca m : getMarcaSet()) {
			if ((m.getDtIniMarca() == null || m.getDtIniMarca().before(agora))
					&& (m.getDtFimMarca() == null || m.getDtFimMarca().after(
							agora)))
				set.add(m);
		}
		return set;
	}

	public boolean isJuntada() {
		return sofreuMov(TIPO_MOVIMENTACAO_JUNTADA,
				TIPO_MOVIMENTACAO_DESENTRANHAMENTO);
	}

	public boolean isEditado() {
		return !getIdSolicitacao().equals(getHisIdIni());
	}

	public boolean isCancelado() {
		return getUltimaMovimentacaoPorTipo(TIPO_MOVIMENTACAO_CANCELAMENTO_DE_SOLICITACAO) != null;
	}

	public Boolean isRascunho() {
		return getRascunho() != null ? getRascunho() : false;
	}

	public boolean isFechado() {
		return sofreuMov(TIPO_MOVIMENTACAO_FECHAMENTO,
				TIPO_MOVIMENTACAO_REABERTURA) && !isCancelado();
	}

	public boolean isPendente() {
		Set<SrMovimentacao> setIni = getMovimentacaoSetPorTipo(TIPO_MOVIMENTACAO_INICIO_PENDENCIA);
		for (SrMovimentacao ini : setIni) {
			if ((!ini.isFinalizada())
					&& (ini.dtAgenda == null || ini.dtAgenda.after(new Date())))

				return true;
		}
		return false;
	}

	public boolean isEmAtendimento() {
		long idsTpsMovs[] = { TIPO_MOVIMENTACAO_INICIO_ATENDIMENTO,
				TIPO_MOVIMENTACAO_REABERTURA };
		return sofreuMov(idsTpsMovs, TIPO_MOVIMENTACAO_FECHAMENTO)
				&& !isCancelado() && !isJuntada();
	}

	public boolean isEscalonada() {
		return sofreuMov(
				SrTipoMovimentacao.TIPO_MOVIMENTACAO_ESCALONAMENTO,
				SrTipoMovimentacao.TIPO_MOVIMENTACAO_CANCELAMENTO_DE_MOVIMENTACAO);
	}

	public boolean isAFechar() {
		Set<SrSolicitacao> filhas = getSolicitacaoFilhaSet();
		if (filhas.size() == 0)
			return false;
		for (SrSolicitacao filha : filhas) {
			if (!filha.isFechado() && !filha.isCancelado())
				return false;
		}
		return true;
	}

	public boolean sofreuMov(long idTpMov, long... idsTpsReversores) {
		return sofreuMov(new long[] { idTpMov }, idsTpsReversores);
	}

	public boolean sofreuMov(long[] idsTpsMovs, long... idsTpsReversores) {
		for (SrMovimentacao mov : getMovimentacaoSet()) {
			for (long idTpMov : idsTpsMovs)
				if (mov.tipoMov.idTipoMov == idTpMov)
					return true;
				else
					for (long idTpReversor : idsTpsReversores)
						if (mov.tipoMov.idTipoMov == idTpReversor)
							return false;
		}
		return false;
	}

	public SrSolicitacao getSolicitacaoPrincipal() {
		for (SrMovimentacao mov : getMovimentacaoSet()) {
			if (mov.tipoMov.idTipoMov == SrTipoMovimentacao.TIPO_MOVIMENTACAO_DESENTRANHAMENTO)
				return null;
			if (mov.tipoMov.idTipoMov == SrTipoMovimentacao.TIPO_MOVIMENTACAO_JUNTADA)
				return mov.solicitacaoReferencia;
		}
		return null;
	}

	public boolean estaCom(DpPessoa pess, DpLotacao lota) {
		SrMovimentacao ultMov = getUltimaMovimentacao();
		SrMovimentacao ultMovDoPai = null;
		if (isFilha())
			ultMovDoPai = this.getSolicitacaoPai().getUltimaMovimentacao();
		if (isRascunho())
			return foiCadastradaPor(pess, lota) || foiSolicitadaPor(pess, lota);
		return (ultMov != null && ((ultMov.atendente != null && pess != null && ultMov.atendente.equivale(pess))
					|| (ultMov.lotaAtendente != null && ultMov.lotaAtendente.equivale(lota))))

				|| (ultMovDoPai != null && ((ultMovDoPai.atendente != null && ultMovDoPai.atendente
						.equivale(pess)) || (ultMovDoPai.lotaAtendente != null && ultMovDoPai.lotaAtendente
						.equivale(lota))));

	}

	public boolean foiSolicitadaPor(DpPessoa pess, DpLotacao lota) {
		return (getSolicitante().equivale(pess) || getLotaSolicitante().equivale(lota));
	}

	public boolean foiCadastradaPor(DpPessoa pess, DpLotacao lota) {
		return (getCadastrante().equivale(pess) || (getLotaTitular() != null && getLotaTitular().equivale(lota)));
	}

	public boolean isParteDeArvore() {
		return getSolicitacaoPai() != null
				|| (getSolicitacaoFilhaSet() != null && !getSolicitacaoFilhaSet()
						.isEmpty());
	}

	public SrSolicitacao getPaiDaArvore() {
		SrSolicitacao pai = this;
		while (pai.getSolicitacaoPai() != null) {
			pai = pai.getSolicitacaoPai();
		}
		return pai;
	}

	public boolean temArquivosAnexos() {
		return getArquivoAnexoNaCriacao() != null
				|| getMovimentacoesAnexacao().size() > 0;
	}

	public SrArquivo getArquivoAnexoNaCriacao() {
		if (getSolicitacaoInicial() != null)
			for (SrSolicitacao sol : getHistoricoSolicitacao())
				if (sol.getArquivo() != null)

					return sol.getArquivo();
		return null;
	}

	public Set<SrMovimentacao> getMovimentacoesAnexacao() {
		return getMovimentacaoSetPorTipo(TIPO_MOVIMENTACAO_ANEXACAO_ARQUIVO);
	}

	public boolean podeEscalonar(DpPessoa pess, DpLotacao lota) {
		return estaCom(pess, lota) && isEmAtendimento();
	}

	public boolean podeJuntar(DpPessoa pess, DpLotacao lota) {
		return estaCom(pess, lota) && (isEmAtendimento() || isPendente())
				&& !isJuntada();
	}

	public boolean podeDesentranhar(DpPessoa pess, DpLotacao lota) {
		return estaCom(pess, lota) && isJuntada();
	}

	public boolean podeVincular(DpPessoa titular, DpLotacao lotaTitular) {
		return !isRascunho();
	}

	public boolean podeDesfazerMovimentacao(DpPessoa pess, DpLotacao lota) {
		SrMovimentacao ultCancelavel = getUltimaMovimentacaoCancelavel();
		if (ultCancelavel == null || ultCancelavel.cadastrante == null)
			return false;
		return ultCancelavel.lotaCadastrante.equivale(lota);
	}

	public boolean podeEditar(DpPessoa pess, DpLotacao lota) {
		return (estaCom(pess, lota) || isEmListaPertencenteA(lota))
				&& isRascunho()
				&& (!jaFoiDesignada());
	}

	public boolean podePriorizar(DpPessoa pess, DpLotacao lota) {
		return podeEditar(pess, lota);
	}

	public boolean podeAbrirJaFechando(DpPessoa pess, DpLotacao lota) {
		return false;
	}

	public boolean podeFechar(DpPessoa pess, DpLotacao lota) {
		return estaCom(pess, lota)
				&& (isEmAtendimento());
	}

	public boolean podeExcluir(DpPessoa pess, DpLotacao lota) {
		return foiCadastradaPor(pess, lota) && isRascunho();
	}

	public boolean podeCancelar(DpPessoa pess, DpLotacao lota) {
		return estaCom(pess, lota)
				&& isEmAtendimento();
	}

	public boolean podeDeixarPendente(DpPessoa pess, DpLotacao lota) {
		return isRascunho()
				|| ((isEmAtendimento() || isPendente()) && estaCom(pess, lota));
	}

	public boolean podeAlterarPrazo(DpPessoa pess, DpLotacao lota) {
		return !isRascunho() && !isFechado() && estaCom(pess, lota);
	}

	public boolean podeTerminarPendencia(DpPessoa pess, DpLotacao lota) {
		return isPendente() && estaCom(pess, lota);
	}

	public boolean podeReabrir(DpPessoa pess, DpLotacao lota) {
		return isFechado()
				&& (estaCom(pess, lota) || foiCadastradaPor(pess, lota) || foiSolicitadaPor(
					pess, lota));
	}

	public boolean podeAnexarArquivo(DpPessoa pess, DpLotacao lota) {
		return (isEmAtendimento() || isPendente() || isRascunho());
	}

	public boolean podeImprimirTermoAtendimento(DpPessoa pess, DpLotacao lota) {
		return isEmAtendimento() && estaCom(pess, lota);
	}

	public boolean podeIncluirEmLista(DpPessoa pess, DpLotacao lota) {
		return isEmAtendimento();
	}

	public boolean podeTrocarAtendente(DpPessoa pess, DpLotacao lota) {
		return estaCom(pess, lota) && isEmAtendimento();
	}

	public boolean podeResponderPesquisa(DpPessoa pess, DpLotacao lota)
			throws Exception {

		if (!isFechado() || !foiSolicitadaPor(pess, lota)
				/*|| !temPesquisaSatisfacao()*/)
			return false;

		for (SrMovimentacao mov : getMovimentacaoSet())
			if (mov.tipoMov.idTipoMov == SrTipoMovimentacao.TIPO_MOVIMENTACAO_AVALIACAO)
				return false;
			else if (mov.tipoMov.idTipoMov == SrTipoMovimentacao.TIPO_MOVIMENTACAO_FECHAMENTO)
				return true;

		return false;

	}

	public boolean podeFecharPaiAutomatico() {
		return isFilha()
				&& getSolicitacaoPai().getSolicitacaoAtual().isFechadoAutomaticamente()
				&& getSolicitacaoPai().isAFechar();
	}

	@SuppressWarnings("unchecked")
	public SrSolicitacao deduzirLocalRamalEMeioContato() {

		if (getSolicitante() == null)
			return this;

		if (getLotaSolicitante() == null)
			setLotaSolicitante(getSolicitante().getLotacao());

		// Tenta buscar a ultima aberta pelo solicitante
		String queryString = "from SrSolicitacao sol where sol.idSolicitacao = ("
				+ "	select max(idSolicitacao) from SrSolicitacao "
				+ "	where solicitante.idPessoa in ("
				+ "		select idPessoa from DpPessoa "
				+ "		where idPessoaIni = "
				+ getSolicitante().getIdPessoaIni() + "))";
		List<SrSolicitacao> listaProvisoria = AR.em().createQuery(queryString)
				.getResultList();
		SrSolicitacao ultima = null;
		if (listaProvisoria != null && listaProvisoria.size() > 0)
			ultima = listaProvisoria.get(0);

		// Tenta buscar a ultima aberta pela lotacao dele
		if (ultima == null && getLotaSolicitante() != null) {
			queryString = "from SrSolicitacao sol where sol.idSolicitacao = ("
					+ "	select max(idSolicitacao) from SrSolicitacao "
					+ "	where lotaSolicitante.idLotacao in ("
					+ "		select idLotacao from DpLotacao "
					+ "		where idLotacaoIni = "
					+ getLotaSolicitante().getIdLotacaoIni() + "))";
			listaProvisoria = AR.em().createQuery(queryString).getResultList();
			if (listaProvisoria != null && listaProvisoria.size() > 0)
				ultima = listaProvisoria.get(0);
		}

		if (ultima != null) {
			setTelPrincipal(ultima.getTelPrincipal());
			setLocal(ultima.getLocal());
			setMeioComunicacao(ultima.getMeioComunicacao());
		} else {
			setTelPrincipal("");
			setLocal(null);
		}

		return this;
	}

	@SuppressWarnings("unchecked")
	public List<CpComplexo> getLocaisDisponiveis() {
		List<CpComplexo> locais = new ArrayList<CpComplexo>();
		if (getSolicitante() != null)
			locais = AR
					.em()
					.createQuery(
							"from CpComplexo where orgaoUsuario.idOrgaoUsu = "
									+ getSolicitante().getOrgaoUsuario()
											.getIdOrgaoUsu()).getResultList();
		return locais;
	}

	public List<SrItemConfiguracao> getItensDisponiveis() throws Exception {
		if (getSolicitante() == null)
			return null;

		List<SrItemConfiguracao> listaTodosItens = new ArrayList<SrItemConfiguracao>();
		List<SrItemConfiguracao> listaFinal = new ArrayList<SrItemConfiguracao>();

		List<SrConfiguracao> listaPessoasAConsiderar = getFiltrosParaConsultarDesignacoes();
		listaTodosItens = SrItemConfiguracao.listar(false);

		for (SrItemConfiguracao i : listaTodosItens) {
			if (!i.isEspecifico())
				continue;
			for (SrConfiguracao c : listaPessoasAConsiderar)
				if (!listaFinal.contains(i)) {

					c.setItemConfiguracaoFiltro(i);

					if (SrConfiguracao.buscarDesignacao(c,
							new int[] { SrConfiguracaoBL.ACAO}) != null){
						listaFinal.add(i);
						SrItemConfiguracao itemPai = i.getPai();
						while (itemPai != null) {
							if (!listaFinal.contains(itemPai))
								listaFinal.add(itemPai);
							itemPai = itemPai.getPai();
						}
					}
				}
		}

		Collections.sort(listaFinal, new SrItemConfiguracaoComparator());

		return listaFinal;
	}

	public List<SrAcao> getAcoesDisponiveis() throws Exception {
		//Map<SrAcao, SrConfiguracao> acoesEAtendentes = getAcoesDisponiveisComAtendente();
		//return acoesEAtendentes != null ? new ArrayList<SrAcao>(acoesEAtendentes.keySet()) : null;
		List<SrTarefa> acoesEAtendentes = getAcoesDisponiveisComAtendente();
		List<SrAcao> acoes = new ArrayList<SrAcao>();

		if(acoesEAtendentes == null)
			return null;
		for (SrTarefa t: acoesEAtendentes)
			acoes.add(t.acao);
		return acoes;
	}

	public List<SrTarefa> getAcoesDisponiveisComAtendente()
			throws Exception {

		if (getSolicitante() == null || getItemConfiguracao() == null)
			return null;

		List<SrTarefa> listaFinal = new ArrayList<SrTarefa>();
		Set<SrTarefa> setTerafa = new HashSet<SrTarefa>();
		List<SrConfiguracao> listaPessoasAConsiderar = getFiltrosParaConsultarDesignacoes();
		SrTarefa tarefa = null;

		for (SrAcao a : SrAcao.listar(false)) {
			if (!a.isEspecifico())
				continue;
			for (SrConfiguracao c : listaPessoasAConsiderar) {
				c.setItemConfiguracaoFiltro(getItemConfiguracao());
				c.setAcaoFiltro(a);
				c.setCpTipoConfiguracao(AR.em().find(CpTipoConfiguracao.class,
						CpTipoConfiguracao.TIPO_CONFIG_SR_DESIGNACAO));

				List<SrConfiguracao> confs = SrConfiguracao.listar(c, new int[] { SrConfiguracaoBL.ATENDENTE});
				if (confs.size() > 0)
					for (SrConfiguracao conf : confs) {
						tarefa = this.new SrTarefa();
						tarefa.acao = a;
						tarefa.conf = conf;
						setTerafa.add(tarefa);
					}
			}
		}
		listaFinal.addAll(setTerafa);
		return listaFinal;
	}

	public Map<SrAcao, List<SrTarefa>> getAcoesEAtendentes() throws Exception {
		Map<SrAcao, List<SrTarefa>> acoesEAtendentesFinal = new TreeMap<SrAcao, List<SrTarefa>>(new Comparator<SrAcao>() {
	        @Override
	        public int compare(SrAcao  a1, SrAcao a2) {
				int i = a1.getTituloAcao().compareTo(a2.getTituloAcao());
				if (i != 0)
					return i;
				return a1.getIdAcao().compareTo(a2.getIdAcao());
	        }
	        });

		List<SrTarefa> acoesEAtendentes = getAcoesDisponiveisComAtendente();
		if (acoesEAtendentes != null && this.getItemConfiguracao() != null){

			if (acoesEAtendentes.size() == 1)
				this.setAcao(acoesEAtendentes.get(0).acao);
			else this.setAcao(null);

			for (SrTarefa t : acoesEAtendentes){
				List<SrTarefa> tarefas = acoesEAtendentesFinal.get(t.getAcao().getPai());
				if (tarefas == null)
					tarefas = new ArrayList<SrTarefa>();
				tarefas.add(t);
				acoesEAtendentesFinal.put(t.getAcao().getPai(), tarefas);
			}

			//Edson: melhor se fosse um SortedSet
			for (List<SrTarefa> tarefas : acoesEAtendentesFinal.values()){
				Collections.sort(tarefas , new Comparator<SrTarefa>() {
			        @Override
			        public int compare(SrTarefa  o1, SrTarefa o2) {
						int i = o1.acao.getTituloAcao().compareTo(o2.acao.getTituloAcao());
						if (i != 0)
							return i;
						return o1.acao.getIdAcao().compareTo(o2.acao.getIdAcao());
			        }
			    });
			}

		}

		return acoesEAtendentesFinal;
	}

	@SuppressWarnings("serial")
	public SortedSet<SrOperacao> operacoes(final DpPessoa pess, final DpLotacao lota)
					throws Exception {
		SortedSet<SrOperacao> operacoes = new TreeSet<SrOperacao>() {
			@Override
			public boolean add(SrOperacao e) {
				// Edson: ser� que essas coisas poderiam estar dentro do
				// SrOperacao?
				if (e.params == null)
					e.params = new HashMap<String, Object>();
				e.params.put("id", getIdSolicitacao());

				if (!e.isModal())
					e.url = Router.reverse(e.url, e.params).url;
				if (!e.pode)
					return false;
				return super.add(e);
			}
		};

		operacoes.add(new SrOperacao("pencil", "Editar", podeEditar(
				pess, lota), "Application.editar"));

		operacoes.add(new SrOperacao("table_relationship", "Vincular",
				podeVincular(pess, lota),
				"vincular", "modal=true"));

		operacoes.add(new SrOperacao("arrow_divide",
				"Escalonar", podeEscalonar(pess, lota),
				"escalonar", "modal=true"));

		operacoes.add(new SrOperacao("arrow_join", "Juntar",
				podeJuntar(pess, lota),
				"juntar", "modal=true"));

		operacoes.add(new SrOperacao("arrow_out", "Desentranhar",
				podeDesentranhar(pess, lota), "desentranhar", "modal=true"));

		operacoes.add(new SrOperacao("text_list_numbers", "Incluir em Lista",
				podeIncluirEmLista(pess, lota), "incluirEmLista",
				"modal=true"));

		operacoes.add(new SrOperacao("lock", "Fechar", podeFechar(pess, lota
				), "fechar", "modal=true"));

		operacoes.add(new SrOperacao("script_edit", "Responder Pesquisa",
				podeResponderPesquisa(pess, lota),
				"responderPesquisa", "modal=true"));

		operacoes.add(new SrOperacao("cross", "Cancelar Solicitação",
				podeCancelar(pess, lota), "Application.cancelar"));

		operacoes.add(new SrOperacao("lock_open", "Reabrir", podeReabrir(
				pess, lota), "Application.reabrir"));

		operacoes.add(new SrOperacao("clock_pause", "Incluir Pendência",
				podeDeixarPendente(pess, lota), "pendencia",
				"modal=true"));

		/*
		 * operacoes.add(new SrOperacao("clock_edit", "Alterar Prazo",
		 * podeAlterarPrazo(lotaTitular, titular), "alterarPrazo",
		 * "modal=true"));
		 */
		operacoes.add(new SrOperacao("cross", "Excluir", "Application.excluir",
				podeExcluir(pess, lota),
				"Deseja realmente excluir esta solicitação?", null, "", ""));

		operacoes.add(new SrOperacao("attach", "Anexar Arquivo",
				podeAnexarArquivo(pess, lota), "anexarArquivo",
				"modal=true"));

		operacoes.add(new SrOperacao("printer", "Termo de Atendimento",
				podeImprimirTermoAtendimento(pess, lota),
				"Application.termoAtendimento", "popup=true"));

		SrMovimentacao ultCancelavel = getUltimaMovimentacaoCancelavel();
		operacoes.add(new SrOperacao("cancel", "Desfazer "
				+ (ultCancelavel != null ? ultCancelavel.tipoMov.nome : ""),
				podeDesfazerMovimentacao(pess, lota),
				"Application.desfazerUltimaMovimentacao"));

		return operacoes;
	}

	public void salvar(DpPessoa cadastrante, DpLotacao lotaCadastrante,
			DpPessoa titular, DpLotacao lotaTitular)
			throws Exception {
		this.setCadastrante(cadastrante);
		this.setLotaCadastrante(lotaCadastrante);
		this.setTitular(titular);
		this.setLotaTitular(lotaTitular);
		salvar();
	}

	@SuppressWarnings("unused")
	public void salvar() throws Exception {

		checarEPreencherCampos();
		//Edson: Ver por que isto est� sendo necess�rio. Sem isso, ap�s o salvar(),
		//ocorre LazyIniException ao tentar acessar esses meuMovimentacaoSet's
		if (getSolicitacaoInicial() != null)
			for (SrSolicitacao s : getSolicitacaoInicial().getMeuSolicitacaoHistoricoSet()) {
				for (SrMovimentacao m : s.getMeuMovimentacaoSet()) {
				}
			}

		super.salvar();

		// Edson: melhorar isto, pra nao precisar salvar novamente

		if (isRascunho()) {
			atualizarCodigo();
			save();
		}

		if (!isRascunho() && !jaFoiDesignada()) {

			if (isFecharAoAbrir())
				fechar(getCadastrante(), getLotaCadastrante(), getTitular(), getLotaTitular(), getMotivoFechamentoAbertura());
			else
				iniciarAtendimento(getCadastrante(), getLotaCadastrante(), getTitular(), getLotaTitular());

			incluirEmListasAutomaticas();

			if (!isEditado()
					&& getFormaAcompanhamento() != SrFormaAcompanhamento.ABERTURA_FECHAMENTO)
				Correio.notificarAbertura(this);
		} else
			atualizarMarcas();
	}

	private void incluirEmListasAutomaticas() throws Exception {
		for (ListaInclusaoAutomatica dadosInclusao : getListasParaInclusaoAutomatica(getLotaCadastrante())) {
			incluirEmLista(dadosInclusao.getLista(), getCadastrante(), getLotaCadastrante(), dadosInclusao.getPrioridadeNaLista(), Boolean.FALSE);
		}
	}

	public void excluir() throws Exception {
		finalizar();
		for (SrMarca e : getMarcaSet()) {
			e.solicitacao.getMeuMarcaSet().remove(e);
			e.delete();
		}
	}

	public void atualizarAcordos() throws Exception {
		setAcordos(new ArrayList<SrAcordo>());

		List<SrConfiguracao> filtrosConf = new ArrayList<SrConfiguracao>();

		SrConfiguracao confSolicitante = new SrConfiguracao();
		confSolicitante.setDpPessoa(getSolicitante());
		confSolicitante.setLotacao(getLotaSolicitante());
		confSolicitante.setComplexo(getLocal());
		confSolicitante.setBuscarPorPerfis(true);
		filtrosConf.add(confSolicitante);

		if (getTitular() != null){
			SrConfiguracao confTitular = new SrConfiguracao();
			confTitular.setDpPessoa(getTitular());
			confTitular.setLotacao(getLotaTitular());
			confTitular.setComplexo(getLocal());
			confTitular.setBuscarPorPerfis(true);
			filtrosConf.add(confTitular);
		}

		for (SrConfiguracao c : filtrosConf) {

			c.setItemConfiguracaoFiltro(getItemConfiguracao());
			c.setAcaoFiltro(getAcao());
			c.setPrioridade(getPrioridade());
			if (getDesignacao() != null)
				c.setAtendente(getDesignacao().getAtendente());
			c.setCpTipoConfiguracao(AR.em().find(CpTipoConfiguracao.class,
					CpTipoConfiguracao.TIPO_CONFIG_SR_ABRANGENCIA_ACORDO));

			List<SrConfiguracao> confs = SrConfiguracao.listar(c);
			for (SrConfiguracao conf : confs) {
				if (conf.getAcordo() != null) {
					SrAcordo acordoAtual = ((SrAcordo) SrAcordo.AR
							.findById(conf.getAcordo().getIdAcordo())).getAcordoAtual();
					if (acordoAtual != null && !getAcordos().contains(acordoAtual))
						getAcordos().add(acordoAtual);
				}
			}
		}
	}

	private void checarEPreencherCampos() throws Exception {

		if (getCadastrante() == null)
			throw new Exception("Cadastrante não pode ser nulo");

		if (getDtReg() == null)
			setDtReg(new Date());

		if (getArquivo() != null) {
			double lenght = (double) getArquivo().blob.length / 1024 / 1024;
			if (lenght > 2)
				throw new IllegalArgumentException("O tamanho do arquivo ("
						+ new DecimalFormat("#.00").format(lenght)
						+ "MB) � maior que o m�ximo permitido (2MB)");
		}

		if (getLotaCadastrante() == null)
			setLotaCadastrante(getCadastrante().getLotacao());

		if (getTitular() == null)
			setTitular(getCadastrante());

		if (getLotaTitular() == null)
			setLotaTitular(getTitular().getLotacao());

		if (getSolicitante() == null)
			setSolicitante(getCadastrante());

		if (getLotaSolicitante() == null)
			setLotaSolicitante(getSolicitante().getLotacao());

		if (getSolicitante().equivale(getCadastrante())) {
			setDtOrigem(null);
			setMeioComunicacao(null);
		}

		if (getOrgaoUsuario() == null)
			setOrgaoUsuario(getLotaSolicitante().getOrgaoUsuario());

		if (getNumSolicitacao() == null)
			// DB1: Verifica se é uma Solicitação Filha, pois caso seja não
			// deve atualizar o número da solicitação, caso contrário não
			// funcionará o filtro por código para essa filha
			if (!isRascunho() && !isFilha()) {
				setNumSolicitacao(getProximoNumero());
				atualizarCodigo();
			}

		if (getGravidade() == null)
			setGravidade(SrGravidade.NORMAL);

		if (getUrgencia() == null)
			setUrgencia(SrUrgencia.NORMAL);

		if (getTendencia() == null)
			setTendencia(SrTendencia.PIORA_MEDIO_PRAZO);

		// só valida o atendente caso não seja rascunho
		if (!isRascunho() && getDesignacao() == null)
			throw new Exception(
					"Não foi encontrado nenhum atendente designado "
							+ "para esta solicitação. Sugestão: alterar item de "
							+ "configuração e/ou ação");

		if (isFilha()) {
			if (getDescrSolicitacao().equals(getSolicitacaoPai().getDescrSolicitacao())
					|| getDescrSolicitacao().trim().isEmpty())
				setDescrSolicitacao(null);

			if (this.meuAtributoSolicitacaoSet != null)
				this.meuAtributoSolicitacaoSet = null;
		}

		atualizarAcordos();
	}

	public void desfazerUltimaMovimentacao(DpPessoa cadastrante,
			DpLotacao lotaCadastrante, DpPessoa titular, DpLotacao lotaTitular) throws Exception {
		if (!podeDesfazerMovimentacao(cadastrante, lotaTitular))
			throw new Exception("Operação não permitida");

		SrMovimentacao movimentacao = getUltimaMovimentacaoCancelavel();

		// tratamento pois pode ter retorno nulo do método
		// getUltimaMovimentacaoCancelave()
		if (movimentacao != null) {

			if (movimentacao.tipoMov != null) {
				// caso seja movimentacao cancelada ou fechada, reinsere nas
				// listas de prioridade
				if (movimentacao.tipoMov.idTipoMov == TIPO_MOVIMENTACAO_CANCELAMENTO_DE_SOLICITACAO
						|| movimentacao.tipoMov.idTipoMov == TIPO_MOVIMENTACAO_FECHAMENTO)
					reInserirListasDePrioridade(cadastrante, lotaCadastrante, titular, lotaTitular);

				if (movimentacao.tipoMov.idTipoMov == TIPO_MOVIMENTACAO_JUNTADA) {
					this.save();
				}
			}

			movimentacao.desfazer(cadastrante, lotaCadastrante, titular, lotaTitular);
		}
	}

	public SrSolicitacao criarFilhaSemSalvar() throws Exception {
		SrSolicitacao filha = new SrSolicitacao();
		Util.copiar(filha, this);
		filha.setIdSolicitacao(null);
		filha.setSolicitacaoPai(this);
		filha.setNumSolicitacao(this.getNumSolicitacao());
		filha.setRascunho(null);
		filha.setSolicitacaoInicial(null);
		filha.setMeuMovimentacaoSet(null);
		filha.setDtIniEdicao(new Date());
		filha.setMeuMovimentacaoReferenciaSet(null);
		for (SrSolicitacao s : getSolicitacaoFilhaSet())
			filha.setNumSequencia(s.getNumSequencia());
		if (filha.getNumSequencia() == null)
			filha.setNumSequencia(1L);
		else
			filha.setNumSequencia(filha.getNumSequencia() + 1);
		filha.atualizarCodigo();
		return filha;
	}

	public void atualizarMarcas() {
		SortedSet<SrMarca> setA = new TreeSet<SrMarca>();

		// Edson: Obtido do sigagc - Excluir marcas duplicadas (???)
		for (SrMarca m : getMarcaSet()) {
			if (setA.contains(m))
				m.delete();
			else
				setA.add(m);
		}
		SortedSet<SrMarca> setB = calcularMarcadores();
		Set<SrMarca> marcasAIncluir = new TreeSet<SrMarca>();
		Set<SrMarca> marcasAExcluir = new TreeSet<SrMarca>();
		Set<Par<SrMarca, SrMarca>> atualizar = new TreeSet<Par<SrMarca, SrMarca>>();
		encaixar(setA, setB, marcasAIncluir, marcasAExcluir, atualizar);

		if (getMeuMarcaSet() == null)
			setMeuMarcaSet(new TreeSet<SrMarca>());
		for (SrMarca i : marcasAIncluir) {
			i.save();
			getMeuMarcaSet().add(i);
		}
		for (SrMarca e : marcasAExcluir) {
			e.solicitacao.getMeuMarcaSet().remove(e);
			e.delete();
		}
		for (Entry<SrMarca, SrMarca> pair : atualizar) {
			SrMarca a = pair.getKey();
			SrMarca b = pair.getValue();
			a.setDpLotacaoIni(b.getDpLotacaoIni());
			a.setDpPessoaIni(b.getDpPessoaIni());
			a.setDtFimMarca(b.getDtFimMarca());
			a.setDtIniMarca(b.getDtIniMarca());
			a.save();
		}
	}

	private SortedSet<SrMarca> calcularMarcadores() {
		SortedSet<SrMarca> set = new TreeSet<SrMarca>();

		if (isRascunho())
			acrescentarMarca(set,
					CpMarcador.MARCADOR_SOLICITACAO_EM_ELABORACAO, null, null,
					getCadastrante(), getLotaTitular());

		Set<SrMovimentacao> movs = getMovimentacaoSetOrdemCrescente();

		if (movs != null && movs.size() > 0) {
			Long marcador = 0L;
			SrMovimentacao movMarca = null;

			List<SrMovimentacao> pendencias = new ArrayList<SrMovimentacao>();

			for (SrMovimentacao mov : movs) {
				Long t = mov.tipoMov.idTipoMov;
				if (mov.isCancelada())
					continue;
				if (t == TIPO_MOVIMENTACAO_INICIO_ATENDIMENTO) {
					marcador = CpMarcador.MARCADOR_SOLICITACAO_EM_ANDAMENTO;
					movMarca = mov;
				}
				if (t == TIPO_MOVIMENTACAO_FECHAMENTO) {
					marcador = CpMarcador.MARCADOR_SOLICITACAO_FECHADO;
					movMarca = mov;
				}
				if (t == TIPO_MOVIMENTACAO_REABERTURA) {
					marcador = CpMarcador.MARCADOR_SOLICITACAO_EM_ANDAMENTO;
					movMarca = mov;
				}
				if (t == TIPO_MOVIMENTACAO_CANCELAMENTO_DE_SOLICITACAO) {
					marcador = CpMarcador.MARCADOR_SOLICITACAO_CANCELADO;
					movMarca = mov;
				}
				if (t == TIPO_MOVIMENTACAO_JUNTADA) {
					marcador = CpMarcador.MARCADOR_JUNTADO;
					movMarca = mov;
				}
				if (t == TIPO_MOVIMENTACAO_DESENTRANHAMENTO) {
					marcador = CpMarcador.MARCADOR_SOLICITACAO_EM_ANDAMENTO;
					movMarca = mov;
				}
				if (t == TIPO_MOVIMENTACAO_INICIO_PENDENCIA) {
					if (mov.getDtFimMov() == null || mov.getDtFimMov().after(new Date()))
						pendencias.add(mov);
				}

				if (t == TIPO_MOVIMENTACAO_ANDAMENTO || t == SrTipoMovimentacao.TIPO_MOVIMENTACAO_ESCALONAMENTO) {
					movMarca = mov;
				}

			}

			if (marcador != 0L)
				acrescentarMarca(set, marcador, movMarca.dtIniMov, null,
						movMarca.atendente, movMarca.lotaAtendente);

			if (marcador == CpMarcador.MARCADOR_SOLICITACAO_FECHADO
					&& isFilha())
				getSolicitacaoPai().atualizarMarcas();

			if (!isFechado() && isAFechar())
				acrescentarMarca(set,
						CpMarcador.MARCADOR_SOLICITACAO_FECHADO_PARCIAL, movMarca.dtIniMov,
						null, movMarca.atendente, movMarca.lotaAtendente);

			if (marcador != 0L)
				acrescentarMarca(set, marcador, movMarca.dtIniMov, null,
						movMarca.atendente, movMarca.lotaAtendente);


			if (pendencias.size() > 0) {
				SrMovimentacao ultimaPendencia = pendencias.get(pendencias.size()-1);
				Date dtFimPendenciaMaisLonge = null;
				for (SrMovimentacao pendencia : pendencias){
					if (pendencia.dtAgenda != null && (dtFimPendenciaMaisLonge == null || pendencia.dtAgenda.after(dtFimPendenciaMaisLonge)))
						dtFimPendenciaMaisLonge = pendencia.dtAgenda;
				}
				if (isRascunho())
					acrescentarMarca(set,
							CpMarcador.MARCADOR_SOLICITACAO_PENDENTE,
							ultimaPendencia.dtIniMov, dtFimPendenciaMaisLonge,
							getCadastrante(), getLotaCadastrante());
				else
					acrescentarMarca(set,
							CpMarcador.MARCADOR_SOLICITACAO_PENDENTE,
							ultimaPendencia.dtIniMov, dtFimPendenciaMaisLonge,
							ultimaPendencia.atendente, ultimaPendencia.lotaAtendente);
			}


			if (!isFechado() && !isCancelado()) {
				acrescentarMarca(set,
						CpMarcador.MARCADOR_SOLICITACAO_COMO_CADASTRANTE, null,
						null, getCadastrante(), getLotaCadastrante());
				acrescentarMarca(set,
						CpMarcador.MARCADOR_SOLICITACAO_COMO_SOLICITANTE, null,
						null, getSolicitante(), getLotaSolicitante());

				Date prazo = getDtPrazoAtendimentoAcordado();
				if (prazo != null)
					acrescentarMarca(set,
							CpMarcador.MARCADOR_SOLICITACAO_FORA_DO_PRAZO,
							prazo, null, movMarca.atendente,
							movMarca.lotaAtendente);
			}
		}

		return set;
	}

	private void encaixar(SortedSet<SrMarca> setA, SortedSet<SrMarca> setB,
			Set<SrMarca> incluir, Set<SrMarca> excluir,
			Set<Par<SrMarca, SrMarca>> atualizar) {
		Iterator<SrMarca> ia = setA.iterator();
		Iterator<SrMarca> ib = setB.iterator();

		SrMarca a = null;
		SrMarca b = null;

		if (ia.hasNext())
			a = ia.next();
		if (ib.hasNext())
			b = ib.next();
		while (a != null || b != null) {
			if ((a == null) || (b != null && a.compareTo(b) > 0)) {
				// Existe em setB, mas nao existe em setA
				incluir.add(b);
				if (ib.hasNext())
					b = ib.next();
				else
					b = null;

			} else if (b == null || (a != null && b.compareTo(a) > 0)) {
				// Existe em setA, mas nao existe em setB
				excluir.add(a);
				if (ia.hasNext())
					a = ia.next();
				else
					a = null;
			} else {

				// O registro existe nos dois
				atualizar.add(new Par<SrMarca, SrMarca>(a, b));
				if (ib.hasNext())
					b = ib.next();
				else
					b = null;
				if (ia.hasNext())
					a = ia.next();
				else
					a = null;
			}
		}
		ib = null;
		ia = null;
	}

	private void acrescentarMarca(SortedSet<SrMarca> set, Long idMarcador,
			Date dtIni, Date dtFim, DpPessoa pess, DpLotacao lota) {
		SrMarca mar = new SrMarca();
		// Edson: nao eh necessario ser this.solicitacaoInicial em vez de this
		// porque este metodo soh eh chamado por atualizarMarcas(), que ja se
		// certifica de chamar este metodo apenas para a solicitacao inicial
		mar.solicitacao = this;
		mar.setCpMarcador((CpMarcador) CpMarcador.findById(idMarcador));
		if (pess != null)
			mar.setDpPessoaIni(pess.getPessoaInicial());
		if (lota != null)
			mar.setDpLotacaoIni(lota.getLotacaoInicial());
		mar.setDtIniMarca(dtIni);
		mar.setDtFimMarca(dtFim);
		set.add(mar);
	}

	public String getMarcadoresEmHtml() {
		return getMarcadoresEmHtml(null, null);
	}

	public String getMarcadoresEmHtml(DpPessoa pess, DpLotacao lota) {
		StringBuilder sb = new StringBuilder();
		List<Long> marcadoresDesconsiderar = Arrays.asList(new Long[] {
				CpMarcador.MARCADOR_SOLICITACAO_COMO_CADASTRANTE,
				CpMarcador.MARCADOR_SOLICITACAO_COMO_SOLICITANTE });

		Set<SrMarca> marcas = getMarcaSetAtivas();

		for (SrMarca mar : marcas) {
			if (marcadoresDesconsiderar.contains(mar.getCpMarcador()
					.getIdMarcador()))
				continue;
			if (sb.length() > 0)
				sb.append(", ");
			sb.append(mar.getCpMarcador().getDescrMarcador());
			sb.append(" (");
			if (mar.getDpPessoaIni() != null) {
				String nome = mar.getDpPessoaIni()
						.getDescricaoIniciaisMaiusculas();
				sb.append(nome.substring(0, nome.indexOf(" ")));
				sb.append(", ");
			}
			if (mar.getDpLotacaoIni() != null) {
				DpLotacao atual = mar.getDpLotacaoIni().getLotacaoAtual();
				if (atual == null)
					atual = mar.getDpLotacaoIni();
				sb.append(atual.getSigla());
			}
			sb.append(")");
		}

		if (sb.length() == 0)
			return null;
		return sb.toString();
	}

	public boolean isMarcada(long marcador) {
		return isMarcada(marcador, null, null);
	}

	public boolean isMarcada(long marcador, DpLotacao lota) {
		return isMarcada(marcador, lota, null);
	}

	public boolean isMarcada(long marcador, DpLotacao lota, DpPessoa pess) {
		for (SrMarca m : getMarcaSet())
			if (m.getCpMarcador().getIdMarcador().equals(marcador)
					&& (lota == null || m.getDpLotacaoIni().equivale(lota))
					&& (pess == null || m.getDpPessoaIni().equivale(pess)))
				return true;
		return false;
	}

	@Override
	public boolean semelhante(Assemelhavel obj, int profundidade) {
		return false;
	}

	public List<ListaInclusaoAutomatica> getListasParaInclusaoAutomatica(DpLotacao lotaTitular) throws Exception {
		SrConfiguracao filtro = new SrConfiguracao();
		filtro.setDpPessoa(getSolicitante());
		filtro.setOrgaoUsuario(getOrgaoUsuario());
		filtro.setLotacao(lotaTitular);
		filtro.setPrioridade(getPrioridade());
		filtro.setItemConfiguracaoFiltro(getItemConfiguracao());
		filtro.setAcaoFiltro(getAcao());
		filtro.setCpTipoConfiguracao(JPA.em().find(CpTipoConfiguracao.class,CpTipoConfiguracao.TIPO_CONFIG_SR_DEFINICAO_INCLUSAO_AUTOMATICA));

		List<ListaInclusaoAutomatica> listaFinal = new ArrayList<ListaInclusaoAutomatica>();
		for (SrConfiguracao conf : SrConfiguracao.listar(filtro, new int[] { SrConfiguracaoBL.ATENDENTE, SrConfiguracaoBL.LISTA_PRIORIDADE })) {
			if (conf.getListaPrioridade() != null) {
				ListaInclusaoAutomatica listaInclusaoAutomatica = new ListaInclusaoAutomatica(conf.getListaPrioridade().getListaAtual(), conf.getPrioridadeNaLista());

				if (!listaFinal.contains(listaInclusaoAutomatica))
					listaFinal.add(listaInclusaoAutomatica);
			}
		}
		return listaFinal;
	}

	public List<DpPessoa> getPessoasAtendentesDisponiveis(){
		List<DpPessoa> listaFinal = new ArrayList<DpPessoa>();
		if (getLotaAtendente() != null){
			DpLotacao atendente = getLotaAtendente().getLotacaoAtual();
			if (atendente == null)
				atendente = getLotaAtendente();
			for (DpPessoa p : atendente.getDpPessoaLotadosSet()){
				if (p.getHisDtFim() == null)
					listaFinal.add(p);
			}
			Collections.sort(listaFinal, new Comparator<DpPessoa>() {
		        @Override
		        public int compare(DpPessoa  o1, DpPessoa o2) {
					if (o1 != null && o2 != null && o1.getId().equals(o2.getId()))
						return 0;
					return o1.getNomePessoa().compareTo(o2.getNomePessoa());
		        }
		    });
		}
		return listaFinal;
	}

	public List<SrLista> getListasDisponiveisParaInclusao(
			DpLotacao lotaTitular, DpPessoa cadastrante) throws Exception {
		List<SrLista> listaFinal = SrLista.getCriadasPelaLotacao(lotaTitular);

		for (SrLista l : SrLista.listar(false)) {
			SrLista atual = l.getListaAtual();
			if (atual.podeIncluir(lotaTitular, cadastrante))
				if(!listaFinal.contains(atual))
					listaFinal.add(atual);
		}
		listaFinal.removeAll(getListasAssociadas());
		Collections.sort(listaFinal, new Comparator<SrLista>() {
			@Override
			public int compare(SrLista l1, SrLista l2) {
				return l1.nomeLista.compareTo(l2.nomeLista);
			}
		});
		return listaFinal;
	}

	public Set<SrLista> getListasAssociadas() {
		Set<SrLista> associadas = new HashSet<SrLista>();
		for (SrMovimentacao mov : getMovimentacaoSetOrdemCrescente())
			if (mov.tipoMov.idTipoMov == TIPO_MOVIMENTACAO_INCLUSAO_LISTA
					|| mov.tipoMov.idTipoMov == TIPO_MOVIMENTACAO_ALTERACAO_PRIORIDADE_LISTA
					&& mov.lista.isAtivo())
				associadas.add(mov.lista);
			else if (mov.tipoMov.idTipoMov == TIPO_MOVIMENTACAO_RETIRADA_DE_LISTA)
				associadas.remove(mov.lista);
		return associadas;
	}

	public Set<SrSolicitacao> getSolicitacoesVinculadas() {
		Set<SrSolicitacao> solVinculadas = new HashSet<SrSolicitacao>();

		// vincula��es partindo desta solicita��o
		for (SrMovimentacao mov : getMovimentacaoSetPorTipo(SrTipoMovimentacao.TIPO_MOVIMENTACAO_VINCULACAO))
			if (mov.tipoMov.idTipoMov == TIPO_MOVIMENTACAO_VINCULACAO)
				if (mov.solicitacaoReferencia != null)
					solVinculadas.add(mov.solicitacaoReferencia);

		// vincula��es partindo de outra solicita��o referenciando esta
		for (SrMovimentacao mov : getMovimentacaoReferenciaSetPorTipo(SrTipoMovimentacao.TIPO_MOVIMENTACAO_VINCULACAO))
			if (this.equals(mov.solicitacaoReferencia))
				solVinculadas.add(mov.solicitacao);
		return solVinculadas;
	}

	public Set<SrSolicitacao> getSolicitacoesJuntadas() {
		Set<SrSolicitacao> solJuntadas = new HashSet<SrSolicitacao>();

		for (SrMovimentacao mov : getMovimentacaoReferenciaSetPorTipo(SrTipoMovimentacao.TIPO_MOVIMENTACAO_JUNTADA))
			if (!mov.isFinalizada() && this.equals(mov.solicitacaoReferencia))
				solJuntadas.add(mov.solicitacao);
		return solJuntadas;
	}

	public boolean isEmLista() {
		return getListasAssociadas().size() > 0;
	}

	public boolean isEmListaPertencenteA(DpLotacao lota) {
		for (SrLista l : getListasAssociadas()) {
			if (l.lotaCadastrante.equivale(lota))
				return true;
		}
		return false;
	}

	public boolean isEmLista(SrLista lista) {
		for (SrLista l : getListasAssociadas())
			if (l.equivale(lista))
				return true;
		return false;
	}

	public long getPrioridadeNaLista(SrLista lista) throws Exception {
		SrPrioridadeSolicitacao prioridadeSolicitacao = lista.getSrPrioridadeSolicitacao(this);
		return prioridadeSolicitacao != null ? prioridadeSolicitacao.numPosicao : -1;
	}

	public void incluirEmLista(SrLista lista, DpPessoa pess, DpLotacao lota, SrPrioridade prioridade, boolean naoReposicionarAutomatico) throws Exception {
		if (lista == null)
			throw new IllegalArgumentException("Lista não informada");

		if (isEmLista(lista))
			throw new IllegalArgumentException("Lista " + lista.nomeLista + " já contém a solicitação " + getCodigo());

		SrMovimentacao mov = new SrMovimentacao();
		mov.cadastrante = pess;
		mov.lotaCadastrante = lota;
		mov.tipoMov = SrTipoMovimentacao.findById(TIPO_MOVIMENTACAO_INCLUSAO_LISTA);
		mov.descrMovimentacao = "Inclusão na lista " + lista.nomeLista;
		mov.lista = lista;
		mov.solicitacao = this;
		mov.salvar();

		lista.incluir(this, prioridade, naoReposicionarAutomatico);
	}

	public void retirarDeLista(SrLista lista, DpPessoa cadastrante, DpLotacao lotaCadastrante,
			DpPessoa titular, DpLotacao lotaTitular)
			throws Exception {
		if (lista == null)
			throw new IllegalArgumentException("Lista não informada");

		SrMovimentacao mov = new SrMovimentacao();
		mov.cadastrante = cadastrante;
		mov.lotaCadastrante = lotaCadastrante;
		mov.tipoMov = SrTipoMovimentacao.findById(TIPO_MOVIMENTACAO_RETIRADA_DE_LISTA);
		mov.descrMovimentacao = "Cancelamento de InclusÃ£o em Lista";
		mov.solicitacao = this;
		mov.lista = lista;
		mov.salvar();
		lista.retirar(this, cadastrante, lotaCadastrante);
	}

	private void iniciarAtendimento(DpPessoa cadastrante, DpLotacao lotaCadastrante,
			DpPessoa titular, DpLotacao lotaTitular)
			throws Exception {
		SrMovimentacao mov = new SrMovimentacao(this);
		mov.tipoMov = SrTipoMovimentacao
				.findById(TIPO_MOVIMENTACAO_INICIO_ATENDIMENTO);
		if (getAtendenteNaoDesignado() == null)
			mov.lotaAtendente = getDesignacao().getAtendente();
		else
			mov.lotaAtendente = getAtendenteNaoDesignado();
		mov.salvar(cadastrante, lotaCadastrante, titular, lotaTitular);
	}

	public void fechar(DpPessoa cadastrante, DpLotacao lotaCadastrante,
			DpPessoa titular, DpLotacao lotaTitular, String motivo)
			throws Exception {

		if(isPai() && !isAFechar())
			throw new Exception("Operação não permitida. Necessário fechar toda solicitação " +
									"filha criada partir dessa que deseja fechar.");

		if ((cadastrante != null) && !podeFechar(cadastrante, lotaTitular))
			throw new Exception("Operação não permitida");

		SrMovimentacao mov = new SrMovimentacao(this);
		mov.tipoMov = SrTipoMovimentacao.findById(TIPO_MOVIMENTACAO_FECHAMENTO);
		mov.descrMovimentacao = motivo;
		mov.salvar(cadastrante, lotaCadastrante, titular, lotaTitular);

		removerDasListasDePrioridade(cadastrante, lotaCadastrante, titular, lotaTitular);

		if (podeFecharPaiAutomatico())
			getSolicitacaoPai().fechar(cadastrante, lotaCadastrante, titular, lotaTitular,
							"Solicitação fechada automaticamente");

		/*if (temPesquisaSatisfacao())
			enviarPesquisa();*/
	}

	public void enviarPesquisa() throws Exception {
		// Implementar
		Correio.pesquisaSatisfacao(this);
	}

	public void responderPesquisa(DpPessoa cadastrante, DpLotacao lotaCadastrante, DpPessoa titular, DpLotacao lotaTitular,
			Map<Long, String> respostaMap) throws Exception {
		if (!podeResponderPesquisa(titular, lotaTitular))
			throw new Exception("Operação não permitida");
		SrMovimentacao movimentacao = new SrMovimentacao(this);
		//movimentacao.pesquisa = this.getPesquisaDesignada();
		movimentacao.descrMovimentacao = "Avaliação realizada.";
		movimentacao.tipoMov = SrTipoMovimentacao
				.findById(TIPO_MOVIMENTACAO_AVALIACAO);
		movimentacao.setRespostaMap(respostaMap);
		movimentacao.salvar(cadastrante, lotaCadastrante, titular, lotaTitular);
	}

	private void removerDasListasDePrioridade(DpPessoa cadastrante, DpLotacao lotaCadastrante,
			DpPessoa titular, DpLotacao lotaTitular)
			throws Exception {
		for (SrLista lista : this.getListasAssociadas()) {
			this.retirarDeLista(lista, cadastrante, lotaCadastrante, titular, lotaTitular);
		}
	}

	public void reabrir(DpPessoa cadastrante, DpLotacao lotaCadastrante, DpPessoa titular, DpLotacao lotaTitular) throws Exception {
		if (!podeReabrir(titular, lotaTitular))
			throw new Exception("Operação não permitida");
		reInserirListasDePrioridade(cadastrante, lotaCadastrante, titular, lotaTitular);

		SrMovimentacao mov = new SrMovimentacao(this);
		mov.tipoMov = SrTipoMovimentacao
				.findById(SrTipoMovimentacao.TIPO_MOVIMENTACAO_REABERTURA);
		mov.lotaAtendente = getUltimoAtendenteEtapaAtendimento();
		mov.salvar(cadastrante, lotaCadastrante, titular, lotaTitular);
	}

	private void reInserirListasDePrioridade(DpPessoa cadastrante, DpLotacao lotaCadastrante, DpPessoa titular, DpLotacao lotaTitular)
			throws Exception {
		for (SrMovimentacao mov : getMovimentacaoSet()) {
			if (mov.tipoMov.idTipoMov == SrTipoMovimentacao.TIPO_MOVIMENTACAO_FECHAMENTO
					|| mov.tipoMov.idTipoMov == SrTipoMovimentacao.TIPO_MOVIMENTACAO_CANCELAMENTO_DE_SOLICITACAO)
				break;

			if (mov.tipoMov.idTipoMov == SrTipoMovimentacao.TIPO_MOVIMENTACAO_RETIRADA_DE_LISTA)
				incluirEmLista(mov.lista, cadastrante, lotaCadastrante, getPrioridade(), false);

		}
	}

	public void deixarPendente(DpPessoa cadastrante, DpLotacao lotaCadastrante, DpPessoa titular, DpLotacao lotaTitular,
			SrTipoMotivoPendencia motivo, String calendario, String horario,
			String detalheMotivo) throws Exception {
		if (!podeDeixarPendente(titular, lotaTitular))
			throw new Exception("Operação não permitida");
		SrMovimentacao movimentacao = new SrMovimentacao(this);

		if (calendario != null && !calendario.equals("")){
			DateTime dateTime = null;
			if (horario != null && !horario.equals("")){
				DateTimeFormatter formatter = DateTimeFormat
						.forPattern("dd/MM/yyyy HH:mm");
				dateTime = new DateTime(formatter.parseDateTime(calendario + " "
						+ horario));
			} else {
				DateTimeFormatter formatter = DateTimeFormat
						.forPattern("dd/MM/yyyy");
				dateTime = new DateTime(formatter.parseDateTime(calendario));
			}
			if (dateTime != null)
				movimentacao.dtAgenda = dateTime.toDate();
		}

		movimentacao.tipoMov = SrTipoMovimentacao
				.findById(SrTipoMovimentacao.TIPO_MOVIMENTACAO_INICIO_PENDENCIA);
		movimentacao.motivoPendencia = motivo;
		movimentacao.descrMovimentacao = motivo.descrTipoMotivoPendencia;
		if (detalheMotivo != null && !detalheMotivo.trim().equals(""))
			movimentacao.descrMovimentacao += " | " + detalheMotivo;
		if (movimentacao.dtAgenda != null)
			movimentacao.descrMovimentacao += " | Fim previsto: " + movimentacao.getDtAgendaDDMMYYHHMM();
		movimentacao.salvar(cadastrante, lotaCadastrante, titular, lotaTitular);
	}

	public void alterarPrazo(DpPessoa cadastrante, DpLotacao lotaCadastrante, DpPessoa titular, DpLotacao lotaTitular, String motivo,
			String calendario, String horario) throws Exception {
		if (!podeAlterarPrazo(titular, lotaTitular))
			throw new Exception("Opera��o n�o permitida");
		SrMovimentacao movimentacao = new SrMovimentacao(this);
		DateTime datetime = new DateTime();
		DateTimeFormatter formatter = DateTimeFormat
				.forPattern("dd/MM/yyyy HH:mm");
		if (!calendario.equals("")) {
			datetime = new DateTime(formatter.parseDateTime(calendario + " "
					+ horario));
			movimentacao.dtAgenda = datetime.toDate();
		}

		movimentacao.tipoMov = SrTipoMovimentacao
				.findById(SrTipoMovimentacao.TIPO_MOVIMENTACAO_ALTERACAO_PRAZO);
		movimentacao.descrMovimentacao = "Prazo alterado para " + calendario + " "
				+ horario + " - " + motivo;
		movimentacao.salvar(cadastrante, lotaCadastrante, titular, lotaTitular);
	}

	public void terminarPendencia(DpPessoa cadastrante, DpLotacao lotaCadastrante, DpPessoa titular, DpLotacao lotaTitular, String descricao, Long idMovimentacao)
			throws Exception {
		if (!podeTerminarPendencia(titular, lotaTitular))
			throw new Exception("Operação não permitida");
		SrMovimentacao movimentacao = new SrMovimentacao(this);
		movimentacao.tipoMov = SrTipoMovimentacao
				.findById(SrTipoMovimentacao.TIPO_MOVIMENTACAO_FIM_PENDENCIA);

		// Edson: eh necessario setar a finalizadora na finalizada antes de
		// salvar() a finalizadora, pq se n�o, ao atualizarMarcas(), vai
		// parecer que a pendencia nao foi finalizada, atrapalhando calculos
		// de prazo
		SrMovimentacao movFinalizada = SrMovimentacao.findById(idMovimentacao);
		movFinalizada.movFinalizadora = movimentacao;

		movimentacao.descrMovimentacao = descricao;
		movimentacao.descrMovimentacao += " | Terminando pendencia iniciada em " + movFinalizada.getDtIniMovDDMMYYHHMM();
		movimentacao = movimentacao.salvar(cadastrante, lotaCadastrante, titular, lotaTitular);
		movFinalizada.save();
	}

	public void cancelar(DpPessoa cadastrante, DpLotacao lotaCadastrante, DpPessoa titular, DpLotacao lotaTitular) throws Exception {
		if (!podeCancelar(titular, lotaTitular))
			throw new Exception("Operação não permitida");
		SrMovimentacao movimentacao = new SrMovimentacao(this);
		movimentacao.tipoMov = SrTipoMovimentacao
				.findById(SrTipoMovimentacao.TIPO_MOVIMENTACAO_CANCELAMENTO_DE_SOLICITACAO);
		movimentacao.salvar(cadastrante, lotaCadastrante, titular, lotaTitular);

		// remove das listas apos finalizar, para que seja possivel reabrir
		// depois
		removerDasListasDePrioridade(cadastrante, lotaCadastrante, titular, lotaTitular);
	}

	public void juntar(DpPessoa cadastrante, DpLotacao lotaCadastrante, DpPessoa titular, DpLotacao lotaTitular,
			SrSolicitacao solRecebeJuntada, String justificativa)
			throws Exception {
		if ((cadastrante != null) && !podeJuntar(titular, lotaTitular))
			throw new Exception("Operação não permitida");
		if (solRecebeJuntada.equivale(this))
			throw new Exception(
					"Não e possivel juntar uma solicitação a si mesma.");
		if (solRecebeJuntada.isJuntada()
				&& solRecebeJuntada.getSolicitacaoPrincipal().equivale(this))
			throw new Exception("Não e possivel realizar juntada circular.");
		if (solRecebeJuntada.isFilha()
				&& solRecebeJuntada.getSolicitacaoPai().equivale(this))
			throw new Exception(
					"Não e possivel juntar uma solicitação a uma das suas filhas. Favor realizar o processo inverso.");

		SrMovimentacao movimentacao = new SrMovimentacao(this);
		movimentacao.tipoMov = SrTipoMovimentacao
				.findById(TIPO_MOVIMENTACAO_JUNTADA);
		movimentacao.solicitacaoReferencia = solRecebeJuntada;
		movimentacao.descrMovimentacao = justificativa + " | Juntando a " + solRecebeJuntada.codigo;
		movimentacao.salvar(cadastrante, lotaCadastrante, titular, lotaTitular);
	}

	public void desentranhar(DpPessoa cadastrante, DpLotacao lotaCadastrante, DpPessoa titular, DpLotacao lotaTitular, String justificativa)
			throws Exception {
		if (!podeDesentranhar(titular, lotaTitular))
			throw new Exception("Operação não permitida");
		SrMovimentacao movimentacao = new SrMovimentacao(this);
		movimentacao.tipoMov = SrTipoMovimentacao
				.findById(SrTipoMovimentacao.TIPO_MOVIMENTACAO_DESENTRANHAMENTO);
		movimentacao.descrMovimentacao = justificativa;
		movimentacao = movimentacao.salvar(cadastrante, lotaCadastrante, titular, lotaTitular);

		SrMovimentacao juntada = getUltimaMovimentacaoPorTipo(SrTipoMovimentacao.TIPO_MOVIMENTACAO_JUNTADA);
		juntada.movFinalizadora = movimentacao;
		juntada.save();

	}

	public void vincular(DpPessoa cadastrante, DpLotacao lotaCadastrante,
			DpPessoa titular, DpLotacao lotaTitular,
			SrSolicitacao solRecebeVinculo, String justificativa)
			throws Exception {
		if ((cadastrante != null) && !podeVincular(titular, lotaTitular))
			throw new Exception("Operação não permitida");
		if (solRecebeVinculo.equivale(this))
	        throw new Exception("Não e possivel vincular uma solicita�ao a si mesma.");
		SrMovimentacao movimentacao = new SrMovimentacao(this);
		movimentacao.tipoMov = SrTipoMovimentacao
				.findById(TIPO_MOVIMENTACAO_VINCULACAO);
		movimentacao.solicitacaoReferencia = solRecebeVinculo;
		movimentacao.descrMovimentacao = justificativa + " | Vinculando a " + solRecebeVinculo.codigo;
		movimentacao.salvar(cadastrante, lotaCadastrante, titular, lotaTitular);
	}

	public String getGcTags() {
		String s = "tags=@servico";
		if (getAcao() != null)
			s += getAcao().getGcTags();
		if (getItemConfiguracao() != null)
			s += getItemConfiguracao().getGcTags();
		return s;
	}

	public String getGcTagAbertura() {
		String s = "^sr:";
		if (getAcao() != null)
			s += Texto.slugify(getAcao().getTituloAcao(), true, false);
		if (getItemConfiguracao() != null)
			s += "-"
					+ Texto.slugify(getItemConfiguracao().getTituloItemConfiguracao(),
							true, false);
		return s;
	}

	public String getGcTituloAbertura() {
		String s = "";
		if (getAcao() != null)
			s += getAcao().getTituloAcao();
		if (getItemConfiguracao() != null)
			s += " - " + getItemConfiguracao().getTituloItemConfiguracao();
		return s;
	}

	public String getDtOrigemDDMMYYYYHHMM() {
		if (getDtOrigem() != null) {
			final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			return df.format(getDtOrigem());
		}
		return "";
	}

	public String getDtOrigemHHMM() {
		if (getDtOrigem() != null) {
			final SimpleDateFormat df = new SimpleDateFormat("HH:mm");
			return df.format(getDtOrigem());
		}
		return "";
	}

	public String getDtOrigemDDMMYYYY() {
		if (getDtOrigem() != null) {
			final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			return df.format(getDtOrigem());
		}
		return "";
	}

	public String getDtOrigemString() {
		if (getDtOrigem() != null) {
			SigaPlayCalendar cal = new SigaPlayCalendar();
			cal.setTime(getDtOrigem());
			return cal.getTempoTranscorridoString(false);
		}
		return "";
	}

	public void setDtOrigemString(String stringDtMeioContato) {
		DateTimeFormatter formatter = forPattern("dd/MM/yyyy HH:mm");
		if (stringDtMeioContato != null && !stringDtMeioContato.isEmpty()
				&& stringDtMeioContato.contains("/")
				&& stringDtMeioContato.contains(":"))
			this.setDtOrigem(new DateTime(
					formatter.parseDateTime(stringDtMeioContato)).toDate());
	}

	public String getDtIniEdicaoDDMMYYYYHHMMSS() {
		if (getDtIniEdicao() != null) {
			final SimpleDateFormat df = new SimpleDateFormat(
					"dd/MM/yyyy HH:mm:ss");
			return df.format(getDtIniEdicao());
		}
		return "";
	}

	public void setDtIniEdicaoDDMMYYYYHHMMSS(String string) {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		try {
			this.setDtIniEdicao(df.parse(string));
		} catch (Exception e) {

		}
	}

	public Date getDtInicioPrimeiraEdicao() {
		if (getSolicitacaoInicial() != null)
			return getSolicitacaoInicial().getDtIniEdicao();
		else
			return this.getDtIniEdicao();
	}

	public Date getDtInicioAtendimento() {
		for (SrMovimentacao mov : getMovimentacaoSetOrdemCrescente())
			if (mov.tipoMov.idTipoMov == SrTipoMovimentacao.TIPO_MOVIMENTACAO_INICIO_PRE_ATENDIMENTO
					|| mov.tipoMov.idTipoMov == SrTipoMovimentacao.TIPO_MOVIMENTACAO_INICIO_ATENDIMENTO)
				return mov.dtIniMov;
		return null;
	}

	public Date getDtEfetivoFechamento() {
		SrMovimentacao fechamento = getUltimaMovimentacaoPorTipo(SrTipoMovimentacao.TIPO_MOVIMENTACAO_FECHAMENTO);
		if (fechamento == null)
			return null;
		return fechamento.dtIniMov;
	}

	public Date getDtCancelamento() {
		SrMovimentacao cancelamento = getUltimaMovimentacaoPorTipo(SrTipoMovimentacao.TIPO_MOVIMENTACAO_CANCELAMENTO_DE_SOLICITACAO);
		if (cancelamento == null)
			return null;
		return cancelamento.dtIniMov;
	}

	// Edson: retorna os periodos de pendencia de forma linear, ou seja,
	// colapsando as sobreposicoes, para facilitar os calculos:
	// Transforma: -------
	//                -------
	//
	// em: ----------
	private Map<Date, Date> getTrechosPendentes() {
		Map<Date, Date> pendencias = new LinkedHashMap<Date, Date>();
		Date dtIniEmAberto = null, dtFimEmAberto = null;

		for (SrMovimentacao mov : getMovimentacaoSetOrdemCrescente()) {

			if (dtIniEmAberto != null && dtFimEmAberto != null
					&& mov.dtIniMov.after(dtFimEmAberto)) {
				dtIniEmAberto = null;
				dtFimEmAberto = null;
			}

			if (mov.tipoMov.idTipoMov == SrTipoMovimentacao.TIPO_MOVIMENTACAO_INICIO_PENDENCIA) {
				if (dtIniEmAberto == null)
					dtIniEmAberto = mov.dtIniMov;
				if (dtFimEmAberto == null
						|| (mov.getDtFimMov() != null && mov.getDtFimMov()
								.after(dtFimEmAberto)))
					dtFimEmAberto = mov.getDtFimMov();
				pendencias.put(dtIniEmAberto, dtFimEmAberto);
				if (dtFimEmAberto == null)
					return pendencias;
			}
		}
		return pendencias;
	}

	public Date getDataAPartirDe(Date dtBase, Long segundosAdiante){
		Map<Date, Date> pendencias = getTrechosPendentes();
		for (Date dtIniPendencia : pendencias.keySet()) {
			if (dtIniPendencia.before(dtBase))
				continue;
			Date dtFimPendencia = pendencias.get(dtIniPendencia);
			if (dtFimPendencia == null)
				return null;
			int delta = (int) (dtIniPendencia.getTime() - dtBase.getTime()) / 1000;
			if (delta <= segundosAdiante) {
				segundosAdiante -= delta;
				dtBase = dtFimPendencia;
			} else
				break;
		}
		return new Date(dtBase.getTime() + segundosAdiante * 1000);
	}

	private Date getDtPrazoCadastramentoAcordado() {
		if (getAcordos() == null || getAcordos().size() == 0 || isCancelado())
			return null;
		Long menorTempoAcordado = null;
		for (SrAcordo a : getAcordos()){
			Long acordado = a.getAcordoAtual().getAtributoEmSegundos("tempoDecorridoCadastramento");
			if (menorTempoAcordado == null || (acordado != null && acordado < menorTempoAcordado))
				menorTempoAcordado = acordado;
		}
		if (menorTempoAcordado == null)
			return null;
		return getDataAPartirDe(getDtInicioPrimeiraEdicao(), menorTempoAcordado);
	}

	private Date getDtPrazoAtendimentoAcordado() {
		if (getAcordos() == null || getAcordos().size() == 0 || isCancelado())
			return null;
		Long menorTempoAcordado = null;
		for (SrAcordo a : getAcordos()){
			Long acordado = a.getAcordoAtual().getAtributoEmSegundos("tempoDecorridoAtendimento");
			if (menorTempoAcordado == null || (acordado != null && acordado < menorTempoAcordado))
				menorTempoAcordado = acordado;
		}
		if (menorTempoAcordado == null)
			return null;
		return getDataAPartirDe(getDtInicioAtendimento(), menorTempoAcordado);
	}

	public Cronometro getCronometro() throws Exception {
		if (getCron() == null) {
			setCron(new Cronometro());
			boolean fechado = isFechado(), cancelado = isCancelado(),
					pendente = isPendente();
			if (jaFoiDesignada()) {
				getCron().setDescricao("Atendimento");
				getCron().setInicio(getDtInicioAtendimento());
				getCron().setFim(fechado ? getDtEfetivoFechamento()
						: cancelado ? getDtCancelamento()
								: getDtPrazoAtendimentoAcordado());
				if (getCron().getFim() == null || fechado || cancelado)
					getCron().setDecorrido(getTempoDecorridoAtendimento().getValor()*1000);
				else
					getCron().setRestante(getCron().getFim().getTime() - new Date()
							.getTime());
			} else {
				getCron().setDescricao("Cadastro");
				getCron().setInicio(getDtInicioPrimeiraEdicao());
				getCron().setFim(getDtPrazoCadastramentoAcordado());
				if (getCron().getFim() == null || fechado || cancelado)
					getCron().setDecorrido(getTempoDecorridoCadastramento()
							.getValor()*1000);
				else
					getCron().setRestante(getCron().getFim().getTime() - new Date()
							.getTime());
			}
			getCron().setLigado(!fechado && !cancelado
					&& (getCron().getFim() != null || !pendente));
		}

		return getCron();
	}

	// Edson: retorna o tempo decorrido entre duas datas, descontando
	// os per�odos de pend�ncia (blocos).
	// PPP, abaixo, � o bloco pendente. I � dtIni e F � dtFim
	public SrValor getTempoDecorrido(Date dtIni, Date dtFim) {
		Map<Date, Date> pendencias = getTrechosPendentes();
		Long decorrido = 0L;
		for (Date dtIniBlocoPendencia : pendencias.keySet()) {
			Date dtFimBlocoPendencia = pendencias.get(dtIniBlocoPendencia);

			// ----------I----------F---PPPP---
			if (dtIniBlocoPendencia.after(dtFim))
				break;

			// ---PPPP---I----------F----------
			if (dtFimBlocoPendencia != null && dtFimBlocoPendencia.before(dtIni))
				continue;

			// ----------I---PPPP---F----------
			if (dtIniBlocoPendencia.after(dtIni))
				decorrido += (int) ((dtIniBlocoPendencia.getTime() - dtIni
						.getTime()) / 1000);

			dtIni = dtFimBlocoPendencia;

			// ----------I---------PPFP--------- ou
			// ----------I---------PPFPPPPPPPPPP...
			if (dtFimBlocoPendencia == null || dtFimBlocoPendencia.after(dtFim))
				return new SrValor(decorrido, CpUnidadeMedida.SEGUNDO);
		}
		decorrido += (int) ((dtFim.getTime() - dtIni.getTime()) / 1000);
		return new SrValor(decorrido, CpUnidadeMedida.SEGUNDO);
	}

	public SrValor getTempoDecorridoCadastramento(){
		Date dtFimCadastro = isFechado() ? getDtEfetivoFechamento()
				: isCancelado() ? getDtCancelamento() : getDtInicioAtendimento();
		if (dtFimCadastro == null)
			dtFimCadastro = new Date();
		return getTempoDecorrido(getDtInicioPrimeiraEdicao(), dtFimCadastro);
	}

	public SrValor getTempoDecorridoAtendimento() {
		Date dtFechamento = isFechado() ? getDtEfetivoFechamento()
				: isCancelado() ? getDtCancelamento() : new Date();
		return getTempoDecorrido(getDtInicioAtendimento(), dtFechamento);
	}

	//Edson: implementar no futuro
	public Long getResultadoPesquisaSatisfacao(){
		return 0L;
	}

	public boolean isAcordosSatisfeitos() {
		if (getAcordos() == null || getAcordos().size() == 0)
			return true;
		for (SrAcordo a : getAcordos()) {
			if (!isAcordoSatisfeito(a))
				return false;
		}
		return true;
	}

	public boolean isAcordoSatisfeito(SrAcordo acordo) {
		if (acordo == null)
			return true;
		acordo = acordo.getAcordoAtual();
		if (acordo.getAtributoAcordoSet() == null)
			return true;
		for (SrAtributoAcordo pa : acordo.getAtributoAcordoSet()) {
			if (!isAtributoAcordoSatisfeito(pa))
				return false;
		}
		return true;
	}

	public boolean isAtributoAcordoSatisfeito(SrAtributoAcordo atributoAcordo) {
		try {
			SrValor valor = (SrValor) SrSolicitacao.class.getMethod(
					atributoAcordo.getAtributo().asGetter()).invoke(this);
			if (valor == null)
				return true;
			return atributoAcordo.isNaFaixa(valor);
		} catch (NoSuchMethodException nsme) {
			return false;
		} catch (InvocationTargetException ite) {
			return false;
		} catch (IllegalAccessException iae) {
			return false;
		}
	}

	@Override
	public boolean equivale(Object other) {
		try {
			SrSolicitacao outra = (SrSolicitacao) other;
			return outra.getHisIdIni().equals(this.getHisIdIni());
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * No caso de solicitacoes filhas, deve ser considerado o solicitante e o
	 * cadastrante para fins de exibicao de itens de configuracao e acoes
	 * disponiveis, alem do atendente designado da solicitacao.
	 */
	private List<SrConfiguracao> getFiltrosParaConsultarDesignacoes() {

		List<SrConfiguracao> pessoasAConsiderar = new ArrayList<SrConfiguracao>();

		if (getTitular() == null)
			return pessoasAConsiderar;

		SrConfiguracao confSolicitante = new SrConfiguracao();
		confSolicitante.setDpPessoa(getSolicitante());
		confSolicitante.setLotacao(getLotaSolicitante());
		confSolicitante.setComplexo(getLocal());
		confSolicitante.setBuscarPorPerfis(true);
		pessoasAConsiderar.add(confSolicitante);

		if (jaFoiDesignada()){
			SrConfiguracao confTitular = new SrConfiguracao();
			confTitular.setDpPessoa(getTitular());
			confTitular.setLotacao(getLotaTitular());
			confTitular.setComplexo(getLocal());
			confTitular.setBuscarPorPerfis(true);
			pessoasAConsiderar.add(confTitular);
		}

		return pessoasAConsiderar;

	}

	public List<SrItemConfiguracao> getHistoricoItem() {
		List<SrItemConfiguracao> historicoItens = listarHistoricoItemInicial();
		SrItemConfiguracao anterior = getItemConfiguracao();

		for (SrMovimentacao movimentacao : getMovimentacaoSetOrdemCrescentePorTipo(SrTipoMovimentacao.TIPO_MOVIMENTACAO_ESCALONAMENTO)) {
			if (movimentacao.getItemConfiguracao() != null && anterior != null && !movimentacao.getItemConfiguracao().equivale(anterior)) {
				historicoItens.add(movimentacao.getItemConfiguracao());
				anterior = movimentacao.getItemConfiguracao();
			}
		}
		return historicoItens;
	}

	public List<SrAcao> getHistoricoAcao() {
		List<SrAcao> historicoAcoes = listaHistoricoAcaoInicial();
		SrAcao acaoAnterior = getAcao();
		for (SrMovimentacao movimentacao : getMovimentacaoSetOrdemCrescentePorTipo(SrTipoMovimentacao.TIPO_MOVIMENTACAO_ESCALONAMENTO)) {
			if (movimentacao.getAcao() != null && acaoAnterior != null && !movimentacao.getAcao().equivale(acaoAnterior)) {
				historicoAcoes.add(movimentacao.getAcao());
				acaoAnterior = movimentacao.getAcao();
			}
		}
		return historicoAcoes;
	}

	private Set<SrMovimentacao> getMovimentacaoSetOrdemCrescentePorTipo(
			Long idTipoMovimentacao) {
		return getMovimentacaoSet(false, null, true, false, false, false,
				Arrays.asList(idTipoMovimentacao));
	}

	private List<SrAcao> listaHistoricoAcaoInicial() {
		List<SrAcao> acoes = new ArrayList<SrAcao>();
		if (getAcao() != null) {
			acoes.add(getAcao());
		}
		return acoes;
	}

	private List<SrItemConfiguracao> listarHistoricoItemInicial() {
		List<SrItemConfiguracao> itensConfiguracao = new ArrayList<SrItemConfiguracao>();
		if (getItemConfiguracao() != null) {
			itensConfiguracao.add(getItemConfiguracao());
		}
		return itensConfiguracao;
	}

	public SrItemConfiguracao getItemAtual() {
		List<SrItemConfiguracao> historicoItem = getHistoricoItem();
		if (historicoItem.isEmpty()) {
			return null;
		}
		int size = historicoItem.size();
		return historicoItem.get(size - 1);
	}

	public SrAcao getAcaoAtual() {
		List<SrAcao> historicoAcao = getHistoricoAcao();
		if (historicoAcao.isEmpty()) {
			return null;
		}
		int size = historicoAcao.size();
		return historicoAcao.get(size - 1);
	}

	public Long getIdSolicitacao() {
		return idSolicitacao;
	}

	public void setIdSolicitacao(Long idSolicitacao) {
		this.idSolicitacao = idSolicitacao;
	}

	public DpPessoa getSolicitante() {
		return solicitante;
	}

	public void setSolicitante(DpPessoa solicitante) {
		this.solicitante = solicitante;
	}

	public DpPessoa getInterlocutor() {
		return interlocutor;
	}

	public void setInterlocutor(DpPessoa interlocutor) {
		this.interlocutor = interlocutor;
	}

	public DpLotacao getLotaSolicitante() {
		return lotaSolicitante;
	}

	public void setLotaSolicitante(DpLotacao lotaSolicitante) {
		this.lotaSolicitante = lotaSolicitante;
	}

	public DpPessoa getCadastrante() {
		return cadastrante;
	}

	public void setCadastrante(DpPessoa cadastrante) {
		this.cadastrante = cadastrante;
	}

	public DpLotacao getLotaCadastrante() {
		return lotaCadastrante;
	}

	public void setLotaCadastrante(DpLotacao lotaCadastrante) {
		this.lotaCadastrante = lotaCadastrante;
	}

	public DpPessoa getTitular() {
		return titular;
	}

	public void setTitular(DpPessoa titular) {
		this.titular = titular;
	}

	public DpLotacao getLotaTitular() {
		return lotaTitular;
	}

	public void setLotaTitular(DpLotacao lotaTitular) {
		this.lotaTitular = lotaTitular;
	}

	public SrConfiguracao getDesignacao() {
		return designacao;
	}

	public void setDesignacao(SrConfiguracao designacao) {
		this.designacao = designacao;
	}

	public DpLotacao getAtendenteNaoDesignado() {
		return atendenteNaoDesignado;
	}

	public void setAtendenteNaoDesignado(DpLotacao atendenteNaoDesignado) {
		this.atendenteNaoDesignado = atendenteNaoDesignado;
	}

	public Cronometro getCron() {
		return cron;
	}

	public void setCron(Cronometro cron) {
		this.cron = cron;
	}

	public CpOrgaoUsuario getOrgaoUsuario() {
		return orgaoUsuario;
	}

	public void setOrgaoUsuario(CpOrgaoUsuario orgaoUsuario) {
		this.orgaoUsuario = orgaoUsuario;
	}

	public SrSolicitacao getSolicitacaoPai() {
		return solicitacaoPai;
	}

	public void setSolicitacaoPai(SrSolicitacao solicitacaoPai) {
		this.solicitacaoPai = solicitacaoPai;
	}

	public List<SrAcordo> getAcordos() {
		return acordos;
	}

	public void setAcordos(List<SrAcordo> acordos) {
		this.acordos = acordos;
	}

	public SrFormaAcompanhamento getFormaAcompanhamento() {
		return formaAcompanhamento;
	}

	public void setFormaAcompanhamento(SrFormaAcompanhamento formaAcompanhamento) {
		this.formaAcompanhamento = formaAcompanhamento;
	}

	public SrMeioComunicacao getMeioComunicacao() {
		return meioComunicacao;
	}

	public void setMeioComunicacao(SrMeioComunicacao meioComunicacao) {
		this.meioComunicacao = meioComunicacao;
	}

	public SrItemConfiguracao getItemConfiguracao() {
		return itemConfiguracao;
	}

	public void setItemConfiguracao(SrItemConfiguracao itemConfiguracao) {
		this.itemConfiguracao = itemConfiguracao;
	}

	public SrArquivo getArquivo() {
		return arquivo;
	}

	public void setArquivo(SrArquivo arquivo) {
		this.arquivo = arquivo;
	}

	public SrAcao getAcao() {
		return acao;
	}

	public void setAcao(SrAcao acao) {
		this.acao = acao;
	}

	public String getDescrSolicitacao() {
		return descrSolicitacao;
	}

	public void setDescrSolicitacao(String descrSolicitacao) {
		this.descrSolicitacao = descrSolicitacao;
	}

	public SrTendencia getTendencia() {
		return tendencia;
	}

	public void setTendencia(SrTendencia tendencia) {
		this.tendencia = tendencia;
	}

	public SrGravidade getGravidade() {
		return gravidade;
	}

	public void setGravidade(SrGravidade gravidade) {
		this.gravidade = gravidade;
	}

	public SrUrgencia getUrgencia() {
		return urgencia;
	}

	public void setUrgencia(SrUrgencia urgencia) {
		this.urgencia = urgencia;
	}

	public SrPrioridade getPrioridade() {
		return prioridade;
	}

	public void setPrioridade(SrPrioridade prioridade) {
		this.prioridade = prioridade;
	}

	public Date getDtReg() {
		return dtReg;
	}

	public void setDtReg(Date dtReg) {
		this.dtReg = dtReg;
	}

	public Date getDtIniEdicao() {
		return dtIniEdicao;
	}

	public void setDtIniEdicao(Date dtIniEdicao) {
		this.dtIniEdicao = dtIniEdicao;
	}

	public Date getDtOrigem() {
		return dtOrigem;
	}

	public void setDtOrigem(Date dtOrigem) {
		this.dtOrigem = dtOrigem;
	}

	public CpComplexo getLocal() {
		return local;
	}

	public void setLocal(CpComplexo local) {
		this.local = local;
	}

	public String getTelPrincipal() {
		return telPrincipal;
	}

	public void setTelPrincipal(String telPrincipal) {
		this.telPrincipal = telPrincipal;
	}

	public boolean isFecharAoAbrir() {
		return fecharAoAbrir;
	}

	public void setFecharAoAbrir(boolean fecharAoAbrir) {
		this.fecharAoAbrir = fecharAoAbrir;
	}

	public String getMotivoFechamentoAbertura() {
		return motivoFechamentoAbertura;
	}

	public void setMotivoFechamentoAbertura(String motivoFechamentoAbertura) {
		this.motivoFechamentoAbertura = motivoFechamentoAbertura;
	}

	public Long getNumSolicitacao() {
		return numSolicitacao;
	}

	public void setNumSolicitacao(Long numSolicitacao) {
		this.numSolicitacao = numSolicitacao;
	}

	public Long getNumSequencia() {
		return numSequencia;
	}

	public void setNumSequencia(Long numSequencia) {
		this.numSequencia = numSequencia;
	}

	public SrSolicitacao getSolicitacaoInicial() {
		return solicitacaoInicial;
	}

	public void setSolicitacaoInicial(SrSolicitacao solicitacaoInicial) {
		this.solicitacaoInicial = solicitacaoInicial;
	}

	public List<SrSolicitacao> getMeuSolicitacaoHistoricoSet() {
		return meuSolicitacaoHistoricoSet;
	}

	public void setMeuSolicitacaoHistoricoSet(List<SrSolicitacao> meuSolicitacaoHistoricoSet) {
		this.meuSolicitacaoHistoricoSet = meuSolicitacaoHistoricoSet;
	}

	public Set<SrMovimentacao> getMeuMovimentacaoSet() {
		return meuMovimentacaoSet;
	}

	public void setMeuMovimentacaoSet(Set<SrMovimentacao> meuMovimentacaoSet) {
		this.meuMovimentacaoSet = meuMovimentacaoSet;
	}

	public Set<SrSolicitacao> getMeuSolicitacaoFilhaSet() {
		return meuSolicitacaoFilhaSet;
	}

	public void setMeuSolicitacaoFilhaSet(Set<SrSolicitacao> meuSolicitacaoFilhaSet) {
		this.meuSolicitacaoFilhaSet = meuSolicitacaoFilhaSet;
	}

	public Set<SrMovimentacao> getMeuMovimentacaoReferenciaSet() {
		return meuMovimentacaoReferenciaSet;
	}

	public void setMeuMovimentacaoReferenciaSet(Set<SrMovimentacao> meuMovimentacaoReferenciaSet) {
		this.meuMovimentacaoReferenciaSet = meuMovimentacaoReferenciaSet;
	}

	public Set<SrMarca> getMeuMarcaSet() {
		return meuMarcaSet;
	}

	public void setMeuMarcaSet(Set<SrMarca> meuMarcaSet) {
		this.meuMarcaSet = meuMarcaSet;
	}

	public Boolean getRascunho() {
		return rascunho;
	}

	public void setRascunho(Boolean rascunho) {
		this.rascunho = rascunho;
	}
}
