package br.gov.jfrf.siga.sr.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import br.gov.jfrf.siga.sr.models.SrAcao;
import br.gov.jfrf.siga.sr.models.SrItemConfiguracao;
import br.gov.jfrf.siga.sr.models.SrLista;
import br.gov.jfrf.siga.sr.models.SrSelecionavel;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import play.data.binding.Global;
import play.data.binding.TypeBinder;
import play.db.jpa.JPA;

@Global
public class SrListaBinder implements TypeBinder<SrLista> {

	@Override
	public Object bind(String name, Annotation[] anns, String value,
			Class clazz, Type arg4) throws Exception {
		if (value != null && !value.equals("")) 
			return SrLista.findById(Long.valueOf(value));
		return null;
	}

}
