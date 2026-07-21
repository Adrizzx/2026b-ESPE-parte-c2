package es.upm.grise.order;

import java.util.ArrayList;

import es.upm.grise.order.exceptions.CannotAddItemsToPlacedOrderException;
import es.upm.grise.order.exceptions.IncorrectItemException;
import es.upm.grise.order.exceptions.NonExistingItemException;

public class Order {

    private ArrayList<Item> items;
    private Status status;
    private Invoice invoice;

    public Order() {
        this.items = new ArrayList<Item>();
        this.status = null;
    }

    public void addItem(Item item) throws CannotAddItemsToPlacedOrderException, IncorrectItemException {
        if (status == Status.PLACED) {
            throw new CannotAddItemsToPlacedOrderException();
        }
        if (item.getPrice() < 0) {
            throw new IncorrectItemException();
        }
        if (item.getQuantity() <= 0) {
            throw new IncorrectItemException();
        }

        boolean wasEmpty = items.isEmpty();

        Item existingItem = findItemByProduct(item.getProduct());
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
            existingItem.setPrice(Math.max(existingItem.getPrice(), item.getPrice()));
        } else {
            items.add(item);
        }

        if (wasEmpty) {
            status = Status.UNCONFIRMED;
        }
    }

    public void removeItem(Item item) throws NonExistingItemException {
        if (!items.remove(item)) {
            throw new NonExistingItemException();
        }
        if (items.isEmpty()) {
            status = null;
        }
    }

    private Item findItemByProduct(Product product) {
        for (Item existing : items) {
            if (existing.getProduct().getId() == product.getId()) {
                return existing;
            }
        }
        return null;
    }

    /*
     * getters
     */

    public ArrayList<Item> getItems() {
        return items;
    }

    public Status getStatus() {
        return status;
    }

    public Invoice getInvoice() {
        return invoice;
    }

}