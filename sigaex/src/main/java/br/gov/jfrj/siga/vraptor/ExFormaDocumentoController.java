package br.gov.jfrj.siga.vraptor;

import java.util.HashSet;
import java.util.List;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.view.Results;
import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.ex.ExFormaDocumento;
import br.gov.jfrj.siga.ex.ExTipoDocumento;
import br.gov.jfrj.siga.ex.ExTipoFormaDoc;
import br.gov.jfrj.siga.ex.bl.Ex;
import br.gov.jfrj.siga.hibernate.ExDao;

@Resource
public class ExFormaDocumentoController extends ExController {

	public ExFormaDocumentoController(HttpServletRequest request, HttpServletResponse response, ServletContext context, Result result, SigaObjects so,
			EntityManager em) {
		super(request, response, context, result, ExDao.getInstance(), so, em);

		result.on(AplicacaoException.class).forwardTo(this).appexception();
		result.on(Exception.class).forwardTo(this).exception();
	}

	@Get("app/forma/listar")
	public void listarFormas(final String ordenar) {
		assertAcesso("MOD:Gerenciar modelos");

		final List<ExFormaDocumento> itens;

		if (ordenar != null && "sigla".equals(ordenar)) {
			itens = dao().listarTodosOrdenarPorSigla();
		} else {
			itens = dao().listarTodosOrdenarPorDescricao();
		}

		result.include("itens", itens);
	}

	@Get("app/forma/editar")
	public void editarForma(final Integer id) {
		assertAcesso("MOD:Gerenciar modelos");

		final ExFormaDocumento forma = id != null ? recuperarForma(id) : new ExFormaDocumento();

		if (getPostback() == null) {
			result.include("descricao", forma.getDescrFormaDoc());
			result.include("sigla", forma.getSigla());
			if (forma.getExTipoFormaDoc() != null) {
				result.include("idTipoFormaDoc", forma.getExTipoFormaDoc().getIdTipoFormaDoc());
			}

			boolean origemExterno = false;
			boolean origemInternoImportado = false;
			boolean origemInternoProduzido = false;

			if (forma.getExTipoDocumentoSet() != null) {
				for (ExTipoDocumento origem : forma.getExTipoDocumentoSet()) {
					if (origem.getId() == ExTipoDocumento.TIPO_DOCUMENTO_EXTERNO) {
						origemExterno = true;
					}
					if (origem.getId() == ExTipoDocumento.TIPO_DOCUMENTO_INTERNO) {
						origemInternoProduzido = true;
					}
					if (origem.getId() == ExTipoDocumento.TIPO_DOCUMENTO_INTERNO_ANTIGO) {
						origemInternoImportado = true;
					}
				}
			}

			result.include("origemExterno", origemExterno);
			result.include("origemInternoImportado", origemInternoImportado);
			result.include("origemInternoProduzido", origemInternoProduzido);
		}

		result.include("id", id);
		result.include("listaTiposFormaDoc", dao().listarExTiposFormaDoc());
	}

	@Get("app/forma/verificar_sigla")
	public void aVerificarSigla(final Integer id, final String sigla) {
		ExFormaDocumento formaConsulta = new ExFormaDocumento();

		if (sigla != null && !sigla.isEmpty()) {

			if (!formaConsulta.isSiglaValida(sigla)) {
				setMensagem("Sigla invalida. A sigla deve ser formada por 3 letras.");
			}

			formaConsulta.setSigla(sigla);
			formaConsulta = dao().consultarPorSigla(formaConsulta);

			if (formaConsulta != null) {
				setMensagem("Esta sigla ja esta sendo utilizada");
			}
		}
		
		result.use(Results.page()).forwardTo("/WEB-INF/page/mensagemAjax.jsp");
	}

	@Post("app/forma/gravar")
	public void gravar(final Integer postback, final Integer id, final String descricao, final String sigla, final Long idTipoFormaDoc, final boolean origemExterno,
			final boolean origemInternoImportado, final boolean origemInternoProduzido) {
		assertAcesso("MOD:Gerenciar modelos");
		setPostback(postback);

		final ExFormaDocumento forma = id != null ? recuperarForma(id) : new ExFormaDocumento();

		forma.setDescrFormaDoc(descricao);
		forma.setSigla(sigla);
		forma.setExTipoFormaDoc(dao().consultar(idTipoFormaDoc, ExTipoFormaDoc.class, false));

		if (forma.getExTipoDocumentoSet() == null) {
			forma.setExTipoDocumentoSet(new HashSet<ExTipoDocumento>());
		} else {
			forma.getExTipoDocumentoSet().clear();
		}
		if (origemInternoProduzido) {
			forma.getExTipoDocumentoSet().add(dao().consultar(ExTipoDocumento.TIPO_DOCUMENTO_INTERNO, ExTipoDocumento.class, false));
		}
		if (origemInternoImportado) {
			forma.getExTipoDocumentoSet().add(dao().consultar(ExTipoDocumento.TIPO_DOCUMENTO_INTERNO_ANTIGO, ExTipoDocumento.class, false));
		}
		if (origemExterno) {
			forma.getExTipoDocumentoSet().add(dao().consultar(ExTipoDocumento.TIPO_DOCUMENTO_EXTERNO, ExTipoDocumento.class, false));
		}

		Ex.getInstance().getBL().gravarForma(forma);

		result.include("id", forma.getIdFormaDoc());
		result.include("descricao", descricao);
		result.include("sigla", sigla);
		result.include("idTipoFormaDoc", idTipoFormaDoc);
		result.include("origemExterno", origemExterno);
		result.include("origemInternoImportado", origemInternoImportado);
		result.include("origemInternoProduzido", origemInternoProduzido);
		
		result.redirectTo("/app/forma/editar?id=" + forma.getIdFormaDoc());
	}

	private ExFormaDocumento recuperarForma(final Integer id) {
		return dao().consultar(id, ExFormaDocumento.class, false);
	}

}
