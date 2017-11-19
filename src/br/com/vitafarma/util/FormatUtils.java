package br.com.vitafarma.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class FormatUtils {
	private static SimpleDateFormat formatador = new SimpleDateFormat(
			"dd/MM/yyyy");

	/*
	 * formato da entrada: dia - mês - ano
	 */
	public static String getDateString(Integer dia, Integer mes, Integer ano) {
		Date date = FormatUtils.getDate(dia, mes, ano);
		String dateStr = FormatUtils.formatador.format(date);
		return dateStr;
	}

	/*
	 * formato da entrada: java.util.Date
	 */
	public static String getDateString(Date date) {
		String dateStr = FormatUtils.formatador.format(date);
		return dateStr;
	}

	/*
	 * formato da entrada: dia - mês - ano
	 */
	public static Date getDate(Integer dia, Integer mes, Integer ano) {
		String diaStr = (dia.toString().length() == 2 ? dia.toString() : "0"
				+ dia.toString());
		String mesStr = (mes.toString().length() == 2 ? mes.toString() : "0"
				+ mes.toString());

		String dateStr = (diaStr + '/' + mesStr + '/' + ano.toString());
		Date date = null;
		try {
			date = FormatUtils.formatador.parse(dateStr);
		} catch (ParseException ex) {
			InputOutputUtils.imprime("\n\nFormato invalido de data:"
					+ "\nDia: " + dia + "\nMes: " + mes + "\nAno:" + ano
					+ "\n\n" + ex.getMessage());
		}

		return date;
	}
}
