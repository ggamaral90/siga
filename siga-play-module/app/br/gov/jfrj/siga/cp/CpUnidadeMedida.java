package br.gov.jfrj.siga.cp;

import javax.persistence.Entity;
import javax.persistence.Table;

import br.gov.jfrj.siga.base.util.Catalogs;

@Entity
@Table(name = "CP_UNIDADE_MEDIDA", schema = Catalogs.CORPORATIVO)
public class CpUnidadeMedida extends AbstractCpUnidadeMedida {

	final static public int ANO = 1;

	final static public int MES = 2;

	final static public int DIA = 3;
	
	final static public int HORA = 4;
	
	final static public int MINUTO = 5;
	
	final static public int SEGUNDO = 6;
	
	public String getPlural(){
		if (getDescricao().endsWith("s"))
			return getDescricao() + "es";
		return getDescricao() + "s";
	}

}
