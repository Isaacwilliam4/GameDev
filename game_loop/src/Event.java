public class Event {
    public Event(String name, double interval, int times){
        this.name = name;
        this.interval = interval;
        this.times = times;
    }

    public String name;
    public double interval;
    public int times;
    public double currInterval;
}
