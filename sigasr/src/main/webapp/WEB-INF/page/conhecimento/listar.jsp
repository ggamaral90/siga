<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>

<siga:pagina titulo="Edi��o de Conhecimento">
	<jsp:include page="../main.jsp"></jsp:include>
	<div class="gt-bd gt-cols clearfix">
	    <div class="gt-content">
        	<h2>Cadastro de Conhecimento</h2>
            <div class="gt-form gt-content-box">
            	<form enctype="multipart/form-data">
               		<div class="gt-form-row">
						<label>Item de Configura��o</label> 
						<siga:selecao2 propriedade="itemConfiguracao" tipo="itemConfiguracao" tema="simple" modulo="sigasr" onchange="mostraOuEscondeAcao();carregarRelacionados();"/>
					</div>
					<div style="display: inline-block" >
						<div id="gc-ancora-item"></div>
					</div>
					<div class="gt-form-row" id="divAcao">
						<label>A&ccedil;&atilde;o</label> 
						<siga:selecao2 propriedade="acao" tipo="acao" tema="simple" modulo="sigasr" onchange="carregarRelacionados();"/>
					</div>
					<div style="display: inline-block" >
						<div id="gc-ancora-item-acao"></div>
					</div>
                </form>
        	</div>
	    </div>
	    <div id="divRelacionados"></div>
	</div>
</siga:pagina>

<script>
function mostraOuEscondeAcao(){
	if ($('#formulario_itemConfiguracao_sigla')[0].value != '')
		$("#divAcao").show();
	else {
		$("#divAcao").hide();
		$("#acao").val('');
		$("#formulario_acao_descricao").val('');
		$("#formulario_acao_sigla").val('');
		$("#acaoSpan").html('');
		$("#gc-ancora-item").html('');
		$("#gc-ancora-item-acao").html('');
	}
};


function carregarRelacionados() {
	jQuery.blockUI(objBlock);
	PassAjaxResponseToFunction('/sigasr/app/solicitacao/conhecimento/listar?ajax=true&idItem=' 
			+ $("#formulario_itemConfiguracao_id").val() + '&idAcao=' + $("#formulario_acao_id").val(), 'carregouRelacionados', null, false, null);
}

function carregouRelacionados(response, param){
	var div = document.getElementById('divRelacionados');
	div.innerHTML = response;

	var scripts = div.getElementsByTagName("script");
	for(var i=0;i<scripts.length;i++)  
		   eval(scripts[i].text); 
	jQuery.unblockUI();
}

mostraOuEscondeAcao();
</script>