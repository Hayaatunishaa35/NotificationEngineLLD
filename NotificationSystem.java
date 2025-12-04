import java.time.LocalDateTime;
import java.util.*;

// Notification and Decorators - Decorator design pattern

// INotification--SimpleNotification
// INotification--INotificationDecorator

// INotificationDecorator--TimeStampDecorator
// INotificationDecorator--SignatureDecorator

interface INotification{
    String getContent();
}

class SimpleNotification implements INotification{
    private String text;

    public SimpleNotification(String text){
        this.text = text;
    }
    
    @Override
    public String getContent(){
        return this.text;
    }
}

abstract class INotificationDecorator implements INotification{
    INotification notification;

    public INotificationDecorator(INotification notification){
        this.notification = notification;
    }
}

class TimeStampDecorator extends INotificationDecorator{
    public TimeStampDecorator(INotification notification){
        super(notification);
    } 

    @Override
    public String getContent(){
        String time = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return time + " " + notification.getContent();
    }
}

class SignatureDecorator extends INotificationDecorator{
    public SignatureDecorator(INotification notification){
        super(notification);
    } 

    @Override
    public String getContent(){
        return "Sample signature " + notification.getContent();
    }
}


// IObservable, IObserver - Observer Design pattern 
// IObservable--NotificationObservable
// Iobserver--Logger, NotificationEngine

interface IObservable{
    void addObserver(IObserver observer);
    void removeObserver(IObserver observer);
    void notifyObserver();
}

interface IObserver{
    void update();
}

class NotificationObservable implements IObservable{

    private List<IObserver> observers = new ArrayList<>();
    private INotification currentNotification;

    @Override
    public void addObserver(IObserver observer){
        observers.add(observer);
    }

    @Override
    public void removeObserver(IObserver observer){
        observers.remove(observer);
    }

    @Override
    public void notifyObserver(){
        for(IObserver observer:observers){
            observer.update();
        }
    }

    public void setNotification(INotification notification){
        currentNotification = notification;
        notifyObserver();
    }

    public INotification getNotification(){
        return currentNotification;
    }

    public String getNotificationContent(){
        return currentNotification.getContent();
    }
}

class Logger implements IObserver{
    private NotificationObservable notificationObservable;

    public Logger(NotificationObservable notificationObservable){
        this.notificationObservable = notificationObservable;
    }

    @Override
    public void update(){
        System.out.println("New notification: "+notificationObservable.getNotificationContent());
    }
}


class NotificationEngine implements IObserver{
    private NotificationObservable notificationObservable;
    private List<INotificationStrategy> notificationStrategies = new ArrayList<>();

    public NotificationEngine(NotificationObservable notificationObservable){
        this.notificationObservable = notificationObservable;
    }

    public void addNotificationStrategy(INotificationStrategy notificationStrategy){
        notificationStrategies.add(notificationStrategy);
    }

    @Override
    public void update(){
        String content = notificationObservable.getNotificationContent();

        for(INotificationStrategy notificationStrategy: notificationStrategies){
            notificationStrategy.sendNotification(content);
        }
    }
}


// Notification Strategies -- Strategy design pattern
// Email, sms, popup

interface INotificationStrategy{
    void sendNotification(String content);
}

class EmailStrategy implements INotificationStrategy{
    private String email;

    public EmailStrategy(String email){
        this.email = email;
    }

    @Override
    public void sendNotification(String content){
        System.out.println("Sending Email notification on "+email+"\n"+content);
    }
}


class SMSStrategy implements INotificationStrategy{
    private String mobileNumber;

    public SMSStrategy(String mobileNumber){
        this.mobileNumber = mobileNumber;
    }

    @Override
    public void sendNotification(String content){
        System.out.println("Sending SMS notification on "+mobileNumber+"\n"+content);
    }
}

class PopUpStrategy implements INotificationStrategy{
    @Override
    public void sendNotification(String content){
        System.out.println("Sending pop up notification \n"+content);
    }
}


// Notification Service -- Interacting directly with the client/ user
// Singleton class
class NotificationService{
    private NotificationObservable observable;
    private static NotificationService instance;
    private List<INotification> notifications;

    private NotificationService(){
        observable = new NotificationObservable();
        notifications = new ArrayList<>();
    }

    public static NotificationService getInstance(){
        if(instance==null){
            instance = new NotificationService();
        }
        return instance;
    }

    public NotificationObservable getObservable(){
        return observable;
    }

    public void sendNotification(INotification notification){
        notifications.add(notification);
        observable.setNotification(notification);
    }
}

public class NotificationSystem {
    public static void main(String[] args) {
        // create notification service
        NotificationService notificationService = NotificationService.getInstance();

        // Observable
        NotificationObservable observable = notificationService.getObservable();

        // Observers
        Logger logger = new Logger(observable);
        NotificationEngine notificationEngine = new NotificationEngine(observable);

        notificationEngine.addNotificationStrategy(new EmailStrategy("hayat123@gmail.com"));
        notificationEngine.addNotificationStrategy(new SMSStrategy("99999999999"));
        notificationEngine.addNotificationStrategy(new PopUpStrategy());

        // subscribe the observable through observers
        observable.addObserver(logger);
        observable.addObserver(notificationEngine);

        // Create notifications and wrap decorators
        INotification notification = new SimpleNotification("Notification system design is ready");
        notification = new TimeStampDecorator(notification);
        notification = new SignatureDecorator(notification);

        notificationService.sendNotification(notification);
    }
}