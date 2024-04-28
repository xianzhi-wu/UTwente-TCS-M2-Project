# Java

## [1. What’s OOP?](https://www.w3schools.com/java/java_oop.asp) 
Object-Oriented Programming (**OOP**) is a programming paradigm centred around the concept of objects, which **encapsulate** both data and methods to operate on that data. 
Contrary to procedural programming, where procedures or functions manipulate data, 
**OOP** emphasises the organisation of code into discrete (separate, distinct, or individually identifiable), reusable units called objects. \
**OOP** offers several advantages over procedural programming:\
**Efficiency**: **OOP** can often be faster and easier to execute due to its modular nature, allowing for more efficient reuse of code.\
**Structure**: **OOP** provides a clear structure for the programs, making it easier to understand and maintain.\
**DRY Principle**: **OOP** supports the "Don't Repeat Yourself" principle by enabling the extraction of common code into reusable components, reducing redundancy and enhancing maintainability.\
**Reusability**: **OOP** facilitates the creation of reusable components, leading to shorter development times and less code duplication.

## [2. What are Classes and Objects?](https://www.w3schools.com/java/java_oop.asp) 
A class is a template for objects that share common characteristics, and an object is an instance of a class. \
**Example**:\
class: Fruit \
objects: Banana, Apple, Mango 

## [3. Classes and Objects](https://www.w3schools.com/java/java_classes.asp)
In Java, everything is associated with classes and objects, along with its attributes and methods. For instance, in real life, a car is an object. The car has attributes, such as weight and color, and methods, such as drive and brake.\
A Class is an object **constructor**, or a "blueprint" for creating objects.

## [4. Class Attributes](https://www.w3schools.com/java/java_class_attributes.asp)
Class attributes are variables within a class.

## [5. Class Methods](https://www.w3schools.com/java/java_class_methods.asp)
Methods are declared within a class, and they are used to perform certain actions.

## [6. Constructors](https://www.w3schools.com/java/java_constructors.asp)
In Java, a constructor is a special method used to initialize objects. It is invoked when an instance of a class is created. It can be used to set initial values for object attributes:

```ts
// Create a class Example
public class Example {
  private int x;  // Create a private class attribute

  // Create a class constructor for the class Example
  public Example() {
    x = 5;  // Set the initial value for the class attribute x
  }

  // Public method
  public void printX() {
    System.out.println(x)
  }

  public static void main(String[] args) {
    Example myExample = new Example(); // Create an object of class Example (The constructor will be called)
    myExample.printX(); // Print the value of x
  }
}

// Outputs 5
```

**Note** that the constructor name must match the class name, and it cannot have a return type. If you don't define a constructor, Java generates a default one for you.

Constructors can also take parameters, which is used to initialize attributes.

```ts
public class Example {
  private int x; 

  public Example(int x) {
    this.x = x;
  }

  public void printX() {
    System.out.println(x)
  }

  public static void main(String[] args) {
    Example myExample = new Example();
    myExample.printX();
  }
}

// Outputs 5
```

## [7. Modifiers](https://www.w3schools.com/java/java_modifiers.asp)
Modifiers in Java are categorized into two groups:

**- Access Modifiers**: regulate the access level.

For classes, you can specify either public or use the default access level:\
***public***: Makes the class accessible from any other class.\
***default***: The class is only accessible by classes in the same package. This is used when no modifier is specified.

For attributes, methods, and constructors, the following modifiers can be applied:\
***public***: Makes the code accessible to all classes.\
***private***: Restricts access to only the declared class.\
***default***: Limits access to the same package. This is the default if no modifier is specified.\
***protected***: Allows access in the same package and subclasses. This is particularly relevant in the context of inheritance.

**- Non-Access Modifiers**: don't regulate the access level, but offer other functionalities.

For classes, you can apply either final or abstract:\
***final***: Prevents the class from being inherited by other classes.\
***abstract***:  Cannot be used to create objects. To utilize an abstract class, it must be inherited from another class.

For attributes and methods, the following modifiers are available:\
***final***: Attributes and methods cannot be overridden or modified.\
***static***: Attributes and methods belong to the class itself rather than to individual objects.\
***abstract***: Can only be used in an abstract class and exclusively on methods. Abstract methods lack a body, for example, abstract void run();. The implementation is provided by the subclass through inheritance.\
***transient***: Attributes and methods are excluded when serializing the containing object.\
***[synchronized](https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.4.3.6)***: Methods can only be accessed by one thread at a time.\
***[volatile](https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.3.1.4)***: Ensures that the value of an attribute is not cached thread-locally, and it is always read from the main memory.

**Note**: Locking(synchronized) can guarantee both visibility and atomicity; volatile variables can only guarantee visibility. (from 'Java Concurrency in Practice')\
***Visibility***: Visibility refers to the guarantee that changes made by one thread to shared data are visible to other threads.\
***Atomicity***: Atomicity refers to the guarantee that an operation on shared data will either fully execute or not execute at all, without being interrupted by another thread.

**static**\
Unlike public, a static can be accessed without creating an object of the class.\
Here is an example to demonstrate the differences between static and public methods:

```ts
public class Example {
  // Static method
  static void myStaticMethod() {
    System.out.println("Static methods can be called without creating objects");
  }

  // Public method
  public void myPublicMethod() {
    System.out.println("Public methods must be called by creating objects");
  }

  public static void main(String[ ] args) {
    myStaticMethod(); // Call the static method
    // myPublicMethod(); This would output an error

    Example myExample = new Example(); // Create an object of Example
    myExample.myPublicMethod(); // Call the public method
  }
}
```

**abstract**\
An abstract method belongs to an abstract class, and it does not have a body. The body is provided by the subclass.

```ts
// abstract class
abstract class Person {
  public String fname = "Xianzhi";
  public int age = 24;
  public abstract void study(); // abstract method
}

// Subclass (inherit from Person)
class Student extends Person {
  public int graduationYear = 2023;
  public void study() { // the body of the abstract method is provided here
    System.out.println("Studying CS all day long");
  }
}

class Main {
  public static void main(String[] args) {
    // create an object of the Student class (which inherits attributes and methods from Person)
    Student student = new Student();

    System.out.println("Name: " + student.fname);
    System.out.println("Age: " + student.age);
    System.out.println("Graduation Year: " + student.graduationYear);
    student.study(); // call abstract method
  }
}
```

**transient**\
Here's an example showing how you might serialize and deserialize a User object, while the password field, marked as **transient**, is excluded from serialization:

```ts
import java.io.*;

public class User implements Serializable {
    private String username;
    private transient String password; // sensitive information

    // Constructor
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Example usage
    public static void main(String[] args) {
        // Create a User object
        User user = new User("exampleUser", "sensitivePassword");

        // Serialize the User object
        try {
            FileOutputStream fileOut = new FileOutputStream("user.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(user);
            out.close();
            fileOut.close();
            System.out.println("User object serialized successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Deserialize the User object
        try {
            FileInputStream fileIn = new FileInputStream("user.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            User deserializedUser = (User) in.readObject();
            in.close();
            fileIn.close();
            System.out.println("User object deserialized successfully.");
            System.out.println("Username: " + deserializedUser.getUsername());
            // Password will be null since it's transient and not serialized
            System.out.println("Password: " + deserializedUser.getPassword());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
```

## 8. Encapsulation
[Encapsulation*](https://docs.oracle.com/en/database/oracle/oracle-database/19/jjdev/Java-overview.html#GUID-68EE1A7B-1F78-4074-AB76-AF9B2CE878F6) (from Java Developer's Guide by Oracle) describes the ability of an object to hide its data and methods from the rest of the world and is one of the fundamental principles of object-oriented programming. In Java, a class encapsulates the fields, which hold the state of an object, and the methods, which define the actions of the object. 



[Encapsulation*](https://www.w3schools.com/java/java_encapsulation.asp) (From w3schools) ensures that sensitive data is hidden from users by:
1. Declaring class variables/attributes as private.
2. Providing public getter and setter methods to access and update the value of private variables.

**Get and Set**\
Public getter and setter methods to access and modify private variables.\
The "get" method retrieves the variable value, while the "set" method assigns a new value to the variable.
```ts
// Encapsulated class with private attribute and public methods
public class Circle {
    private double radius;

    // Getter method
    public double getRadius() {
        return radius;
    }

    // Setter method
    public void setRadius(double radius) {
        this.radius = radius;
    }
}
```

**Why Encapsulation?**
1. Enhanced Control: Encapsulation provides precise control over class attributes and methods, allowing developers to manage their accessibility and behavior effectively. Class attributes can be made read-only (if you only use the get method), or write-only (if you only use the set method).

The Circle class (the above example) encapsulates the radius attribute with private access and provides public getter and setter methods to control access to it.

2. Flexibility: Encapsulation enables modular code, allowing changes in one part without affecting others. Modifying internal implementation is easier, maintaining separation of concerns.

```ts
// Encapsulated class with private attribute and public method
public class TemperatureConverter {
    private double temperature;

    // Method to convert Celsius to Fahrenheit
    public double convertCelsiusToFahrenheit() {
        return (temperature * 9 / 5) + 32;
    }
}
```

The TemperatureConverter class encapsulates the temperature attribute with private access and provides a method to convert Celsius to Fahrenheit. If the internal logic for conversion changes, it can be modified within this method without affecting other parts of the code.

3. Increased Security of Data: By controlling access to class attributes, encapsulation prevents direct manipulation, enforcing validation rules and data consistency. This reduces the risk of unintended data corruption.

```ts
// Encapsulated class with private attribute and public methods
public class BankAccount {
    private double balance;

    // Method to deposit money
    public void deposit(double amount) {
        // Add validation logic
        if (amount > 0) {
            balance += amount;
        }
    }

    // Method to withdraw money
    public void withdraw(double amount) {
        // Add validation logic
        if (amount > 0 && amount <= balance) {
            balance -= amount;
        }
    }
}
```

The BankAccount class encapsulates the balance attribute with private access and provides methods to deposit and withdraw money. Validation logic ensures that only valid transactions are processed, enhancing the security of the account data.

## [9. Java Packages & API](https://www.w3schools.com/java/java_packages.asp)
A package is used to organize related classes, akin to a folder in a file directory. It helps prevent name conflicts and promotes maintainable code. There are two types of packages:
1. Built-in Packages (packages from the Java API)
2. User-defined Packages (created by developers)

## [10. Inheritance](https://www.w3schools.com/java/java_inheritance.asp)
In Java, we can inherit attributes and methods from one class to another. This concept is categorized into:

Subclass (Child): The class that inherits from another class.\
Superclass (Parent): The class being inherited from.

To inherit from a class, the extends keyword is used.\
In the following example, the Car class (subclass) inherits the attributes and methods from the Vehicle class (superclass):

```ts
class Vehicle {
  protected String brand = "Ford";        // Vehicle attribute
  public void honk() {                    // Vehicle method
    System.out.println("Tuut, tuut!");
  }
}

class Car extends Vehicle {
  private String modelName = "Mustang";    // Car attribute
  public static void main(String[] args) {

    // Create a myCar object
    Car myCar = new Car();

    // Call the honk() method (from the Vehicle class) on the myCar object
    myCar.honk();

    // Display the value of the brand attribute (from the Vehicle class) and the value of the modelName from the Car class
    System.out.println(myCar.brand + " " + myCar.modelName);
  }
}
```

**Why And When To Use "Inheritance"?**\
Inheritance is beneficial for code reusability, allowing you to utilize attributes and methods from an existing class when creating a new class.

**Note**: To prevent other classes from inheriting from a class, you can use the final keyword.

## [11. Polymorphism](https://www.geeksforgeeks.org/polymorphism-in-java/)
Polymorphism means "having many forms".\
Inheritance enables us inherit attributes and methods from another class, while Polymorphism uses those methods to perform different tasks. (Polymorphism enables the execution of a single action in different ways.)

## [12. Abstraction](https://www.w3schools.com/java/java_abstract.asp)
Data abstraction is the process of hiding certain details and only showing important information to the user. Abstraction can be achieved with either abstract classes or interfaces.

The abstract keyword is a non-access modifier used for classes and methods:\
***Abstract Class***: A restricted class that cannot be instantiated on its own and must be inherited from another class to be used.\
***Abstract Method***: A method declared in an abstract class that lacks implementation details and must be overridden by subclasses.
An abstract class can contain both abstract and regular methods:

```ts
abstract class Animal {
    public abstract void animalSound();
    
    public void sleep() {
        System.out.println("Zzz");
    }
}
```

## [13. Interface](https://www.w3schools.com/java/java_interface.asp)
An interface is a completely "abstract class" that is used to group related methods with empty bodies (without implmentation).\
To use the methods defined in an interface, another class must implement it using the "implements" keyword (instead of 'extends'). The implementing class then provides the implementation for the interface methods.

```ts
interface Identity {
    String getName();
    int getAge();
}

class Person implements Identity {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getAge() {
        return age;
    }

    // Other methods and properties specific to a person
}
```

**Notes** on interfaces:
* An interface does not contain a constructor so it cannot be used to create objects.
* Interface methods do not have a body so the body is provided (implemented) by the class that 'implements' it.
* When implementing an interface, all its methods must be overrided.
* Interface methods are abstract and public by default.
* Interface attributes are public, static and final by default.

***Java doesn't support "multiple inheritance," where a class inherits from more than one superclass. However, it can achieve similar functionality through interfaces. A class can implement multiple interfaces by listing them separated by commas.***

```ts
interface Identity {
    String getName();
    int getAge();
}

interface Employment {
    void hire();
    void fire();
}

class Person implements Identity, Employment {
    private String name;
    private int age;
    private boolean employed;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
        this.employed = false; // Initially not employed
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public void hire() {
        if (!employed) {
            employed = true;
            System.out.println(name + " has been hired.");
        } else {
            System.out.println(name + " is already employed.");
        }
    }

    @Override
    public void fire() {
        if (employed) {
            employed = false;
            System.out.println(name + " has been fired.");
        } else {
            System.out.println(name + " is not currently employed.");
        }
    }

    // Other methods and properties specific to a person
}
```
## [14. Enums](https://www.w3schools.com/java/java_enums.asp)
An enum is a 'special' class that is used to represent a group of constants (immutable variables).\
To define an enum, use the enum keyword instead of class or interface, and list the constants separated by commas. Conventionally, these constants are in uppercase letters.

```ts
enum Level {
  LOW,
  MEDIUM,
  HIGH
}
```

Enum constants can be accessed with the dot syntax:

```ts
Level myVar = Level.MEDIUM;
```

An enum can also nested inside a class:

```ts
public class Main {
  enum Level {
    LOW,
    MEDIUM,
    HIGH
  }

  public static void main(String[] args) {
    Level myVar = Level.MEDIUM; 
    System.out.println(myVar);
  }
}
```

Enums can be used in switch statements to check for corresponding values:

```ts
enum Level {
  LOW,
  MEDIUM,
  HIGH
}

public class Main {
  public static void main(String[] args) {
    Level myVar = Level.MEDIUM;

    switch(myVar) {
      case LOW:
        System.out.println("Low level");
        break;
      case MEDIUM:
         System.out.println("Medium level");
        break;
      case HIGH:
        System.out.println("High level");
        break;
    }
  }
}
```

The enum type provides a `values()` method, which returns an array containing all enum constants. This method is useful for iterating through the constants of an enum.

```ts
for (Level myVar : Level.values()) {
  System.out.println(myVar);
}
```


**Difference*** between Enums and Classes
Just like a class, an enum can have attributes and methods. The only difference is that enum constants are public, static and final (immutable).\
Unlike a class, an enum cannot be used to create objects, and it cannot extend other classes either. However, it can implement interfaces.

***Why And When To Use Enums?***
Use enums when you have values that you aren't going to change, like days of the week, months, colors, etc.

---
# [Concurrency](https://web.mit.edu/6.031/www/sp21/classes/20-concurrency/)

## 1. Concurrency
**Concurrency** means multiple computations are happening at the same time. (multiple computations running simultaneously)

Concurrency is essential in modern programming:
* Web sites must handle multiple simultaneous users.
* Mobile apps need to do some of their processing on servers (“in the cloud”).

## 2. Two models for concurrent programming
There are two common models for concurrent programming: shared memory and message passing.

***Shared memory***. In the shared memory model of concurrency, concurrent modules interact by reading and writing shared objects in memory. \
***Message passing***. In the message-passing model, concurrent modules interact by sending messages to each other through a communication channel. Modules send off messages, and incoming messages to each module are queued up for handling.

## 3. Processes, threads, time-slicing
The message-passing and shared-memory models are about how concurrent modules communicate. The concurrent modules themselves come in two different kinds: processes and threads.

**Process**. A process is an instance of a running program that is isolated from other processes on the same machine. In particular, it has its own private section of the machine’s memory.

**Thread**. A thread is a locus of control inside a running program. 

**Time slicing**. How can you have many concurrent threads with only one or two processors in your computer? When there are more threads than processors, concurrency is simulated by time slicing, which means that the processor switches between threads. \
On most systems, time slicing happens unpredictably and nondeterministically, meaning that a thread may be paused or resumed at any time.

## 4. Race condition
A race condition means that the correctness of the program (the satisfaction of postconditions and invariants) depends on the relative timing of events in concurrent computations A and B. When this happens, we say “A is in a race with B.”


Source: MIT 6.031 Software Construction course reading 20 - Concurrency. [https://web.mit.edu/6.031/www/sp21/classes/20-concurrency/](https://web.mit.edu/6.031/www/sp21/classes/20-concurrency/)

---
# Locks and Synchronization

## 1. Synchronization
The correctness of a concurrent program should not depend on accidents of timing.

Since race conditions caused by concurrent manipulation of shared mutable data are disastrous bugs — hard to discover, hard to reproduce, hard to debug — we need a way for concurrent modules that share memory to **synchronize** with each other.

**Locks** are one synchronization technique. A lock is an abstraction that allows at most one thread to own it at a time. Holding a lock is how one thread informs other threads: “I’m working with this thing, don’t touch it right now.”

Locks have two operations:

**acquire** allows a thread to take ownership of a lock. If a thread tries to acquire a lock currently owned by another thread, it blocks until the other thread releases the lock. At that point, it will contend with any other threads that are trying to acquire the lock. At most one thread can own the lock at a time.

**release** relinquishes ownership of the lock, allowing another thread to take ownership of it.

Using a lock also tells the compiler and processor that you’re using shared memory concurrently, so that registers and caches will be written back correctly to shared storage.

**Blocking means**, in general, that a thread waits (without doing further work) until an event occurs.

## 2. Locking
Locks are so commonly-used that Java provides them as a built-in language feature.

In Java, every object has a lock implicitly associated with it — a String, an array, an ArrayList, and every class you create, all of their object instances have a lock. Even a humble Object has a lock, so bare Objects are often used for explicit locking:

```ts
Object lock = new Object();
```

You can’t call acquire and release on Java’s intrinsic locks, however. Instead you use the synchronized statement to acquire the lock for the duration of a statement block:

```ts
synchronized (lock) { // thread blocks here until lock is free
    // now this thread has the lock
    // do something
} // exiting the block releases the lock
```

Synchronized regions like this provide **mutual exclusion**: only one thread at a time can be in a synchronized region guarded by a given object’s lock. In other words, you are back in sequential programming world, with only one thread running at a time, at least with respect to other synchronized regions that refer to the same object.

## 3. [Deadlock](https://docs.oracle.com/javase/tutorial/essential/concurrency/deadlock.html)
Deadlock occurs when concurrent modules are stuck waiting for each other to do something. A deadlock may involve more than two modules, e.g., A may be waiting for B, which is waiting for C, which is waiting for A. None of them can make progress. The essential feature of deadlock is a cycle of dependencies like this.

***Deadlock solution 1: lock ordering***
***Deadlock solution 2: coarse-grained locking**

Source: MIT 6.031 Software Construction course reading 22 - Locks and Synchronization. [https://web.mit.edu/6.031/www/sp21/classes/22-locks/](https://web.mit.edu/6.031/www/sp21/classes/22-locks/)

---
# Sockets & Networking

## 1. Client/server design pattern
The client/server design pattern is used for communication with message passing.
In this pattern there are two kinds of processes: clients and servers. A client initiates the communication by connecting to a server. The client sends requests to the server, and the server sends replies back. Finally, the client disconnects. A server might handle connections from many clients concurrently, and clients might also connect to multiple servers.

Many Internet applications work this way: web browsers are clients for web servers, an email program like Outlook is a client for a mail server, etc.

## 2. Sockets and streams
There are some important concepts related to network communication, and to input/output in general. Input/output (I/O) refers to communication into and out of a process – perhaps over a network, or to/from a file, or with the user on the command line or a graphical user interface.

**[2.1. IP addresses](https://en.wikipedia.org/wiki/IP_address)**\
A network interface is identified by an IP address. 

**[2.2. Hostnames](https://en.wikipedia.org/wiki/Hostname)**\
Hostnames are names that can be translated into IP addresses. A single hostname can map to different IP addresses at different times; and multiple hostnames can map to the same IP address.

**[2.3. Port](https://en.wikipedia.org/wiki/Port_(computer_networking)) numbers**\
A single machine might have multiple server applications that clients wish to connect to, so we need a way to direct traffic on the same network interface to different processes.

Network interfaces have multiple ports identified by a 16-bit number. Port 0 is reserved, so port numbers effectively run from 1 to 65535.

A server process binds to a particular port — it is now listening on that port. A port can have only one listener at a time, so if some other server process tries to listen to the same port, it will fail.

**2.4. Network sockets**\
A [socket](https://en.wikipedia.org/wiki/Network_socket) represents one end of the connection between client and server.

A ***listening socket*** is used by a server process to wait for connections from remote clients.\
In Java, use [ServerSocket](https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/net/ServerSocket.html) to make a listening socket, and use its [accept](https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/net/ServerSocket.html#accept()) method to listen to it. Remember that a port can only have one listener, so this will fail if another thread or process is currently listening at the same port.

A ***connected socket*** can send and receive messages to and from the process on the other end of the connection.\
In Java, clients use a [Socket](https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/net/Socket.html) constructor to establish a socket connection to a server. Servers obtain a connected socket as a Socket object returned from ServerSocket.accept.

**2.5. Buffers**\
The data that clients and servers exchange over the network is sent in chunks.\
The network chops that chunk up into packets, and each packet is routed separately over the network. At the other end, the receiver reassembles the packets together into a stream of bytes.\
When data arrive, they go into a buffer, an array in memory that holds the data until you read it.

**2.6. Byte streams**\
The data going into or coming out of a socket is a stream of bytes.

In Java, InputStream objects represent sources of data flowing into your program. For example:
* User input from System.in
* Input from a network socket
  
OutputStream objects represent data sinks, places we can write data to. For example:
* System.out for normal output to the user
* System.err for error output
* Output to a network socket

**2.7. Character streams**\
The stream of bytes provided by InputStream and OutputStream is often too low-level to be useful. We may need to interpret the stream of bytes as a stream of Unicode characters, because Unicode can represent a wide variety of human languages (not to mention emoji). A String is a sequence of Unicode characters, not a sequence of bytes, so if we want to use strings to manipulate the data inside our program, then we need to convert incoming bytes into Unicode, and convert Unicode back to bytes when we write it out.

In Java, Reader and Writer represent incoming and outgoing streams of Unicode characters. For example, the wrappers InputStreamReader and OutputStreamWriter adapt a byte stream into a character stream.

**2.8. Blocking**\
Input/output streams exhibit blocking behavior. For example, for socket streams:
* When an incoming socket’s buffer is empty, calling read blocks until data are available.
* When the destination socket’s buffer is full, calling write blocks until space is available.

## [3. Using network sockets in Java (Example)](https://web.mit.edu/6.031/www/sp21/classes/24-sockets-networking/#using_network_sockets_in_java)
* readLine() returns null if the stream it is reading from has been closed by the other side. For a socket stream, this would indicate that the server has closed its side of the connection.
* The message content isn’t sent to the server until the buffer fills up or the client closes its side of the connection. So it’s critically important to flush the buffers after writing, forcing all buffer contents to be sent.

## 4. Wire protocols
A **protocol** is a set of messages that can be exchanged by two communicating parties. A **wire protocol** in particular is a set of messages represented as byte sequences, like hello world and bye (assuming we’ve agreed on a way to encode those characters into bytes).

Many Internet applications use simple ASCII-based wire protocols.
* Telnet client
* HTTP
* SMTP

Source: MIT 6.031 Software Construction course reading 24 - Sockets & Networking. [https://web.mit.edu/6.031/www/sp21/classes/24-sockets-networking/](https://web.mit.edu/6.031/www/sp21/classes/24-sockets-networking/)

