package br.gov.jfrj.siga.sr.model;

public enum SrTipoDisponibilidade {
	DISPONIVEL("Dispon�vel", "/sigasr/images/icons/disponibilidade/disponivel.png"),
	INDISPONIVEL_PERMITE_SOLICITACOES("Indispon�vel com possibilidade de abertura de solicita��es", "/sigasr/images/icons/disponibilidade/indisponivel_permite_solicitacoes.png"),
	INDISPONIVEL_BLOQUEIO_SOLICITACOES("Indispon�vel com bloqueio de solicita��es", "/sigasr/images/icons/disponibilidade/indisponivel_bloqueio_solicitacoes.png"),
	NAO_UTILIZADO("N�o Utilizado", "/sigasr/images/icons/disponibilidade/nao_utilizado.png"),
	DISPONIVEL_IRREGULAR("Dispon�vel com funcionamento irregular", "/sigasr/images/icons/disponibilidade/disponivel_irregular.png"),
	DISPONIVEL_AVISO("Dispon�vel com mensagem de aviso", "/sigasr/images/icons/disponibilidade/disponivel_aviso.png"),
	NENHUM("Nenhum", "/sigasr/images/icons/disponibilidade/sem_disponibilidade.png");

	private String descricao;
	private String caminhoIcone;

	private SrTipoDisponibilidade(String descricao, String caminhoIcone) {
		this.descricao = descricao;
		this.caminhoIcone = caminhoIcone;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getCaminhoIcone() {
		return caminhoIcone;
	}

	public static String iconeVazio() {
		return SrTipoDisponibilidade.NENHUM.getCaminhoIcone();
	}
}