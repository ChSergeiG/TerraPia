package ru.chsergeyg.terrapia.server;

public class Server {

    public static void main(String[] args) {
<<<<<<< HEAD
        new Init();
        BufferedReader brd = new BufferedReader(new InputStreamReader(System.in));
        try {
            while (!brd.readLine().equals("quit")) {
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (Exception e) {
            Init.getLogger(Server.class.getName()).warning(e.toString());
        }
        Init.threads.forEach(Thread::interrupt);
=======
         new Init();
>>>>>>> d706eca... auth^
    }
}

