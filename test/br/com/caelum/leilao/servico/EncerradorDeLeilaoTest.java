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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.infra.dao.LeilaoDao;
import br.com.caelum.leilao.infra.email.Carteiro;

public class EncerradorDeLeilaoTest {

	@Test
	public void deveEncerrarLeilaoComecouUmaSemanaAntes() {
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);

		Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma").naData(antiga).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Geladeira").naData(antiga).constroi();

		List<Leilao> leiloesAntigos = Arrays.asList(leilao1, leilao2);

		// Cria um objeto MOCK de LeilaoDao e Carteiro
		LeilaoDao daoFalso = mock(LeilaoDao.class);
		Carteiro carteiroFalso = mock(Carteiro.class);

		// Quando o método correntes for invocado, a lista leiloesAntigos será retornado
		when(daoFalso.correntes()).thenReturn(leiloesAntigos);

		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);
		encerrador.encerra();

		// Verifica se a lista de encerrados é igual a dois
		assertEquals(2, encerrador.getTotalEncerrados());

		// Verifica se o retorno é true
		assertTrue(daoFalso.correntes().get(0).isEncerrado());
		assertTrue(daoFalso.correntes().get(1).isEncerrado());
	}

	@Test
	public void deveAtualizarLeiloesEncerrados() {
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);

		Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma").naData(antiga).constroi();

		List<Leilao> leiloesAntigos = Arrays.asList(leilao1);

		// Cria um objeto MOCK de LeilaoDao e Carteiro
		LeilaoDao daoFalso = mock(LeilaoDao.class);
		Carteiro carteiroFalso = mock(Carteiro.class);

		// Quando o método correntes for invocado, a lista leiloesAntigos será retornado
		when(daoFalso.correntes()).thenReturn(leiloesAntigos);

		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);
		encerrador.encerra();

		// Verifica se o método atualiza do objeto daoFalso será invocado apenas uma única vez
		verify(daoFalso, times(1)).atualiza(leilao1);
		/*
		 * Podemos ainda passar para o verify: atLeastOnce(), atLeast(numero), atMost(numero), never()
		 * Podemos também passar o parâmetro desta forma para generalizar: any(Leilao.class)
		 */
	}

	@Test
	public void deveContinuarExecucaoMesmoQuandoDaoFalha() {
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);

		Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma").naData(antiga).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Geladeira").naData(antiga).constroi();

		List<Leilao> leiloesAntigos = Arrays.asList(leilao1, leilao2);

		LeilaoDao daoFalso = mock(LeilaoDao.class);
		Carteiro carteiroFalso = mock(Carteiro.class);

		// Quando o método correntes for invocado, a lista leiloesAntigos será retornado
		when(daoFalso.correntes()).thenReturn(leiloesAntigos);

		// Será retornado uma exceção sempre que o método atualiza do daoFalso ser invocado com o objeto leilao1
		doThrow(new RuntimeException()).when(daoFalso).atualiza(leilao1);

		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);
		encerrador.encerra();

		// Verifica se o método atualiza/envia dos objetos daoFalso/carteiroFalso serão invocados apenas uma/nenhuma vez
		verify(daoFalso, times(1)).atualiza(leilao2);
		verify(carteiroFalso, times(1)).envia(leilao2);
		verify(carteiroFalso, times(0)).envia(leilao1);
		
		/*  Para garantir que o método atualiza será executado antes do método envia
		 * 
		 *	InOrder inOrder = inOrder(daoFalso, carteiroFalso);
         *	inOrder.verify(daoFalso, times(1)).atualiza(leilao1);    
         *	inOrder.verify(carteiroFalso, times(1)).envia(leilao1);
		 */
	}
}
