package ecs.Components;

public class Countdown extends Component {
    public double timeRemaining;

    public Countdown(double howLong) {
        timeRemaining = howLong;
    }
}
