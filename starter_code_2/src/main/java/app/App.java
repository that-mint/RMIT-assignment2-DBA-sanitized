package app;
 
import io.javalin.Javalin;
import io.javalin.core.util.RouteOverviewPlugin;

/**
 * Main Application Class.
 * <p>
 * Running this class as regular java application will start the Javalin HTTP
 * Server and our web application.
 *
 * @author Timothy Wiley, 2021. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 * @author Halil Ali, 2021. email halil.ali@rmit.edu.au
 */
public class App {

   public static final int JAVALIN_PORT = 7000;
   public static final String CSS_DIR = "css/";
   public static final String IMAGES_DIR = "images/";
   public static final String JS_DIR = "js/";



   public static void main(String[] args) throws Exception {

      // Establish database connection
      if (MongoDBConnection.getConnection() == null) {
         throw new Exception("Could not establish connection to database");
      }

      // Create our HTTP server and listen in port 7000
      Javalin app = Javalin.create(config -> {
         config.registerPlugin(new RouteOverviewPlugin("/help/routes"));

         // Uncomment this if you have files in the CSS Directory
         config.addStaticFiles(CSS_DIR);

         config.addStaticFiles(JS_DIR);


         // Uncomment this if you have files in the Images Directory
         config.addStaticFiles(IMAGES_DIR);
      }).start(JAVALIN_PORT);

      // capture ctrl-c signal so we can shutdown server safely
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
         app.stop();
      }));

      // handle shutdown events by closing database and ssh tunnel connections
      app.events(event -> {
         event.serverStopping(() -> {
            System.out.println("server stopping");
         });
         event.serverStopped(() -> {
            System.out.println("server stopped");
            // Close Database connection
            MongoDBConnection.closeConnection();
         });
      });

      // Configure Web Routes
      configureRoutes(app);
   }

   /**
    * set up each individual page of site
    **/
   public static void configureRoutes(Javalin app) {
      // All webpages are listed here as GET pages
      app.get(Index.URL, new Index());
      app.get(Accpage.URL, new Accpage());
      app.get(Reviewers.URL, new Reviewers());
      
      app.post(Reviewers.URL, new Reviewers());
      app.post(Index.URL, new Index());
      app.post(Accpage.URL, new Accpage());

   }

}
