package br.gov.jfrj.siga.sr.model.vo;

import br.gov.jfrj.siga.cp.CpUnidadeMedida;
import br.gov.jfrj.siga.sr.model.SrAtributoAcordo;
import br.gov.jfrj.siga.sr.model.SrOperador;

public class SrAtributoAcordoVO {
	
	public Long idAtributoAcordo;
	public SrOperador operador;
	public String operadorNome;
	public Long valor;
	public CpUnidadeMedida unidadeMedida;
	public String unidadeMedidaPlural;
	public SrAtributoVO atributo;
	public boolean ativo;
	
	public SrAtributoAcordoVO(SrAtributoAcordo atributoAcordo) {
		this.idAtributoAcordo = atributoAcordo.getIdAtributoAcordo(); 
		this.operador = atributoAcordo.getOperador();
		this.operadorNome = atributoAcordo.getOperador() != null ? atributoAcordo.getOperador().getNome() : "";
		this.valor = atributoAcordo.getValor();
		this.unidadeMedida = atributoAcordo.getUnidadeMedida();
		this.unidadeMedidaPlural = atributoAcordo.getUnidadeMedida() != null ? atributoAcordo.getUnidadeMedida().getPlural() : "";
		this.atributo = SrAtributoVO.createFrom(atributoAcordo.getAtributo(), false);
		this.ativo = atributoAcordo.isAtivo();
	}
	
	public static SrAtributoAcordoVO createFrom(SrAtributoAcordo atributoAcordo) {
		if (atributoAcordo != null)
			return new SrAtributoAcordoVO(atributoAcordo);
		else
			return null;
	}
}
