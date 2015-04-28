<%@ tag body-content="scriptless"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>

<%@ attribute name="nomeSelPessoa" required="false"%>
<%@ attribute name="nomeSelLotacao" required="false"%>
<%@ attribute name="nomeSelFuncao" required="false"%>
<%@ attribute name="nomeSelCargo" required="false"%>
<%@ attribute name="nomeSelGrupo" required="false"%>

<%-- <%@ attribute name="valuePessoa" required="false"%> --%>
<%-- <%@ attribute name="valueLotacao" required="false"%> --%>
<%-- <%@ attribute name="valueFuncao" required="false"%> --%>
<%-- <%@ attribute name="valueCargo" required="false"%> --%>
<%-- <%@ attribute name="valueGrupo" required="false"%> --%>


<%@ attribute name="disabled" required="false"%>
<%@ attribute name="onchange" required="false"%>
<%@ attribute name="cssClass" required="false"%>
<%@ attribute name="id" required="false"%>


<c:set var="nomeSelPessoaClean" value="${fn:replace(nomeSelPessoa,'.','')}" />
<c:set var="nomeSelLotacaoClean" value="${fn:replace(nomeSelLotacao,'.','')}" />
<c:set var="nomeSelFuncaoClean" value="${fn:replace(nomeSelFuncao,'.','')}" />
<c:set var="nomeSelCargoClean" value="${fn:replace(nomeSelCargo,'.','')}" />
<c:set var="nomeSelGrupoClean" value="${fn:replace(nomeSelGrupo,'.','')}" />

<c:set var="desativar" value="nao"></c:set>
<c:if test="${disabled == 'true'}">
	<c:set var="pessoaLotaFuncCargoSelecaoDisabled" value="disabled='disabled'"></c:set>
	<c:set var="desativar" value="sim"></c:set>
</c:if>

<select id="${nomeSelPessoaClean}${nomeSelLotacaoClean}${nomeSelFuncaoClean}${nomeSelCargoClean}${nomeSelGrupoClean}" onchange="" ${pessoaLotaFuncCargoSelecaoDisabled} >
  <option value="1">Pessoa</option>
  <option value="2">LotaÁ„o</option>
  <option value="3">FunÁ„o</option>
  <option value="4">Cargo</option>
  <option value="5">Grupo</option>
</select>

<span id="spanPessoa${nomeSelPessoaClean}">
	${nomeSelPessoaClean}
	<siga:selecao propriedade="${nomeSelPessoaClean}" tema="simple" modulo="siga" urlAcao="pessoa/buscar" urlSelecionar="selecionar" desativar="${desativar}"/>
</span>

<span id="spanLotacao${nomeSelLotacaoClean}">
	${nomeSelLotacaoClean}
	<siga:selecao propriedade="${nomeSelLotacaoClean}" tema="simple" modulo="siga" urlAcao="pessoa/buscar" urlSelecionar="selecionar" desativar="${desativar}"/>
</span>

<span id="spanFuncao${nomeSelFuncaoClean}">
	${nomeSelFuncaoClean}
	<siga:selecao propriedade="${nomeSelFuncaoClean}" tema="simple" modulo="siga" urlAcao="pessoa/buscar" urlSelecionar="selecionar" desativar="${desativar}"/>
</span>

<span id="spanCargo${nomeSelCargoClean}">
	${nomeSelCargoClean}
	<siga:selecao propriedade="${nomeSelCargoClean}" tema="simple" modulo="siga" urlAcao="pessoa/buscar" urlSelecionar="selecionar" desativar="${desativar}"/>
</span>

<span id="spanGrupo${nomeSelGrupoClean}">
	${nomeSelGrupoClean}
	<siga:selecao propriedade="${nomeSelGrupoClean}" tema="simple" modulo="siga" urlAcao="pessoa/buscar" urlSelecionar="selecionar" desativar="${desativar}"/>
</span>



<script language="javascript">

var select = document.getElementById('${nomeSelPessoaClean}${nomeSelLotacaoClean}${nomeSelFuncaoClean}${nomeSelCargoClean}${nomeSelGrupoClean}');

// O onchange tem de ser definido da forma abaixo porque, quando esta tag est√° dentro de um c√≥digo
// carregado por ajax, n√£o funciona o tratamento do modo tradicional (onchange="", etc)
// http://stackoverflow.com/questions/8893786/uncaught-referenceerror-x-is-not-defined
function limparPessoa() {
	document.getElementById('spanPessoa${nomeSelPessoaClean}').style.display = 'none';
	document.getElementById('formulario_${nomeSelPessoaClean}Sel_sigla').value='';
	document.getElementById('formulario_${nomeSelPessoaClean}Sel_descricao').value='';
	document.getElementById('${nomeSelPessoaClean}SelSpan').innerHTML='';
}

function limparLotacao() {
	document.getElementById('spanLotacao${nomeSelLotacaoClean}').style.display = 'none';
	document.getElementById('formulario_${nomeSelLotacaoClean}Sel_sigla').value='';
	document.getElementById('formulario_${nomeSelLotacaoClean}Sel_descricao').value='';
	document.getElementById('${nomeSelLotacaoClean}SelSpan').innerHTML='';
}

function limparFuncao() {
	document.getElementById('spanFuncao${nomeSelFuncaoClean}').style.display = 'none';
	document.getElementById('formulario_${nomeSelFuncaoClean}Sel_sigla').value='';
	document.getElementById('formulario_${nomeSelFuncaoClean}Sel_descricao').value='';
	document.getElementById('${nomeSelFuncaoClean}SelSpan').innerHTML='';
}

function limparCargo() {
	document.getElementById('spanCargo${nomeSelCargoClean}').style.display = 'none';
	document.getElementById('formulario_${nomeSelCargoClean}Sel_sigla').value='';
	document.getElementById('formulario_${nomeSelCargoClean}Sel_descricao').value='';
	document.getElementById('${nomeSelCargoClean}SelSpan').innerHTML='';
}

function limparGrupo() {
	document.getElementById('spanGrupo${nomeSelGrupoClean}').style.display = 'none';
	document.getElementById('formulario_${nomeSelGrupoClean}Sel_sigla').value='';
	document.getElementById('formulario_${nomeSelGrupoClean}Sel_descricao').value='';
	document.getElementById('${nomeSelGrupoClean}SelSpan').innerHTML='';
}

select.onchange = function(){
	var select = document.getElementById('${nomeSelPessoaClean}${nomeSelLotacaoClean}${nomeSelFuncaoClean}${nomeSelCargoClean}${nomeSelGrupoClean}');

	if (select.value == '1'){
		document.getElementById('spanPessoa${nomeSelPessoaClean}').style.display = 'inline';
		limparLotacao();
		limparFuncao();
		limparCargo();
		limparGrupo();
	} else if (select.value == '2'){
		document.getElementById('spanLotacao${nomeSelLotacaoClean}').style.display = 'inline';
		limparPessoa();
		limparFuncao();
		limparCargo();
		limparGrupo();
	} else if (select.value == '3'){
		document.getElementById('spanFuncao${nomeSelFuncaoClean}').style.display = 'inline';
		limparPessoa();
		limparLotacao();
		limparCargo();
		limparGrupo();
	} else if (select.value == '4'){
		document.getElementById('spanCargo${nomeSelCargoClean}').style.display = 'inline';
		limparPessoa();
		limparLotacao();
		limparFuncao();
		limparGrupo();
		
	} else if (select.value == '5'){
		document.getElementById('spanGrupo${nomeSelGrupoClean}').style.display = 'inline';
		limparPessoa();
		limparLotacao();
		limparFuncao();
		limparCargo();
	}
}

select.onchange();
</script>