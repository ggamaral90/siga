<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>


<style>
	.inline {
		display: inline-flex !important;
	}
	
	.tabela {
		margin-top: -10px;
		min-width: 200px;
	}
	
	.tabela tr {
	 	border: solid;
	 	border-color: rgb(169, 169, 169);
	 	border-width: 1px;
	 	font-weight: bold;
		line-height: 20px;
	}
	
	.tabela th {
	 	color: #365b6d;
	 	font-size: 100%;
	 	padding: 5px 10px;
	 	border: solid 1px rgb(169, 169, 169);
	}
	
	.tabela td {
		color: #000;
	 	padding-right: 10px !important;
	}
	
	.gt-form-table td {
		padding-right: 0px;
	}
	
	.barra-subtitulo-top {
		border-radius: 5px 5px 0 0;
	}
	
	.barra-subtitulo {
		background-color: #eee;
		padding: 10px 15px;
		border: 1px solid #ccc;
		color: #365b6d !important;
		border-bottom: 1px solid #ccc;
		border-radius: 5px !important;
		margin: 0 -16px 10px -16px;
	}
	
	.div-editar-equipe {
		width: 800px !important;
		max-width: 800px !important;
	}
	
	.gt-content-box {
		padding-top: 0px !important;
	}
	
	.ui-widget-header {
		border: 1px solid #365b6d;
		background: #365b6d;
	}
	
	#equipe_dialog, #excecaoHorario_dialog {
		height:auto !important;
	}
	
</style>
		<div class="gt-form gt-content-box div-editar-equipe">
			<form id="form" class="formEditarEquipe" enctype="multipart/form-data">
				<input type="hidden" id="idEquipe" name="equipe.idEquipe">
				<input type="hidden" id="idEquipeIni" name="equipe.hisIdIni">
				
				<p class="gt-error" style="display:none;" id="erroEquipeCamposObrigatorios">Alguns campos obrigat�rios n�o foram preenchidos</p>
				<div class="gt-form-table">
					<div class="barra-subtitulo barra-subtitulo-top header"
						align="center" valign="top">Dados b�sicos</div>
				</div>
				<div class="gt-form-row gt-width-100">
					<label>Lota��o</label>
					<input type="hidden" name="lotacaoEquipe" id="lotacaoEquipe" class="selecao">
					<siga:selecao propriedade="lotacaoEquipe" tema="simple" modulo="siga" urlAcao="buscar" desativar="sim" />
				</div>
				
				<div class="gt-form-table">
					<div class="barra-subtitulo barra-subtitulo-top header"
						align="center" valign="top">Hor�rio de Trabalho</div>
				</div>
				
				<div class="gt-form-row">
					<label style="font-weight: bold;">Exce��es ao calend�rio padr�o</label>
					<!-- content bomex -->
					<div class="gt-content-box dataTables_div">
						<table id="excecoes_table" class="gt-table display">
							<thead>
								<tr>
									<th>Dia Semana</th>
									<th>Data (String)</th>
									<th>Dia</th>
									<th>In�cio Expediente</th>
									<th>Final Expediente</th>
									<th>In�cio Intervalo</th>
									<th>Final Intervalo</th>
									<th></th>
								</tr>
							</thead>
							
						</table>
					</div>
					<div class="gt-form-row">
						<a href="javascript: modalExcecaoAbrir()" class="gt-btn-medium gt-btn-left">Incluir</a>
					</div>
				</div>
				
				<div class="gt-form-table">
					<div class="barra-subtitulo barra-subtitulo-top header"
						align="center" valign="top">Designa��es</div>
				</div>
			</form>
			
			<div class="gt-form-row">
				<siga:designacao modoExibicao="equipe" designacoes="${designacoes}" mostrarDesativados="mostrarDesativado"
					unidadesMedida="${unidadesMedida}" orgaos="${orgaos}" locais="${locais}" 
					pesquisaSatisfacao="${pesquisaSatisfacao}" listasPrioridade="${listasPrioridade}" /> 
			</div>
			<div class="gt-form-row">
				<input type="button" value="Gravar" class="gt-btn-medium gt-btn-left" onclick="equipeService.gravar()"/>
				<a class="gt-btn-medium gt-btn-left" onclick="equipeService.cancelarGravacao()">Cancelar</a>
				<input type="button" value="Aplicar" class="gt-btn-medium gt-btn-left" onclick="equipeService.aplicar()"/>
			</div>
		</div>
		
<siga:modal nome="excecaoHorario" titulo="Adicionar Exce��o de Hor�rio">
	<div id="dialogExcecaoHorario">
		<div class="gt-content">
			<form id="excecaoHorarioForm" method="get" action="" enctype="multipart/form-data">
				<div class="gt-form gt-content-box">
					<div class="gt-form-row gt-width-100">
						<label>Dia da Semana</label>
						<select name="diaSemana" class="select-siga" style="{width:100%;}">
							<option value=0>Nenhuma</optgroup>
							<c:forEach items="${diasSemana}" var="dia">
								<option value="${dia}">${dia.descrDiaSemana}</option>
							</c:forEach>						
						</select> 
						<span style="display:none;color: red" id="diaSemanaError">Dia da Semana n�o informado</span>
					</div>
					<div class="gt-form-row gt-width-100">
						<label>Data Espec�fica</label>
						<siga:dataCalendar nome="dataEspecifica" value="${dataEspecifica}"></siga:dataCalendar>
						<span style="display:none;color: red" id="dataEspecificaError">Data Espec�fica n�o informada</span>
					</div>
					
					<div id="erroHorarioInvalido" style="display:none;width:300px" class="gt-form-row gt-width-100">
						<span style="color: red">O per�odo informado est� inv�lido. A data de in�cio deve ser menor que a data de t�rmino e o per�odo de intervalo deve estar contido no per�odo de expediente</span>
					</div>
					
					<div class="gt-form-row gt-width-100">
						<label>In�cio Expediente <span>*</span></label>
						<input type="text" name="horaIni" id="horaIni" value="${horaIni}" class="hora" required>
					</div>
					<div class="gt-form-row gt-width-100">
						<label>Fim Expediente <span>*</span></label>
						<input type="text" name="horaFim" id="horaFim" value="${horaFim}" class="hora" required>
					</div>
					<div class="gt-form-row gt-width-100">
						<label>In�cio Intervalo <span>*</span></label>
						<input type="text" name="interIni" id="interIni" value="${interIni}" class="hora" required>
					</div>
					<div class="gt-form-row gt-width-100">
						<label>Fim Intervalo <span>*</span></label>
						<input type="text" name="interFim" id="interFim" value="${interFim}" class="hora" required>
					</div>
					
					<div class="gt-form-row">
						<a href="javascript: inserirExcecaoHorario()" class="gt-btn-medium gt-btn-left">Ok</a>
						<a href="javascript: modalExcecaoFechar()" class="gt-btn-medium gt-btn-left">Cancelar</a>
					</div>
				</div>
			</form>
		</div>
	</div>
</siga:modal>


<script type="text/javascript">
	var validatorForm,
		validatorFormExcessao = null;
	
	jQuery(document).ready(function($) {
		$.validator.addMethod(
		        "hora",
		        function(value, element, regexp) {
		            var re = new RegExp('^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$');
		            return this.optional(element) || re.test(value);
		        },
		        "Hora inv�lida."
		);

		validatorForm = jQuery("#form").validate();
		
		validatorFormExcessao = $("#excecaoHorarioForm").validate({
			onfocusout: false
		});
		
		$("#dataEspecifica").mask("99/99/9999");
		$(".hora").mask("99:99");
		$("#horaIni").rules("add", {hora: ""});
		$("#horaFim").rules("add", {hora: ""});
		$("#interIni").rules("add", {hora: ""});
		$("#interFim").rules("add", {hora: ""});
	});

	function modalExcecaoAbrir() {
		// limpa as mensagens de erros
		resetMensagensErro();
		resetCamposTela();
		
		$("#dataEspecifica").datepicker({
		    showOn: "button",
		    buttonImage: "/siga/css/famfamfam/icons/calendar.png",
		    buttonImageOnly: true,
		    dateFormat: 'dd/mm/yy'
		});
		
		$("#excecaoHorario_dialog").dialog('open');
	}
	
	function inserirExcecaoHorario() {
		if (isValidFormExc() && isvalidFormExcecao()) {
			// recupera as comboboxes
			var jDiaSemanaCbb = document.getElementsByName("diaSemana")[0],
				jDiaSemana = jDiaSemanaCbb.options[jDiaSemanaCbb.selectedIndex],
				dataSelecionada = $("#dataEspecifica").datepicker("getDate"),
				diaSemanaValue = '',
				dataToISOString = '',
				diaText = '';
			
			if (document.getElementById("dataEspecifica").value == "") {
				diaSemanaValue = jDiaSemana.value;
				diaText = jDiaSemana.text;
			}
			else {
				dataToISOString = dataSelecionada.toISOString();
				diaText = document.getElementById("dataEspecifica").value;
			}
			
			var row = [
			           diaSemanaValue,
			           dataToISOString,
			           diaText,
			           document.getElementById("horaIni").value,
			           document.getElementById("horaFim").value,
			           document.getElementById("interIni").value,
			           document.getElementById("interFim").value,
			           "<a class=\"excecao_remove\"><img src=\"/siga/css/famfamfam/icons/delete.png\" style=\"visibility: inline; cursor: pointer\" /></a>"];
			
			equipeService.excecoesTable.api().row.add(row).draw();
			modalExcecaoFechar();
		}
	}
	
	function modalExcecaoFechar() {
		$("#excecaoHorario_dialog").dialog('close');
	}
	
	function isvalidFormExcecao() {
		var jDiaSemanaCbb = document.getElementsByName("diaSemana")[0],
			isValid = true;
		
		// limpa as mensagens de erros
		resetMensagensErro();
		esconderValidacaoHorario();
		
		// Mostra a mensagem de campos obrigat�rios
		if (document.getElementById("dataEspecifica").value == "" && jDiaSemanaCbb.selectedIndex == 0) {
			document.getElementById("diaSemanaError").style.display = "inline";
			document.getElementById("dataEspecificaError").style.display = "inline";
			isValid = false;
			alert("Por favor, preencha o Dia da Semana ou a Data Espec�fica.");
		}
		
		return isValid;
	}
	
	function resetMensagensErro() {
		validatorFormExcessao.resetForm();
		document.getElementById("diaSemanaError").style.display = "none";
		document.getElementById("dataEspecificaError").style.display = "none";

		$("#horaIni").removeClass("error");
		$("#horaFim").removeClass("error");
		$("#interIni").removeClass("error");
		$("#interFim").removeClass("error");
	}
	
	function resetCamposTela() {
		var jDiaSemanaCbb = document.getElementsByName("diaSemana")[0];
		
		jDiaSemanaCbb.selectedIndex = 0;
		document.getElementById("horaIni").value = '';
        document.getElementById("horaFim").value = '';
        document.getElementById("interIni").value = '';
        document.getElementById("interFim").value = '';
        document.getElementById("dataEspecifica").value = '';
	}

	function atualizaData(dataString) {
		return new Date(dataString);
	}
	
	function atualizaHora(data, horaString) {
		var horaMinuto = horaString.split(":"),
			hora = horaMinuto[0],
			minuto = horaMinuto[1];
		
		data.setHours(hora);
		data.setMinutes(minuto);
		
		return data;
	}

	function horarioEstaValido() {
		var horaInicio = moment($("#horaIni").val(), 'hh:mm'),
		  horaTermino = moment($("#horaFim").val(), 'hh:mm'),
		  inicioIntervalo = moment($("#interIni").val(), 'hh:mm'),
		  terminoIntervalo = moment($("#interFim").val(), 'hh:mm'),
		  valido = true,
		  mensagem = null;
		  
		if(!estaAntes(horaInicio, horaTermino)) {
			mensagem = "A hora de in�cio de expediente deve ser menor que a de t�rmino."
			valido = false;
		}
		else if(!estaAntes(inicioIntervalo, terminoIntervalo)) {
			mensagem = "A hora de in�cio do intervalo deve ser menor que a de t�rmino."
			valido = false;
		}
		else if(!estaNoIntervalo(inicioIntervalo, horaInicio, horaTermino)) {
			mensagem = "A hora de in�cio do intervalo est� fora do periodo do expediente."
			valido = false;
		} 
		else if(!estaNoIntervalo(terminoIntervalo, horaInicio, horaTermino)) {
			mensagem = "A hora de t�rmino do intervalo est� fora do periodo do expediente."
			valido = false;
		}
		mostrarValidacaoHorario(mensagem);
		return valido;
	}
		
	function estaAntes(inicio, fim) {
		return inicio.isBefore(fim)
	}

	function estaNoIntervalo(data, inicio, fim) {
		return data.isSame(inicio)
		    || data.isSame(fim)
		    || (data.isAfter(inicio) && data.isBefore(fim));
	}

	function mostrarValidacaoHorario(mensagem) {
		if(mensagem) {
			$('#erroHorarioInvalido').css('display', 'block');
			$('#erroHorarioInvalido span').html(mensagem);
		}
	}

	function esconderValidacaoHorario() {
		$('#erroHorarioInvalido').css('display', 'none');
	}
	
	function isValidFormExc() {
		if(jQuery("#excecaoHorarioForm").valid()) {
			return horarioEstaValido();
		}
		else
			return false;
	}
</script>