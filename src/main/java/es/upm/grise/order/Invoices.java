package es.upm.grise.order;

import java.util.ArrayList;

public class Invoices {
	
	private static ArrayList<Invoice> invoices = new ArrayList<Invoice>();
	
	public static void add(Invoice invoice) {
		
		invoices.add(invoice);
		
	}
	
	/*
	 * Useful for testing
	 */
	public static ArrayList<Invoice> getInvoices() {
		
		return invoices;
		
	}

}
