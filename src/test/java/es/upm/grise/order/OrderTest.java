package es.upm.grise.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.upm.grise.order.exceptions.CannotAddItemsToPlacedOrderException;
import es.upm.grise.order.exceptions.IncorrectItemException;
import es.upm.grise.order.exceptions.NonExistingItemException;

class OrderTest {

	private Order order;

	@BeforeEach
	public void setUp() {
		order = new Order();
	}

	private Product productWithId(long id) {
		Product product = new Product();
		product.id = id;
		return product;
	}

	@Test
	public void newOrderHasEmptyItemsAndNullStatus() {
		assertTrue(order.getItems().isEmpty());
		assertNull(order.getStatus());
	}

	@Test
	public void addingFirstItemSetsStatusToUnconfirmed() throws CannotAddItemsToPlacedOrderException, IncorrectItemException {
		Item item = new Item(productWithId(1), 2, 10.0);

		order.addItem(item);

		assertEquals(Status.UNCONFIRMED, order.getStatus());
		assertEquals(1, order.getItems().size());
	}

	@Test
	public void addItemThrowsIncorrectItemExceptionWhenPriceIsNegative() {
		Item item = new Item(productWithId(1), 1, -0.01);

		assertThrows(IncorrectItemException.class, () -> order.addItem(item));
	}

	@Test
	public void addItemAcceptsZeroPrice() throws CannotAddItemsToPlacedOrderException, IncorrectItemException {
		Item item = new Item(productWithId(1), 1, 0.0);

		order.addItem(item);

		assertEquals(1, order.getItems().size());
	}

	@Test
	public void addItemThrowsIncorrectItemExceptionWhenQuantityIsZero() {
		Item item = new Item(productWithId(1), 0, 10.0);

		assertThrows(IncorrectItemException.class, () -> order.addItem(item));
	}

	@Test
	public void addItemThrowsIncorrectItemExceptionWhenQuantityIsNegative() {
		Item item = new Item(productWithId(1), -1, 10.0);

		assertThrows(IncorrectItemException.class, () -> order.addItem(item));
	}

	@Test
	public void addItemThrowsCannotAddItemsToPlacedOrderExceptionWhenOrderIsPlaced()
			throws CannotAddItemsToPlacedOrderException, IncorrectItemException, ReflectiveOperationException {
		Item firstItem = new Item(productWithId(1), 1, 10.0);
		order.addItem(firstItem);
		placeOrderDirectly(order, Status.PLACED);

		Item secondItem = new Item(productWithId(2), 1, 5.0);

		assertThrows(CannotAddItemsToPlacedOrderException.class, () -> order.addItem(secondItem));
	}

	private void placeOrderDirectly(Order order, Status status) throws ReflectiveOperationException {
		Field statusField = Order.class.getDeclaredField("status");
		statusField.setAccessible(true);
		statusField.set(order, status);
	}

	@Test
	public void addingSameProductWithSamePriceIncreasesQuantityWithoutDuplicating()
			throws CannotAddItemsToPlacedOrderException, IncorrectItemException {
		Product product = productWithId(1);
		Item firstItem = new Item(product, 2, 10.0);
		Item secondItem = new Item(product, 3, 10.0);

		order.addItem(firstItem);
		order.addItem(secondItem);

		assertEquals(1, order.getItems().size());
		Item merged = order.getItems().get(0);
		assertEquals(5, merged.getQuantity());
		assertEquals(10.0, merged.getPrice());
	}

	@Test
	public void addingSameProductWithDifferentPriceKeepsHighestPrice()
			throws CannotAddItemsToPlacedOrderException, IncorrectItemException {
		Product product = productWithId(1);
		Item cheaperItem = new Item(product, 2, 8.0);
		Item pricierItem = new Item(product, 1, 12.0);

		order.addItem(cheaperItem);
		order.addItem(pricierItem);

		assertEquals(1, order.getItems().size());
		Item merged = order.getItems().get(0);
		assertEquals(3, merged.getQuantity());
		assertEquals(12.0, merged.getPrice());
	}

	@Test
	public void addingSameProductWithLowerPriceStillKeepsHighestPrice()
			throws CannotAddItemsToPlacedOrderException, IncorrectItemException {
		Product product = productWithId(1);
		Item pricierItem = new Item(product, 1, 12.0);
		Item cheaperItem = new Item(product, 2, 8.0);

		order.addItem(pricierItem);
		order.addItem(cheaperItem);

		assertEquals(1, order.getItems().size());
		Item merged = order.getItems().get(0);
		assertEquals(3, merged.getQuantity());
		assertEquals(12.0, merged.getPrice());
	}

	@Test
	public void addingDifferentProductsCreatesSeparateItems()
			throws CannotAddItemsToPlacedOrderException, IncorrectItemException {
		order.addItem(new Item(productWithId(1), 1, 10.0));
		order.addItem(new Item(productWithId(2), 1, 20.0));

		assertEquals(2, order.getItems().size());
	}

	@Test
	public void removeItemDeletesItemFromList() throws CannotAddItemsToPlacedOrderException, IncorrectItemException, NonExistingItemException {
		Item firstItem = new Item(productWithId(1), 1, 10.0);
		Item secondItem = new Item(productWithId(2), 1, 20.0);
		order.addItem(firstItem);
		order.addItem(secondItem);

		order.removeItem(firstItem);

		assertEquals(1, order.getItems().size());
		assertEquals(secondItem, order.getItems().get(0));
	}

	@Test
	public void removeItemThrowsNonExistingItemExceptionWhenItemIsNotInList() {
		Item item = new Item(productWithId(1), 1, 10.0);

		assertThrows(NonExistingItemException.class, () -> order.removeItem(item));
	}

	@Test
	public void removeItemSetsStatusToNullWhenListBecomesEmpty()
			throws CannotAddItemsToPlacedOrderException, IncorrectItemException, NonExistingItemException {
		Item item = new Item(productWithId(1), 1, 10.0);
		order.addItem(item);

		order.removeItem(item);

		assertTrue(order.getItems().isEmpty());
		assertNull(order.getStatus());
	}

	@Test
	public void removeItemKeepsUnconfirmedStatusWhenItemsRemain()
			throws CannotAddItemsToPlacedOrderException, IncorrectItemException, NonExistingItemException {
		Item firstItem = new Item(productWithId(1), 1, 10.0);
		Item secondItem = new Item(productWithId(2), 1, 20.0);
		order.addItem(firstItem);
		order.addItem(secondItem);

		order.removeItem(firstItem);

		assertEquals(Status.UNCONFIRMED, order.getStatus());
	}

}
