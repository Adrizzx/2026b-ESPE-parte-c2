# Conversación de pruebas con LLM

**LLM utilizado:** Gemini (Google), a través de Gemini CLI (interfaz de agente sobre el modelo Gemini, ejecutado por terminal).

**Nota:** el profesor indicó que es válido usar cualquier herramienta LLM para esta parte, incluyendo herramientas por terminal como Gemini CLI, no exclusivamente la interfaz web original.

---

## Contexto proporcionado al LLM

- La especificación funcional de `Order` (`ESP-OrderSpecification.pdf`).
- La implementación ya realizada de `Order.java` (constructor, `addItem`, `removeItem`).
- El resto de clases del dominio: `Item`, `Product`, `Status`, `Invoice`, `Invoices` y las excepciones del paquete `exceptions`.
- El fichero de test existente (`OrderTest.java`), que solo contenía una clase `CruiseControlTest` con un `smokeTest` vacío.

## Petición al LLM

Confeccionar pruebas unitarias jUnit 5 para la clase `Order`, cubriendo:

- Estado inicial: lista de `items` vacía y `status` a `null`.
- `addItem`:
  - Lanza `IncorrectItemException` con `price` negativo.
  - Lanza `IncorrectItemException` con `quantity` cero o negativa.
  - Lanza `CannotAddItemsToPlacedOrderException` cuando el pedido está `PLACED`.
  - Al añadir el primer item, el `status` pasa a `UNCONFIRMED`.
  - Al añadir un item de un `product` ya existente (mismo `id`) con el mismo `price`, se incrementa la `quantity` sin duplicar el item.
  - Al añadir un item de un `product` ya existente con `price` distinto (mayor o menor), se incrementa la `quantity` y el `price` final es el máximo de ambos.
  - Añadir productos distintos genera items distintos en la lista.
- `removeItem`:
  - Elimina el item de la lista.
  - Lanza `NonExistingItemException` si el item no está en la lista.
  - Si la lista queda vacía tras eliminar, el `status` vuelve a `null`.
  - Si quedan items, el `status` permanece `UNCONFIRMED`.

## Detalles técnicos resueltos durante la conversación

- El campo `id` de `Product` es de acceso de paquete (sin modificador), por lo que los tests —al estar en el mismo paquete `es.upm.grise.order`— pueden asignarlo directamente (`product.id = 1;`) para crear productos con un `id` concreto.
- Para probar el caso de "pedido `PLACED`" no existe todavía un método público `place()` en esta parte de la especificación, así que se usó reflexión (`Field.setAccessible(true)`) para fijar el campo privado `status` a `Status.PLACED` únicamente en ese test, sin alterar la clase `Order`.
- Se renombró la clase de test de `CruiseControlTest` (nombre residual de una plantilla) a `OrderTest`, coincidiendo con el nombre del fichero.

## Resultado

Se generó `src/test/java/es/upm/grise/order/OrderTest.java` con 15 casos de prueba.

## Verificación

Se ejecutó `mvn test`:

```
Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
```

Todos los tests pasan.
