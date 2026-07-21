# Conversación de programación con LLM

**LLM utilizado:** Gemini (Google), a través de Gemini CLI (interfaz de agente sobre el modelo Gemini).

**Nota:** El enunciado permite (no obliga) el uso de un LLM para esta parte ("puede utilizar el LLM indicado por el profesor"). Se optó por Gemini en lugar del LLM sugerido.

---

## Contexto proporcionado al LLM

Se proporcionaron al LLM los ficheros de especificación del proyecto:

- `ESP-Instrucciones.pdf`: enunciado del ejercicio (Grupo 2 – ejercicio 1, parte c2).
- `ESP-OrderSpecification.pdf`: especificación funcional de la clase `Order` (diagrama de clases y reglas de negocio de `addItem` y `removeItem`).

Y el estado inicial del repositorio (clases stub sin implementar):
`Order.java`, `Item.java`, `Product.java`, `Status.java`, `Invoice.java`, `Invoices.java`,
y las excepciones `CannotAddItemsToPlacedOrderException`, `CannotPlaceEmptyOrderException`,
`IncorrectItemException`, `NonExistingItemException`.

## Petición al LLM

Implementar en `Order.java`:

- El constructor `Order()`: lista de `items` vacía y `status` a `null`.
- `addItem(Item item)`:
  - Lanza `CannotAddItemsToPlacedOrderException` si el `status` es `PLACED`.
  - Lanza `IncorrectItemException` si `price < 0` o `quantity <= 0`.
  - Si ya existe un item del mismo `product` (mismo `id`) en la lista, no se añade un nuevo item: se incrementa la `quantity` del existente y el `price` pasa a ser el máximo entre el antiguo y el nuevo (coincida o no el precio).
  - Si la lista estaba vacía antes de añadir, el `status` pasa a `UNCONFIRMED`.
- `removeItem(Item item)`:
  - Elimina el item de la lista.
  - Lanza `NonExistingItemException` si el item no está en la lista.
  - Si la lista queda vacía, el `status` vuelve a `null`.

## Resultado aportado por el LLM

```java
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
```

## Verificación

Se compiló el proyecto con `mvn compile`, finalizando sin errores.
