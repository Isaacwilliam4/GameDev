package ecs.Entities;

public class Countdown {
    public static Entity create(double howLong) {
        var timer = new Entity();

        timer.add(new ecs.Components.Countdown(howLong));

        return timer;
    }
}
