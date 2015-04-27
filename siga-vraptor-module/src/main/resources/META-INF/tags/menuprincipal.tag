<%@ tag body-content="empty"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://localhost/libstag" prefix="f"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>
<%-- <%@ taglib uri="http://localhost/customtag" prefix="app"%> --%>

<li><a id="menu_siga" class="" href="#">SIGA</a>
	<ul>
		<li><a href="/siga/app/principal">P�gina Inicial</a>
		</li>
		<c:if test="${empty pagina_de_erro}">
			<li><a href="#">M�dulos</a>
				<ul>
					<c:if
						test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;DOC:M�dulo de Documentos')}">
						<li><a
							href="/sigaex/app/expediente/doc/listar?primeiraVez=sim">Documentos</a>
						</li>
					</c:if>

					<c:if
						test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;WF:M�dulo de Workflow')}">
						<li><a href="/sigawf/resumo.action">Workflow</a>
						</li>
					</c:if>

					<c:if
						test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;SR')}">
						<li><a href="/sigasr/">Servi�os</a>
						</li>
					</c:if>

					<c:if
						test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;GC:M�dulo de Gest�o de Conhecimento')}">
						<li><a href="/sigagc/app/estatisticaGeral">Gest�o de Conhecimento</a>
						</li>
					</c:if>
					

					<!-- <li><a href="/sigatr/">Treinamento</a>
					</li> -->
					<!-- <li><a href="/SigaServicos/">Servi�os</a>
					</li> -->
					<c:if
						test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;AQ: M�dulo de Adicional de Qualifica��o') or 
						f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;BNF: M�dulo de Benef�cios') or 
						f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;CAD: M�dulo de Cadastro') or 
						f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;CST: M�dulo de Consultas') or 
						f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;LOT: M�dulo de Lota��o') or 
						f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;TRN: M�dulo de Treinamento')}">
						<li><a href="#">Pessoas</a>
							<ul>
								<c:if
									test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;AQ: M�dulo de Adicional de Qualifica��o')}">
									<li><a href="${f:getURLSistema('siga.sgp.aq')}">AQ</a></li>
								</c:if>
								<c:if
									test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;BNF: M�dulo de Benef�cios')}">
									<li><a href="${f:getURLSistema('siga.sgp.bnf')}">Benef�cios</a>
									</li>
								</c:if>
								<c:if
									test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;CAD: M�dulo de Cadastro')}">
									<li><a href="${f:getURLSistema('siga.sgp.cad')}">Cadastro</a>
									</li>
								</c:if>
								<c:if
									test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;CST: M�dulo de Consultas')}">
									<li><a href="${f:getURLSistema('siga.sgp.cst')}">Consultas</a>
									</li>
								</c:if>
								<c:if
									test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;LOT: M�dulo de Lota��o')}">
									<li><a href="${f:getURLSistema('siga.sgp.lot')}">Lota��o</a>
									</li>
								</c:if>
								<c:if
									test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;TRN: M�dulo de Treinamento')}">
									<li><a href="${f:getURLSistema('siga.sgp.trn')}">Treinamento</a>
									</li>
								</c:if>
							</ul>
						</li>
					</c:if>
					
					<c:if test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;PP:Agendamento de per�cias do INSS')}">
					<li><a href="#">Agendas</a>
						<ul>
							<c:if
								test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;PP')}">
								<li><a href="/sigapp/">Per�cias M�dicas</a>
								</li>
							</c:if>
						</ul>
					</li>
					</c:if>
						
					<c:if test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;TP:M�dulo de Transportes')}">
                    	<li><a href="/sigatp/">Transportes</a>
                    </li>
                </c:if>
					
				</ul>
			</li>
			<li><a href="#">Administra��o</a>
				<ul>
					<li><a href="/siga/app/usuario/trocar_senha" >Trocar senha</a>
					</li>
					<%--
					<li><a href="/siga/substituicao/substituir.action">Entrar
							como substituto</a>
					</li>
					<c:if
						test="${(not empty lotaTitular && lotaTitular.idLotacao!=cadastrante.lotacao.idLotacao) ||(not empty titular && titular.idPessoa!=cadastrante.idPessoa)}">
						<li><a href="/siga/substituicao/finalizar.action">Finalizar substitui��o de 
					<c:choose>
									<c:when
										test="${not empty titular && titular.idPessoa!=cadastrante.idPessoa}">${titular.nomePessoa}</c:when>
									<c:otherwise>${lotaTitular.sigla}</c:otherwise>
								</c:choose>
							</a>
						</li>
					</c:if>
					 --%>
					 <c:choose>
					 <c:when test="${(cadastrante.idPessoa != titular.idPessoa) || (cadastrante.lotacao.idLotacao != lotaTitular.idLotacao)}">
						<%-- � uma substitui��o --%>
						<c:if
							test="${f:podeCadastrarQqSubstituicaoPorConfiguracao(cadastrante, cadastrante.lotacao)}">
							<li><a
									href="${serverAndPort}/siga/app/substituicao/listar">Gerenciar poss�veis substitutos</a>
							</li>
						</c:if>
					</c:when>
					<c:otherwise>
						<li><a
								href="${serverAndPort}/siga/app/substituicao/listar">Gerenciar poss�veis substitutos</a>
						</li>
					</c:otherwise>
					</c:choose>
				</ul>
			</li>


			<c:if test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA:Sistema Integrado de Gest�o Administrativa;GI:M�dulo de Gest�o de Identidade')}">
				<li><a href="#">Gest�o de Identidade</a>
					<ul>
						<c:if
							test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;GI;ID:Gerenciar identidades')}">
							<li><a href="/siga/app/gi/identidade/listar">Identidade</a>
							</li>
						</c:if>
						<c:if
							test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;GI;PERMISSAO:Gerenciar permiss�es')}">
							<li><a href="/siga/app/gi/acesso/listar">Configurar Permiss�es</a>
							</li>
						</c:if>
						<c:if
							test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;GI;PERFIL:Gerenciar perfis de acesso')}">
							<li><a href="/siga/app/gi/perfil/listar">Perfil de Acesso</a>
							</li>
						</c:if>
						<c:if
							test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;GI;PERFILJEE:Gerenciar perfis do JEE')}">
							<li><a href="/siga/app/gi/perfilJEE/listar">Perfil de Acesso do JEE</a>
							</li>
						</c:if>
						<c:if 						
							test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;GI;GDISTR:Gerenciar grupos de distribui��o')
							       || (f:podeGerirAlgumGrupo(titular,lotaTitular,2) && f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;GI;GDISTR;DELEG:Gerenciar grupos de distribui��o delegados'))}"> 	
	 						<li><a
									href="${serverAndPort}/siga/app/gi/grupoDeEmail/listar">Grupo de Distribui��o</a>
							</li>
						</c:if>
						<c:if
							test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;GI;SELFSERVICE:Gerenciar servi�os da pr�pria lota��o')}">
							<li><a href="/siga/app/gi/servico/editar">Acesso a Servi�os</a>
							</li>
						</c:if>
						<c:if
							test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;GI;REL:Gerar relat�rios')}">
							<li><a href="#">Relat�rios</a>
								<ul>
									<li><a
											href="/siga/app/gi/relatorio/selecionar_acesso_servico">Acesso aos Servi�os</a>
									</li>
									<li><a
											href="/siga/app/gi/relatorio/selecionar_permissao_usuario">Permiss�es de Usu�rio</a>
									</li>
									<li><a
											href="/siga/app/gi/relatorio/selecionar_alteracao_direitos">Altera��o de Direitos</a>
									</li>
									<li><a
											href="/siga/app/gi/relatorio/selecionar_historico_usuario">Hist�rico de Usu�rio</a>
									</li>
								</ul></li>
						</c:if>
					</ul></li>
			</c:if>
		</c:if>

		<c:if
			test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA:Sistema Integrado de Gest�o Administrativa;FE:Ferramentas')}">
			<li><a href="#">Ferramentas</a>
				<ul>
					<c:if
						test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;FE;MODVER:Visualizar modelos')}">
						<li><a href="/siga/app/modelo/listar">Cadastro de modelos</a>
						</li>
					</c:if>
					<c:if
						test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;FE;CAD_ORGAO:Cadastrar Org�os')}">
						<li><a href="/siga/orgao/listar.action">Cadastro de Org�os Externos</a>
						</li>
					</c:if>
					<c:if
						test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;FE;WF_ADMIN:Administrar SIGAWF')}">
						<li><a href="/sigawf/administrar.action">Administrar SIGA WF</a>
						</li>
					</c:if>
					<c:if
						test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;FE;CAD_FERIADO:Cadastrar Feriados')}">
						<li><a href="/siga/app/feriado/listar">Cadastro de Feriados</a></li>
					</c:if>	
				</ul></li>
		</c:if>

		<%--	<li><a target="_blank"
			href="/wiki/Wiki.jsp?page=${f:removeAcento(titulo)}">Ajuda</a></li>
 --%>
		<li><a href="#">Substituir</a>
			<ul class="navmenu-large">
				<c:forEach var="substituicao" items="${meusTitulares}">
					<li>
						<a style="border-left: 0px; float: right; padding-left: 0.5em; padding-right: 0.5em;"
						    href="javascript:if (confirm('Deseja excluir substitui��o?')) location.href='/siga/app/substituicao/exclui?id=${substituicao.idSubstituicao}&porMenu=true';">
							<img style="display: inline;"
							src="/siga/css/famfamfam/icons/cancel_gray.png" title="Excluir"
							onmouseover="this.src='/siga/css/famfamfam/icons/cancel.png';"
							onmouseout="this.src='/siga/css/famfamfam/icons/cancel_gray.png';">
						</a> 
						<a href="/siga/app/substituicao/substituirGravar?idTitular=${substituicao.titular.idPessoa}&idLotaTitular=${substituicao.lotaTitular.idLotacao}">
							<c:choose>
								<c:when test="${not empty substituicao.titular}">
									${f:maiusculasEMinusculas(substituicao.titular.nomePessoa)}
								</c:when>
								<c:otherwise>
									${f:maiusculasEMinusculas(substituicao.lotaTitular.nomeLotacao)}
								</c:otherwise>
							</c:choose> 
						</a>
					</li>
				</c:forEach>

			</ul>
		</li>


		<li><a href="/siga/?GLO=true">Logoff</a>
		</li>

	</ul>
</li>
<!-- insert menu -->
<c:import url="/paginas/menus/menu.jsp"></c:import>
