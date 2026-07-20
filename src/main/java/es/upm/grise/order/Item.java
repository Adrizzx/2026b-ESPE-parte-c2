package es.upm.grise.order;

public class Item {
	
	private Product product;
	private int quantity;
	private double price;
	
	public Item(Product product, int quantity, double price) {
		
		this.product = product;
		this.quantity = quantity;
		this.price = price;
		
	}
	
	public double getPrice() {
		
		return price;
		
	}

	public int getQuantity() {
		
		return quantity;
	}

	public Product getProduct() {
		
		return product;
		
	}

	public void setQuantity(int quantity) {
		
		this.quantity = quantity;
		
	}

	public void setPrice(double price) {
		
		this.price = price;
		
	}

}
