package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import org.example.clases.*;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

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

            while (opcion != 7) {
                System.out.println("Elija una opción:");
                System.out.println("1. Ver datos de usuario");
                System.out.println("2. Agregar producto");
                System.out.println("3. Eliminar producto");
                System.out.println("4. Editar producto");
                System.out.println("5. Ver historial de cambios de productos");
                System.out.println("6. Ver facturas de un usuario");
                System.out.println("7. Cerrar sesión");

                opcion = sc.nextInt();

                switch (opcion){
                    case 1:

                        System.out.println("Ingrese el id del usuario:");

                        int idUsuarioBuscar = sc.nextInt();

                        TypedQuery<Usuario> usrFindQuery = usrEntityManager.createQuery("SELECT u FROM Usuario u WHERE u.id =:idUsuario", Usuario.class);
                        List<Usuario> usuarios = usrFindQuery.setParameter("idUsuario",idUsuarioBuscar).getResultList();

                        if (!usuarios.isEmpty()){
                            for (Usuario usuario : usuarios) {
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

                        break;

                    case 3:

                        System.out.println("Ingresa el ID del producto a eliminar:");

                        int idProducto = sc.nextInt();

                        break;

                    case 4:

                        System.out.println("Ingresa el ID del producto a editar:");

                        int idProductoEditar =  sc.nextInt();



                    case 6:
                        System.out.println("Ingrese el ID del usuario:");

                        int idUsuarioFacturas = sc.nextInt();

                        pedidoManager.listarFacturas(idUsuarioFacturas);

                        break;

                    case 7:

                        System.out.println("Cerrando sesión...");

                        currentUser.actualizarCategoria();

                        break;
                }
            }
        }








    }
}