package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import org.bson.Document;
import org.example.clases.Carrito.Carrito;
import org.example.clases.Carrito.CarritoManager;
import org.example.clases.Enums.EstadoFactura;
import org.example.clases.Enums.MedioPago;
import org.example.clases.Factura.Factura;
import org.example.clases.Factura.Pago;
import org.example.clases.Mongo.MongoManager;
import org.example.clases.Mongo.ProductoCatalogoDAO;
import org.example.clases.Mongo.ProductoCatalogoService;
import org.example.clases.Pedido.PedidoManager;
import org.example.clases.Producto.Producto;
import org.example.clases.Usuario.Sesion;
import org.example.clases.Usuario.Usuario;
import org.bson.types.ObjectId;
import java.time.LocalDateTime;
import java.util.*;



public class Main {
    public static void main(String[] args) {

        //Logger solo errores
        System.setProperty("org.slf4j.simpleLogger.log.org.mongodb.driver", "error");


        Scanner sc = new Scanner(System.in);

        int opcion = 0;

        EntityManagerFactory usrManagerFactory =
                Persistence.createEntityManagerFactory(
                        "objectdb/db/users.odb");
        EntityManager usrEntityManager = usrManagerFactory.createEntityManager();



        Usuario currentUser = null;

        //CarritoManager carritoManager = new CarritoManager("192.168.1.25", 6379);
        CarritoManager carritoManager = new CarritoManager("rediss://default:AYjpAAIjcDE3ZGU4Y2U4NDc2ZTI0NGRjOTYwZTI4MTNjMmQzMmY4ZHAxMA@flowing-fowl-35049.upstash.io:6379");
        PedidoManager pedidoManager = new PedidoManager(usrEntityManager, carritoManager);

        ProductoCatalogoDAO productoCatalogoDAO = new ProductoCatalogoDAO(MongoManager.getDatabase());

        ProductoCatalogoService productoCatalogoService = new ProductoCatalogoService(MongoManager.getDatabase().getCollection("productos"));

        while (currentUser == null) {
            System.out.println("Bienvenido al sistema de pedidos. Elija una opción:");
            System.out.println("1. Ingresar usuario");
            System.out.println("2. Crear usuario");
            System.out.println("3. Salir");

            String usr;
            String passwd;
            List<Usuario> usuarios;

            opcion = sc.nextInt();
            sc.nextLine(); //Limpia el buffer

            switch (opcion) {
                case 1:

                    System.out.println("Nombre de Usuario: ");
                    usr = sc.nextLine();
                    System.out.println("Contraseña: ");
                    passwd = sc.nextLine();

                    TypedQuery<Usuario> usrCreationQuery = usrEntityManager.createQuery("SELECT u FROM Usuario u WHERE u.nombre =:usr", Usuario.class);
                    usuarios = usrCreationQuery.setParameter("usr",usr).getResultList();

                    for (Usuario usuario : usuarios) {
                        if (usuario.getPasswd().equals(passwd)) {
                            currentUser = usuario;
                        }
                    }

                    if (currentUser == null){
                        System.out.println("Usuario o contraseña incorrectos");
                    } else {
                        System.out.println("Logeado como: " + currentUser.getNombre() + ", Bienvenido!");
                        if (currentUser.getSesiones() == null){
                            currentUser.setSesiones(new LinkedList<>());
                        }

                        usrEntityManager.getTransaction().begin();

                        System.out.println(LocalDateTime.now());
                        Sesion sesion = new Sesion(LocalDateTime.now());
                        currentUser.getSesiones().add(sesion);

                        usrEntityManager.persist(currentUser);
                        usrEntityManager.getTransaction().commit();
                    }

                    break;

                case 2:

                    System.out.println("Ingresa el nombre del nuevo usuario");

                    usr = sc.nextLine();
                    System.out.println(usr);
                    System.out.println("Ingresa la contraseña del nuevo usuario");
                    passwd = sc.nextLine();
                    System.out.println("Ingresa el DNI del nuevo usuario");
                    int dni = sc.nextInt();
                    sc.nextLine(); //Limpia el buffer
                    System.out.println("Ingresa la direccion del nuevo usuario");
                    String direccion = sc.nextLine();

                    //IVA
                    System.out.println("Ingrese la condición del IVA del nuevo usuario: ");
                    System.out.println("1. Responsable inscripto");
                    System.out.println("2. Consumidor final");
                    System.out.println("3. Monotributista");
                    System.out.println("4. Exento");

                    int condicionIVASwitch = sc.nextInt();
                    sc.nextLine(); //Limpia el buffer

                    String condicionIVA = switch(condicionIVASwitch){
                        case 1 -> "Responsable inscripto";
                        case 2 -> "Consumidor final";
                        case 3 -> "Monotributista";
                        default -> "Exento";
                    };

                    TypedQuery<Usuario> query = usrEntityManager.createQuery("SELECT u FROM Usuario u", Usuario.class);

                    usuarios = query.getResultList();

                    boolean existe = false;

                    for (Usuario u : usuarios) {
                        if (u.getNombre().equals(usr)) {
                            existe = true;
                            break;
                        }
                    }

                    if (existe) {
                        System.out.println("Nombre del usuario existe");
                    } else {
                        usrEntityManager.getTransaction().begin();
                        Usuario u = new Usuario(String.valueOf(usuarios.size()+1), dni, passwd, usr, direccion, condicionIVA);

                        currentUser = u;

                        System.out.println("Logeado como: " + currentUser.getNombre() + ", Bienvenido!");
                        if (currentUser.getSesiones() == null){
                            currentUser.setSesiones(new LinkedList<>());
                        }
                        currentUser.getSesiones().add(new Sesion(LocalDateTime.now()));

                        usrEntityManager.persist(u);
                        usrEntityManager.getTransaction().commit();


                    }

                    break;

                case 3:

                    usrEntityManager.close();
                    usrManagerFactory.close();

                    break;
            }
        }

        if (Objects.equals(currentUser.getNombre(), "admin")){

            while (opcion != 9) {
                System.out.println("Elija una opción:");
                System.out.println("1. Ver datos de usuario");
                System.out.println("2. Agregar producto");
                System.out.println("3. Eliminar producto");
                System.out.println("4. Editar producto");
                System.out.println("5. Ver el historial de cambios de un producto");
                System.out.println("6. Ver facturas de un usuario");
                System.out.println("7. Ver pagos de un usuario");
                System.out.println("8. Ver catálogo de productos");
                System.out.println("9. Cerrar sesión");

                opcion = sc.nextInt();
                sc.nextLine(); // Limpia el buffer

                switch (opcion){
                    case 1:

                        System.out.println("Ingrese el id del usuario:");

                        int idUsuarioBuscar = sc.nextInt();
                        sc.nextLine(); //Limpia el buffer

                        TypedQuery<Usuario> usrFindQuery = usrEntityManager.createQuery("SELECT u FROM Usuario u WHERE u.id =:idUsuario", Usuario.class);
                        List<Usuario> usuarioBuscar = usrFindQuery.setParameter("idUsuario", String.valueOf(idUsuarioBuscar)).getResultList();

                        if (!usuarioBuscar.isEmpty()){
                            for (Usuario usuario : usuarioBuscar) {
                                System.out.println("ID del usuario: " + usuario.getId());
                                System.out.println("Nombre del usuario: " + usuario.getNombre());
                                System.out.println("Clave del usuario: " + usuario.getPasswd());
                                System.out.println("DNI del usuario: " + usuario.getDni());
                                System.out.println("Dirección del usuario: " + usuario.getDireccion());
                                System.out.println("Tipo de usuario: " + usuario.getCategoria());
                                System.out.println("Condicion ante el IVA: " +  usuario.getCondicionIVA());
                            }
                        } else {
                            System.out.println("ID de usuario no encontrado.");
                        }

                        break;

                    case 2:

                        System.out.println("Nombre del Producto: ");
                        String nombre = sc.nextLine();
                        System.out.println("Descripcion del Producto: ");
                        String descripcion = sc.nextLine();
                        System.out.println("Precio del Producto: ");
                        int precio = sc.nextInt();
                        sc.nextLine(); //Limpia el buffer
                        System.out.println("Cantidad del Producto: ");
                        int cantidad = sc.nextInt();
                        sc.nextLine(); //Limpia el buffer
                        System.out.println("Ingrese el URL de la foto/video del producto:");
                        String url = sc.nextLine();
                        System.out.println("Ingrese algún comentario del producto:");
                        String comentario = sc.nextLine();

                        Producto producto = new Producto(nombre, descripcion, precio, cantidad, comentario);
                        productoCatalogoDAO.insertarProducto(producto, url);
                        System.out.println("Producto agregado con éxito!");
                        break;

                    case 3:

                        System.out.println("Ingresa el ID del producto a eliminar:");
                        String idProductoEliminar = sc.nextLine();
                        productoCatalogoDAO.eliminarProducto(idProductoEliminar);
                        break;

                    case 4:

                        System.out.println("Ingresa el ID del producto a editar:");

                        String idProductoEditar =  sc.nextLine();

                        if(ObjectId.isValid(idProductoEditar)) {
                            if (productoCatalogoDAO.existeProducto(idProductoEditar)) {
                                System.out.println("Producto: " + productoCatalogoDAO.getProductoById(idProductoEditar).get("nombre"));
                                System.out.println("Elige el campo a editar:");
                                System.out.println("1. Editar el nombre");
                                System.out.println("2. Editar la descripción");
                                System.out.println("3. Editar el precio");
                                System.out.println("4. Editar la foto/video");
                                System.out.println("5. Editar el comentario");
                                System.out.println("6. Editar la cantidad en stock");

                                int opcionEditar = sc.nextInt();
                                sc.nextLine(); //Limpia el buffer
                                String nombreOperador;

                                switch (opcionEditar) {
                                    case 1:
                                        System.out.println("Ingrese el nuevo nombre del producto:");
                                        String nuevoNombre = sc.nextLine();
                                        System.out.println("Ingrese el nombre del operador:");
                                        nombreOperador = sc.nextLine();
                                        productoCatalogoService.actualizarNombre(idProductoEditar, nuevoNombre, nombreOperador);
                                        break;

                                    case 2:
                                        System.out.println("Ingrese la nueva descripcion del producto:");
                                        String nuevaDescripcion = sc.nextLine();
                                        System.out.println("Ingrese el nombre del operador:");
                                        nombreOperador = sc.nextLine();
                                        productoCatalogoService.actualizarDescripcion(idProductoEditar, nuevaDescripcion, nombreOperador);
                                        break;

                                    case 3:
                                        System.out.println("Ingrese el nuevo precio del producto:");
                                        double nuevoPrecio = sc.nextDouble();
                                        sc.nextLine(); //Limpia el buffer
                                        System.out.println("Ingrese el nombre del operador:");
                                        nombreOperador = sc.nextLine();
                                        productoCatalogoService.actualizarPrecio(idProductoEditar, nuevoPrecio, nombreOperador);
                                        break;

                                    case 4:
                                        System.out.println("Ingrese el URL de la nueva foto/video del producto:");
                                        String nuevoURL = sc.nextLine();
                                        System.out.println("Ingrese el nombre del operador:");
                                        nombreOperador = sc.nextLine();
                                        productoCatalogoService.actualizarFoto(idProductoEditar, nuevoURL, nombreOperador);
                                        break;

                                    case 5:
                                        System.out.println("Ingrese el nuevo comentario del producto:");
                                        String nuevoComentario = sc.nextLine();
                                        System.out.println("Ingrese el nombre del operador:");
                                        nombreOperador = sc.nextLine();
                                        productoCatalogoService.actualizarComentarios(idProductoEditar, nuevoComentario, nombreOperador);
                                        break;

                                    case 6:
                                        System.out.println("Ingrese la nueva cantidad de stock: ");
                                        int nuevaCantidad = sc.nextInt();
                                        sc.nextLine();
                                        System.out.println("Ingrese el nombre del operador: ");
                                        nombreOperador = sc.nextLine();
                                        productoCatalogoService.actualizarCantidad(idProductoEditar, nuevaCantidad, nombreOperador);
                                        break;

                                    default:
                                        System.out.println("El número ingresado no es válido.");
                                        break;
                                }
                                System.out.println("Producto editado con éxito!");
                            } else {
                                System.out.println("El ID del producto no existe.");
                            }
                        } else{
                            System.out.println("ID inválido. Debe tener 24 caracteres hexadecimales");
                            }

                        break;

                    case 5:

                        System.out.println("Ingrese el id del producto:");
                        String idProducto = sc.nextLine();

                        List<Document> historial = productoCatalogoService.getHistorialCambios(idProducto);

                        if (!historial.isEmpty()){
                            for(Document cambio : historial){
                                Object anterior = cambio.get("valorAnterior");
                                Object nuevo = cambio.get("valorNuevo");

                                System.out.println("_____________________________________");
                                System.out.println("Fecha: " + cambio.getString("fecha"));
                                System.out.println("Operador: " + cambio.getString("operador"));
                                System.out.println("Campo modificado: " + cambio.getString("campo"));
                                System.out.println("Valor anterior: " +  anterior.toString());
                                System.out.println("Valor final: " +  nuevo.toString());
                            }
                        } else {
                            System.out.println("El producto no tiene cambios registrados.");
                        }

                        break;

                    case 6:

                        System.out.println("Ingrese el ID del usuario:");

                        String idUsuarioFacturas = sc.nextLine();

                        TypedQuery<Usuario> FacturasFindQuery = usrEntityManager.createQuery("SELECT u FROM Usuario u WHERE u.id =:idUsuario", Usuario.class);
                        List<Usuario> usuarioFactura = FacturasFindQuery.setParameter("idUsuario", idUsuarioFacturas).getResultList();

                        if (!usuarioFactura.isEmpty()) {
                            Usuario usuarioFact = usuarioFactura.get(0);
                            List<Factura> facturasListar = pedidoManager.listarFacturas(idUsuarioFacturas);

                            if (!facturasListar.isEmpty()) {
                                System.out.println("_________________________________");
                                System.out.println("FACTURAS DE: " + usuarioFact.getNombre());
                                System.out.println("DNI: " + usuarioFact.getDni());

                                for (Factura f : facturasListar) {
                                    System.out.println("---------------------");
                                    System.out.println("ID FACTURA: " + f.getId());
                                    System.out.println("Fecha: " + f.getPedido().getFecha());

                                    //muestra segun el tipo de condicion de iva que tenga una diferente factura
                                    if (currentUser.getCondicionIVA().equals("Responsable inscripto")) {
                                        System.out.println("Subtotal: $" + f.getSubtotal());
                                        System.out.println("IVA: $" + f.getImpuestos());
                                        System.out.println("Total: $" + f.getTotal());
                                    } else if (currentUser.getCondicionIVA().equals("Consumidor final") ||
                                            currentUser.getCondicionIVA().equals("Monotributista")) {
                                        System.out.println("Total: $" + f.getTotal() + " (IVA incluido)");
                                    } else if (currentUser.getCondicionIVA().equals("Exento")) {
                                        System.out.println("Total: $" + f.getTotal() + " (Exento de IVA)");
                                    }

                                    System.out.println("Estado: " + f.getEstado());
                                    System.out.println("ID Pedido origen: " + f.getPedido().getIdPedido());
                                }
                            } else {
                                System.out.println("El usuario con ID " + usuarioFact.getId() + " no tiene facturas.");
                            }
                        } else {
                            System.out.println("ID de usuario no encontrado.");
                        }
                        break;

                    case 7:

                        System.out.println("Ingrese el ID del usuario:");

                        String idUsuarioPagos = sc.nextLine();

                        TypedQuery<Usuario> PagosFindQuery = usrEntityManager.createQuery("SELECT u FROM Usuario u WHERE u.id =:idUsuario", Usuario.class);
                        List<Usuario> usuarioPagos = PagosFindQuery.setParameter("idUsuario", idUsuarioPagos).getResultList();

                        if (!usuarioPagos.isEmpty()) {
                            Usuario usuarioPago = usuarioPagos.get(0);
                            List<Pago> pagosListar = pedidoManager.listarPagos(idUsuarioPagos);

                            if (!pagosListar.isEmpty()) {
                                System.out.println("__________________________________");
                                System.out.println("PAGOS DE: " + usuarioPago.getNombre());
                                System.out.println("DNI: " + usuarioPago.getDni());

                                for (Pago pago : pagosListar) {
                                    System.out.println("---------------------");
                                    System.out.println("ID PAGO: " + pago.getId());
                                    System.out.println("Fecha: " + pago.getFecha());
                                    System.out.println("Monto: $" + pago.getMonto());
                                    System.out.println("Medio: " + pago.getMedioPago());
                                    System.out.println("Operador: " + (pago.getOperador() != null ? pago.getOperador() : "N/A"));
                                    System.out.print("Facturas asociadas (IDs): ");
                                    pago.getFacturasAplicadas().forEach(f -> System.out.print(f.getId() + " "));
                                    System.out.println();
                                }
                            } else {
                                System.out.println("El usuario con ID " + usuarioPago.getId() + " no tiene pagos registrados.");
                            }
                        } else {
                            System.out.println("ID de usuario no encontrado.");
                        }

                        break;

                    case 8:

                        ArrayList<Document> documentList = productoCatalogoDAO.getAll();

                        for (Document document : documentList) {

                            System.out.println("_______________________________________");
                            System.out.println("ID: " + document.get("_id").toString());
                            System.out.println("Nombre: " + document.get("nombre").toString());
                            System.out.println("Descripcion: " + document.get("descripcion").toString());
                            System.out.println("Precio: " + document.get("precio"));
                            System.out.println("Stock: " + document.get("cantidad"));
                            System.out.println("Multimedia: " + document.get("multimedia"));
                            System.out.println("Comentarios: " + document.get("comentarios"));
                        }

                        break;

                    case 9:

                        System.out.println("Cerrando sesión...");

                        //Cierra la ultima sesion y la persiste
                        List<Sesion> sesiones = currentUser.getSesiones();
                        if (sesiones != null && !sesiones.isEmpty()) {
                            Sesion ultima = sesiones.get(sesiones.size() - 1);
                            if (ultima.getFin() == null) {
                                ultima.cerrarSesion();
                            }
                        }

                        currentUser.actualizarCategoria();

                        usrEntityManager.getTransaction().begin();
                        usrEntityManager.merge(currentUser);
                        usrEntityManager.getTransaction().commit();

                        break;
                }
            }
        } else {

            int opcion2 = 0;

            Carrito carrito = carritoManager.obtenerCarrito(currentUser.getId());

            if (carrito == null){
                carrito = new Carrito();
            }

            while (opcion2 != 12) {

                System.out.println("__________________");
                System.out.println("Elija una opcion:");
                System.out.println("__________________");
                System.out.println("1. Ver catálogo de productos");
                System.out.println("2. Agregar item al carrito");
                System.out.println("3. Eliminar item del carrito");
                System.out.println("4. Ver carrito");
                System.out.println("5. Modificar la cantidad de un producto del carrito");
                System.out.println("6. Deshacer el último cambio del carrito");
                System.out.println("7. Vaciar Carrito");
                System.out.println("8. Confirmar carrito");
                System.out.println("9. Pagar pedidos");
                System.out.println("10. Ver facturas del usuario");
                System.out.println("11. Ver historial de pagos");
                System.out.println("12. Cerrar sesión");

                opcion2 = sc.nextInt();
                sc.nextLine(); //Limpia el buffer
                switch (opcion2) {

                    case 1:

                        ArrayList<Document> documentList = productoCatalogoDAO.getAll();

                        for (Document document : documentList) {

                            System.out.println("_______________________________________");
                            System.out.println("ID: " + document.get("_id").toString());
                            System.out.println("Nombre: " + document.get("nombre").toString());
                            System.out.println("Descripcion: " + document.get("descripcion").toString());
                            System.out.println("Precio: " + document.get("precio"));
                            System.out.println("Stock: " + document.get("cantidad"));
                            System.out.println("Multimedia: " + document.get("multimedia"));
                            System.out.println("Comentarios: " + document.get("comentarios"));
                        }

                        break;

                    case 2:

                        System.out.println("ID del producto a agregar: ");
                        String idProductoAgregar = sc.nextLine();

                        if(ObjectId.isValid(idProductoAgregar)) {
                            System.out.println("Producto: " + productoCatalogoDAO.getProductoById(idProductoAgregar).getInteger("nombre"));
                            System.out.println("Cantidad: ");
                            int cantidadAgregar = sc.nextInt();
                            sc.nextLine(); //Limpia el buffer
                            if (productoCatalogoDAO.existeProducto(idProductoAgregar)) {
                                if (Integer.parseInt(productoCatalogoDAO.getProductoById(idProductoAgregar).get("cantidad").toString()) >= cantidadAgregar) {

                                    // Guarda snapshot, agrega item y guarda el carrito
                                    carritoManager.snapshotCarrito(currentUser.getId(), carrito);
                                    carrito.agregarItem(idProductoAgregar, cantidadAgregar);
                                    carritoManager.guardarCarrito(currentUser.getId(), carrito);
                                    System.out.println("Producto agregado al carrito con éxito!");
                                } else {
                                    System.out.println("No hay suficiente stock disponible");
                                }
                            } else {
                                System.out.println("Producto no encontrado");
                            }
                        } else{
                            System.out.println("ID inválido. Debe tener 24 caracteres hexadecimales");
                        }

                        break;

                    case 3:

                        if (!carrito.estaVacio()) {
                            System.out.println("ID del producto a eliminar: ");
                            String idProductoEliminar = sc.nextLine();

                            if (carrito.getCarrito().containsKey(idProductoEliminar)) {

                                carritoManager.snapshotCarrito(currentUser.getId(), carrito);
                                carrito.eliminarItem(idProductoEliminar);
                                carritoManager.guardarCarrito(currentUser.getId(), carrito);

                                System.out.println("Producto eliminado del carrito.");
                            } else {
                                System.out.println("Producto no encontrado en el carrito");
                            }
                        } else {
                            System.out.println("El carrito está vacío.");
                        }

                        break;

                    case 4:

                        if (!carrito.estaVacio()) {
                            System.out.println("CARRITO: ");
                            System.out.println("_________________________________________");
                            for (String s : carrito.getCarrito().keySet()) {
                                Document document = productoCatalogoDAO.getProductoById(s);
                                System.out.println("_________________________________________");
                                System.out.println("ID: " + document.get("_id").toString());
                                System.out.println("Nombre: " + document.get("nombre").toString());
                                System.out.println("Descripcion: " + document.get("descripcion").toString());
                                System.out.println("Precio: " + document.get("precio"));
                                System.out.println("Cantidad En Carrito: " + carrito.getCarrito().get(document.get("_id").toString()));
                                System.out.println("Multimedia: " +  document.get("multimedia"));
                                System.out.println("Comentarios: " + document.get("comentarios"));
                            }
                        } else {
                            System.out.println("El carrito está vacío.");
                        }

                        break;

                    case 5:

                        if (!carrito.estaVacio()) {
                            System.out.println("ID del producto a modificar la cantidad ");
                            String idProductoCantidad = sc.nextLine();

                            if (carrito.getCarrito().containsKey(idProductoCantidad)) {
                                System.out.println("Producto: " + productoCatalogoDAO.getProductoById(idProductoCantidad).getInteger("nombre"));
                                System.out.println("Cantidad (ingrese 0 si quiere eliminarlo): ");
                                int cantidadEditar = sc.nextInt();
                                sc.nextLine(); //Limpia el buffer

                                Document productoDoc = productoCatalogoDAO.getProductoById(idProductoCantidad);
                                int stockDisponible = Integer.parseInt(productoDoc.get("cantidad").toString());

                                if (stockDisponible >= cantidadEditar && cantidadEditar >= 0) {

                                    //Guarda snapshot para deshacer y guarda el carrito
                                    carritoManager.snapshotCarrito(currentUser.getId(), carrito);
                                    carrito.modificarCantidad(idProductoCantidad, cantidadEditar);
                                    carritoManager.guardarCarrito(currentUser.getId(), carrito);
                                    System.out.println("Producto editado con éxito!");
                                } else {
                                    if (cantidadEditar < 0) {
                                        System.out.println("Ingrese un valor correcto.");
                                    } else {
                                        System.out.println("Imposible agregar mas productos de los que hay en stock.");
                                    }
                                }
                            } else {
                                System.out.println("Producto no encontrado en el carrito");
                            }
                        } else {
                            System.out.println("El carrito está vacío.");
                        }

                        break;

                    case 6:

                        System.out.println("Deshaciendo cambios...");
                        carrito = carritoManager.deshacer(currentUser.getId());

                        break;

                    case 7:

                        if (!carrito.estaVacio()) {

                            //Guarda snapshot para deshacer y guarda el carrito
                            carritoManager.snapshotCarrito(currentUser.getId(), carrito);
                            carrito.vaciarCarrito();
                            carritoManager.guardarCarrito(currentUser.getId(), carrito);
                            System.out.println("Carrito Eliminado!");
                        } else {
                            System.out.println("El carrito ya está vacío.");
                        }

                        break;

                    case 8:

                        if (!carrito.estaVacio()) {
                            System.out.println("Creando pedido...");
                            boolean check = pedidoManager.generarYGuardarPedido(currentUser.getId(), currentUser);
                            if(check) {
                                carrito.vaciarCarrito();
                                carritoManager.eliminarCarrito(currentUser.getId());
                                //Vacia el snapshot del carrito
                                carritoManager.eliminarSnapshots(currentUser.getId());
                                System.out.println("Pedido creado con éxito!");
                            }
                        } else {
                            System.out.println("El carrito está vacío.");
                        }
                        break;

                    case 9:

                        List<Factura> facturas = pedidoManager.listarFacturas(currentUser.getId());

                        if (!facturas.isEmpty()) {
                            boolean checkFacturasPendientes = false;
                            System.out.println("Facturas Pendientes: ");
                            for (Factura factura : facturas) {
                                if(factura.getEstado() == EstadoFactura.PENDIENTE){
                                    checkFacturasPendientes = true;
                                    System.out.println("ID: " + factura.getId() + " Total: " + factura.getTotal());
                                }
                            }

                            if (checkFacturasPendientes) {
                                System.out.println("ID del pago a realizar de las facturas a realizar, FORMATO: 1,2,3: ");
                                String idFacturas = sc.nextLine();

                                String[] ids = idFacturas.split("\\,");

                                LinkedList<Factura> facturasSelecionadas = new LinkedList<>();

                                if (ids.length != 0)
                                    for (String id : ids) {
                                        boolean check = false;
                                        for (Factura factura : facturas) {
                                            if (Objects.equals(factura.getId(), id)) {
                                                if (factura.getEstado() != EstadoFactura.PAGADA){
                                                    facturasSelecionadas.add(factura);
                                                    check = true;
                                                }

                                            }
                                        }
                                        if (check){
                                            System.out.println("Factura " + id + " Encontrada!");
                                        } else {
                                            System.out.println("Factura " + id + " No encontrada");
                                        }

                                    }
                                else {
                                    System.out.println("Usuario no tiene facturas generadas");
                                }

                                if (!facturasSelecionadas.isEmpty()) {
                                    System.out.println("Facturas a pagar: ");
                                    facturasSelecionadas.forEach(f -> System.out.print(f.getId() + " "));

                                    int medioPago = 0;
                                    MedioPago medio = null;
                                    while (!List.of(1, 2, 3, 4, 5).contains(medioPago)) {
                                        System.out.println("Elija el medio de pago: ");
                                        System.out.println("1. Efectivo");
                                        System.out.println("2. Transferencia");
                                        System.out.println("3. Tarjeta");
                                        System.out.println("4. En punto de retiro");
                                        System.out.println("5. Cuenta corriente");
                                        medioPago = sc.nextInt();
                                        sc.nextLine(); //Limpia el buffer
                                        medio = switch (medioPago) {
                                            case 1 -> MedioPago.EFECTIVO;
                                            case 2 -> MedioPago.TRANSFERENCIA;
                                            case 3 -> MedioPago.TARJETA;
                                            case 4 -> MedioPago.EN_PUNTO_RETIRO;
                                            case 5 -> MedioPago.CTA_CTE;
                                            default -> null;
                                        };
                                    }
                                    double monto = 0;

                                    for (Factura facturasSelecionada : facturasSelecionadas) {
                                        monto = monto + facturasSelecionada.getTotal();
                                    }

                                    System.out.println("Nombre del operador: ");
                                    String operador = sc.nextLine();

                                    pedidoManager.registrarPago(facturasSelecionadas, medio, operador, currentUser);
                                    System.out.println("Factura pagada con éxito!");
                                } else {
                                    System.out.println("Ningun ID de factura valido fue aportado.");
                                }
                            } else {
                                System.out.println("Usuario no debe facturas pendientes.");
                            }


                        } else {
                            System.out.println("El usuario no tiene facturas generadas.");
                        }

                        break;

                    case 10:

                        List<Factura> facturasListar = pedidoManager.listarFacturas(currentUser.getId());

                        if (!facturasListar.isEmpty()) {
                            System.out.println("_________________________________");
                            System.out.println("FACTURAS DE: " + currentUser.getNombre());
                            System.out.println("DNI: " +  currentUser.getDni());

                            for (Factura f : facturasListar) {
                                System.out.println("---------------------");
                                System.out.println("ID FACTURA: " + f.getId());
                                System.out.println("Fecha: " + f.getPedido().getFecha());

                                //muestra segun el tipo de condicion de iva que tenga una diferente factura
                                if (currentUser.getCondicionIVA().equals("Responsable inscripto")) {
                                    System.out.println("Subtotal: $" + f.getSubtotal());
                                    System.out.println("IVA: $" + f.getImpuestos());
                                    System.out.println("Total: $" + f.getTotal());
                                } else if (currentUser.getCondicionIVA().equals("Consumidor final") ||
                                        currentUser.getCondicionIVA().equals("Monotributista")) {
                                    System.out.println("Total: $" + f.getTotal() + " (IVA incluido)");
                                } else if (currentUser.getCondicionIVA().equals("Exento")) {
                                    System.out.println("Total: $" + f.getTotal() + " (Exento de IVA)");
                                }
                                System.out.println("Estado: " + f.getEstado());
                                System.out.println("ID Pedido origen: " + f.getPedido().getIdPedido());
                            }
                        } else{
                            System.out.println("El usuario con ID " + currentUser.getId() + " no tiene facturas.");
                        }

                        break;

                    case 11:

                        List<Pago> pagosListar = pedidoManager.listarPagos(currentUser.getId());

                        if (!pagosListar.isEmpty()) {
                             System.out.println("__________________________________");
                             System.out.println("PAGOS DE: " + currentUser.getNombre());
                             System.out.println("DNI: " + currentUser.getDni());

                             for (Pago pago : pagosListar) {
                                 System.out.println("---------------------");
                                 System.out.println("ID PAGO: " + pago.getId());
                                 System.out.println("Fecha: " + pago.getFecha());
                                 System.out.println("Monto: $" + pago.getMonto());
                                 System.out.println("Medio: " + pago.getMedioPago());
                                 System.out.println("Operador: " + (pago.getOperador() != null ? pago.getOperador() : "N/A"));
                                 System.out.print("Facturas asociadas (IDs): ");
                                 pago.getFacturasAplicadas().forEach(f -> System.out.print(f.getId() + " "));
                                 System.out.println();
                             }
                         } else{
                             System.out.println("El usuario con ID " + currentUser.getId() + " no tiene pagos registrados.");
                         }

                        break;

                    case 12:

                        System.out.println("Cerrando sesion...");

                        //Cierra la ultima sesion y la persiste
                        List<Sesion> sesiones = currentUser.getSesiones();
                        if (sesiones != null && !sesiones.isEmpty()) {
                            Sesion ultima = sesiones.get(sesiones.size() - 1);
                            if (ultima.getFin() == null) {
                                ultima.cerrarSesion();
                            }
                        }

                        currentUser.actualizarCategoria();

                        usrEntityManager.getTransaction().begin();
                        usrEntityManager.persist(currentUser);
                        usrEntityManager.getTransaction().commit();

                        break;
                }
            }

            usrEntityManager.close();
            usrManagerFactory.close();
            MongoManager.close();
        }
    }
}