package somihmih;

import java.io.IOException;

public class Ex {
    public static void main(String[] args) {
        try {
            foo();
        } catch (RuntimeException e) {
            System.out.println("EXC:" + e.getMessage());
        }
        System.out.println("End main");
    }

    private static void foo() {

            String str = null;
            System.out.println("Hi");
            System.out.println("Hi 1");
            System.out.println("Length: " + str.length());
            System.out.println("Hi 2");
            System.out.println("Hi 3");


        System.out.println("End method");
    }
}
