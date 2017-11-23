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

import java.util.Calendar;

public class Pagamento {

	private final double valor;
	private final Calendar data;

	public Pagamento(double valor, Calendar data) {
		this.valor = valor;
		this.data = data;
	}

	public double getValor() {
		return valor;
	}

	public Calendar getData() {
		return data;
	}
}
