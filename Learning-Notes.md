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

**Notes** on interfaces:\
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



