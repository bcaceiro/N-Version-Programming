package client.Server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Created by jorl17 on 15/05/15.
 */
public class Server extends Thread{
    @Override
    public void run() {
        this.setName("Jetty Serverthread");

        ResourceHandler resourceHandler= new ResourceHandler();
        resourceHandler.setResourceBase(".");


        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/REST");

        org.eclipse.jetty.server.Server jettyServer = new org.eclipse.jetty.server.Server(8080);
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resourceHandler, context });

        // Add the handlers to the server and start jetty.
        jettyServer.setHandler(handlers);

        context.addServlet(new ServletHolder(new MealtimeInsulinDoseRest()),"/MealtimeInsulinDose");

        try {
            try {
                jettyServer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                jettyServer.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } finally {
            jettyServer.destroy();
        }
    }

    public static void main(String[] args) {
        new Server().start();
    }
}
