package ru.chsergeyg.terrapia.server;

import fi.iki.elonen.NanoHTTPD;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
public class HTTPDThread extends NanoHTTPD {

    private static final Logger LOGGER = Logger.getLogger(HTTPDThread.class.getName());

    public HTTPDThread(int port) {
        super(8080);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Method method = session.getMethod();
        String uri = session.getUri();

        StringBuilder bld = new StringBuilder();

        bld.append("<html>\n");
        bld.append(" <head></head>\n");
        bld.append(" <body>\n");
        bld.append("  <h1>HW!</h1>\n");
        Map<String, List<String>> parameters = session.getParameters();

        if (parameters.get("name") == null) {
            bld.append("  <form action='?' method='get'>\n");
            bld.append("   <p>Your name: <input type='text' name='username'></p>\n");
            bld.append("  </form>\n");
        } else {
            bld.append("  <p>Hello, ").append(parameters.get("username")).append("!</p>\n");
        }

        bld.append(" </body>");
        bld.append("</html>");
        return newFixedLengthResponse(bld.toString());
    }

}
