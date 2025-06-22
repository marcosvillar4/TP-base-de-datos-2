package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import org.example.clases.Carrito.Carrito;
import org.example.clases.Carrito.CarritoManager;
import org.example.clases.Enums.MedioPago;
import org.example.clases.Mongo.MongoManager;
import org.example.clases.Mongo.ProductoCatalogoDAO;
import org.example.clases.Mongo.ProductoCatalogoService;
import org.example.clases.Pedido.PedidoManager;
import org.example.clases.Producto.Producto;
import org.example.clases.Usuario.*;

import java.time.LocalDateTime;
import java.util.*;

import org.bson.Document;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int opcion = 0;

        EntityManagerFactory usrManagerFactory =
                Persistence.createEntityManagerFactory(
                        "objectdb/db/users.odb");
        EntityManager usrEntityManager = usrManagerFactory.createEntityManager();

        Usuario currentUser = null;

        CarritoManager carritoManager = new CarritoManager();
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
            List<Usuario> usuarios = new LinkedList<>();

            opcion = Integer.parseInt(sc.nextLine());


            switch (opcion) {
                case 1:

                    System.out.println("Nombre de Usuario: ");
                    usr = sc.next();
                    System.out.println("Contraseña: ");
                    passwd = sc.next();

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
                        currentUser.getSesiones().add(new Sesion(LocalDateTime.now(), currentUser.getSesiones().size()));

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

                    TypedQuery<Usuario> query = usrEntityManager.createQuery("SELECT u FROM Usuario u", Usuario.class);

                    usuarios = query.getResultList();

                    boolean existe = false;

                    for (Usuario u : usuarios) {
                        if (u.getNombre().equals(usr)) {
                            existe = true;
                        }
                    }

                    if (existe) {
                        System.out.println("Nombre del usuario existe");
                    } else {
                        usrEntityManager.getTransaction().begin();
                        Usuario u = new Usuario(usuarios.size()+1, dni, passwd, usr, direccion);

                        currentUser = u;

                        System.out.println("Logeado como: " + currentUser.getNombre() + ", Bienvenido!");
                        if (currentUser.getSesiones() == null){
                            currentUser.setSesiones(new LinkedList<>());
                        }
                        currentUser.getSesiones().add(new Sesion(LocalDateTime.now(), currentUser.getSesiones().size()));

                        usrEntityManager.persist(u);
                        usrEntityManager.getTransaction().commit();

                        usrEntityManager.close();
                        usrManagerFactory.close();


                    }


                    break;

                case 3:

                    usrEntityManager.close();
                    usrManagerFactory.close();

                    break;



            }
        };

        if (Objects.equals(currentUser.getNombre(), "admin")){

            while (opcion != 8) {
                System.out.println("Elija una opción:");
                System.out.println("1. Ver datos de usuario");
                System.out.println("2. Agregar producto");
                System.out.println("3. Eliminar producto");
                System.out.println("4. Editar producto");
                System.out.println("5. Ver historial de cambios de productos");
                System.out.println("6. Ver facturas de un usuario");
                System.out.println("7. Ver catálogo de productos");
                System.out.println("8. Cerrar sesión");

                opcion = Integer.parseInt(sc.nextLine());

                switch (opcion){
                    case 1:

                        System.out.println("Ingrese el id del usuario:");

                        int idUsuarioBuscar = sc.nextInt();

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
                        System.out.println("Cantidad del Producto: ");
                        int cantidad = sc.nextInt();

                        Producto producto = new Producto(nombre, descripcion, precio, cantidad);
                        productoCatalogoDAO.insertarProducto(producto);



                        break;

                    case 3:

                        System.out.println("Ingresa el ID del producto a eliminar:");
                        String idProductoEliminar  = sc.nextLine();
                        productoCatalogoDAO.eliminarProducto(idProductoEliminar);
                        break;

                    case 4:

                        System.out.println("Ingresa el ID del producto a editar:");

                        String idProductoEditar =  sc.nextLine();

                        if (productoCatalogoDAO.encontrarProducto(idProductoEditar)){
                            System.out.println("Elige el campo a editar:");
                            System.out.println("1. Editar el nombre");
                            System.out.println("2. Editar la descripción");
                            System.out.println("3. Editar el precio");
                            System.out.println("4. Editar la foto/video");
                            System.out.println("5. Editar el comentario");

                            int opcionEditar = sc.nextInt();

                            switch (opcionEditar){
                                case 1:
                                    System.out.println("Ingrese el nuevo nombre del producto:");
                                    String nuevoNombre = sc.nextLine();
                                    productoCatalogoService.actualizarNombre(idProductoEditar, nuevoNombre, currentUser.getNombre());
                                    break;

                                case 2:
                                    System.out.println("Ingrese la nueva descripcion del producto:");
                                    String nuevaDescripcion = sc.nextLine();
                                    productoCatalogoService.actualizarDescripcion(idProductoEditar, nuevaDescripcion, currentUser.getNombre());
                                    break;

                                case 3:
                                    System.out.println("Ingrese el nuevo precio del producto:");
                                    double nuevoPrecio = sc.nextDouble();
                                    productoCatalogoService.actualizarPrecio(idProductoEditar, nuevoPrecio, currentUser.getNombre());
                                    break;

                                case 4:
                                    System.out.println("Ingrese el URL de la nueva foto/video del producto:");
                                    String nuevoURL = sc.nextLine();
                                    productoCatalogoService.actualizarFoto(idProductoEditar, nuevoURL, currentUser.getNombre());
                                    break;

                                case 5:
                                    System.out.println("Ingrese el nuevo comentario del producto:");
                                    String nuevoComentario = sc.nextLine();
                                    productoCatalogoService.actualizarComentarios(idProductoEditar, nuevoComentario, currentUser.getNombre());
                                    break;

                                default:
                                    System.out.println("El número ingresado no es válido.");
                                    break;
                            }
                        }


                    case 6:
                        System.out.println("Ingrese el ID del usuario:");

                        int idUsuarioFacturas = sc.nextInt();

                        TypedQuery<Usuario> usrFindQuery2 = usrEntityManager.createQuery("SELECT u FROM Usuario u WHERE u.id =:idUsuario", Usuario.class);
                        List<Usuario> usuarioFactura = usrFindQuery2.setParameter("idUsuario",idUsuarioFacturas).getResultList();

                        if (!usuarioFactura.isEmpty()){
                            pedidoManager.listarFacturas(idUsuarioFacturas);
                        } else {
                            System.out.println("ID del usuario no encontrado.");
                        }

                        break;

                    case 8:

                        System.out.println("Cerrando sesión...");

                        currentUser.actualizarCategoria();

                        break;
                }
            }
        } else {

            int opcion2 = 0;

            Carrito carrito = carritoManager.obtenerCarrito(currentUser.getId());

            while (opcion2 != 6){


                System.out.println("Elija una opcion:");
                System.out.println("__________________");
                System.out.println("1. Ver catálogo de productos");
                System.out.println("2. Agregar item al carrito");
                System.out.println("3. Eliminar item del carrito");
                System.out.println("4. Ver carrito");
                System.out.println("5. Confirmar y pagar pedido");
                System.out.println("6. Salir");

                opcion2 = sc.nextInt();

                switch (opcion2){
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

                        if (productoCatalogoDAO.encontrarProducto(idProductoAgregar)){
                            if (Integer.parseInt(productoCatalogoDAO.getProductoById(idProductoAgregar).get("cantidad").toString()) > cantidadAgregar){
                                carrito.agregarItem(idProductoAgregar, cantidadAgregar);
                            } else {
                                System.out.println("Imposible agregar mas productos que stock disponible");
                            }
                        } else {
                            System.out.println("Producto no encontrado");
                        }
                        break;
                    case 3:

                        System.out.println("ID del producto a eliminar: ");
                        String idProductoEliminar = sc.nextLine();
                        System.out.println("Cantidad: ");
                        int cantidadEliminar = sc.nextInt();

                        if (carrito.getCarrito().containsKey(idProductoEliminar)){
                            if (carrito.getCarrito().get(idProductoEliminar) >= cantidadEliminar){
                                carrito.eliminarItem(idProductoEliminar, cantidadEliminar);
                            } else {
                                System.out.println("Imposible eliminar mas productos de los que hay en el carrito");
                            }
                        } else {
                            System.out.println("Producto no encontrado en el carrito");
                        }


                    case 5:
                        int medioPago = 0;
                        MedioPago medio = null;
                        while(!List.of(1,2,3,4,5).contains(medioPago)) {
                            System.out.println("Elija el medio de pago: ");
                            System.out.println("1. Efectivo");
                            System.out.println("2. Transferencia");
                            System.out.println("3. Tarjeta");
                            System.out.println("4. En punto de retiro");
                            System.out.println("5. Cuenta corriente");
                            medioPago = sc.nextInt();

                            medio = switch (medioPago) {
                                case 1 -> MedioPago.EFECTIVO;
                                case 2 -> MedioPago.TRANSFERENCIA;
                                case 3 -> MedioPago.TARJETA;
                                case 4 -> MedioPago.EN_PUNTO_RETIRO;
                                case 5 -> MedioPago.CTA_CTE;
                                default -> null;
                            };

                        }
                        System.out.println("Ingrese el nombre del operador: ");
                        sc.nextLine();
                        String operador = sc.nextLine();

                        pedidoManager.cerrarPedidoYRegistrarPago(currentUser.getId(), currentUser, medio, operador);
                    case 6:

                        System.out.println("Cerrando sesion...");
                        currentUser.actualizarCategoria();
                        break;
                }
            }

        }








    }
}