# Conversacion de pruebas con Gemini

**Herramienta indicada por el profesor:** Gemini.

**Formato:** resumen en forma de dialogo entre yo y Gemini, usando la especificacion, la implementacion de `Order` y el proyecto Maven.

---

## Conversacion

**Yo:** Gemini, el ingeniero indico usar Gemini como herramienta LLM para esta parte del trabajo. Ya tengo implementada la clase `Order` y ahora necesito crear pruebas unitarias con JUnit 5 para validar el comportamiento pedido en la especificacion.

**Gemini:** Bien. Para probar `Order`, conviene cubrir el estado inicial, las validaciones de `addItem`, la union de items con el mismo producto, el cambio de estado del pedido y el comportamiento de `removeItem`.

**Yo:** Que casos de prueba deberia incluir?

**Gemini:** Te propongo estos casos:

1. Un pedido nuevo debe tener la lista de items vacia y `status` en `null`.
2. Al agregar el primer item valido, el estado debe cambiar a `UNCONFIRMED`.
3. `addItem` debe lanzar `IncorrectItemException` si el precio es negativo.
4. `addItem` debe aceptar precio cero.
5. `addItem` debe lanzar `IncorrectItemException` si la cantidad es cero.
6. `addItem` debe lanzar `IncorrectItemException` si la cantidad es negativa.
7. `addItem` debe lanzar `CannotAddItemsToPlacedOrderException` si el pedido esta en `PLACED`.
8. Si se agrega el mismo producto con el mismo precio, debe aumentar la cantidad sin duplicar el item.
9. Si se agrega el mismo producto con distinto precio, debe mantener el precio mayor.
10. Si se agregan productos distintos, deben quedar como items separados.
11. `removeItem` debe eliminar un item existente.
12. `removeItem` debe lanzar `NonExistingItemException` si el item no esta en la lista.
13. Si se elimina el unico item, el estado vuelve a `null`.
14. Si se elimina un item pero quedan otros, el estado permanece en `UNCONFIRMED`.

**Yo:** Necesito crear productos con ids concretos para probar si dos items son del mismo producto. Como lo hago?

**Gemini:** Como los tests estan en el mismo paquete `es.upm.grise.order`, puedes asignar directamente el atributo `id` de `Product`. Crea un metodo auxiliar:

```java
private Product productWithId(long id) {
    Product product = new Product();
    product.id = id;
    return product;
}
```

**Yo:** Y como pruebo el caso de un pedido en estado `PLACED` si todavia no hay un metodo publico para cambiar ese estado?

**Gemini:** Para ese caso concreto puedes usar reflexion dentro del test. Asi no modificas la clase de produccion solo para preparar el escenario:

```java
private void placeOrderDirectly(Order order, Status status) throws ReflectiveOperationException {
    Field statusField = Order.class.getDeclaredField("status");
    statusField.setAccessible(true);
    statusField.set(order, status);
}
```

**Yo:** Dame el codigo base del test.

**Gemini:** Puedes crear `src/test/java/es/upm/grise/order/OrderTest.java` con esta estructura:

```java
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
    public void addingFirstItemSetsStatusToUnconfirmed()
            throws CannotAddItemsToPlacedOrderException, IncorrectItemException {
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
    public void removeItemSetsStatusToNullWhenListBecomesEmpty()
            throws CannotAddItemsToPlacedOrderException, IncorrectItemException, NonExistingItemException {
        Item item = new Item(productWithId(1), 1, 10.0);
        order.addItem(item);

        order.removeItem(item);

        assertTrue(order.getItems().isEmpty());
        assertNull(order.getStatus());
    }
}
```

**Yo:** Ese codigo cubre todo?

**Gemini:** Es la base. Luego se completan los casos restantes en el mismo archivo `OrderTest.java`, agregando pruebas para cantidad cero, cantidad negativa, precio cero, productos distintos, precio mayor al unir items, eliminacion correcta y excepcion por eliminar un item inexistente.

**Yo:** Como verifico los tests?

**Gemini:** Ejecuta:

```bash
mvn test
```

El resultado esperado es que todos los tests pasen sin fallos.

## Resultado

Se genero `src/test/java/es/upm/grise/order/OrderTest.java` con pruebas unitarias para:

- Estado inicial de `Order`.
- Validaciones de `addItem`.
- Union de items por `product.id`.
- Comportamiento de precios al unir items.
- Eliminacion de items.
- Cambio de estado del pedido.
- Excepciones esperadas.

## Verificacion

Se ejecuto:

```bash
mvn test
```

Resultado:

```text
Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
```
