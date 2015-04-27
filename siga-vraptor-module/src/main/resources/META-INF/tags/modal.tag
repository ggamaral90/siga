<%@ tag body-content="scriptless"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ attribute name="nome" required="false"%>
<%@ attribute name="url" required="false"%>
<%@ attribute name="titulo" required="false"%>
<%@ attribute name="altura" required="false"%>
<%@ attribute name="largura" required="false"%>

<script language="javascript">

function ${nome}(){
	var url = '${url}';
	
	if(url != "")
		PassAjaxResponseToFunction('${url}', 'carregouAjax${nome}', null, false, null);
	else 
		$( "#${nome}_dialog" ).dialog( "open" );
}

function carregouAjax${nome}(response, param){
		$("#${nome}_dialog").html(response);
		$("#${nome}_dialog").dialog( "open" );
}

</script>

<div id="${nome}_dialog" title="${titulo}">
	<jsp:doBody/>
</div>

<script>
var altura = ${altura} == "" ? "'auto'" : ${altura};
var largura = ${largura} == "" ? "'auto'" : ${largura};

$("#${nome}_dialog").dialog({
    autoOpen: false,
    height: altura,
    width: largura,
    modal: true,
    resizable: false
  });
</script>