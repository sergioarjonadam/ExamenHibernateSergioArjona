package services;

import model.Opinion.Opinion;
import model.Pelicula.Pelicula;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

/**
 * Servicio único que implementa todas las historias de usuario.
 * Este servicio utiliza Hibernate para interactuar con la base de datos.
 */
public class DataService {

    private final SessionFactory sessionFactory;

    /**
     * Constructor de la clase DataService.
     *
     * @param sessionFactory La fábrica de sesiones de Hibernate utilizada para las operaciones de base de datos.
     */
    public DataService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Historia de Usuario 1: Registro de nuevas películas.
     *
     * Este método permite registrar una nueva película en la base de datos.
     *
     * @param titulo Título de la película. No puede ser nulo ni vacío.
     * @return La película creada.
     * @throws IllegalArgumentException Si el título es nulo o vacío.
     */
    public Pelicula registrarPelicula(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Pelicula pelicula = new Pelicula();
            pelicula.setTitulo(titulo.trim());

            session.persist(pelicula);
            session.getTransaction().commit();

            return pelicula;
        }
    }

    /**
     * Historia de Usuario 2: Obtener opiniones de un usuario específico por correo.
     *
     * Este método devuelve una lista de opiniones asociadas a un usuario específico.
     *
     * @param correo Correo electrónico del usuario. No puede ser nulo ni vacío.
     * @return Lista de opiniones del usuario.
     * @throws IllegalArgumentException Si el correo es nulo o vacío.
     */
    public List<Opinion> obtenerOpinionesPorCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            throw new IllegalArgumentException("El correo no puede estar vacío");
        }

        try (Session session = sessionFactory.openSession()) {
            Query<Opinion> query = session.createQuery(
                    "SELECT o FROM Opinion o " +
                            "LEFT JOIN FETCH o.pelicula " +
                            "WHERE o.correo = :correo",
                    Opinion.class
            );
            query.setParameter("correo", correo.trim());
            return query.list();
        }
    }

    /**
     * Historia de Usuario 3: Añadir opiniones a una película existente.
     *
     * Este método permite añadir una opinión a una película existente en la base de datos.
     *
     * @param peliculaId ID de la película. Debe ser un número positivo.
     * @param correo Correo del usuario que opina. No puede ser nulo ni vacío.
     * @param puntuacion Puntuación de la opinión (0-10). Debe estar dentro de este rango.
     * @param descripcion Texto de la opinión. Puede ser nulo.
     * @return La opinión creada.
     * @throws IllegalArgumentException Si alguno de los parámetros no es válido.
     */
    public Opinion anadirOpinion(Long peliculaId, String correo, Integer puntuacion, String descripcion) {
        if (peliculaId == null || peliculaId <= 0) {
            throw new IllegalArgumentException("El ID de película debe ser un número positivo");
        }
        if (correo == null || correo.trim().isEmpty()) {
            throw new IllegalArgumentException("El correo no puede estar vacío");
        }
        if (puntuacion == null || puntuacion < 0 || puntuacion > 10) {
            throw new IllegalArgumentException("La puntuación debe estar entre 0 y 10");
        }

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            // Verificar que la película existe
            Pelicula pelicula = session.find(Pelicula.class, peliculaId.intValue());
            if (pelicula == null) {
                session.getTransaction().rollback();
                throw new IllegalArgumentException("No existe película con ID: " + peliculaId);
            }

            // Crear la opinión
            Opinion opinion = new Opinion();
            opinion.setCorreo(correo.trim());
            opinion.setPuntuacion(puntuacion);
            opinion.setDescripcion(descripcion != null ? descripcion.trim() : "");
            opinion.setPelicula(pelicula);

            session.persist(opinion);
            session.getTransaction().commit();

            return opinion;
        }
    }

    /**
     * Historia de Usuario 4: Listar películas con baja puntuación.
     *
     * Este método devuelve una lista de títulos de películas que tienen al menos una opinión con puntuación ≤ 3.
     *
     * @return Lista de títulos de películas con puntuación ≤ 3.
     */
    public List<String> listarPeliculasBajaPuntuacion() {
        try (Session session = sessionFactory.openSession()) {
            Query<String> query = session.createQuery(
                    "SELECT DISTINCT p.titulo FROM Pelicula p " +
                            "JOIN p.opiniones o " +
                            "WHERE o.puntuacion <= 3",
                    String.class
            );
            return query.list();
        }
    }
}