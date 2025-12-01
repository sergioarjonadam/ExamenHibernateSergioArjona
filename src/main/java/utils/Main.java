package utils;

import model.Opinion.Opinion;
import model.Pelicula.Pelicula;
import org.hibernate.SessionFactory;
import services.DataService;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        SessionFactory sessionFactory = null;

        try {
            System.out.println("═══════════════════════════════════════════════════");
            System.out.println("  SISTEMA DE GESTIÓN DE PELÍCULAS Y OPINIONES");
            System.out.println("═══════════════════════════════════════════════════\n");

            // Inicializar conexión
            System.out.println("Iniciando conexión a la base de datos...");
            sessionFactory = DataProvider.getSessionFactory();
            System.out.println("Conexión establecida correctamente\n");

            // Crear el servicio
            DataService dataService = new DataService(sessionFactory);

            // HISTORIA DE USUARIO 1: Registro de nuevas películas

            Pelicula pelicula3 = dataService.registrarPelicula("Ciudadano Kane4");
            System.out.println("Película registrada: " + pelicula3.getTitulo() + " (ID: " + pelicula3.getId() + ")");

            // HISTORIA DE USUARIO 3: Añadir opiniones

            // Opiniones para "Ciudadano Kane"
            Opinion op1 = dataService.anadirOpinion(
                    pelicula3.getId().longValue(),
                    "user1@example.com",
                    9,
                    "Una obra maestra del cine. Actuaciones impecables."
            );
            System.out.println("Opinión añadida por " + op1.getCorreo() + " - Puntuación: " + op1.getPuntuacion() + "/10");

            // HISTORIA DE USUARIO 2: Obtener opiniones por correo

            String correoConsulta = "user1@example.com";
            List<Opinion> opinionesUser1 = dataService.obtenerOpinionesPorCorreo(correoConsulta);
            System.out.println("\nOpiniones de: " + correoConsulta);
            System.out.println("───────────────────────────────────────────────────");

            for (Opinion opinion : opinionesUser1) {
                System.out.println("  • Película: " + opinion.getPelicula().getTitulo());
                System.out.println("    Puntuación: " + opinion.getPuntuacion() + "/10 ⭐");
                System.out.println("    Comentario: " + opinion.getDescripcion());
                System.out.println();
            }
            System.out.println("Total: " + opinionesUser1.size() + " opinión(es)");

            // HISTORIA DE USUARIO 4: Listar películas con baja puntuación

            List<String> peliculasBajaPuntuacion = dataService.listarPeliculasBajaPuntuacion();
            System.out.println("\nPelículas con al menos una opinión con puntuación ≤ 3:");
            System.out.println("───────────────────────────────────────────────────");

            List<String> titulosConOpinionBaja = peliculasBajaPuntuacion.stream()
                    .filter(java.util.Objects::nonNull)
                    .distinct()
                    .sorted()
                    .toList();

            if (titulosConOpinionBaja.isEmpty()) {
                System.out.println("No hay películas con baja puntuación.");
            } else {
                titulosConOpinionBaja.forEach(System.out::println);
                System.out.println("\nTotal: " + titulosConOpinionBaja.size() + " película(s)");
            }

        } catch (Exception e) {
            System.err.println("\nError durante la ejecución:");
            e.printStackTrace();
        } finally {
            if (sessionFactory != null) {
                System.out.println("\n🔌 Cerrando conexión a la base de datos...");
                DataProvider.closeSessionFactory();
                System.out.println("Aplicación cerrada correctamente");
            }
        }
    }
}