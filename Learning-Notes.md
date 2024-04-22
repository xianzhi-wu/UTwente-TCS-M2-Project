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


