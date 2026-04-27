package lfaf.university.labs2026;

public class Main {
     static void main(String[] args) {
        var generator = new RegexGenerator();
        System.out.println(generator.generate("M?N{2}(O|P){3}Q*R+"));
        System.out.println(generator.generate("(X|Y|Z){3}8+(9|0)"));
        System.out.println(generator.generate("(H|i)(J|K)L*N?"));

        // Show ASTs

         System.out.println(generator.parse("M?N{2}(O|P){3}Q*R+"));
         System.out.println(generator.parse("(X|Y|Z){3}8+(9|0)"));
         System.out.println(generator.parse("(H|i)(J|K)L*N?"));
    }
}
