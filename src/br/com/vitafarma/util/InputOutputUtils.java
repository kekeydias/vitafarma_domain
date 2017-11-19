package br.com.vitafarma.util;

import java.util.Calendar;
import java.util.Date;

public class InputOutputUtils {
	public static void imprime(Object obj) {
		System.out.print(obj);
	}

	static public boolean isBlank(Object value) {
		return (value == null);
	}

	static public boolean isBlank(String value) {
		return ((value == null) || value == "");
	}

	public static Double getPrecoFinal(Double precoUnitario, Double quantidade, Double desconto) {
		Double result = (precoUnitario == null ? 0.0 : precoUnitario);

		desconto = (desconto == null ? 0.0 : desconto / 100.0);
		quantidade = (quantidade == null ? 0.0 : quantidade);
		result *= quantidade;
		result *= (1 - desconto);

		result = InputOutputUtils.truncate(result);

		return result;
	}

	public static double truncate(double value) {
		return (Math.round(value * 100) / 100d);
	}

	public static Integer getDateHour(Date date) {
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);

		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	public static Integer getDateMinute(Date date) {
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);

		return calendar.get(Calendar.MINUTE);
	}
}
