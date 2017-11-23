//**********************************************************************
// Copyright (c) 2017 Telefonaktiebolaget LM Ericsson, Sweden.
// All rights reserved.
// The Copyright to the computer program(s) herein is the property of
// Telefonaktiebolaget LM Ericsson, Sweden.
// The program(s) may be used and/or copied with the written permission
// from Telefonaktiebolaget LM Ericsson or in accordance with the terms
// and conditions stipulated in the agreement/contract under which the
// program(s) have been supplied.
// **********************************************************************
package br.com.caelum.leilao.servico;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.dominio.Usuario;
import br.com.caelum.leilao.infra.dao.LeilaoDao;
import br.com.caelum.leilao.infra.relogio.Relogio;

public class GeradorDePagamentoTest {

	@Test
	public void deveGerarPagamentoParaUmLeilaoEncerrado() {
		// Cria um objeto MOCK de LeilaoDao, RepositorioDePagamentos e Avaliador
		LeilaoDao leiloes = mock(LeilaoDao.class);
		RepositorioDePagamentos pagamentos = mock(RepositorioDePagamentos.class);
		Avaliador avaliador = mock(Avaliador.class);

		Leilao leilao = new CriadorDeLeilao().para("Playstation").lance(new Usuario("José da Silva"), 2000.0).lance(new Usuario("Maria Pereira"), 2500.0).constroi();

		// Quando o metodo encerrados for invocado, então a lista com o objeto leilao será retornado
		when(leiloes.encerrados()).thenReturn(Arrays.asList(leilao));

		// Quando o metodo maiorLang for invocado, então o resultado será 2500
		when(avaliador.getMaiorLance()).thenReturn(2500.0);

		GeradorDePagamento gerador = new GeradorDePagamento(leiloes, pagamentos, avaliador);
		gerador.gera();

		// A funcionalidade ArgumentCaptor permite capturar um resultado gerado dentro do método gera chamado acima
		ArgumentCaptor<Pagamento> argumento = ArgumentCaptor.forClass(Pagamento.class);
		verify(pagamentos).salva(argumento.capture());

		Pagamento pagamentoGerado = argumento.getValue();

		assertEquals(2500.0, pagamentoGerado.getValor(), 0.00001);
	}

	@Test
	public void deveEmpurrarsParaOProximoDiaUtil() {
		LeilaoDao leiloes = mock(LeilaoDao.class);
		RepositorioDePagamentos pagamentos = mock(RepositorioDePagamentos.class);
		Relogio relogio = mock(Relogio.class);

		Leilao leilao = new CriadorDeLeilao().para("Playstation").lance(new Usuario("José da Silva"), 2000.0).lance(new Usuario("Maria Pereira"), 2500.0).constroi();

		// Quando o metodo encerrados for invocado, então a lista com o objeto leilao será retornado
		when(leiloes.encerrados()).thenReturn(Arrays.asList(leilao));

		Calendar sabado = Calendar.getInstance();
		sabado.set(2012, Calendar.APRIL, 7);

		// Quando o metodo hoje for invocado, então o resultado será o objeto sabado
		when(relogio.hoje()).thenReturn(sabado);

		GeradorDePagamento gerador = new GeradorDePagamento(leiloes, pagamentos, new Avaliador(), relogio);
		gerador.gera();

		// A funcionalidade ArgumentCaptor permite capturar um resultado gerado dentro do método gera chamado acima
		ArgumentCaptor<Pagamento> argumento = ArgumentCaptor.forClass(Pagamento.class);
		verify(pagamentos).salva(argumento.capture());

		Pagamento pagamentoGerado = argumento.getValue();

		assertEquals(Calendar.MONDAY, pagamentoGerado.getData().get(Calendar.DAY_OF_WEEK));
	}
}
