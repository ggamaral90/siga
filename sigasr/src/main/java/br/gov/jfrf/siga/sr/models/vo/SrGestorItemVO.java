package br.gov.jfrf.siga.sr.models.vo;

import br.gov.jfrf.siga.sr.models.SrGestorItem;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;

/**
 * Classe que representa um V.O. de {@link SrGestorItem}.
 */
public class SrGestorItemVO {
	public Long idGestorItem;
	public SelecionavelVO dpPessoaVO;
	public SelecionavelVO dpLotacaoVO;
	
	public SrGestorItemVO(Long idGestorItem, DpPessoa dpPessoa, DpLotacao dpLotacao) {
		this.idGestorItem = idGestorItem;
		this.dpPessoaVO = SelecionavelVO.createFrom(dpPessoa);
		this.dpLotacaoVO = SelecionavelVO.createFrom(dpLotacao);
	}
}