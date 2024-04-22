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
A class is a template for objects, and an object is an instance of a class. \
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

## [Modifiers](https://www.w3schools.com/java/java_modifiers.asp)
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
***abstract***:  cannot be used to create objects. To utilize an abstract class, it must be inherited from another class.

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


