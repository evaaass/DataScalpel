package cn.superhuang.data.scalpel.admin.spark.res;


import org.eclipse.jetty.server.Server;

public class Starter {
    public static final String BASE_URI = "http://localhost:8081/";

    public static Server startServer() {

        // scan packages
//        final ResourceConfig config = new ResourceConfig().packages("cn.superhuang.data.scalpel.admin.spark");
//        final ResourceConfig config = new ResourceConfig(MyResource.class);
//        final Server server = JettyHttpContainerFactory.createServer(URI.create(BASE_URI), config);
//        Server server = new Server(8081);
//        ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
//
//        ctx.setContextPath("/");
//        server.setHandler(ctx);
//
//        ServletHolder serHol = ctx.addServlet(ServletContainer.class, "/*");
//        serHol.setInitOrder(1);
//        serHol.setInitParameter("jersey.config.server.provider.packages", "cn.superhuang.data.scalpel.admin.spark.res");

//        final ResourceConfig config = new ResourceConfig(MyResource.class);

        Server server = new Server(8081);
        return server;

    }

    public static void main(String[] args) throws Exception {
        try {

            final Server server = startServer();
            server.start();
            server.join();
//            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//                try {
//                    System.out.println("Shutting down the application...");
//                    server.stop();
//                    System.out.println("Done, exit.");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }));
//
//            System.out.println(String.format("Application started.%nStop the application using CTRL+C"));
//
//            // block and wait shut down signal, like CTRL+C
//            Thread.currentThread().join();
//
//            // alternative
//            // Thread.sleep(Long.MAX_VALUE);       // sleep forever...
//            // Thread.sleep(Integer.MAX_VALUE);    // sleep around 60+ years

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
