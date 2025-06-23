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
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;



public class Main {
    public static void main(String[] args) {



        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE);

        Scanner sc = new Scanner(System.in);

        int opcion = 0;

        EntityManagerFactory usrManagerFactory =
                Persistence.createEntityManagerFactory(
                        "objectdb/db/users.odb");
        EntityManager usrEntityManager = usrManagerFactory.createEntityManager();



        Usuario currentUser = null;

        CarritoManager carritoManager = new CarritoManager("192.168.1.25", 6379);
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
                    usr = sc.next();
                    sc.nextLine(); //Limpia el buffer
                    System.out.println("Contraseña: ");
                    passwd = sc.next();
                    sc.nextLine(); //Limpia el buffer

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
                        Sesion sesion = new Sesion(LocalDateTime.now(), currentUser.getSesiones().size());
                        currentUser.getSesiones().add(sesion);


                        //NUEVO: Guardar (mergear) los cambios de sesiones del usuario en la base de datos

                        usrEntityManager.persist(sesion);
                        usrEntityManager.persist(currentUser);
                        usrEntityManager.getTransaction().commit();
                    }
                    break;



                case 2:

                    System.out.println("Ingresa el nombre del nuevo usuario" + System.lineSeparator());

                    usr = sc.nextLine();
                    System.out.println(usr);
                    System.out.println("Ingresa la contraseña del nuevo usuario");
                    passwd = sc.nextLine();
                    System.out.println("Ingresa el DNI del nuevo usuario");
                    int dni = Integer.parseInt(sc.nextLine());
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
                        case 4 -> "Exento";
                        default -> null;
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
                        currentUser.getSesiones().add(new Sesion(LocalDateTime.now(), currentUser.getSesiones().size()));

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
                System.out.println("2. Agregar producto"); //Falta
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
                        List<Usuario> usuarioBuscar = usrFindQuery.setParameter("idUsuario",idUsuarioBuscar).getResultList();

                        if (!usuarioBuscar.isEmpty()){
                            for (Usuario usuario : usuarioBuscar) {
                                System.out.println("ID del usuario: " + usuario.getId() + ".");
                                System.out.println("Nombre del usuario: " + usuario.getNombre() + ".");
                                System.out.println("Clave del usuario: " + usuario.getPasswd() + ".");
                                System.out.println("DNI del usuario: " + usuario.getDni() + ".");
                                System.out.println("Dirección del usuario: " + usuario.getDireccion() + ".");
                                System.out.println("Tipo de usuario: " + usuario.getCategoria() + ".");
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



                        break;

                    case 3:

                        System.out.println("Ingresa el ID del producto a eliminar:");
                        String idProductoEliminar = sc.nextLine();
                        productoCatalogoDAO.eliminarProducto(idProductoEliminar);
                        break;

                    case 4:

                        System.out.println("Ingresa el ID del producto a editar:");

                        String idProductoEditar =  sc.nextLine();

                        if (productoCatalogoDAO.existeProducto(idProductoEditar)){
                            System.out.println("Elige el campo a editar:");
                            System.out.println("1. Editar el nombre");
                            System.out.println("2. Editar la descripción");
                            System.out.println("3. Editar el precio");
                            System.out.println("4. Editar la foto/video");
                            System.out.println("5. Editar el comentario");

                            int opcionEditar = sc.nextInt();
                            sc.nextLine(); //Limpia el buffer
                            String nombreOperador;

                            switch (opcionEditar){
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

                                default:
                                    System.out.println("El número ingresado no es válido.");
                                    break;
                            }
                        } else {
                            System.out.println("El ID del producto no existe.");
                        }

                        break;

                    case 5:

                        System.out.println("Ingrese el id del producto:");
                        String idProducto = sc.nextLine();

                        List<Document> historial = productoCatalogoService.getHistorialCambios(idProducto);

                        for(Document cambio : historial){
                            System.out.println("_____________________________________");
                            System.out.println("Fecha: " + cambio.getDate("fecha"));
                            System.out.println("Operador: " + cambio.getString("operador"));
                            System.out.println("Campo modificado: " + cambio.getString("campo"));
                            System.out.println("Valor anterior: " +  cambio.getDouble("valorAnterior"));
                            System.out.println("Valor final: " +  cambio.getDouble("valorNuevo"));
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
                                    System.out.println("Subtotal: $" + f.getSubtotal());
                                    System.out.println("Impuestos: $" + f.getImpuestos());
                                    System.out.println("Total: $" + f.getTotal());
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
                        List<Usuario> usuarioPagos = PagosFindQuery.setParameter("idUsuario",idUsuarioPagos).getResultList();

                        if (!usuarioPagos.isEmpty()){
                            pedidoManager.listarPagos(idUsuarioPagos);
                        } else {
                            System.out.println("ID del usuario no encontrado.");
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
                        }
                        break;

                    case 9:

                        System.out.println("Cerrando sesión...");

                        currentUser.actualizarCategoria();

                        //NUEVO: Guardar (mergear) los cambios de sesiones del usuario en la base de datos
                        //Cierra la ultima sesion y la persiste
                        List<Sesion> sesiones = currentUser.getSesiones();
                        if (sesiones != null && !sesiones.isEmpty()) {
                            Sesion ultima = sesiones.get(sesiones.size() - 1);
                            if (ultima.getFin() == null) {
                                ultima.cerrarSesion();
                            }
                        }
                        usrEntityManager.getTransaction().begin();
                        usrEntityManager.merge(currentUser);
                        usrEntityManager.getTransaction().commit();
                        break;
                }
            }
        }
        else {

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
                System.out.println("12. Salir");

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
                        }
                        break;

                    case 2:

                        System.out.println("ID del producto a agregar: ");
                        String idProductoAgregar = sc.nextLine();
                        System.out.println("Cantidad: ");
                        int cantidadAgregar = sc.nextInt();
                        sc.nextLine(); //Limpia el buffer
                        if (productoCatalogoDAO.existeProducto(idProductoAgregar)) {
                            if (Integer.parseInt(productoCatalogoDAO.getProductoById(idProductoAgregar).get("cantidad").toString()) >= cantidadAgregar) {
                                carrito.agregarItem(idProductoAgregar, cantidadAgregar);

                                //Guarda un snapshot del carrito para deshacer y despues lo guarda
                                carritoManager.snapshotCarrito(currentUser.getId(), carrito);
                                carritoManager.guardarCarrito(currentUser.getId(), carrito);
                            } else {
                                System.out.println("No hay suficiente stock disponible");
                            }
                        } else {
                            System.out.println("Producto no encontrado");
                        }
                        break;

                    case 3:

                        if (!carrito.estaVacio()) {
                            System.out.println("ID del producto a eliminar: ");
                            String idProductoEliminar = sc.nextLine();

                            if (carrito.getCarrito().containsKey(idProductoEliminar)) {
                                System.out.println("Cantidad: ");
                                int cantidadEliminar = sc.nextInt();
                                sc.nextLine(); //Limpia el buffer
                                if (carrito.getCarrito().get(idProductoEliminar) >= cantidadEliminar) {
                                    carrito.eliminarItem(idProductoEliminar, cantidadEliminar);

                                    //Guarda un snapshot del carrito para deshacer y despues lo guarda
                                    carritoManager.snapshotCarrito(currentUser.getId(), carrito);
                                    carritoManager.guardarCarrito(currentUser.getId(), carrito);
                                } else {
                                    System.out.println("Imposible eliminar mas productos de los que hay en el carrito");
                                }
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
                                System.out.println("Cantidad (ingrese 0 si quiere eliminarlo): ");
                                int cantidadEditar = sc.nextInt();
                                sc.nextLine(); //Limpia el buffer
                                if (carrito.getCarrito().get(idProductoCantidad) >= cantidadEditar && cantidadEditar >= 0) {
                                    carrito.modificarCantidad(idProductoCantidad, cantidadEditar);

                                    //Guarda snapshot para deshacer y guarda el carrito
                                    carritoManager.snapshotCarrito(currentUser.getId(), carrito);
                                    carritoManager.guardarCarrito(currentUser.getId(), carrito);
                                } else {
                                    if (cantidadEditar < 0) {
                                        System.out.println("Ingrese un valor correcto.");
                                    } else {
                                        System.out.println("Imposible agregar mas productos de los que hay en el carrito.");
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
                            carrito.vaciarCarrito();

                            //Guarda snapshot para deshacer y guarda el carrito
                            carritoManager.snapshotCarrito(currentUser.getId(), carrito);
                            carritoManager.guardarCarrito(currentUser.getId(), carrito);
                            System.out.println("Carrito Eliminado!");
                        } else {
                            System.out.println("El carrito ya está vacío.");
                        }

                        break;

                    case 8:

                        if (!carrito.estaVacio()) {
                            System.out.println("Creando pedido...");
                            pedidoManager.generarYGuardarPedido(currentUser.getId(), currentUser);
                            carrito.vaciarCarrito();
                            carritoManager.eliminarCarrito(currentUser.getId());
                            //Vacia el buffer del snapshot del carrito
                            carritoManager.snapshotCarrito(currentUser.getId(), null);
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
                                    System.out.println("Facturas a pagar: " + facturasSelecionadas.toString().replace('[', ' ').replace(']', ' '));

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
                                            case 1 -> medio = MedioPago.EFECTIVO;
                                            case 2 -> medio = MedioPago.TRANSFERENCIA;
                                            case 3 -> medio = MedioPago.TARJETA;
                                            case 4 -> medio = MedioPago.EN_PUNTO_RETIRO;
                                            case 5 -> medio = MedioPago.CTA_CTE;
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
                                } else {
                                    System.out.println("Ningun ID de factura valido fue aportado");
                                }
                            } else {
                                System.out.println("Usuario no debe facturas pendientes");
                            }


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
                                System.out.println("Subtotal: $" + f.getSubtotal());
                                System.out.println("Impuestos: $" + f.getImpuestos());
                                System.out.println("Total: $" + f.getTotal());
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
                        currentUser.actualizarCategoria();

                        //NUEVO: Guardar (mergear) los cambios de sesiones del usuario en la base de datos
                        //Cierra la ultima sesion y la persiste
                        List<Sesion> sesiones = currentUser.getSesiones();
                        if (sesiones != null && !sesiones.isEmpty()) {
                            Sesion ultima = sesiones.get(sesiones.size() - 1);
                            if (ultima.getFin() == null) {
                                ultima.cerrarSesion();
                            }
                        }
                        usrEntityManager.getTransaction().begin();
                        usrEntityManager.persist(currentUser);
                        usrEntityManager.getTransaction().commit();
                        break;
                }
            }
            usrEntityManager.close();
            usrManagerFactory.close();
        }
    }
}