# Conversacion de programacion con Gemini

**Herramienta indicada por el profesor:** Gemini.

**Formato:** resumen en forma de dialogo entre yo y Gemini, usando la especificacion y los archivos del proyecto.

---

## Conversacion

**Yo:** Gemini, el ingeniero indico usar Gemini como herramienta LLM para esta parte del trabajo. Tengo un proyecto Java/Maven sobre pedidos y necesito implementar la clase `Order` segun los PDF `ESP-Instrucciones.pdf` y `ESP-OrderSpecification.pdf`.

**Gemini:** Perfecto. Primero revisaria la especificacion funcional de `Order`. Segun lo que describes, la clase debe manejar una lista de items, el estado del pedido y las reglas de negocio para agregar y eliminar productos.

**Yo:** La clase `Order` tiene los atributos `items`, `status` e `invoice`. Necesito completar el constructor, `addItem(Item item)` y `removeItem(Item item)`.

**Gemini:** Los pasos serian estos:

1. En el constructor, inicializar `items` como una lista vacia y dejar `status` en `null`.
2. En `addItem`, validar primero los casos de error:
   - Si el pedido esta en estado `PLACED`, lanzar `CannotAddItemsToPlacedOrderException`.
   - Si el precio del item es negativo, lanzar `IncorrectItemException`.
   - Si la cantidad es cero o negativa, lanzar `IncorrectItemException`.
3. Si el pedido acepta el item, revisar si ya existe otro item con el mismo producto.
4. Si el producto ya existe, sumar cantidades y conservar el precio mayor.
5. Si el producto no existe, agregar el item a la lista.
6. Si antes de agregar la lista estaba vacia, cambiar el estado a `UNCONFIRMED`.
7. En `removeItem`, eliminar el item; si no existe, lanzar `NonExistingItemException`.
8. Si despues de eliminar no quedan items, volver el estado a `null`.

**Yo:** Dame el codigo para implementar eso en `Order.java`.

**Gemini:** Puedes implementarlo asi:

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

**Yo:** Por que se compara el producto por `id`?

**Gemini:** Porque la especificacion indica que dos items representan el mismo producto cuando el producto tiene el mismo identificador. Por eso se usa `existing.getProduct().getId() == product.getId()` y no se compara el objeto completo.

**Yo:** Y si agrego el mismo producto con otro precio?

**Gemini:** La cantidad se acumula en el item existente y el precio final debe ser el mayor de los dos. Por eso se usa:

```java
existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
existingItem.setPrice(Math.max(existingItem.getPrice(), item.getPrice()));
```

**Yo:** Como verifico que la implementacion esta bien?

**Gemini:** Primero compila el proyecto con Maven:

```bash
mvn compile
```

Si no hay errores de compilacion, despues conviene crear pruebas unitarias para cubrir las reglas principales de `addItem` y `removeItem`.

## Resultado

Se implemento `Order.java` con:

- Constructor de pedido vacio.
- Validaciones de `addItem`.
- Agrupacion de items por `product.id`.
- Actualizacion del estado a `UNCONFIRMED`.
- Eliminacion de items con control de errores.
- Regreso del estado a `null` cuando el pedido queda vacio.

## Verificacion

Se compilo el proyecto con:

```bash
mvn compile
```

La compilacion finalizo sin errores.
