import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

public class MyGame {
    private HashMap<String, Event> events = new HashMap<>();
    private long startTime = new Date().getTime();

    public void initialize() {
        System.out.println("GameLoop Demo Initializing...");
    }

    public void run() throws IOException {
        System.out.print("[cmd:] ");
        while (true) {
            try{
                int availableInput = System.in.available();
                if (availableInput > 0){
                    processInput(availableInput);
                }

                long currentTime = new Date().getTime();
                long elapsedTime = currentTime - startTime;
                startTime = currentTime;
                update(elapsedTime);
            }
            catch(IOException e){
                throw new IOException(e.getMessage());
            }
        }
    }

    public void processInput(int availableInput) throws IOException {
        System.out.print("[cmd:] ");
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < availableInput; i++){
            char nextInput = (char) System.in.read();
            string.append(nextInput);
        }

        String cleanString = string.toString().replaceAll("\n","");
        String[] args = cleanString.split(" ");

        if (args[0].equals("quit")){
            System.exit(0);
        }

        if (args[0].equals("create") && args[1].equals("event")){
            try{
                String name = args[2];
                double interval = Double.parseDouble(args[3]);
                int times = Integer.parseInt(args[4]);
                Event event = new Event(name, interval, times);
                events.put(name, event);
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
        else{
            System.out.println("Invalid command");
        }
    }

    public void update(long elapsedTime){
        for(String key: events.keySet()){
            Event e = events.get(key);
            double newTime = e.currInterval - elapsedTime;
            if(newTime > 0){
                e.currInterval = newTime;
            }
            else{
                e.times--;
                render(e);
                e.currInterval = e.interval + newTime;
            }

            if (e.times <= 0){
                events.remove(key);
            }
        }
    }

    public void render(Event e){
        System.out.printf("Event: %s, (%d Remaining)", e.name, e.times);
    }


}
