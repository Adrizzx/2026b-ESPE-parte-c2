package es.upm.grise.order;

import java.util.ArrayList;

import es.upm.grise.order.exceptions.CannotAddItemsToPlacedOrderException;
import es.upm.grise.order.exceptions.IncorrectItemException;
import es.upm.grise.order.exceptions.NonExistingItemException;

public class Order {

    private ArrayList<Item> items;
    private Status status;
    private Invoice invoice;

    /*
     * Method to code/test
     */
    public Order() {

    }

    /*
     * Method to code/test
     */
    public void addItem(Item item) throws CannotAddItemsToPlacedOrderException, IncorrectItemException {

    }

    /*
     * Method to code/test
     */
    public void removeItem(Item item) throws NonExistingItemException {

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