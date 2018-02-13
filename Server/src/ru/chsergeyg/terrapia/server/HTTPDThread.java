package ru.chsergeyg.terrapia.server;

import fi.iki.elonen.NanoHTTPD;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.logging.Logger;

public class HTTPDThread extends NanoHTTPD {

    private static final Logger LOGGER = Logger.getLogger(HTTPDThread.class.getName());
    private static final String LOGIN = "root";
    private static final String PASS_HASH = "982f249c728f730cbe5ddd7b02a3c3e660fe8f860e5c501648e17ab2b0e77472";

    public HTTPDThread() {
        super(8080);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Method method = session.getMethod();
        String uri = session.getUri();
        LOGGER.info(method.name() + " '" + uri + "'");
        StringBuilder bld = new StringBuilder();
        bld.append("<html>\n <head></head>\n <body>\n  <h1>TerraPia</h1>\n");
        Map<String, String> parameters = session.getParms();
        String login = parameters.get("login");
        String password = parameters.get("password");
        if (password == null || login == null) {
            sessionIsUnauthorized(parameters, bld);
        } else if (!login.trim().toLowerCase().equals(LOGIN.toLowerCase())) {
            sessionIsUnauthorized(parameters, bld);
        } else if (!getHash(password).equals(PASS_HASH)) {
            sessionIsUnauthorized(parameters, bld);
        } else {
            sessionIsAuthorized(parameters, bld);
        }
        bld.append(" </body>");
        bld.append("</html>");
        return newFixedLengthResponse(bld.toString());
    }

    private String getHash(String value) {
        byte[] hash = new byte[0];
        try {
            hash = MessageDigest.getInstance("SHA-256").digest(value.getBytes());
        } catch (NoSuchAlgorithmException e) {
            LOGGER.warning(e.toString());
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : hash) {
            stringBuilder.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
        }
        return stringBuilder.toString();
    }

    private void sessionIsUnauthorized(Map<String, String> parameters, StringBuilder bld) {
        bld.append("  <form action='?' method='get'>\n");
        bld.append("   <p>Enter login: \t<input type='text' name='login'></p>\n");
        bld.append("   <p>Enter pass: \t<input type='password' name='password'></p>\n");
        bld.append("   <button type='submit'>Login</button>\n");
        bld.append("  </form>\n");
    }

    private void sessionIsAuthorized(Map<String, String> parameters, StringBuilder bld) {
        bld.
                append("  <code>").
                append(SerialThread.getStateString()).
                append("</code>\n");
    }


}
