<%@ tag body-content="empty"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ attribute name="titulo" required="false"%>
<%@ attribute name="href" required="false"%>
<%@ attribute name="cssClass" required="false"%>
<%@ attribute name="texto" required="false"%>

<a href="${href}" title="${titulo}"
	onclick="javascript:if (isCarregando()) return false; carrega();"
	class="${cssClass}">${texto}</a>
